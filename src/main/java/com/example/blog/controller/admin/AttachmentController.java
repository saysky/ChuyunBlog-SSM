package com.example.blog.controller.admin;

import com.example.blog.controller.common.BaseController;
import com.example.blog.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;


/**
 * <pre>
 *     后台附件控制器
 * </pre>
 *
 * @author : saysky
 * @date : 2017/12/19
 */
@Slf4j
@Controller
@RequestMapping(value = "/admin/file")
public class AttachmentController extends BaseController {


    /**
     * 上传文件
     *
     * @param file file
     * @return Map
     */
    @RequestMapping(method = RequestMethod.POST, value = "/upload", produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public Map<String, Object> uploadFile(@RequestParam("file") MultipartFile file) {
        Map<String, Object> map = new HashMap<>(1);
        String path = FileUtil.upload(file);
        map.put("link", path);
        return map;
    }


}
