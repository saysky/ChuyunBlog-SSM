package com.example.blog.controller.home;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.blog.controller.common.BaseController;
import com.example.blog.dto.PostQueryCondition;
import com.example.blog.entity.Post;
import com.example.blog.entity.Tag;
import com.example.blog.service.PostService;
import com.example.blog.service.TagService;
import com.example.blog.util.PageUtil;
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
public class FrontTagController extends BaseController {

    @Autowired
    private TagService tagService;

    @Autowired
    private PostService postService;

    /**
     * 分类列表
     *
     * @param model
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/tag")
    public String tag(@RequestParam(value = "keywords", required = false) String keywords,
                      Model model) {
        List<Tag> tags = tagService.getHotTags(keywords, 100);
        model.addAttribute("tags", tags);
        return "home/tag";
    }

    /**
     * 标签对应的帖子列表
     *
     * @param model
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/tag/{id}")
    public String index(@PathVariable("id") Long tagId,
                        @RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
                        @RequestParam(value = "size", defaultValue = "10") Integer pageSize,
                        @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                        @RequestParam(value = "order", defaultValue = "desc") String order,
                        Model model) {

        Tag tag = tagService.get(tagId);
        if (tag == null) {
            return renderNotFound();
        }
        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        PostQueryCondition condition = new PostQueryCondition();
        condition.setTagId(tagId);
        Page<Post> postPage = postService.findPostByCondition(condition, page);
        model.addAttribute("posts", postPage);
        model.addAttribute("tag", tag);
        return "home/tag_post";
    }


}
