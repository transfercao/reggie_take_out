package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜品分类
 */
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /*新增菜品分类
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        categoryService.save(category);
        return R.success("新增成功");
    }

    /**
     * 分页
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){
        //构造分页构造器
        Page<Category> pageInfo = new Page(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper();
        //添加排序条件
        lqw.orderByAsc(Category::getSort);
        //执行查询
        categoryService.page(pageInfo,lqw);
        return R.success(pageInfo);
    }

    @DeleteMapping
    public R<String> delete(Long ids){
//        categoryService.removeById(id);
        categoryService.remove(ids);
        return R.success("删除成功");
    }
    @PutMapping
    public R<String> update(@RequestBody Category category){
        categoryService.updateById(category);
        return R.success("修改成功");
    }

    //根据条件查询分类数据
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        //构造条件构造器
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        //添加条件
        lqw.eq(category.getType() != null,Category::getType,category.getType());
        //添加排序条件
        lqw.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(lqw);
        return R.success(list);
    }
}







