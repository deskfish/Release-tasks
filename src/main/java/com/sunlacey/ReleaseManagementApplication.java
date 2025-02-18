package com.sunlacey;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

@Slf4j
@SpringBootApplication
public class ReleaseManagementApplication {

    public static void main(String[] args) {
        // 启动 Spring Boot 应用并获取应用上下文
        ConfigurableApplicationContext context = SpringApplication.run(ReleaseManagementApplication.class, args);

        // 获取 Environment 对象
        String localIpV4Address = getLocalIpV4Address();
        String port = getPort(context);

        log.info("服务启动完成 ：http://" + localIpV4Address + ":" + port + "/release/create");
    }

    private static String getPort(ConfigurableApplicationContext context) {
        // 从 Spring 的环境中获取端口号
        String port = context.getEnvironment().getProperty("server.port");
        if (port == null) {
            port = "8080";  // 默认端口
        }
        return port;
    }

    private static String getLocalIpV4Address() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress inetAddress = addresses.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress.getHostAddress().indexOf(':') == -1) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取本地IPv4地址失败", e);
        }
        return "127.0.0.1"; // 兜底返回本地回环地址
    }
}