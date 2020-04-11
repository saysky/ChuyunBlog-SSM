package com.example.blog.controller.home;

import cn.hutool.core.lang.Validator;
import com.example.blog.common.constant.CommonConstant;
import com.example.blog.controller.common.BaseController;
import com.example.blog.dto.JsonResult;
import com.example.blog.entity.Role;
import com.example.blog.entity.User;
import com.example.blog.entity.UserRoleRef;
import com.example.blog.enums.CommonParamsEnum;
import com.example.blog.enums.TrueFalseEnum;
import com.example.blog.enums.UserStatusEnum;
import com.example.blog.service.*;
import com.example.blog.util.Md5Util;
import com.example.blog.util.RegexUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Objects;

@Controller
@Slf4j
public class LoginController extends BaseController {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRoleRefService userRoleRefService;

    /**
     * 处理跳转到登录页的请求
     *
     * @return 模板路径admin/admin_login
     */
    @RequestMapping(method = RequestMethod.GET, value = "/login")
    public String login() {
        return "home/login";
    }

    /**
     * 处理跳转到登录页的请求
     *
     * @return 模板路径admin/admin_login
     */
    @RequestMapping(method = RequestMethod.GET, value = "/register")
    public String register() {
        return "home/register";
    }

    /**
     * 验证登录信息
     *
     * @param userName 登录名：邮箱／用户名
     * @param userPass password 密码
     * @return JsonResult JsonResult
     */
    @RequestMapping(method = RequestMethod.POST, value = "/login")
    @ResponseBody
    public JsonResult getLogin(@RequestParam("userName") String userName,
                               @RequestParam("userPass") String userPass,
                               HttpSession session) {

        //更新失败次数
        User user;
        if (Validator.isEmail(userName)) {
            user = userService.findByEmail(userName);
        } else {
            user = userService.findByUserName(userName);
        }

        if (user == null) {
            return JsonResult.error("用户不存在");
        }

        // 判断账号是否禁用
        if (Objects.equals(user.getStatus(), UserStatusEnum.BAN.getCode())) {
            return JsonResult.error("账号被封禁");
        }

        // 判断密码是否正确
        if (!Objects.equals(user.getUserPass(), Md5Util.toMd5(userPass, CommonConstant.PASSWORD_SALT, 10))) {
            Integer errorCount = userService.updateUserLoginError(user);
            //超过五次禁用账户
            if (errorCount >= CommonParamsEnum.FIVE.getValue()) {
                userService.updateUserLoginEnable(user, TrueFalseEnum.FALSE.getValue());
            }
            int times = (5 - errorCount) > 0 ? (5 - errorCount) : 0;
            return JsonResult.error("用户名或密码错误，您还有" + times + "次机会");
        }


        // 登录成功，添加Session
        Role role = roleService.findByUserId(user.getId());
        user.setRole(role.getRole());
        session.setAttribute("user", user);
        return JsonResult.success();
    }


    /**
     * 退出登录
     *
     * @return 重定向到/login
     */
    @RequestMapping(method = RequestMethod.GET, value = "/logout")
    public String logOut(HttpSession session) {
        session.removeAttribute("user");
        return "redirect:/login";
    }


    /**
     * 验证注册信息
     *
     * @param userName  用户名
     * @param userEmail 邮箱
     * @return JsonResult JsonResult
     */
    @RequestMapping(method = RequestMethod.POST, value = "/register")
    @ResponseBody
    public JsonResult getRegister(@ModelAttribute("userName") String userName,
                                  @ModelAttribute("userPass") String userPass,
                                  @ModelAttribute("userEmail") String userEmail) {
        // 1.校验是否输入完整
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(userPass) || StringUtils.isEmpty(userEmail)) {
            return JsonResult.error("请填写完整信息");
        }
        // 2.邮箱是否合法
        if (!RegexUtil.isEmail(userEmail)) {
            return JsonResult.error("邮箱不合法");
        }
        // 3.密码长度是否合法
        if (userPass.length() > 20 || userPass.length() < 6) {
            return JsonResult.error("用户密码长度为6-20位!");
        }
        //4.创建用户
        User user = new User();
        user.setUserName(userName);
        user.setUserDisplayName(userName);
        user.setUserEmail(userEmail);
        user.setLoginEnable(TrueFalseEnum.TRUE.getValue());
        user.setLoginError(0);
        user.setUserPass(Md5Util.toMd5(userPass, CommonConstant.PASSWORD_SALT, 10));
        user.setUserAvatar("/static/images/avatar/" + RandomUtils.nextInt(1, 41) + ".jpeg");
        user.setStatus(UserStatusEnum.NORMAL.getCode());
        userService.insert(user);

        //5.关联角色
        Role defaultRole = roleService.findDefaultRole();
        if (defaultRole != null) {
            userRoleRefService.insert(new UserRoleRef(user.getId(), defaultRole.getId()));
        }


        return JsonResult.success("注册成功");
    }


    /**
     * 处理跳转忘记密码的请求
     *
     * @return 模板路径admin/admin_login
     */
    @RequestMapping(method = RequestMethod.GET, value = "/forget")
    public String forget() {
        return "home/forget";
    }

    /**
     * 处理忘记密码
     *
     * @param userName  用户名
     * @param userEmail 邮箱
     * @return JsonResult
     */
    @RequestMapping(method = RequestMethod.POST, value = "/forget")
    @ResponseBody
    public JsonResult getForget(@RequestParam("userName") String userName,
                                @RequestParam("userEmail") String userEmail) {

        User user = userService.findByUserName(userName);
        String newPass = RandomStringUtils.randomNumeric(8);
        if (user != null && userEmail.equalsIgnoreCase(user.getUserEmail())) {
            //验证成功，将密码由邮件方法发送给对方
            //1.修改密码
            userService.updatePassword(user.getId(), newPass);
        } else {
            return JsonResult.error("用户名和电子邮箱不一致");
        }
        return JsonResult.success("您的新密码是：" + newPass);
    }
}
