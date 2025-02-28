package com.campus.api.service.impl;

import com.campus.api.entity.Category;
import com.campus.api.mapper.CategoryMapper;
import com.campus.api.service.CategoryService;
import com.campus.api.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@CacheConfig(cacheNames = "category")
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    @Cacheable(key = "#id")
    public Category getCategoryById(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        return category;
    }

    @Override
    @Cacheable(key = "'list'")
    public List<Category> getCategoryList() {
        return categoryMapper.selectList();
    }

    @Override
    @Cacheable(key = "'featured'")
    public List<Category> getFeaturedCategories() {
        return categoryMapper.selectFeatured();
    }

    @Override
    @Cacheable(key = "'parent_' + #parentId")
    public List<Category> getSubCategories(Long parentId) {
        return categoryMapper.selectByParentId(parentId);
    }

    @Override
    @Cacheable(key = "'level_' + #level")
    public List<Category> getCategoriesByLevel(Integer level) {
        return categoryMapper.selectByLevel(level);
    }

    @Override
    @Transactional
    @CacheEvict(allEntries = true)
    public void createCategory(Category category) {
        // 设置默认值
        if (category.getParentId() == null) {
            category.setParentId(0L);
        }
        if (category.getLevel() == null) {
            category.setLevel(category.getParentId() == 0 ? 1 : 2);
        }
        if (category.getSortOrder() == null) {
            category.setSortOrder(0);
        }
        if (category.getIsFeatured() == null) {
            category.setIsFeatured(0);
        }
        if (category.getStatus() == null) {
            category.setStatus(1);
        }

        // 如果是二级分类，验证父分类是否存在
        if (category.getParentId() > 0) {
            Category parent = categoryMapper.selectById(category.getParentId());
            if (parent == null) {
                throw new BusinessException("父分类不存在");
            }
            if (parent.getLevel() != 1) {
                throw new BusinessException("父分类必须是一级分类");
            }
        }

        categoryMapper.insert(category);
    }

    @Override
    @Transactional
    @CacheEvict(allEntries = true)
    public void updateCategory(Category category) {
        Category existCategory = categoryMapper.selectById(category.getId());
        if (existCategory == null) {
            throw new BusinessException("分类不存在");
        }

        // 如果修改了父分类，需要验证
        if (category.getParentId() != null && !category.getParentId().equals(existCategory.getParentId())) {
            if (category.getParentId() > 0) {
                Category parent = categoryMapper.selectById(category.getParentId());
                if (parent == null) {
                    throw new BusinessException("父分类不存在");
                }
                if (parent.getLevel() != 1) {
                    throw new BusinessException("父分类必须是一级分类");
                }
            }
            // 更新层级
            category.setLevel(category.getParentId() == 0 ? 1 : 2);
        }

        categoryMapper.update(category);
    }

    @Override
    @Transactional
    @CacheEvict(allEntries = true)
    public void deleteCategory(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }

        // 如果是一级分类，检查是否有子分类
        if (category.getLevel() == 1) {
            List<Category> subCategories = categoryMapper.selectByParentId(id);
            if (!subCategories.isEmpty()) {
                throw new BusinessException("请先删除子分类");
            }
        }

        categoryMapper.deleteById(id);
    }

    @Override
    @Transactional
    @CacheEvict(allEntries = true)
    public void updateCategoryStatus(Long id, Integer status) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        categoryMapper.updateStatus(id, status);
    }

    @Override
    @Transactional
    @CacheEvict(allEntries = true)
    public void updateCategorySort(Long id, Integer sortOrder) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        categoryMapper.updateSort(id, sortOrder);
    }

    @Override
    @Transactional
    @CacheEvict(allEntries = true)
    public void updateCategoryFeatured(Long id, Integer isFeatured) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        categoryMapper.updateFeatured(id, isFeatured);
    }
} 