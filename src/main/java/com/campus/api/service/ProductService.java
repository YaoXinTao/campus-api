package com.campus.api.service;

import com.campus.api.dto.product.ProductDTO;
import com.campus.api.dto.product.ProductQuery;
import com.campus.api.common.PageResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    /**
     * 获取商品详情
     */
    ProductDTO getProductById(Long id);

    /**
     * 获取商品列表
     */
    PageResult<ProductDTO> getProductList(ProductQuery query);

    /**
     * 创建商品
     */
    void createProduct(ProductDTO productDTO);

    /**
     * 更新商品
     */
    void updateProduct(ProductDTO productDTO);

    /**
     * 删除商品
     */
    void deleteProduct(Long id);

    /**
     * 更新商品状态
     */
    void updateProductStatus(Long id, Integer status);

    /**
     * 更新审核状态
     */
    void updateVerifyStatus(Long id, Integer verifyStatus);

    /**
     * 更新推荐状态
     */
    void updateFeatured(Long id, Integer isFeatured);

    /**
     * 上传商品图片
     * @param file 图片文件
     * @param type 图片类型：main-主图 album-相册图
     * @return 图片URL
     */
    String uploadImage(MultipartFile file, String type);

    /**
     * 增加商品浏览量
     */
    void incrementViewCount(Long id);

    /**
     * 获取相似商品推荐
     * @param productId 商品ID
     * @param limit 返回数量限制
     * @return 相似商品列表
     */
    List<ProductDTO> getSimilarProducts(Long productId, Integer limit);

    /**
     * 添加商品浏览记录
     * @param userId 用户ID
     * @param productId 商品ID
     */
    void addViewHistory(Long userId, Long productId);
} 