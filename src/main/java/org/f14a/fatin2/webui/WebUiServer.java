package org.f14a.fatin2.webui;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.staticfiles.Location;

import java.util.Map;

public class WebUiServer {

    public void start(int port) {
        Javalin app = Javalin.create(config -> {
            // 1. 配置静态资源 (将前端构建产物放入 resources 目录后)
            config.staticFiles.add(staticFiles -> {
                // hostedPath 是 URL 路径 (例如 localhost:8080/)
                staticFiles.hostedPath = "/";
                // directory 是资源在 classpath 中的路径 (src/main/resources/public)
                staticFiles.directory = "/public";
                // 指定资源位置为 Classpath (打包在 Jar 内)
                staticFiles.location = Location.CLASSPATH;
            });

            // 开发阶段使用 Vite 构建产物目录
            //config.staticFiles.add("../webui/dist", Location.EXTERNAL);

            // 2. 配置 CORS (允许前端开发服务器跨域访问)
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(it -> {
                    it.allowHost("http://localhost:5173"); // Vite 默认端口
                });
            });
        }).start(port);

        // 3. 定义路由
        app.post("/api/login", this::handleLogin);

        // 4. 鉴权拦截器 (Before Handler)
        app.before("/api/protected/*", ctx -> {
            String token = ctx.header("Authorization");
            // 简单剥离 "Bearer " 前缀
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            try {
                // 验证 Token，如果失败会抛出异常
                String user = JwtUtil.verifyToken(token);
                ctx.attribute("user", user); // 将用户信息存入上下文供后续使用
            } catch (Exception e) {
                ctx.status(HttpStatus.UNAUTHORIZED).json(Map.of("message", "Invalid Token"));
                // 阻止请求继续
                ctx.skipRemainingHandlers();
            }
        });

        // 5. 受保护的接口示例
        app.get("/api/protected/status", ctx -> {
            String user = ctx.attribute("user");
            ctx.json(Map.of("status", "running", "user", user));
        });
    }

    private void handleLogin(Context ctx) {
        // 解析请求体 (这里简化处理，实际应使用 DTO 类)
        Map<String, String> credentials = ctx.bodyAsClass(Map.class);

        // 模拟数据库验证
        if ("admin".equals(credentials.get("username")) && "123456".equals(credentials.get("password"))) {
            String token = JwtUtil.createToken("admin");
            ctx.json(Map.of("token", token));
        } else {
            ctx.status(HttpStatus.UNAUTHORIZED).json(Map.of("message", "Login failed"));
        }
    }
}