package com.example.blog.controller.admin;

import com.example.blog.controller.common.BaseController;
import com.example.blog.dto.JsonResult;
import com.example.blog.entity.Permission;
import com.example.blog.entity.User;
import com.example.blog.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * <pre>
 *     后台首页控制器
 * </pre>
 *
 */
@Slf4j
@Controller
@RequestMapping(value = "/admin")
public class AdminController extends BaseController {

    @Autowired
    private PermissionService permissionService;

    /**
     * 请求后台页面
     *
     * @param model model
     * @return 模板路径admin/admin_index
     */
    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model) {

        return "admin/admin_index";
    }


    /**
     * 获得当前用户的菜单
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/currentMenus")
    @ResponseBody
    public JsonResult getMenu() {
        Long userId = getLoginUserId();
        List<Permission> permissions = permissionService.findPermissionTreeByUserIdAndResourceType(userId, "menu");
        return JsonResult.success("", permissions);
    }

    /**
     * 获得当前登录用户
     */
    @RequestMapping(method = RequestMethod.GET, value = "/currentUser")
    @ResponseBody
    public JsonResult currentUser() {
        User user = getLoginUser();
        if (user != null) {
            return JsonResult.success("", user);
        }
        return JsonResult.error("用户未登录");
    }


}
