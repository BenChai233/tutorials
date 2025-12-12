# 基础版 Dockerfile - 简单静态 Web 应用
# 使用官方 Nginx 镜像作为基础镜像
FROM nginx:latest

# 设置工作目录（可选，Nginx 默认工作目录是 /usr/share/nginx/html）
WORKDIR /usr/share/nginx/html

# 复制本地文件到容器中
COPY index.html /usr/share/nginx/html/

# 暴露端口 80
EXPOSE 80

# Nginx 镜像已经设置了 CMD，这里可以省略
# CMD ["nginx", "-g", "daemon off;"]

