package com.sunlacey.controller;

import com.sunlacey.common.ApiResponse;
import com.sunlacey.eneity.ReleaseTask;
import com.sunlacey.eneity.UserRecord;
import com.sunlacey.service.ReleaseTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;

@Controller
@RequestMapping("/release")
public class ReleaseTaskController {
    private static final Logger logger = LoggerFactory.getLogger(ReleaseTaskController.class);

    @Resource
    private ReleaseTaskService releaseTaskService;

    // 缓存本地IPv4地址
    private static String cachedLocalIPv4Address;

    @GetMapping("/detail/{id}")
    public String getTaskDetail(@PathVariable long id, Model model) {
        logger.info("正在获取任务详情，任务ID: {}", id);
        ReleaseTask task = releaseTaskService.getTaskById(id);
        if (task == null) {
            return "error";
        }
        logger.info("成功获取任务详情，任务ID: {}, 任务名称: {}", id, task.getTaskName());
        model.addAttribute("task", task);
        return "task-detail";
    }

    @GetMapping("/create")
    public String showCreatePage() {
        logger.info("访问创建任务页面");
        return "create";
    }

    @PostMapping("/create")
    @ResponseBody
    public ApiResponse<String> createTask(@RequestParam String taskName, @RequestParam String taskDetail,
                                          @RequestParam List<String> selectedUsers, HttpServletRequest request) {
        logger.info("开始创建任务，任务名称: {},  选择的用户数: {}", taskName, selectedUsers.size());

        if (taskName == null || taskName.trim().isEmpty()) {
            logger.warn("创建任务失败：任务名称为空");
            return ApiResponse.error(400, "任务名称不能为空");
        }
        if (selectedUsers.isEmpty()) {
            logger.warn("创建任务失败：未选择用户");
            return ApiResponse.error(400, "必须选择至少一个用户");
        }

        ReleaseTask task = releaseTaskService.createTask(taskName, taskDetail,  selectedUsers);
        try {
            String scheme = request.getScheme();
            String serverIp = getLocalIPv4Address();
            int serverPort = request.getServerPort();
            StringBuilder baseUrlBuilder = new StringBuilder();
            baseUrlBuilder.append(scheme).append("://").append(serverIp);
            if ((scheme.equals("http") && serverPort != 80) || (scheme.equals("https") && serverPort != 443)) {
                baseUrlBuilder.append(":").append(serverPort);
            }
            String baseUrl = baseUrlBuilder.toString();
            String taskLink = baseUrl + "/release/" + task.getId() + "/notice";
            return ApiResponse.success(taskLink);
        } catch (Exception e) {
            logger.error("生成任务链接失败", e);
            return ApiResponse.error(500, "生成链接失败");
        }
    }

    // 获取本地IPv4地址的方法，使用缓存
    private String getLocalIPv4Address() {
        if (cachedLocalIPv4Address != null) {
            return cachedLocalIPv4Address;
        }
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress inetAddress = addresses.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress.getHostAddress().indexOf(':') == -1) {
                        cachedLocalIPv4Address = inetAddress.getHostAddress();
                        return cachedLocalIPv4Address;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("获取本地IPv4地址失败", e);
        }
        cachedLocalIPv4Address = "127.0.0.1"; // 兜底返回本地回环地址
        return cachedLocalIPv4Address;
    }

    @GetMapping("/{id}/notice")
    public ModelAndView releaseNotice(@PathVariable long id) {
        logger.info("访问任务同意页面，任务ID: {}", id);
        ReleaseTask task = releaseTaskService.getTaskById(id);
        if (task == null) {
            return new ModelAndView("error");
        }
        logger.info("成功获取任务信息，任务ID: {}, 任务名称: {}", id, task.getTaskName());
        ModelAndView modelAndView = new ModelAndView("release-notice");
        modelAndView.addObject("task", task);
        return modelAndView;
    }

    @PostMapping("/{id}/approve")
    @ResponseBody
    public ApiResponse<String> submitApproval(@PathVariable long id, @RequestParam boolean agreed, @RequestParam String username) {
        ReleaseTask task = releaseTaskService.getTaskById(id);
        if (task == null) {
            return ApiResponse.error(404, "任务不存在");
        }
        if (task.isEnd()) {
            return ApiResponse.error(400, "任务已结束");
        }

        try {
            releaseTaskService.updateAgreedStatus(id, agreed, username);
            return ApiResponse.success("操作成功");
        } catch (RuntimeException e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @GetMapping("/{id}/user-records")
    @ResponseBody
    public ApiResponse<List<UserRecord>> getUserRecords(@PathVariable long id) {
        try {
            List<UserRecord> records = releaseTaskService.getUserRecordsByTaskId(id);
            return ApiResponse.success(records);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取IP记录失败");
        }
    }
}
