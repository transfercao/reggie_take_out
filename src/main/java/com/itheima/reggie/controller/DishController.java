package com.itheima.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService ds;
    @Autowired
    private DishFlavorService dfs;
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        ds.saveWithFlavor(dishDto);
        return R.success("菜品添加成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //构造分页构造器
        Page<Dish> pageInfo = new Page(page,pageSize);
        Page<DishDto> dishDtoPage = new Page(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper();
        //添加过滤条件
        lqw.like(StringUtils.isNotEmpty(name),Dish::getName,name);
        //添加排序条件
        lqw.orderByDesc(Dish::getUpdateTime);
        //执行查询
        ds.page(pageInfo,lqw);
        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }

    //根据id查询菜品信息和对应的口味信息
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto = ds.getByIdWithFlavor(id);
        return R.success(dishDto);
    }
    //修改菜品
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        ds.updateWithFlavor(dishDto);
        return R.success("菜品修改成功");
    }

//    //根据条件查询对应的菜品数据
//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish){
//        //构造条件构造器
//        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper();
//        //添加查询条件
//        lqw.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
//        //查询状态为1的，在售
//        lqw.eq(Dish::getStatus,1);
//        //添加排序条件
//        lqw.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//        //执行查询
//        List<Dish> list = ds.list(lqw);
//        return R.success(list);
//    }
//根据条件查询对应的菜品数据
@GetMapping("/list")
@Transactional
public R<List<DishDto>> list(Dish dish){
    //构造条件构造器
    LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper();
    //添加查询条件
    lqw.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
    //查询状态为1的，在售
    lqw.eq(Dish::getStatus,1);
    //添加排序条件
    lqw.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
    //执行查询
    List<Dish> list = ds.list(lqw);
    List<DishDto> dishDtoList = list.stream().map((item)->{
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(item,dishDto);
        Long categoryId = item.getCategoryId();
        //根据id查询分类对象
        Category category = categoryService.getById(categoryId);
        if (category != null) {
            String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);
        }
        //当前菜品id
        Long dishId = item.getId();
        LambdaQueryWrapper<DishFlavor> lqw2 = new LambdaQueryWrapper();
        lqw2.eq(DishFlavor::getDishId,dishId);
        //SQL : select * from dish_flavor where dish_id = ?
        List<DishFlavor> dishFlavorList = dfs.list(lqw2);
        dishDto.setFlavors(dishFlavorList);
        return dishDto;
    }).collect(Collectors.toList());
    return R.success(dishDtoList);
}
}








