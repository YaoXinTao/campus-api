package com.campus.api.service;

import com.campus.api.entity.Category;
import java.util.List;

public interface CategoryService {
    /**
     * 获取分类详情
     */
    Category getCategoryById(Long id);
    
    /**
     * 获取所有分类
     */
    List<Category> getCategoryList();
    
    /**
     * 获取推荐分类
     */
    List<Category> getFeaturedCategories();
    
    /**
     * 获取子分类
     */
    List<Category> getSubCategories(Long parentId);
    
    /**
     * 获取指定层级的分类
     */
    List<Category> getCategoriesByLevel(Integer level);
    
    /**
     * 创建分类
     */
    void createCategory(Category category);
    
    /**
     * 更新分类
     */
    void updateCategory(Category category);
    
    /**
     * 删除分类
     */
    void deleteCategory(Long id);
    
    /**
     * 更新分类状态
     */
    void updateCategoryStatus(Long id, Integer status);
    
    /**
     * 更新分类排序
     */
    void updateCategorySort(Long id, Integer sortOrder);
    
    /**
     * 更新分类推荐状态
     */
    void updateCategoryFeatured(Long id, Integer isFeatured);
} 