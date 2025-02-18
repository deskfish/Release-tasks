package com.sunlacey.controller;

import com.sunlacey.common.ApiResponse;
import com.sunlacey.eneity.User;
import com.sunlacey.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/active")
    public ApiResponse<List<User>> getActiveUsers() {
        try {
            List<User> users = userService.getAllActiveUsers();
            return ApiResponse.success(users);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取用户列表失败");
        }
    }

    @PostMapping("/create")
    public ApiResponse<User> createUser(@RequestParam String username, @RequestParam String displayName) {
        try {
            if (userService.existsByUsername(username)) {
                return ApiResponse.error(400, "用户名已存在");
            }
            User user = userService.createUser(username, displayName);
            return ApiResponse.success(user);
        } catch (Exception e) {
            return ApiResponse.error(500, "创建用户失败");
        }
    }

    @PostMapping("/{username}/deactivate")
    public ApiResponse<String> deactivateUser(@PathVariable String username) {
        try {
            userService.deactivateUser(username);
            return ApiResponse.success("用户已停用");
        } catch (Exception e) {
            return ApiResponse.error(500, "停用用户失败");
        }
    }
}