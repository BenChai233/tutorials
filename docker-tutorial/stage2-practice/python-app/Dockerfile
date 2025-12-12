# 基础版 Dockerfile - Python 应用
# 使用官方 Python 镜像
FROM python:3.11

# 设置工作目录
WORKDIR /app

# 复制 requirements.txt
COPY requirements.txt .

# 安装依赖
RUN pip install --no-cache-dir -r requirements.txt

# 复制应用代码
COPY . .

# 暴露端口
EXPOSE 5000

# 设置环境变量
ENV FLASK_ENV=production
ENV PORT=5000

# 启动应用（使用 gunicorn 作为生产服务器）
CMD ["gunicorn", "--bind", "0.0.0.0:5000", "--workers", "4", "app:app"]

