package com.example.blog.controller.home;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.blog.controller.common.BaseController;
import com.example.blog.dto.PostQueryCondition;
import com.example.blog.entity.Category;
import com.example.blog.entity.Post;
import com.example.blog.service.CategoryService;
import com.example.blog.service.PostService;
import com.example.blog.service.TagService;
import com.example.blog.util.PageUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author 言曌
 * @date 2020/3/11 4:59 下午
 */
@Controller
public class FrontCategoryController extends BaseController {

    @Autowired
    private PostService postService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TagService tagService;
    /**
     * 分类列表
     *
     * @param model
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/category")
    public String category(@RequestParam(value = "keywords", required = false) String keywords,
                           Model model) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.orderByDesc("cate_sort");
        if (StringUtils.isNotEmpty(keywords)) {
            queryWrapper.like("cate_name", keywords);
        }
        List<Category> categories = categoryService.findAll(queryWrapper);
        model.addAttribute("categories", categories);
        return "home/category";
    }


    /**
     * 分类对应的帖子列表
     *
     * @param model
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/category/{id}")
    public String index(@PathVariable("id") Long cateId,
                        @RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
                        @RequestParam(value = "size", defaultValue = "10") Integer pageSize,
                        @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                        @RequestParam(value = "order", defaultValue = "desc") String order,
                        Model model) {

        Category category = categoryService.get(cateId);
        if(category == null) {
            return renderNotFound();
        }

        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        PostQueryCondition condition = new PostQueryCondition();
        condition.setCateId(cateId);
        Page<Post> postPage = postService.findPostByCondition(condition, page);
        model.addAttribute("posts", postPage);
        model.addAttribute("category", category);
        return "home/category_post";
    }


}
