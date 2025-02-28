package com.campus.api.service.impl;

import com.campus.api.common.PageResult;
import com.campus.api.common.exception.BusinessException;
import com.campus.api.dto.product.ProductDTO;
import com.campus.api.dto.product.ProductQuery;
import com.campus.api.dto.product.ProductSkuDTO;
import com.campus.api.entity.Product;
import com.campus.api.entity.ProductDetail;
import com.campus.api.entity.ProductViewLog;
import com.campus.api.mapper.ProductMapper;
import com.campus.api.mapper.ProductDetailMapper;
import com.campus.api.mapper.ProductViewLogMapper;
import com.campus.api.service.ProductService;
import com.campus.api.service.ProductSkuService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Map;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductDetailMapper productDetailMapper;

    @Autowired
    private ProductViewLogMapper productViewLogMapper;

    @Autowired
    private ProductSkuService productSkuService;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${upload.path}")
    private String uploadPath;

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.access-key-id}")
    private String accessKeyId;

    @Value("${aliyun.oss.access-key-secret}")
    private String accessKeySecret;

    @Value("${aliyun.oss.bucket-name}")
    private String bucketName;

    @Override
    public ProductDTO getProductById(Long id) {
        // 获取商品基本信息
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        // 获取商品详情
        ProductDetail detail = productDetailMapper.selectByProductId(id);
        
        // 转换为DTO
        return convertToDTO(product, detail);
    }

    @Override
    public PageResult<ProductDTO> getProductList(ProductQuery query) {
        // 计算分页参数
        Integer limit = query.getPageSize();
        Integer offset = (query.getPageNum() - 1) * query.getPageSize();
        
        // 查询商品列表
        List<Product> products = productMapper.selectList(
            query.getKeyword(),
            query.getCategoryId(),
            query.getStatus(),
            query.getVerifyStatus(),
            query.getIsFeatured(),
            query.getSortField(),
            query.getSortOrder(),
            query.getMinPrice(),
            query.getMaxPrice(),
            query.getPageSize(),
            (query.getPageNum() - 1) * query.getPageSize()
        );
        
        // 转换为DTO列表
        List<ProductDTO> dtoList = new ArrayList<>();
        for (Product product : products) {
            ProductDetail detail = productDetailMapper.selectByProductId(product.getId());
            dtoList.add(convertToDTO(product, detail));
        }
        
        // 获取总记录数
        long total = productMapper.count(
            query.getKeyword(),
            query.getCategoryId(),
            query.getStatus(),
            query.getVerifyStatus(),
            query.getIsFeatured()
        );
        
        return PageResult.of(query.getPageNum(), query.getPageSize(), total, dtoList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createProduct(ProductDTO productDTO) {
        // 转换为实体
        Product product = new Product();
        BeanUtils.copyProperties(productDTO, product);
        
        // 设置初始值
        product.setTotalSales(0);
        product.setViewCount(0);
        product.setStatus(productDTO.getStatus() != null ? productDTO.getStatus() : 0);
        product.setVerifyStatus(productDTO.getVerifyStatus() != null ? productDTO.getVerifyStatus() : 0);
        product.setTotalStock(productDTO.getTotalStock() != null ? productDTO.getTotalStock() : 0);
        product.setCreatedAt(LocalDateTime.now());
        
        // 处理相册图片
        try {
            if (productDTO.getAlbum() != null) {
                // 确保album是字符串数组
                List<String> albumList = productDTO.getAlbum();
                product.setAlbum(objectMapper.writeValueAsString(albumList));
            } else {
                product.setAlbum("[]");
            }
        } catch (JsonProcessingException e) {
            log.error("商品相册处理失败", e);
            throw new BusinessException("商品相册格式错误");
        }
        
        // 保存商品基本信息
        productMapper.insert(product);
        
        // 保存商品详情
        ProductDetail detail = new ProductDetail();
        detail.setProductId(product.getId());
        detail.setDescription(productDTO.getDescription());
        detail.setRichContent(productDTO.getRichContent());
        detail.setSpecDesc(productDTO.getSpecDesc());
        detail.setPackingList(productDTO.getPackingList());
        detail.setServiceNotes(productDTO.getServiceNotes());
        detail.setCreatedAt(LocalDateTime.now());
        
        productDetailMapper.insert(detail);

        // 处理SKU列表
        if (productDTO.getSkuList() != null && !productDTO.getSkuList().isEmpty()) {
            log.info("开始创建商品SKU列表, 商品ID: {}, SKU数量: {}", product.getId(), productDTO.getSkuList().size());
            
            for (ProductSkuDTO skuDTO : productDTO.getSkuList()) {
                // 设置商品ID
                skuDTO.setProductId(product.getId());
                
                // 确保SKU图片URL被正确设置
                log.info("SKU图片URL: {}", skuDTO.getImageUrl());
                
                // 创建SKU
                try {
                    productSkuService.createSku(skuDTO);
                    log.info("创建SKU成功, 商品ID: {}, SKU编码: {}, 图片URL: {}", 
                        product.getId(), skuDTO.getSkuCode(), skuDTO.getImageUrl());
                } catch (Exception e) {
                    log.error("创建SKU失败, 商品ID: {}, SKU编码: {}, 错误: {}", 
                        product.getId(), skuDTO.getSkuCode(), e.getMessage(), e);
                    throw new BusinessException("创建SKU失败: " + e.getMessage());
                }
            }
        } else if (productDTO.getSpecDesc() != null) {
            // 如果没有SKU列表但有规格描述，创建默认SKU
            try {
                // 解析规格描述
                Map<String, String> specMap = objectMapper.readValue(productDTO.getSpecDesc(), Map.class);
                
                // 创建SKU DTO
                ProductSkuDTO skuDTO = new ProductSkuDTO();
                skuDTO.setProductId(product.getId());
                skuDTO.setSkuCode(product.getId() + "-default");
                skuDTO.setPrice(product.getPrice());
                skuDTO.setMarketPrice(product.getMarketPrice());
                skuDTO.setStock(product.getTotalStock());
                skuDTO.setSales(0);
                skuDTO.setStatus(1);
                
                // 设置规格数据
                List<ProductSkuDTO.SpecData> specDataList = new ArrayList<>();
                for (Map.Entry<String, String> entry : specMap.entrySet()) {
                    ProductSkuDTO.SpecData specData = new ProductSkuDTO.SpecData();
                    specData.setKey(entry.getKey());
                    specData.setValue(entry.getValue());
                    specDataList.add(specData);
                }
                skuDTO.setSpecData(specDataList);
                
                // 创建SKU
                productSkuService.createSku(skuDTO);
            } catch (Exception e) {
                log.error("创建默认SKU失败", e);
                throw new BusinessException("创建默认SKU失败: " + e.getMessage());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProduct(ProductDTO productDTO) {
        // 检查商品是否存在
        Product existingProduct = productMapper.selectById(productDTO.getId());
        if (existingProduct == null) {
            throw new BusinessException("商品不存在");
        }
        
        // 更新商品基本信息
        Product product = new Product();
        BeanUtils.copyProperties(productDTO, product);
        
        // 处理相册图片
        try {
            if (productDTO.getAlbum() != null && !productDTO.getAlbum().isEmpty()) {
                // 确保album是字符串数组
                List<String> albumList = productDTO.getAlbum().stream()
                    .filter(item -> item != null && !item.trim().isEmpty())
                    .collect(Collectors.toList());
                product.setAlbum(objectMapper.writeValueAsString(albumList));
                log.info("处理后的相册数据: {}", product.getAlbum());
            } else {
                product.setAlbum("[]");
            }
        } catch (JsonProcessingException e) {
            log.error("商品相册处理失败", e);
            throw new BusinessException("商品相册格式错误");
        }
        
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.update(product);
        
        // 更新商品详情
        ProductDetail detail = new ProductDetail();
        detail.setProductId(product.getId());
        detail.setDescription(productDTO.getDescription());
        detail.setRichContent(productDTO.getRichContent());
        detail.setSpecDesc(productDTO.getSpecDesc());
        detail.setPackingList(productDTO.getPackingList());
        detail.setServiceNotes(productDTO.getServiceNotes());
        detail.setUpdatedAt(LocalDateTime.now());
        
        productDetailMapper.update(detail);

        // 处理SKU更新
        if (productDTO.getSkuList() != null && !productDTO.getSkuList().isEmpty()) {
            log.info("开始更新商品SKU, 商品ID: {}, SKU数量: {}", product.getId(), productDTO.getSkuList().size());
            
            // 获取现有的SKU列表
            List<ProductSkuDTO> existingSkus = productSkuService.getSkuListByProductId(product.getId());
            Map<Long, ProductSkuDTO> existingSkuMap = existingSkus.stream()
                .collect(Collectors.toMap(ProductSkuDTO::getId, sku -> sku));
            
            // 遍历新的SKU列表
            for (ProductSkuDTO skuDTO : productDTO.getSkuList()) {
                skuDTO.setProductId(product.getId());
                
                if (skuDTO.getId() != null && existingSkuMap.containsKey(skuDTO.getId())) {
                    // 更新现有SKU
                    log.info("更新现有SKU, ID: {}, 图片URL: {}", skuDTO.getId(), skuDTO.getImageUrl());
                    productSkuService.updateSku(skuDTO);
                    existingSkuMap.remove(skuDTO.getId());
                } else {
                    // 创建新SKU
                    log.info("创建新SKU, 规格: {}, 图片URL: {}", skuDTO.getSpecData(), skuDTO.getImageUrl());
                    productSkuService.createSku(skuDTO);
                }
            }
            
            // 删除不再使用的SKU
            existingSkuMap.keySet().forEach(skuId -> {
                log.info("删除不再使用的SKU, ID: {}", skuId);
                productSkuService.deleteSku(skuId);
            });
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProduct(Long id) {
        // 删除商品详情
        productDetailMapper.deleteByProductId(id);
        // 删除商品基本信息
        productMapper.deleteById(id);
    }

    @Override
    public void updateProductStatus(Long id, Integer status) {
        productMapper.updateStatus(id, status);
    }

    @Override
    public void updateVerifyStatus(Long id, Integer verifyStatus) {
        productMapper.updateVerifyStatus(id, verifyStatus);
    }

    @Override
    public void updateFeatured(Long id, Integer isFeatured) {
        productMapper.updateFeatured(id, isFeatured);
    }

    @Override
    public String uploadImage(MultipartFile file, String type) {
        if (file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }
        
        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException("只能上传图片文件");
        }
        
        try {
            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + extension;
            
            // 创建OSSClient实例
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            
            try {
                // 上传文件到OSS
                String objectName = "product/" + type + "/" + filename;
                ossClient.putObject(bucketName, objectName, file.getInputStream());
                
                // 返回完整的访问URL
                return "https://" + bucketName + "." + endpoint + "/" + objectName;
            } finally {
                ossClient.shutdown();
            }
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new BusinessException("文件上传失败");
        }
    }

    @Override
    public void incrementViewCount(Long id) {
        productMapper.updateViewCount(id);
    }

    @Override
    public List<ProductDTO> getSimilarProducts(Long productId, Integer limit) {
        // 获取当前商品的分类ID
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        // 查询同分类下的其他商品
        ProductQuery query = new ProductQuery();
        query.setCategoryId(product.getCategoryId());
        query.setStatus(1);
        query.setVerifyStatus(1);
        query.setPageSize(limit);
        
        // 排除当前商品
        List<ProductDTO> products = getProductList(query).getList()
            .stream()
            .filter(p -> !p.getId().equals(productId))
            .limit(limit)
            .collect(Collectors.toList());

        return products;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addViewHistory(Long userId, Long productId) {
        // 检查商品是否存在
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        // 添加浏览记录
        ProductViewLog viewLog = new ProductViewLog();
        viewLog.setUserId(userId);
        viewLog.setProductId(productId);
        productViewLogMapper.insert(viewLog);

        // 增加商品浏览量
        incrementViewCount(productId);
    }

    /**
     * 将商品实体转换为DTO
     */
    private ProductDTO convertToDTO(Product product, ProductDetail detail) {
        ProductDTO dto = new ProductDTO();
        BeanUtils.copyProperties(product, dto);
        
        // 设置库存和销量
        dto.setTotalStock(product.getTotalStock() != null ? product.getTotalStock() : 0);
        dto.setTotalSales(product.getTotalSales() != null ? product.getTotalSales() : 0);
        
        // 设置审核状态
        dto.setVerifyStatus(product.getVerifyStatus() != null ? product.getVerifyStatus() : 0);
        
        // 处理相册
        try {
            if (product.getAlbum() != null && !product.getAlbum().trim().isEmpty()) {
                List<String> album = objectMapper.readValue(product.getAlbum(), new TypeReference<List<String>>() {});
                dto.setAlbum(album.stream()
                    .filter(item -> item != null && !item.trim().isEmpty())
                    .collect(Collectors.toList()));
                log.info("转换后的相册数据: {}", dto.getAlbum());
            } else {
                dto.setAlbum(new ArrayList<>());
            }
        } catch (JsonProcessingException e) {
            log.error("商品相册解析失败", e);
            dto.setAlbum(new ArrayList<>());
        }
        
        // 复制详情信息
        if (detail != null) {
            dto.setDescription(detail.getDescription());
            dto.setRichContent(detail.getRichContent());
            dto.setSpecDesc(detail.getSpecDesc());
            dto.setPackingList(detail.getPackingList());
            dto.setServiceNotes(detail.getServiceNotes());
        }

        // 获取SKU列表
        try {
            List<ProductSkuDTO> skuList = productSkuService.getSkuListByProductId(product.getId());
            dto.setSkuList(skuList);
            log.info("获取商品SKU列表成功, 商品ID: {}, SKU数量: {}", product.getId(), skuList.size());
        } catch (Exception e) {
            log.error("获取商品SKU列表失败", e);
            dto.setSkuList(new ArrayList<>());
        }
        
        return dto;
    }
} 