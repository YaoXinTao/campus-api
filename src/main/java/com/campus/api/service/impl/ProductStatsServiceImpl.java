package com.campus.api.service.impl;

import com.campus.api.dto.stats.*;
import com.campus.api.mapper.ProductMapper;
import com.campus.api.mapper.ProductReviewMapper;
import com.campus.api.service.ProductStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductStatsServiceImpl implements ProductStatsService {

    private final ProductMapper productMapper;
    private final ProductReviewMapper reviewMapper;

    @Override
    public ProductStatsDTO getProductStats(String type) {
        ProductStatsDTO stats = new ProductStatsDTO();
        
        // 1. 设置数据概览
        ProductStatsOverviewDTO overview = new ProductStatsOverviewDTO();
        // 获取今日和昨日的统计数据
        Map<String, Object> todayStats = productMapper.selectDailyStats(LocalDate.now());
        Map<String, Object> yesterdayStats = productMapper.selectDailyStats(LocalDate.now().minusDays(1));
        
        // 设置总商品数和增长
        overview.setTotalProducts(((Number) todayStats.get("totalProducts")).intValue());
        overview.setProductIncrease(
            ((Number) todayStats.get("totalProducts")).intValue() - 
            ((Number) yesterdayStats.get("totalProducts")).intValue()
        );
        
        // 设置总销量和增长
        overview.setTotalSales(((Number) todayStats.get("totalSales")).intValue());
        overview.setSalesIncrease(
            ((Number) todayStats.get("totalSales")).intValue() - 
            ((Number) yesterdayStats.get("totalSales")).intValue()
        );
        
        // 设置总评价数和增长
        overview.setTotalComments(((Number) todayStats.get("totalComments")).intValue());
        overview.setCommentIncrease(
            ((Number) todayStats.get("totalComments")).intValue() - 
            ((Number) yesterdayStats.get("totalComments")).intValue()
        );
        
        // 设置平均评分和增长
        overview.setAverageRating(((Number) todayStats.get("averageRating")).doubleValue());
        overview.setRatingIncrease(
            ((Number) todayStats.get("averageRating")).doubleValue() - 
            ((Number) yesterdayStats.get("averageRating")).doubleValue()
        );
        
        stats.setOverview(overview);
        
        // 2. 设置销量趋势
        ProductSalesTrendDTO salesTrend = new ProductSalesTrendDTO();
        List<String> dates = new ArrayList<>();
        List<Integer> values = new ArrayList<>();
        
        // 获取日期范围
        int days = "week".equals(type) ? 7 : 30;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);
        
        // 获取每日销量数据
        List<Map<String, Object>> dailySales = productMapper.selectDailySales(startDate, endDate);
        
        // 填充数据
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            final LocalDate currentDate = date;
            dates.add(currentDate.format(formatter));
            int sales = dailySales.stream()
                .filter(map -> currentDate.equals(map.get("date")))
                .map(map -> ((Number) map.get("sales")).intValue())
                .findFirst()
                .orElse(0);
            values.add(sales);
        }
        
        salesTrend.setDates(dates);
        salesTrend.setValues(values);
        stats.setSalesTrend(salesTrend);
        
        // 3. 设置分类统计
        List<CategoryStatsDTO> categoryStats = productMapper.selectCategoryStats();
        stats.setCategoryStats(categoryStats);
        
        // 4. 设置评分分布
        List<Integer> ratingStats = reviewMapper.selectRatingDistribution();
        stats.setRatingStats(ratingStats);
        
        // 5. 设置热销商品
        List<HotProductDTO> hotProducts = productMapper.selectHotProducts(10);
        stats.setHotProducts(hotProducts);
        
        return stats;
    }
} 