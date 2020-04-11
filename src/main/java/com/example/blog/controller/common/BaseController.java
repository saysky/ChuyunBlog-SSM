package com.example.blog.controller.common;

import com.example.blog.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;

/**
 * Controller抽象类
 */
@Controller
public abstract class BaseController {


    @Autowired
    private HttpServletRequest request;


    /**
     * 渲染404页面
     *
     * @return redirect:/404
     */
    public String renderNotFound() {
        return "forward:/404";
    }


    /**
     * 渲染404页面
     *
     * @return redirect:/404
     */
    public String renderNotAllowAccess() {
        return "redirect:/403";
    }

    /**
     * 获得登录用户
     * @return
     */
    public User getLoginUser() {
        User user = (User) request.getSession().getAttribute("user");
        return user;
    }

    /**
     * 当前用户ID
     *
     * @return
     */
    public Long getLoginUserId() {
        return getLoginUser().getId();
    }

    /**
     * 当前用户是管理员
     *
     * @return
     */
    public Boolean loginUserIsAdmin() {
        User loginUser = getLoginUser();
        if (loginUser != null) {
            return "admin".equalsIgnoreCase(loginUser.getRole());
        }

        return false;
    }


}
