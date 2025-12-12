# 多阶段构建 Dockerfile for Spring Boot
# 阶段1: 构建阶段
FROM maven:3.9-eclipse-temurin-17 AS build

# 设置工作目录
WORKDIR /app

# 复制 pom.xml 和下载依赖（利用 Docker 缓存）
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源代码
COPY src ./src

# 构建应用
RUN mvn clean package -DskipTests

# 阶段2: 运行阶段（安全加固）
FROM eclipse-temurin:17-jre-alpine

# 安装必要的工具（用于健康检查）
RUN apk add --no-cache wget curl

# 创建非root用户（安全最佳实践）
RUN addgroup -S appgroup && \
    adduser -S appuser -G appgroup -u 1000

# 创建应用目录和日志目录
RUN mkdir -p /app/logs && \
    chown -R appuser:appgroup /app

# 设置工作目录
WORKDIR /app

# 从构建阶段复制 JAR 文件
COPY --from=build /app/target/*.jar app.jar

# 更改文件所有者
RUN chown appuser:appgroup app.jar

# 切换到非root用户
USER appuser

# 暴露端口
EXPOSE 8080

# 健康检查（使用HEALTHCHECK指令）
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# 启动应用
ENTRYPOINT ["java", "-jar", "app.jar"]

