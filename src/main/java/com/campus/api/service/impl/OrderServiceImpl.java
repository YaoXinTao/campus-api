package com.campus.api.service.impl;

import com.campus.api.common.PageResult;
import com.campus.api.common.exception.BusinessException;
import com.campus.api.dto.order.CreateOrderDTO;
import com.campus.api.dto.order.OrderDetailDTO;
import com.campus.api.dto.order.OrderQueryDTO;
import com.campus.api.dto.order.RefundApplyDTO;
import com.campus.api.dto.order.ExchangeApplyDTO;
import com.campus.api.dto.order.ExchangeDeliveryDTO;
import com.campus.api.dto.order.ExchangeDetailDTO;
import com.campus.api.dto.order.TrackingInfo;
import com.campus.api.entity.Order;
import com.campus.api.entity.OrderItem;
import com.campus.api.entity.PaymentRecord;
import com.campus.api.entity.RefundRecord;
import com.campus.api.entity.OrderDelivery;
import com.campus.api.entity.OrderLog;
import com.campus.api.entity.ExchangeRecord;
import com.campus.api.entity.ExchangeDelivery;
import com.campus.api.entity.Address;
import com.campus.api.entity.ProductSku;
import com.campus.api.entity.BusinessRule;
import com.campus.api.entity.Coupon;
import com.campus.api.entity.UserCoupon;
import com.campus.api.mapper.OrderMapper;
import com.campus.api.mapper.OrderItemMapper;
import com.campus.api.mapper.OrderDeliveryMapper;
import com.campus.api.mapper.PaymentRecordMapper;
import com.campus.api.mapper.RefundRecordMapper;
import com.campus.api.mapper.OrderLogMapper;
import com.campus.api.mapper.ExchangeRecordMapper;
import com.campus.api.mapper.ExchangeDeliveryMapper;
import com.campus.api.mapper.AddressMapper;
import com.campus.api.mapper.ProductSkuMapper;
import com.campus.api.mapper.ProductMapper;
import com.campus.api.mapper.CouponMapper;
import com.campus.api.mapper.UserCouponMapper;
import com.campus.api.service.BusinessRuleService;
import com.campus.api.service.DistributedLockService;
import com.campus.api.service.MessageService;
import com.campus.api.service.OrderService;
import com.campus.api.common.util.SnowflakeIdGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final TypeReference<List<Map<String, String>>> SKU_SPEC_TYPE = new TypeReference<>() {};
    private static final TypeReference<List<Map<String, Object>>> FULL_SPEC_TYPE = new TypeReference<>() {};

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final OrderDeliveryMapper orderDeliveryMapper;
    private final PaymentRecordMapper paymentRecordMapper;
    private final RefundRecordMapper refundRecordMapper;
    private final OrderLogMapper orderLogMapper;
    private final ObjectMapper objectMapper;
    private final ExchangeRecordMapper exchangeRecordMapper;
    private final ExchangeDeliveryMapper exchangeDeliveryMapper;
    private final AddressMapper addressMapper;
    private final MessageService messageService;
    private final ProductSkuMapper productSkuMapper;
    private final ProductMapper productMapper;
    private final SnowflakeIdGenerator idGenerator;
    private final BusinessRuleService businessRuleService;
    private final DistributedLockService distributedLockService;
    private final CouponMapper couponMapper;
    private final UserCouponMapper userCouponMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOrder(Long userId, CreateOrderDTO createOrderDTO) {
        // 1. 如果使用了优惠券，先检查优惠券状态
        Long couponId = createOrderDTO.getCouponId();
        UserCoupon userCoupon = null;
        String lockKey = null;
        String lockValue = null;
        
        if (couponId != null) {
            // 获取分布式锁
            lockKey = "order:coupon:" + userId + ":" + couponId;
            lockValue = distributedLockService.tryLock(lockKey, 30);
            if (!"OK".equals(lockValue)) {
                throw new BusinessException("系统繁忙，请稍后重试");
            }
            
            try {
                // 查询优惠券使用记录
                userCoupon = userCouponMapper.selectByCouponIdAndUserId(couponId, userId);
                if (userCoupon == null || userCoupon.getStatus() != 1) {
                    throw new BusinessException("优惠券不可用");
                }
                
                // 再次验证优惠券是否过期
                Coupon coupon = couponMapper.selectById(couponId);
                if (coupon == null || coupon.getStatus() != 1 || 
                    LocalDateTime.now().isAfter(coupon.getEndTime())) {
                    throw new BusinessException("优惠券已过期");
                }

                // 计算订单总金额
                BigDecimal totalAmount = createOrderDTO.getItems().stream()
                    .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                // 验证订单金额是否满足优惠券使用条件
                if (totalAmount.compareTo(coupon.getMinSpend()) < 0) {
                    throw new BusinessException("订单金额未满" + coupon.getMinSpend() + "元");
                }

                // 计算优惠金额
                BigDecimal discountAmount;
                if (coupon.getType() == 2) { // 折扣券
                    discountAmount = totalAmount
                        .multiply(BigDecimal.ONE.subtract(coupon.getAmount().divide(BigDecimal.TEN)))
                        .setScale(2, RoundingMode.HALF_UP);
                } else { // 满减券
                    discountAmount = coupon.getAmount();
                }

                // 更新优惠券状态为已使用
                userCoupon.setStatus(2); // 2-已使用
                userCoupon.setUseTime(LocalDateTime.now());
                // 使用乐观锁更新，确保version匹配
                int updateResult = userCouponMapper.updateStatusWithVersion(
                    userCoupon.getId(),
                    userCoupon.getStatus(),
                    userCoupon.getUseTime(),
                    userCoupon.getVersion()
                );
                if (updateResult == 0) {
                    throw new BusinessException("优惠券已被使用，请重新下单");
                }

                log.info("优惠券使用成功: userId={}, couponId={}, discountAmount={}", 
                        userId, couponId, discountAmount);

                // 创建订单主表记录
                Order order = new Order();
                order.setOrderNo(generateOrderNo());
                order.setUserId(userId);
                order.setAddressId(createOrderDTO.getAddressId());
                
                // 设置收货地址相关信息
                order.setReceiver(createOrderDTO.getReceiver());
                order.setPhone(createOrderDTO.getPhone());
                order.setProvince(createOrderDTO.getProvince());
                order.setCity(createOrderDTO.getCity());
                order.setDistrict(createOrderDTO.getDistrict());
                order.setDetailAddress(createOrderDTO.getDetailAddress());
                
                // 设置优惠券ID
                order.setCouponId(couponId);
                
                order.setRemark(createOrderDTO.getRemark());
                order.setOrderStatus(10); // 待付款
                order.setPaymentStatus(0); // 未支付
                order.setDeliveryStatus(0); // 未发货
                order.setCreatedAt(LocalDateTime.now());
                order.setUpdatedAt(order.getCreatedAt());

                // 设置优惠金额和实付金额
                if (createOrderDTO.getDiscountAmount() != null) {
                    order.setDiscountAmount(createOrderDTO.getDiscountAmount());
                }
                if (createOrderDTO.getActualAmount() != null) {
                    order.setActualAmount(createOrderDTO.getActualAmount());
                }
                if (createOrderDTO.getFreightAmount() != null) {
                    order.setFreightAmount(createOrderDTO.getFreightAmount());
                }

                // 计算订单金额
                calculateOrderAmount(order, createOrderDTO.getItems());

                // 保存订单
                if (orderMapper.insert(order) == 0) {
                    throw new BusinessException("创建订单失败");
                }

                // 更新优惠券状态
                userCoupon.setOrderId(order.getId());  // 设置订单ID
                
                // 使用乐观锁更新订单ID
                int updateOrderResult = userCouponMapper.updateOrderId(userCoupon.getId(), order.getId());
                if (updateOrderResult == 0) {
                    throw new BusinessException("优惠券已被使用，请重新下单");
                }

                // 保存订单商品
                List<OrderItem> orderItems = createOrderDTO.getItems().stream().map(item -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrderId(order.getId());
                    orderItem.setOrderNo(order.getOrderNo());
                    orderItem.setProductId(item.getProductId());
                    orderItem.setSkuId(item.getSkuId());
                    orderItem.setProductName(item.getProductName());
                    orderItem.setProductImage(item.getProductImage());
                    // 处理SKU规格数据，确保是有效的JSON格式
                    try {
                        if (item.getSkuSpec() != null && !item.getSkuSpec().trim().isEmpty()) {
                            String skuSpec = item.getSkuSpec();
                            
                            // 获取SKU的完整规格定义
                            ProductSku sku = productSkuMapper.selectById(item.getSkuId());
                            List<Map<String, String>> fullSpecs = null;
                            if (sku != null && sku.getSpecData() != null) {
                                try {
                                    // 解析规格数据，格式应该是：[{"key": "颜色", "values": ["原野绿"]}, {"key": "容量", "values": ["256GB"]}, {"key": "网络", "values": ["5G"]}]
                                    List<Map<String, Object>> specData = objectMapper.readValue(sku.getSpecData(), FULL_SPEC_TYPE);
                                    fullSpecs = specData.stream().map(spec -> {
                                        Map<String, String> map = new HashMap<>();
                                        map.put("name", (String) spec.get("key"));
                                        return map;
                                    }).collect(Collectors.toList());
                                } catch (Exception e) {
                                    log.error("解析SKU规格数据失败: {}", sku.getSpecData(), e);
                                }
                            }
                            
                            // 如果是逗号分隔的字符串，转换为JSON数组格式
                            if (skuSpec.contains("，") || skuSpec.contains(",")) {
                                String[] specs = skuSpec.split("[,，]");
                                List<Map<String, String>> specList = new ArrayList<>();
                                String[] defaultNames = {"颜色", "存储容量", "网络类型"};
                                for (int i = 0; i < specs.length; i++) {
                                    Map<String, String> specMap = new HashMap<>();
                                    String value = specs[i].trim();
                                    specMap.put("value", value);
                                    // 添加规格名称
                                    String name;
                                    if (fullSpecs != null && i < fullSpecs.size()) {
                                        name = fullSpecs.get(i).get("name");
                                    } else {
                                        name = i < defaultNames.length ? defaultNames[i] : "规格" + (i + 1);
                                    }
                                    specMap.put("name", name);
                                    specList.add(specMap);
                                }
                                orderItem.setSkuSpecData(objectMapper.writeValueAsString(specList));
                            } else if (skuSpec.startsWith("[")) {
                                // 已经是JSON数组格式
                                List<Map<String, String>> specList = objectMapper.readValue(skuSpec, SKU_SPEC_TYPE);
                                String[] defaultNames = {"颜色", "存储容量", "网络类型"};
                                for (int i = 0; i < specList.size(); i++) {
                                    Map<String, String> spec = specList.get(i);
                                    if (!spec.containsKey("name") || spec.get("name") == null) {
                                        String name;
                                        if (fullSpecs != null && i < fullSpecs.size()) {
                                            name = fullSpecs.get(i).get("name");
                                        } else {
                                            name = i < defaultNames.length ? defaultNames[i] : "规格" + (i + 1);
                                        }
                                        spec.put("name", name);
                                    }
                                }
                                orderItem.setSkuSpecData(objectMapper.writeValueAsString(specList));
                            } else {
                                // 单个值，转换为数组格式
                                List<Map<String, String>> specList = new ArrayList<>();
                                Map<String, String> specMap = new HashMap<>();
                                String value = skuSpec.trim();
                                specMap.put("value", value);
                                // 添加规格名称
                                String name;
                                if (fullSpecs != null && !fullSpecs.isEmpty()) {
                                    name = fullSpecs.get(0).get("name");
                                } else {
                                    name = "颜色";
                                }
                                specMap.put("name", name);
                                specList.add(specMap);
                                orderItem.setSkuSpecData(objectMapper.writeValueAsString(specList));
                            }
                        } else {
                            orderItem.setSkuSpecData("[]");
                        }
                    } catch (Exception e) {
                        log.error("解析SKU规格数据失败: {}", item.getSkuSpec(), e);
                        orderItem.setSkuSpecData("[]");
                    }
                    orderItem.setPrice(item.getPrice());
                    orderItem.setQuantity(item.getQuantity());
                    orderItem.setTotalAmount(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                    orderItem.setRefundStatus(0);
                    orderItem.setRefundedQuantity(0);
                    orderItem.setRefundAmount(BigDecimal.ZERO);
                    orderItem.setCreatedAt(order.getCreatedAt());
                    orderItem.setUpdatedAt(order.getCreatedAt());
                    return orderItem;
                }).collect(Collectors.toList());
                orderItemMapper.batchInsert(orderItems);

                // 7. 记录操作日志
                saveOrderLog(order.getId(), order.getOrderNo(), userId, 1, 1, "创建订单");

                return order.getId();
            } finally {
                // 释放分布式锁
                if (lockKey != null && lockValue != null) {
                    distributedLockService.releaseLock(lockKey, lockValue);
                }
            }
        }

        try {
        // 2. 创建订单主表记录
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setAddressId(createOrderDTO.getAddressId());
        
        // 设置收货地址相关信息
        order.setReceiver(createOrderDTO.getReceiver());
        order.setPhone(createOrderDTO.getPhone());
        order.setProvince(createOrderDTO.getProvince());
        order.setCity(createOrderDTO.getCity());
        order.setDistrict(createOrderDTO.getDistrict());
        order.setDetailAddress(createOrderDTO.getDetailAddress());
        
        // 设置优惠券ID
        order.setCouponId(couponId);
        
        order.setRemark(createOrderDTO.getRemark());
        order.setOrderStatus(10); // 待付款
        order.setPaymentStatus(0); // 未支付
        order.setDeliveryStatus(0); // 未发货
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(order.getCreatedAt());

        // 设置优惠金额和实付金额
        if (createOrderDTO.getDiscountAmount() != null) {
            order.setDiscountAmount(createOrderDTO.getDiscountAmount());
        }
        if (createOrderDTO.getActualAmount() != null) {
            order.setActualAmount(createOrderDTO.getActualAmount());
        }
        if (createOrderDTO.getFreightAmount() != null) {
            order.setFreightAmount(createOrderDTO.getFreightAmount());
        }

        // 3. 计算订单金额
        calculateOrderAmount(order, createOrderDTO.getItems());

        // 4. 保存订单
        if (orderMapper.insert(order) == 0) {
            throw new BusinessException("创建订单失败");
        }

        // 5. 更新优惠券状态
        if (userCoupon != null) {
            userCoupon.setStatus(2);  // 2-已使用
            userCoupon.setUseTime(LocalDateTime.now());
            userCoupon.setOrderId(order.getId());
            
            // 使用乐观锁更新订单ID
            int updateResult = userCouponMapper.updateOrderId(userCoupon.getId(), order.getId());
            if (updateResult == 0) {
                throw new BusinessException("优惠券已被使用，请重新下单");
            }
        }

        // 6. 保存订单商品
        List<OrderItem> orderItems = createOrderDTO.getItems().stream().map(item -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setOrderNo(order.getOrderNo());
            orderItem.setProductId(item.getProductId());
            orderItem.setSkuId(item.getSkuId());
            orderItem.setProductName(item.getProductName());
            orderItem.setProductImage(item.getProductImage());
            // 处理SKU规格数据，确保是有效的JSON格式
            try {
                if (item.getSkuSpec() != null && !item.getSkuSpec().trim().isEmpty()) {
                    String skuSpec = item.getSkuSpec();
                    
                    // 获取SKU的完整规格定义
                    ProductSku sku = productSkuMapper.selectById(item.getSkuId());
                    List<Map<String, String>> fullSpecs = null;
                    if (sku != null && sku.getSpecData() != null) {
                        try {
                            // 解析规格数据，格式应该是：[{"key": "颜色", "values": ["原野绿"]}, {"key": "容量", "values": ["256GB"]}, {"key": "网络", "values": ["5G"]}]
                                List<Map<String, Object>> specData = objectMapper.readValue(sku.getSpecData(), FULL_SPEC_TYPE);
                            fullSpecs = specData.stream().map(spec -> {
                                Map<String, String> map = new HashMap<>();
                                map.put("name", (String) spec.get("key"));
                                return map;
                            }).collect(Collectors.toList());
                        } catch (Exception e) {
                            log.error("解析SKU规格数据失败: {}", sku.getSpecData(), e);
                        }
                    }
                    
                    // 如果是逗号分隔的字符串，转换为JSON数组格式
                    if (skuSpec.contains("，") || skuSpec.contains(",")) {
                        String[] specs = skuSpec.split("[,，]");
                        List<Map<String, String>> specList = new ArrayList<>();
                        String[] defaultNames = {"颜色", "存储容量", "网络类型"};
                        for (int i = 0; i < specs.length; i++) {
                            Map<String, String> specMap = new HashMap<>();
                            String value = specs[i].trim();
                            specMap.put("value", value);
                            // 添加规格名称
                            String name;
                            if (fullSpecs != null && i < fullSpecs.size()) {
                                name = fullSpecs.get(i).get("name");
                            } else {
                                name = i < defaultNames.length ? defaultNames[i] : "规格" + (i + 1);
                            }
                            specMap.put("name", name);
                            specList.add(specMap);
                        }
                        orderItem.setSkuSpecData(objectMapper.writeValueAsString(specList));
                    } else if (skuSpec.startsWith("[")) {
                        // 已经是JSON数组格式
                            List<Map<String, String>> specList = objectMapper.readValue(skuSpec, SKU_SPEC_TYPE);
                        String[] defaultNames = {"颜色", "存储容量", "网络类型"};
                        for (int i = 0; i < specList.size(); i++) {
                            Map<String, String> spec = specList.get(i);
                            if (!spec.containsKey("name") || spec.get("name") == null) {
                                String name;
                                if (fullSpecs != null && i < fullSpecs.size()) {
                                    name = fullSpecs.get(i).get("name");
                                } else {
                                    name = i < defaultNames.length ? defaultNames[i] : "规格" + (i + 1);
                                }
                                spec.put("name", name);
                            }
                        }
                        orderItem.setSkuSpecData(objectMapper.writeValueAsString(specList));
                    } else {
                        // 单个值，转换为数组格式
                        List<Map<String, String>> specList = new ArrayList<>();
                        Map<String, String> specMap = new HashMap<>();
                        String value = skuSpec.trim();
                        specMap.put("value", value);
                        // 添加规格名称
                        String name;
                        if (fullSpecs != null && !fullSpecs.isEmpty()) {
                            name = fullSpecs.get(0).get("name");
                        } else {
                            name = "颜色";
                        }
                        specMap.put("name", name);
                        specList.add(specMap);
                        orderItem.setSkuSpecData(objectMapper.writeValueAsString(specList));
                    }
                } else {
                    orderItem.setSkuSpecData("[]");
                }
            } catch (Exception e) {
                log.error("解析SKU规格数据失败: {}", item.getSkuSpec(), e);
                orderItem.setSkuSpecData("[]");
            }
            orderItem.setPrice(item.getPrice());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setTotalAmount(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            orderItem.setRefundStatus(0);
            orderItem.setRefundedQuantity(0);
            orderItem.setRefundAmount(BigDecimal.ZERO);
            orderItem.setCreatedAt(order.getCreatedAt());
            orderItem.setUpdatedAt(order.getCreatedAt());
            return orderItem;
        }).collect(Collectors.toList());
        orderItemMapper.batchInsert(orderItems);

        // 7. 记录操作日志
        saveOrderLog(order.getId(), order.getOrderNo(), userId, 1, 1, "创建订单");

        return order.getId();
        } finally {
            // 释放分布式锁
            if (lockKey != null && lockValue != null) {
                distributedLockService.releaseLock(lockKey, lockValue);
            }
        }
    }

    @Override
    public OrderDetailDTO getOrderDetail(Long id) {
        // 1. 查询订单主表
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 2. 转换为DTO
        OrderDetailDTO detailDTO = new OrderDetailDTO();
        BeanUtils.copyProperties(order, detailDTO);

        // 3. 查询订单商品
        List<OrderItem> items = orderItemMapper.selectByOrderId(id);
        detailDTO.setItems(items.stream().map(item -> {
            OrderDetailDTO.OrderItemDTO itemDTO = new OrderDetailDTO.OrderItemDTO();
            BeanUtils.copyProperties(item, itemDTO);
            try {
                String skuSpecData = item.getSkuSpecData();
                if (skuSpecData != null && !skuSpecData.equals("[]")) {
                    try {
                        // 解析规格数据
                        List<Map<String, String>> specList = objectMapper.readValue(skuSpecData, SKU_SPEC_TYPE);
                        
                        // 获取SKU的完整规格定义
                        ProductSku sku = productSkuMapper.selectById(item.getSkuId());
                        if (sku != null && sku.getSpecData() != null) {
                            try {
                                // 解析完整规格定义
                                List<Map<String, Object>> fullSpecs = objectMapper.readValue(sku.getSpecData(), FULL_SPEC_TYPE);
                                String[] defaultNames = {"颜色", "存储容量", "网络类型"};
                                
                                // 为每个规格设置正确的名称
                                for (int i = 0; i < specList.size(); i++) {
                                    Map<String, String> spec = specList.get(i);
                                    String name;
                                    if (i < fullSpecs.size()) {
                                        name = (String) fullSpecs.get(i).get("key");
                                    } else {
                                        name = i < defaultNames.length ? defaultNames[i] : "规格" + (i + 1);
                                    }
                                    spec.put("name", name);
                                }
                            } catch (Exception e) {
                                log.error("解析SKU完整规格定义失败: {}", sku.getSpecData(), e);
                                // 使用默认名称
                                String[] defaultNames = {"颜色", "存储容量", "网络类型"};
                                for (int i = 0; i < specList.size(); i++) {
                                    Map<String, String> spec = specList.get(i);
                                    spec.put("name", i < defaultNames.length ? defaultNames[i] : "规格" + (i + 1));
                                }
                            }
                        } else {
                            // 使用默认名称
                            String[] defaultNames = {"颜色", "存储容量", "网络类型"};
                            for (int i = 0; i < specList.size(); i++) {
                                Map<String, String> spec = specList.get(i);
                                spec.put("name", i < defaultNames.length ? defaultNames[i] : "规格" + (i + 1));
                            }
                        }
                        itemDTO.setSkuSpecData(specList);
                    } catch (JsonProcessingException e) {
                        log.error("解析SKU规格数据失败: {}", skuSpecData, e);
                        itemDTO.setSkuSpecData(Collections.emptyList());
                    }
                } else {
                    itemDTO.setSkuSpecData(Collections.emptyList());
                }
            } catch (Exception e) {
                log.error("处理SKU规格数据失败", e);
                itemDTO.setSkuSpecData(Collections.emptyList());
            }
            return itemDTO;
        }).collect(Collectors.toList()));

        // 4. 查询物流信息
        OrderDelivery delivery = orderDeliveryMapper.selectByOrderId(id);
        if (delivery != null) {
            OrderDetailDTO.OrderDeliveryDTO deliveryDTO = new OrderDetailDTO.OrderDeliveryDTO();
            BeanUtils.copyProperties(delivery, deliveryDTO);
            try {
                String trackingData = delivery.getTrackingData();
                if (trackingData != null) {
                    deliveryDTO.setTrackingData(objectMapper.readValue(trackingData, Object.class));
                }
            } catch (JsonProcessingException e) {
                log.error("解析物流跟踪数据失败", e);
            }
            detailDTO.setDelivery(deliveryDTO);
        }

        return detailDTO;
    }

    @Override
    public PageResult<OrderDetailDTO> getOrderList(OrderQueryDTO queryDTO) {
        // 1. 计算分页参数
        int offset = (queryDTO.getPageNum() - 1) * queryDTO.getPageSize();

        // 2. 查询总数
        long total = orderMapper.selectCount(
                queryDTO.getUserId(),
                queryDTO.getOrderNo(),
                queryDTO.getOrderStatus(),
                queryDTO.getPaymentStatus(),
                queryDTO.getDeliveryStatus(),
                queryDTO.getReceiver(),
                queryDTO.getPhone(),
                queryDTO.getStartTime(),
                queryDTO.getEndTime()
        );

        if (total == 0) {
            return PageResult.empty(queryDTO.getPageNum(), queryDTO.getPageSize());
        }

        // 3. 查询数据
        List<Order> orders = orderMapper.selectList(
                queryDTO.getUserId(),
                queryDTO.getOrderNo(),
                queryDTO.getOrderStatus(),
                queryDTO.getPaymentStatus(),
                queryDTO.getDeliveryStatus(),
                queryDTO.getReceiver(),
                queryDTO.getPhone(),
                queryDTO.getStartTime(),
                queryDTO.getEndTime(),
                offset,
                queryDTO.getPageSize()
        );

        // 4. 转换为DTO
        List<OrderDetailDTO> dtoList = orders.stream().map(order -> {
            OrderDetailDTO dto = new OrderDetailDTO();
            BeanUtils.copyProperties(order, dto);
            
            // 查询订单商品
            List<OrderItem> items = orderItemMapper.selectByOrderId(order.getId());
            dto.setItems(items.stream().map(item -> {
                OrderDetailDTO.OrderItemDTO itemDTO = new OrderDetailDTO.OrderItemDTO();
                BeanUtils.copyProperties(item, itemDTO);
                try {
                    String skuSpecData = item.getSkuSpecData();
                    if (skuSpecData != null && !skuSpecData.equals("[]")) {
                        try {
                            // 解析规格数据
                            List<Map<String, String>> specList = objectMapper.readValue(skuSpecData, SKU_SPEC_TYPE);
                            
                            // 获取SKU的完整规格定义
                            ProductSku sku = productSkuMapper.selectById(item.getSkuId());
                            if (sku != null && sku.getSpecData() != null) {
                                try {
                                    // 解析完整规格定义
                                    List<Map<String, Object>> fullSpecs = objectMapper.readValue(sku.getSpecData(), FULL_SPEC_TYPE);
                                    String[] defaultNames = {"颜色", "存储容量", "网络类型"};
                                    
                                    // 为每个规格设置正确的名称
                                    for (int i = 0; i < specList.size(); i++) {
                                        Map<String, String> spec = specList.get(i);
                                        String name;
                                        if (i < fullSpecs.size()) {
                                            name = (String) fullSpecs.get(i).get("key");
                                        } else {
                                            name = i < defaultNames.length ? defaultNames[i] : "规格" + (i + 1);
                                        }
                                        spec.put("name", name);
                                    }
                                } catch (Exception e) {
                                    log.error("解析SKU完整规格定义失败: {}", sku.getSpecData(), e);
                                    // 使用默认名称
                                    String[] defaultNames = {"颜色", "存储容量", "网络类型"};
                                    for (int i = 0; i < specList.size(); i++) {
                                        Map<String, String> spec = specList.get(i);
                                        spec.put("name", i < defaultNames.length ? defaultNames[i] : "规格" + (i + 1));
                                    }
                                }
                            } else {
                                // 使用默认名称
                                String[] defaultNames = {"颜色", "存储容量", "网络类型"};
                                for (int i = 0; i < specList.size(); i++) {
                                    Map<String, String> spec = specList.get(i);
                                    spec.put("name", i < defaultNames.length ? defaultNames[i] : "规格" + (i + 1));
                                }
                            }
                            itemDTO.setSkuSpecData(specList);
                        } catch (JsonProcessingException e) {
                            log.error("解析SKU规格数据失败: {}", skuSpecData, e);
                            itemDTO.setSkuSpecData(Collections.emptyList());
                        }
                    } else {
                        itemDTO.setSkuSpecData(Collections.emptyList());
                    }
                } catch (Exception e) {
                    log.error("处理SKU规格数据失败", e);
                    itemDTO.setSkuSpecData(Collections.emptyList());
                }
                return itemDTO;
            }).collect(Collectors.toList()));

            // 查询物流信息
            OrderDelivery delivery = orderDeliveryMapper.selectByOrderId(order.getId());
            if (delivery != null) {
                OrderDetailDTO.OrderDeliveryDTO deliveryDTO = new OrderDetailDTO.OrderDeliveryDTO();
                BeanUtils.copyProperties(delivery, deliveryDTO);
                try {
                    String trackingData = delivery.getTrackingData();
                    if (trackingData != null) {
                        deliveryDTO.setTrackingData(objectMapper.readValue(trackingData, Object.class));
                    }
                } catch (JsonProcessingException e) {
                    log.error("解析物流跟踪数据失败", e);
                }
                dto.setDelivery(deliveryDTO);
            }

            return dto;
        }).collect(Collectors.toList());

        return PageResult.of(queryDTO.getPageNum(), queryDTO.getPageSize(), total, dtoList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long userId, Long orderId, String reason) {
        // 1. 查询订单
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 2. 校验订单所属用户
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }

        // 3. 校验订单状态
        if (order.getOrderStatus() != 10) {
            throw new BusinessException("只能取消待付款订单");
        }

        // 4. 更新订单状态
        LocalDateTime now = LocalDateTime.now();
        order.setOrderStatus(50); // 已取消
        order.setCancelTime(now);
        order.setCancelReason(reason);
        order.setUpdatedAt(now);
        orderMapper.updateCancelInfo(orderId, now, reason);

        // 5. 如果使用了优惠券，需要退回优惠券
        if (order.getCouponId() != null) {
            String lockKey = "order:coupon:" + userId + ":" + order.getCouponId();
            String lockValue = distributedLockService.tryLock(lockKey, 30);
            if (!"OK".equals(lockValue)) {
                log.error("获取优惠券分布式锁失败，订单号：{}，优惠券ID：{}", order.getOrderNo(), order.getCouponId());
                // 这里不抛出异常，因为退款流程不应该因为优惠券处理失败而回滚
            } else {
                try {
                    // 查询优惠券使用记录
                    UserCoupon userCoupon = userCouponMapper.selectByCouponIdAndUserId(order.getCouponId(), userId);
                    if (userCoupon != null && userCoupon.getStatus() == 2 && order.getId().equals(userCoupon.getOrderId())) {
                        // 更新优惠券状态为可用
                        userCoupon.setStatus(1);
                        userCoupon.setUseTime(null);
                        userCoupon.setOrderId(null);
                        
                        // 使用新的更新方法
                        int cancelResult = userCouponMapper.updateStatusForCancel(userCoupon.getId(), userCoupon.getStatus());
                        if (cancelResult == 0) {
                            log.error("退回优惠券失败，订单号：{}，优惠券ID：{}", order.getOrderNo(), userCoupon.getId());
                        } else {
                            // 发送优惠券退回通知
                            Coupon coupon = couponMapper.selectById(order.getCouponId());
                            if (coupon != null) {
                                messageService.sendCouponReturnNotice(order.getUserId(), coupon.getName(), order.getOrderNo());
                            }
                        }
                    }
                } finally {
                    distributedLockService.releaseLock(lockKey, lockValue);
                }
            }
        }

        // 6. 记录操作日志
        saveOrderLog(orderId, order.getOrderNo(), userId, 1, 5, "取消订单，原因：" + reason);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object payOrder(Long userId, Long orderId, Integer paymentMethod) {
        // 1. 查询订单
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 2. 校验订单状态
        if (order.getOrderStatus() != 10) {
            throw new BusinessException("订单状态不正确");
        }

        if (order.getPaymentStatus() != 0) {
            throw new BusinessException("订单已支付");
        }

        // 3. 生成支付记录
        String paymentNo = generatePaymentNo();
        LocalDateTime now = LocalDateTime.now();
        PaymentRecord paymentRecord = new PaymentRecord();
        paymentRecord.setPaymentNo(paymentNo);
        paymentRecord.setOrderId(orderId);
        paymentRecord.setOrderNo(order.getOrderNo());
        paymentRecord.setUserId(userId);
        paymentRecord.setPaymentMethod(paymentMethod);
        paymentRecord.setPaymentAmount(order.getActualAmount());
        paymentRecord.setPaymentStatus(0);
        paymentRecord.setCreatedAt(now);
        paymentRecord.setUpdatedAt(now);
        paymentRecordMapper.insert(paymentRecord);

        // 4. 更新订单状态
        order.setOrderStatus(20); // 待发货
        order.setPaymentStatus(1); // 已支付
        order.setPaymentTime(now);
        order.setUpdatedAt(now);
        
        orderMapper.updateOrderStatus(orderId, 20);
        orderMapper.updatePaymentStatus(orderId, 1, now);

        // 5. 更新商品库存和销量
        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(orderId);
        for (OrderItem item : orderItems) {
            // 更新SKU库存和销量
            productSkuMapper.confirmLockedStock(item.getSkuId(), item.getQuantity());
            // 更新商品总销量和库存
            productMapper.incrementSales(item.getProductId(), item.getQuantity());
        }

        // 6. 记录操作日志
        saveOrderLog(orderId, order.getOrderNo(), userId, 1, 2, "支付成功");

        // 7. 返回支付参数
        return simulatePayment(paymentRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmReceive(Long userId, Long orderId) {
        // 1. 查询订单
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 2. 校验订单所属用户
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }

        // 3. 校验订单状态
        if (order.getOrderStatus() != 30) {
            throw new BusinessException("订单状态不正确");
        }

        if (order.getDeliveryStatus() != 1) {
            throw new BusinessException("订单未发货");
        }

        // 4. 更新订单状态
        LocalDateTime now = LocalDateTime.now();
        order.setOrderStatus(40); // 已完成
        order.setDeliveryStatus(2); // 已收货
        order.setReceiveTime(now);
        order.setFinishTime(now);
        order.setUpdatedAt(now);
        
        orderMapper.updateReceiveTime(orderId, now);
        orderMapper.updateFinishTime(orderId, now);
        orderMapper.updateOrderStatus(orderId, 40);
        orderMapper.updateDeliveryStatus(orderId, 2, null);

        // 5. 更新物流状态
        OrderDelivery delivery = orderDeliveryMapper.selectByOrderId(orderId);
        if (delivery != null) {
            orderDeliveryMapper.updateReceiveInfo(delivery.getId(), now, 2);
        }

        // 6. 记录操作日志
        saveOrderLog(orderId, order.getOrderNo(), userId, 1, 4, "确认收货");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applyRefund(Long userId, RefundApplyDTO refundApplyDTO) {
        // 1. 查询订单
        Order order = orderMapper.selectById(refundApplyDTO.getOrderId());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 2. 校验订单所属用户
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }

        // 3. 校验订单状态
        if (order.getOrderStatus() == 50 || order.getOrderStatus() == 60) {
            throw new BusinessException("订单已取消或已退款");
        }

        if (order.getPaymentStatus() != 1) {
            throw new BusinessException("订单未支付");
        }

        // 4. 校验订单商品
        OrderItem orderItem = orderItemMapper.selectById(refundApplyDTO.getOrderItemId());
        if (orderItem == null) {
            throw new BusinessException("订单商品不存在");
        }

        if (orderItem.getRefundStatus() != 0) {
            throw new BusinessException("该商品已申请退款");
        }

        // 5. 校验退款规则
        BusinessRule maxTimesRule = businessRuleService.getRule("REFUND", "MAX_TIMES");
        BusinessRule timeLimitRule = businessRuleService.getRule("REFUND", "TIME_LIMIT");
        BusinessRule amountLimitRule = businessRuleService.getRule("REFUND", "AMOUNT_LIMIT");
        BusinessRule autoApproveRule = businessRuleService.getRule("REFUND", "AUTO_APPROVE");

        // 检查退款次数
        int refundTimes = refundRecordMapper.countByOrderId(order.getId());
        if (refundTimes >= Integer.parseInt(maxTimesRule.getRuleValue())) {
            throw new BusinessException("超过最大退款次数限制");
        }

        // 检查退款时限
        if (order.getFinishTime() != null) {
            long days = java.time.Duration.between(order.getFinishTime(), LocalDateTime.now()).toDays();
            if (days > Integer.parseInt(timeLimitRule.getRuleValue())) {
                throw new BusinessException("超过退款时限");
            }
        }

        // 6. 创建退款记录
        RefundRecord refundRecord = new RefundRecord();
        refundRecord.setRefundNo(generateRefundNo());
        refundRecord.setOrderId(order.getId());
        refundRecord.setOrderNo(order.getOrderNo());
        refundRecord.setOrderItemId(orderItem.getId());
        refundRecord.setUserId(userId);
        refundRecord.setRefundType(refundApplyDTO.getRefundType());
        refundRecord.setRefundReasonType(refundApplyDTO.getRefundReasonType());
        refundRecord.setRefundReason(refundApplyDTO.getRefundReason());
        refundRecord.setRefundAmount(refundApplyDTO.getRefundAmount());
        refundRecord.setRefundQuantity(refundApplyDTO.getQuantity());
        refundRecord.setIsPartial(refundApplyDTO.getQuantity() < orderItem.getQuantity() ? 1 : 0);
        
        try {
            refundRecord.setEvidenceImages(objectMapper.writeValueAsString(refundApplyDTO.getEvidenceImages()));
        } catch (JsonProcessingException e) {
            log.error("序列化凭证图片失败", e);
            throw new BusinessException("凭证图片格式错误");
        }
        
        // 判断是否需要人工审核
        boolean needManualReview = true;
        if (Integer.parseInt(autoApproveRule.getRuleValue()) == 1) {
            // 小额退款自动审核
            if (refundApplyDTO.getRefundAmount().compareTo(new BigDecimal(amountLimitRule.getRuleValue())) <= 0) {
                needManualReview = false;
                refundRecord.setRefundStatus(1); // 自动同意
            }
        }
        
        if (needManualReview) {
            refundRecord.setRefundStatus(0); // 待处理
        }
        
        refundRecord.setCreatedAt(LocalDateTime.now());
        refundRecord.setUpdatedAt(refundRecord.getCreatedAt());
        refundRecordMapper.insert(refundRecord);

        // 7. 更新订单商品退款状态
        orderItem.setRefundStatus(1);
        orderItem.setRefundId(refundRecord.getId());
        orderItemMapper.updateRefundStatus(orderItem.getId(), 1, refundRecord.getId());

        // 8. 记录操作日志
        saveOrderLog(order.getId(), order.getOrderNo(), userId, 1, 6, "申请退款");

        // 9. 如果是自动审核通过，直接处理退款
        if (!needManualReview) {
            agreeRefund(refundRecord.getId());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void agreeRefund(Long refundId) {
        // 1. 查询退款记录
        RefundRecord refundRecord = refundRecordMapper.selectById(refundId);
        if (refundRecord == null) {
            throw new BusinessException("退款记录不存在");
        }

        // 2. 校验退款状态
        if (refundRecord.getRefundStatus() != 0 && refundRecord.getRefundStatus() != 1) {
            throw new BusinessException("退款状态不正确");
        }

        // 3. 如果是退货退款，需要等待买家退货
        if (refundRecord.getRefundType() == 2) {
            refundRecord.setRefundStatus(4); // 待退货
            refundRecordMapper.updateRefundStatus(refundId, 4, null);
            
            // 发送退货提醒
            messageService.sendReturnGoodsReminder(refundId);
            return;
        }

        // 4. 更新退款状态
        LocalDateTime now = LocalDateTime.now();
        refundRecord.setRefundStatus(3); // 已完成
        refundRecord.setRefundTime(now);
        refundRecord.setUpdatedAt(now);
        refundRecordMapper.updateRefundStatus(refundId, 3, now);

        // 5. 更新订单商品退款状态
        orderItemMapper.updateRefundStatus(refundRecord.getOrderItemId(), 2, refundId);

        // 6. 检查是否所有商品都已退款
        Order order = orderMapper.selectById(refundRecord.getOrderId());
        List<OrderItem> orderItems = orderItemMapper.selectByOrderId(order.getId());
        boolean allRefunded = orderItems.stream().allMatch(item -> 
            item.getRefundStatus() == 2 || 
            (item.getRefundStatus() == 1 && item.getRefundedQuantity().equals(item.getQuantity()))
        );
        
        if (allRefunded) {
            // 7. 更新订单状态为已退款
            order.setOrderStatus(60);
            order.setPaymentStatus(2);
            order.setUpdatedAt(now);
            orderMapper.updateOrderStatus(order.getId(), 60);
            orderMapper.updatePaymentStatus(order.getId(), 2, null);

            // 8. 如果订单使用了优惠券，需要退回优惠券
            if (order.getCouponId() != null) {
                String lockKey = "order:coupon:" + order.getUserId() + ":" + order.getCouponId();
                String lockValue = distributedLockService.tryLock(lockKey, 30);
                if (!"OK".equals(lockValue)) {
                    log.error("获取优惠券分布式锁失败，订单号：{}，优惠券ID：{}", order.getOrderNo(), order.getCouponId());
                    // 这里不抛出异常，因为退款流程不应该因为优惠券处理失败而回滚
                } else {
                    try {
                        // 查询优惠券使用记录
                        UserCoupon userCoupon = userCouponMapper.selectByCouponIdAndUserId(order.getCouponId(), order.getUserId());
                        if (userCoupon != null && userCoupon.getStatus() == 2 && order.getId().equals(userCoupon.getOrderId())) {
                            // 更新优惠券状态为可用
                            userCoupon.setStatus(1);
                            userCoupon.setUseTime(null);
                            userCoupon.setOrderId(null);
                            
                            // 使用新的更新方法
                            int cancelResult = userCouponMapper.updateStatusForCancel(userCoupon.getId(), userCoupon.getStatus());
                            if (cancelResult == 0) {
                                log.error("退回优惠券失败，订单号：{}，优惠券ID：{}", order.getOrderNo(), userCoupon.getId());
                            } else {
                                // 发送优惠券退回通知
                                Coupon coupon = couponMapper.selectById(order.getCouponId());
                                if (coupon != null) {
                                    messageService.sendCouponReturnNotice(order.getUserId(), coupon.getName(), order.getOrderNo());
                                }
                            }
                        }
                    } finally {
                        distributedLockService.releaseLock(lockKey, lockValue);
                    }
                }
            }
        }

        // 9. 记录操作日志
        saveOrderLog(order.getId(), order.getOrderNo(), 0L, 2, 7, "同意退款");

        // 10. 发送退款成功通知
        messageService.sendRefundSuccessNotice(refundId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deliver(Long orderId, String deliveryCompany, String deliveryNo) {
        // 1. 查询订单
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 2. 校验订单状态
        if (order.getOrderStatus() != 20) {
            throw new BusinessException("订单状态不正确");
        }

        if (order.getDeliveryStatus() != 0) {
            throw new BusinessException("订单已发货");
        }

        // 3. 创建物流记录
        LocalDateTime now = LocalDateTime.now();
        OrderDelivery delivery = new OrderDelivery();
        delivery.setOrderId(orderId);
        delivery.setOrderNo(order.getOrderNo());
        delivery.setDeliveryCompany(deliveryCompany);
        delivery.setDeliveryNo(deliveryNo);
        delivery.setDeliveryStatus(1);
        delivery.setDeliveryTime(now);
        delivery.setCreatedAt(now);
        delivery.setUpdatedAt(now);
        orderDeliveryMapper.insert(delivery);

        // 4. 更新订单状态
        order.setOrderStatus(30); // 待收货
        order.setDeliveryStatus(1); // 已发货
        order.setDeliveryTime(now);
        order.setUpdatedAt(now);
        
        orderMapper.updateOrderStatus(orderId, 30);
        orderMapper.updateDeliveryStatus(orderId, 1, now);

        // 5. 记录操作日志
        saveOrderLog(orderId, order.getOrderNo(), 0L, 2, 3, 
                String.format("发货成功，物流公司：%s，物流单号：%s", deliveryCompany, deliveryNo));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectRefund(Long refundId, String reason) {
        // 1. 查询退款记录
        RefundRecord refundRecord = refundRecordMapper.selectById(refundId);
        if (refundRecord == null) {
            throw new BusinessException("退款记录不存在");
        }

        // 2. 校验退款状态
        if (refundRecord.getRefundStatus() != 0) {
            throw new BusinessException("退款状态不正确");
        }

        // 3. 更新退款状态
        LocalDateTime now = LocalDateTime.now();
        refundRecord.setRefundStatus(2); // 已拒绝
        refundRecord.setRejectReason(reason);
        refundRecord.setRejectTime(now);
        refundRecord.setUpdatedAt(now);
        refundRecordMapper.updateRejectInfo(refundId, 2, reason, now);

        // 4. 更新订单商品退款状态
        orderItemMapper.updateRefundStatus(refundRecord.getOrderItemId(), 3, refundId);

        // 5. 记录操作日志
        Order order = orderMapper.selectById(refundRecord.getOrderId());
        saveOrderLog(order.getId(), order.getOrderNo(), 0L, 2, 8, 
                String.format("拒绝退款，原因：%s", reason));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOrder(Long userId, Long orderId) {
        // 1. 查询订单
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 2. 校验订单所属用户
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }

        // 3. 校验订单状态
        if (order.getOrderStatus() != 40 && order.getOrderStatus() != 50) {
            throw new BusinessException("只能删除已完成或已取消的订单");
        }

        // 4. 记录操作日志
        saveOrderLog(orderId, order.getOrderNo(), userId, 1, 9, "删除订单");

        // 5. 删除订单相关数据（实际业务中可能只是标记删除）
        // TODO: 根据实际需求实现删除逻辑
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applyExchange(Long userId, ExchangeApplyDTO exchangeApplyDTO) {
        // 1. 查询订单
        Order order = orderMapper.selectById(exchangeApplyDTO.getOrderId());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 2. 校验订单所属用户
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }

        // 3. 校验订单状态
        if (order.getOrderStatus() == 50 || order.getOrderStatus() == 60) {
            throw new BusinessException("订单已取消或已退款");
        }

        if (order.getPaymentStatus() != 1) {
            throw new BusinessException("订单未支付");
        }

        // 4. 校验订单商品
        OrderItem orderItem = orderItemMapper.selectById(exchangeApplyDTO.getOrderItemId());
        if (orderItem == null) {
            throw new BusinessException("订单商品不存在");
        }

        if (orderItem.getRefundStatus() != 0) {
            throw new BusinessException("该商品已申请退款或换货");
        }

        // 5. 创建换货记录
        ExchangeRecord exchangeRecord = new ExchangeRecord();
        exchangeRecord.setExchangeNo(generateExchangeNo());
        exchangeRecord.setOrderId(order.getId());
        exchangeRecord.setOrderNo(order.getOrderNo());
        exchangeRecord.setOrderItemId(orderItem.getId());
        exchangeRecord.setUserId(userId);
        exchangeRecord.setOldSkuId(orderItem.getSkuId());
        exchangeRecord.setNewSkuId(exchangeApplyDTO.getNewSkuId());
        exchangeRecord.setOldSkuSpecData(orderItem.getSkuSpecData());
        // TODO: 需要查询新SKU的规格数据
        exchangeRecord.setExchangeReasonType(exchangeApplyDTO.getExchangeReasonType());
        exchangeRecord.setExchangeReason(exchangeApplyDTO.getExchangeReason());
        exchangeRecord.setExchangeStatus(0);
        exchangeRecord.setRemark(exchangeApplyDTO.getRemark());
        
        try {
            exchangeRecord.setEvidenceImages(objectMapper.writeValueAsString(exchangeApplyDTO.getEvidenceImages()));
        } catch (JsonProcessingException e) {
            log.error("序列化凭证图片失败", e);
            throw new BusinessException("凭证图片格式错误");
        }
        
        exchangeRecord.setCreatedAt(LocalDateTime.now());
        exchangeRecord.setUpdatedAt(exchangeRecord.getCreatedAt());
        exchangeRecordMapper.insert(exchangeRecord);

        // 6. 更新订单商品状态
        orderItem.setRefundStatus(1);
        orderItemMapper.updateRefundStatus(orderItem.getId(), 1, null);

        // 7. 记录操作日志
        saveOrderLog(order.getId(), order.getOrderNo(), userId, 1, 10, "申请换货");

        // 8. 发送换货申请通知给商家
        messageService.sendExchangeApplyNotice(exchangeRecord.getId());

        // 锁定新SKU库存
        lockNewSkuStock(exchangeApplyDTO.getNewSkuId(), 1);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void agreeExchange(Long exchangeId) {
        // 1. 查询换货记录
        ExchangeRecord exchangeRecord = exchangeRecordMapper.selectById(exchangeId);
        if (exchangeRecord == null) {
            throw new BusinessException("换货记录不存在");
        }

        // 2. 校验换货状态
        if (exchangeRecord.getExchangeStatus() != 0) {
            throw new BusinessException("换货状态不正确");
        }

        // 3. 更新换货状态为待买家退货
        exchangeRecord.setExchangeStatus(3);
        exchangeRecord.setUpdatedAt(LocalDateTime.now());
        exchangeRecordMapper.updateExchangeStatus(exchangeId, 3);

        // 4. 记录操作日志
        saveOrderLog(exchangeRecord.getOrderId(), exchangeRecord.getOrderNo(), 0L, 2, 11, "同意换货申请");

        // 5. 发送换货申请结果通知给用户
        messageService.sendExchangeResultNotice(exchangeId, true, null);
        messageService.sendReturnGoodsReminder(exchangeId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectExchange(Long exchangeId, String reason) {
        // 1. 查询换货记录
        ExchangeRecord exchangeRecord = exchangeRecordMapper.selectById(exchangeId);
        if (exchangeRecord == null) {
            throw new BusinessException("换货记录不存在");
        }

        // 2. 校验换货状态
        if (exchangeRecord.getExchangeStatus() != 0) {
            throw new BusinessException("换货状态不正确");
        }

        // 3. 更新换货状态为已拒绝
        LocalDateTime now = LocalDateTime.now();
        exchangeRecord.setExchangeStatus(2);
        exchangeRecord.setRejectReason(reason);
        exchangeRecord.setRejectTime(now);
        exchangeRecord.setUpdatedAt(now);
        exchangeRecordMapper.updateRejectInfo(exchangeId, 2, reason, now);

        // 4. 更新订单商品状态
        orderItemMapper.updateRefundStatus(exchangeRecord.getOrderItemId(), 0, null);

        // 5. 记录操作日志
        saveOrderLog(exchangeRecord.getOrderId(), exchangeRecord.getOrderNo(), 0L, 2, 12, 
                String.format("拒绝换货申请，原因：%s", reason));

        // 6. 发送换货申请结果通知给用户
        messageService.sendExchangeResultNotice(exchangeId, false, reason);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void returnGoods(Long userId, Long exchangeId, ExchangeDeliveryDTO deliveryDTO) {
        // 1. 查询换货记录
        ExchangeRecord exchangeRecord = exchangeRecordMapper.selectById(exchangeId);
        if (exchangeRecord == null) {
            throw new BusinessException("换货记录不存在");
        }

        // 2. 校验换货记录所属用户
        if (!exchangeRecord.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此换货记录");
        }

        // 3. 校验换货状态
        if (exchangeRecord.getExchangeStatus() != 3) {
            throw new BusinessException("换货状态不正确");
        }

        // 4. 创建退货物流记录
        ExchangeDelivery delivery = new ExchangeDelivery();
        delivery.setExchangeId(exchangeId);
        delivery.setExchangeNo(exchangeRecord.getExchangeNo());
        delivery.setDeliveryType(1);
        delivery.setDeliveryCompany(deliveryDTO.getDeliveryCompany());
        delivery.setDeliveryNo(deliveryDTO.getDeliveryNo());
        delivery.setSenderName(deliveryDTO.getSenderName());
        delivery.setSenderPhone(deliveryDTO.getSenderPhone());
        delivery.setSenderAddress(deliveryDTO.getSenderAddress());
        // TODO: 需要设置收件人信息（从系统配置或商家信息中获取）
        delivery.setDeliveryStatus(1);
        delivery.setDeliveryTime(LocalDateTime.now());
        delivery.setCreatedAt(delivery.getDeliveryTime());
        delivery.setUpdatedAt(delivery.getDeliveryTime());
        exchangeDeliveryMapper.insert(delivery);

        // 5. 更新换货状态为已退货待确认
        exchangeRecord.setExchangeStatus(4);
        exchangeRecord.setUpdatedAt(delivery.getDeliveryTime());
        exchangeRecordMapper.updateExchangeStatus(exchangeId, 4);

        // 6. 记录操作日志
        saveOrderLog(exchangeRecord.getOrderId(), exchangeRecord.getOrderNo(), userId, 1, 13,
                String.format("买家已退回商品，物流公司：%s，物流单号：%s", 
                        deliveryDTO.getDeliveryCompany(), deliveryDTO.getDeliveryNo()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmReturn(Long exchangeId) {
        // 1. 查询换货记录
        ExchangeRecord exchangeRecord = exchangeRecordMapper.selectById(exchangeId);
        if (exchangeRecord == null) {
            throw new BusinessException("换货记录不存在");
        }

        // 2. 校验换货状态
        if (exchangeRecord.getExchangeStatus() != 4) {
            throw new BusinessException("换货状态不正确");
        }

        // 3. 查询退货物流记录
        ExchangeDelivery returnDelivery = exchangeDeliveryMapper.selectByExchangeIdAndType(exchangeId, 1);
        if (returnDelivery == null) {
            throw new BusinessException("退货物流记录不存在");
        }

        // 4. 更新退货物流状态
        LocalDateTime now = LocalDateTime.now();
        returnDelivery.setDeliveryStatus(2);
        returnDelivery.setReceiveTime(now);
        returnDelivery.setUpdatedAt(now);
        exchangeDeliveryMapper.updateReceiveInfo(returnDelivery.getId(), now, 2);

        // 5. 更新换货状态为待发货
        exchangeRecord.setExchangeStatus(5);
        exchangeRecord.setUpdatedAt(now);
        exchangeRecordMapper.updateExchangeStatus(exchangeId, 5);

        // 6. 记录操作日志
        saveOrderLog(exchangeRecord.getOrderId(), exchangeRecord.getOrderNo(), 0L, 2, 14, "确认收到退回商品");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendExchangeGoods(Long exchangeId, String deliveryCompany, String deliveryNo) {
        // 1. 查询换货记录
        ExchangeRecord exchangeRecord = exchangeRecordMapper.selectById(exchangeId);
        if (exchangeRecord == null) {
            throw new BusinessException("换货记录不存在");
        }

        // 2. 校验换货状态
        if (exchangeRecord.getExchangeStatus() != 5) {
            throw new BusinessException("换货状态不正确");
        }

        // 3. 创建换货物流记录
        ExchangeDelivery delivery = new ExchangeDelivery();
        delivery.setExchangeId(exchangeId);
        delivery.setExchangeNo(exchangeRecord.getExchangeNo());
        delivery.setDeliveryType(2);
        delivery.setDeliveryCompany(deliveryCompany);
        delivery.setDeliveryNo(deliveryNo);
        // TODO: 需要设置寄件人信息（从系统配置或商家信息中获取）
        // TODO: 需要设置收件人信息（从订单中获取）
        delivery.setDeliveryStatus(1);
        delivery.setDeliveryTime(LocalDateTime.now());
        delivery.setCreatedAt(delivery.getDeliveryTime());
        delivery.setUpdatedAt(delivery.getDeliveryTime());
        exchangeDeliveryMapper.insert(delivery);

        // 4. 更新换货状态为已发货
        exchangeRecord.setExchangeStatus(6);
        exchangeRecord.setUpdatedAt(delivery.getDeliveryTime());
        exchangeRecordMapper.updateExchangeStatus(exchangeId, 6);

        // 5. 记录操作日志
        saveOrderLog(exchangeRecord.getOrderId(), exchangeRecord.getOrderNo(), 0L, 2, 15,
                String.format("换货商品已发出，物流公司：%s，物流单号：%s", deliveryCompany, deliveryNo));

        // 6. 发送换货商品发货通知给用户
        messageService.sendExchangeDeliveryNotice(exchangeId, deliveryCompany, deliveryNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmExchangeReceive(Long userId, Long exchangeId) {
        // 1. 查询换货记录
        ExchangeRecord exchangeRecord = exchangeRecordMapper.selectById(exchangeId);
        if (exchangeRecord == null) {
            throw new BusinessException("换货记录不存在");
        }

        // 2. 校验换货记录所属用户
        if (!exchangeRecord.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此换货记录");
        }

        // 3. 校验换货状态
        if (exchangeRecord.getExchangeStatus() != 6) {
            throw new BusinessException("换货状态不正确");
        }

        // 4. 查询换货物流记录
        ExchangeDelivery exchangeDelivery = exchangeDeliveryMapper.selectByExchangeIdAndType(exchangeId, 2);
        if (exchangeDelivery == null) {
            throw new BusinessException("换货物流记录不存在");
        }

        // 5. 更新换货物流状态
        LocalDateTime now = LocalDateTime.now();
        exchangeDelivery.setDeliveryStatus(2);
        exchangeDelivery.setReceiveTime(now);
        exchangeDelivery.setUpdatedAt(now);
        exchangeDeliveryMapper.updateReceiveInfo(exchangeDelivery.getId(), now, 2);

        // 6. 更新换货状态为已完成
        exchangeRecord.setExchangeStatus(7);
        exchangeRecord.setUpdatedAt(now);
        exchangeRecordMapper.updateExchangeStatus(exchangeId, 7);

        // 7. 更新订单商品状态
        orderItemMapper.updateRefundStatus(exchangeRecord.getOrderItemId(), 0, null);

        // 8. 记录操作日志
        saveOrderLog(exchangeRecord.getOrderId(), exchangeRecord.getOrderNo(), userId, 1, 16, "确认收到换货商品");

        // 9. 发送换货完成通知
        messageService.sendExchangeCompleteNotice(exchangeId);

        // 更新新旧SKU库存
        updateSkuStock(exchangeRecord.getOldSkuId(), exchangeRecord.getNewSkuId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelExchange(Long userId, Long exchangeId) {
        // 1. 查询换货记录
        ExchangeRecord exchangeRecord = exchangeRecordMapper.selectById(exchangeId);
        if (exchangeRecord == null) {
            throw new BusinessException("换货记录不存在");
        }

        // 2. 校验换货记录所属用户
        if (!exchangeRecord.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此换货记录");
        }

        // 3. 校验换货状态（只有待处理、待退货状态可以取消）
        if (exchangeRecord.getExchangeStatus() != 0 && exchangeRecord.getExchangeStatus() != 3) {
            throw new BusinessException("当前状态不可取消换货");
        }

        // 4. 更新换货状态为已取消
        LocalDateTime now = LocalDateTime.now();
        exchangeRecord.setExchangeStatus(8);
        exchangeRecord.setUpdatedAt(now);
        exchangeRecordMapper.updateExchangeStatus(exchangeId, 8);

        // 5. 更新订单商品状态
        orderItemMapper.updateRefundStatus(exchangeRecord.getOrderItemId(), 0, null);

        // 6. 记录操作日志
        saveOrderLog(exchangeRecord.getOrderId(), exchangeRecord.getOrderNo(), userId, 1, 17, "取消换货申请");

        // 7. 发送换货取消通知
        messageService.sendExchangeTimeoutNotice(exchangeId, "用户主动取消");

        // 释放新SKU库存
        releaseNewSkuStock(exchangeRecord.getNewSkuId(), 1);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateExchangeAddress(Long userId, Long exchangeId, Long addressId) {
        // 1. 查询换货记录
        ExchangeRecord exchangeRecord = exchangeRecordMapper.selectById(exchangeId);
        if (exchangeRecord == null) {
            throw new BusinessException("换货记录不存在");
        }

        // 2. 校验换货记录所属用户
        if (!exchangeRecord.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此换货记录");
        }

        // 3. 校验换货状态（只有待发货、已发货状态可以修改地址）
        if (exchangeRecord.getExchangeStatus() != 5 && exchangeRecord.getExchangeStatus() != 6) {
            throw new BusinessException("当前状态不可修改收货地址");
        }

        // 4. 查询新的收货地址
        Address address = addressMapper.selectById(addressId);
        if (address == null) {
            throw new BusinessException("收货地址不存在");
        }

        if (!address.getUserId().equals(userId)) {
            throw new BusinessException("无权使用此收货地址");
        }

        // 5. 更新换货物流收货地址
        ExchangeDelivery delivery = exchangeDeliveryMapper.selectByExchangeIdAndType(exchangeId, 2);
        if (delivery != null) {
            delivery.setReceiverName(address.getReceiver());
            delivery.setReceiverPhone(address.getPhone());
            delivery.setReceiverAddress(String.format("%s%s%s%s", 
                    address.getProvince(), address.getCity(), 
                    address.getDistrict(), address.getDetailAddress()));
            delivery.setUpdatedAt(LocalDateTime.now());
            exchangeDeliveryMapper.updateReceiveInfo(delivery.getId(), null, delivery.getDeliveryStatus());
        }

        // 6. 记录操作日志
        saveOrderLog(exchangeRecord.getOrderId(), exchangeRecord.getOrderNo(), userId, 1, 18, "更新换货收货地址");
    }

    @Override
    public ExchangeDetailDTO getExchangeDetail(Long userId, Long exchangeId) {
        // 1. 查询换货记录
        ExchangeRecord exchangeRecord = exchangeRecordMapper.selectById(exchangeId);
        if (exchangeRecord == null) {
            throw new BusinessException("换货记录不存在");
        }

        // 2. 查询换货物流信息
        List<ExchangeDelivery> deliveries = exchangeDeliveryMapper.selectByExchangeId(exchangeId);

        // 3. 组装并返回数据
        return buildExchangeDetailDTO(exchangeRecord, deliveries);
    }

    @Override
    public PageResult<ExchangeDetailDTO> getExchangeList(Long userId, Integer pageNum, Integer pageSize, Integer exchangeStatus) {
        int offset = (pageNum - 1) * pageSize;
        List<ExchangeRecord> records = exchangeRecordMapper.selectList(userId, exchangeStatus, null, null, offset, pageSize);
        long total = exchangeRecordMapper.selectCount(userId, exchangeStatus, null, null);
        
        List<ExchangeDetailDTO> list = records.stream()
                .map(record -> buildExchangeDetailDTO(record, exchangeDeliveryMapper.selectByExchangeId(record.getId())))
                .collect(Collectors.toList());
        
        return new PageResult<ExchangeDetailDTO>(total, list);
    }

    @Override
    public PageResult<ExchangeDetailDTO> getExchangeListForAdmin(Integer pageNum, Integer pageSize, Integer exchangeStatus,
                                                               Long userId, String orderNo, String exchangeNo) {
        // TODO: 实现管理员查询换货列表
        return new PageResult<ExchangeDetailDTO>(0L, Collections.emptyList());
    }

    @Override
    public ExchangeDetailDTO getExchangeDetailForAdmin(Long exchangeId) {
        // 1. 查询换货记录
        ExchangeRecord exchangeRecord = exchangeRecordMapper.selectById(exchangeId);
        if (exchangeRecord == null) {
            throw new BusinessException("换货记录不存在");
        }

        // 2. 查询换货物流信息
        List<ExchangeDelivery> deliveries = exchangeDeliveryMapper.selectByExchangeId(exchangeId);

        // 3. 组装并返回数据
        return buildExchangeDetailDTO(exchangeRecord, deliveries);
    }

    /**
     * 构建换货详情DTO
     */
    private ExchangeDetailDTO buildExchangeDetailDTO(ExchangeRecord record, List<ExchangeDelivery> deliveries) {
        ExchangeDetailDTO dto = new ExchangeDetailDTO();
        BeanUtils.copyProperties(record, dto);

        // 设置物流信息
        if (deliveries != null && !deliveries.isEmpty()) {
            deliveries.forEach(delivery -> {
                if (delivery.getDeliveryType() == 1) {
                    dto.setReturnDelivery(delivery);
                } else {
                    dto.setExchangeDelivery(delivery);
                }
            });
        }

        return dto;
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        return String.format("%s%s",
                LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")),
                String.format("%04d", (int) (Math.random() * 10000)));
    }

    /**
     * 生成支付单号
     */
    private String generatePaymentNo() {
        return String.format("PAY%s%s",
                LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")),
                String.format("%04d", (int) (Math.random() * 10000)));
    }

    /**
     * 计算订单金额
     */
    private void calculateOrderAmount(Order order, List<CreateOrderDTO.OrderItemDTO> items) {
        BigDecimal totalAmount = items.stream()
            .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 设置订单金额
        order.setTotalAmount(totalAmount);
        
        // 如果前端传入了优惠金额和实付金额，直接使用
        if (order.getDiscountAmount() != null && order.getActualAmount() != null) {
            return;
        }
        
        // 否则计算优惠金额
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (order.getCouponId() != null) {
            // 从数据库查询优惠券信息并计算优惠金额
            Coupon coupon = couponMapper.selectById(order.getCouponId());
            if (coupon != null && coupon.getStatus() == 1) {
                // 判断是否满足使用条件
                if (totalAmount.compareTo(coupon.getMinSpend()) >= 0) {
                    if (coupon.getType() == 2) { // 折扣券
                        discountAmount = totalAmount
                            .multiply(BigDecimal.ONE.subtract(coupon.getAmount().divide(BigDecimal.TEN)))
                            .setScale(2, RoundingMode.HALF_UP);
                    } else { // 满减券
                        discountAmount = coupon.getAmount();
                    }
                    log.info("优惠券计算成功: orderId={}, couponId={}, totalAmount={}, discountAmount={}", 
                        order.getId(), order.getCouponId(), totalAmount, discountAmount);
                } else {
                    log.warn("订单金额未满足优惠券使用条件: orderId={}, couponId={}, totalAmount={}, minSpend={}", 
                        order.getId(), order.getCouponId(), totalAmount, coupon.getMinSpend());
                }
            }
        }
        
        // 设置优惠金额
        order.setDiscountAmount(discountAmount);
        // 设置运费（暂时为0）
        order.setFreightAmount(BigDecimal.ZERO);
        // 计算实付金额 = 总金额 - 优惠金额 + 运费
        order.setActualAmount(totalAmount.subtract(discountAmount).add(order.getFreightAmount()));
    }

    /**
     * 模拟支付
     */
    private Object simulatePayment(PaymentRecord paymentRecord) {
        // 这里模拟返回支付参数
        return new Object() {
            public String getPaymentNo() {
                return paymentRecord.getPaymentNo();
            }
            public BigDecimal getAmount() {
                return paymentRecord.getPaymentAmount();
            }
        };
    }

    /**
     * 保存订单操作日志
     */
    private void saveOrderLog(Long orderId, String orderNo, Long operatorId, Integer operatorType,
                            Integer actionType, String actionDesc) {
        OrderLog log = new OrderLog();
        log.setOrderId(orderId);
        log.setOrderNo(orderNo);
        log.setOperatorId(operatorId);
        log.setOperatorType(operatorType);
        log.setActionType(actionType);
        log.setActionDesc(actionDesc);
        log.setIp(getRequestIp());
        log.setCreatedAt(LocalDateTime.now());
        orderLogMapper.insert(log);
    }

    /**
     * 获取请求IP
     */
    private String getRequestIp() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return "";
        }
        HttpServletRequest request = attributes.getRequest();
        String ip = request.getHeader("X-Real-IP");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 生成退款单号
     */
    private String generateRefundNo() {
        return String.format("REF%s%s",
                LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")),
                String.format("%04d", (int) (Math.random() * 10000)));
    }

    /**
     * 生成换货单号
     */
    private String generateExchangeNo() {
        return String.format("EX%s%s",
                LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")),
                String.format("%04d", (int) (Math.random() * 10000)));
    }

    /**
     * 锁定新SKU库存
     */
    private void lockNewSkuStock(Long skuId, Integer quantity) {
        // 1. 查询SKU库存
        ProductSku sku = productSkuMapper.selectById(skuId);
        if (sku == null) {
            throw new BusinessException("商品规格不存在");
        }

        // 2. 校验库存是否充足
        if (sku.getStock() < quantity) {
            throw new BusinessException("商品库存不足");
        }

        // 3. 锁定库存（减少可用库存，增加锁定库存）
        int rows = productSkuMapper.lockStock(skuId, quantity);
        if (rows == 0) {
            throw new BusinessException("库存锁定失败，请重试");
        }
    }

    /**
     * 释放新SKU库存
     */
    private void releaseNewSkuStock(Long skuId, Integer quantity) {
        // 释放库存（增加可用库存，减少锁定库存）
        int rows = productSkuMapper.releaseStock(skuId, quantity);
        if (rows == 0) {
            log.error("库存释放失败，skuId={}，quantity={}", skuId, quantity);
        }
    }

    /**
     * 更新新旧SKU库存
     */
    private void updateSkuStock(Long oldSkuId, Long newSkuId) {
        // 1. 增加原SKU库存
        productSkuMapper.increaseStock(oldSkuId, 1);
        
        // 2. 减少新SKU库存（之前已锁定）
        productSkuMapper.confirmLockedStock(newSkuId, 1);
    }

    @Override
    public List<TrackingInfo> getExchangeDeliveryTracking(Long userId, Long exchangeId, Integer type) {
        ExchangeRecord record = exchangeRecordMapper.selectById(exchangeId);
        if (record == null || !record.getUserId().equals(userId)) {
            throw new BusinessException("换货记录不存在");
        }

        List<ExchangeDelivery> deliveries;
        if (type != null) {
            ExchangeDelivery delivery = exchangeDeliveryMapper.selectByExchangeIdAndType(exchangeId, type);
            deliveries = delivery != null ? Collections.singletonList(delivery) : Collections.emptyList();
        } else {
            deliveries = exchangeDeliveryMapper.selectByExchangeId(exchangeId);
        }

        List<TrackingInfo> trackingList = new ArrayList<>();
        for (ExchangeDelivery delivery : deliveries) {
            if (delivery.getTrackingData() != null) {
                try {
                    List<TrackingInfo> list = objectMapper.readValue(delivery.getTrackingData(),
                            new TypeReference<List<TrackingInfo>>() {});
                    trackingList.addAll(list);
                } catch (JsonProcessingException e) {
                    log.error("解析物流数据失败", e);
                }
            }
        }
        
        return trackingList;
    }
} 