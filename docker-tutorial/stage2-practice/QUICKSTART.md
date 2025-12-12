# 快速开始指南

## 🚀 5 分钟快速体验

### 步骤 1：验证 Docker 环境

```bash
# 检查 Docker 是否运行
docker --version
docker info
```

### 步骤 2：实验 1 - 镜像基础操作（5分钟）

```bash
# 搜索镜像
docker search nginx

# 拉取镜像
docker pull nginx:latest

# 查看本地镜像
docker images

# 查看镜像详细信息
docker inspect nginx:latest

# 查看镜像历史（分层结构）
docker history nginx:latest

# 给镜像打标签
docker tag nginx:latest my-nginx:v1.0

# 查看标签后的镜像
docker images | grep nginx
```

### 步骤 3：实验 2 - 第一个 Dockerfile（10分钟）

```bash
# 进入简单 Web 应用目录
cd simple-web

# 查看 Dockerfile
cat Dockerfile

# 构建镜像
docker build -t my-simple-web:1.0 .

# 查看构建的镜像
docker images | grep my-simple-web

# 运行容器
docker run -d --name web-test -p 8080:80 my-simple-web:1.0

# 访问 http://localhost:8080 查看效果

# 查看容器日志
docker logs web-test

# 停止并删除容器
docker stop web-test
docker rm web-test
```

### 步骤 4：实验 3 - Node.js 应用（15分钟）

```bash
# 进入 Node.js 应用目录
cd ../nodejs-app

# 查看基础 Dockerfile
cat Dockerfile

# 构建基础版镜像
docker build -f Dockerfile -t nodejs-app:basic .

# 查看镜像大小
docker images | grep nodejs-app

# 查看优化版 Dockerfile
cat Dockerfile.optimized

# 如果package-lock.json不存在，则构造前需要安装依赖
npm install

# 构建优化版镜像
docker build -f Dockerfile.optimized -t nodejs-app:optimized .

# 对比镜像大小
docker images | grep nodejs-app

# 运行优化版容器
docker run -d --name nodejs-test -p 3000:3000 nodejs-app:optimized

# 访问 http://localhost:3000 查看效果

# 清理
docker stop nodejs-test
docker rm nodejs-test
```

### 步骤 5：实验 4 - Python 应用（15分钟）

```bash
# 进入 Python 应用目录
cd ../python-app

# 构建基础版镜像
docker build -f Dockerfile -t python-app:basic .

# 构建优化版镜像
docker build -f Dockerfile.optimized -t python-app:optimized .

# 对比镜像大小
docker images | grep python-app

# 运行优化版容器
docker run -d --name python-test -p 5000:5000 python-app:optimized

# 访问 http://localhost:5000 查看效果

# 清理
docker stop python-test
docker rm python-test
```

### 步骤 6：实验 5 - Java 应用（20分钟）

```bash
# 进入 Java 应用目录
cd ../java-app

# 构建基础版镜像（需要较长时间，首次构建需要下载 Maven 依赖）
docker build -f Dockerfile -t java-app:basic .

# 构建优化版镜像（多阶段构建）
docker build -f Dockerfile.optimized -t java-app:optimized .

# 对比镜像大小（优化版应该小很多）
docker images | grep java-app

# 运行优化版容器
docker run -d --name java-test -p 8080:8080 java-app:optimized

# 等待应用启动（约 10-20 秒）
sleep 15

# 访问 http://localhost:8080 查看效果

# 清理
docker stop java-test
docker rm java-test
```

### 步骤 7：镜像优化对比

```bash
# 查看所有构建的镜像及其大小
docker images | grep -E "(my-simple-web|nodejs-app|python-app|java-app)"

# 使用 docker system df 查看磁盘使用情况
docker system df

# 查看镜像详细信息
docker inspect java-app:optimized | grep -A 10 "Layers"
```

### 步骤 8：清理资源

```bash
# 删除所有测试容器
docker ps -a | grep -E "(web-test|nodejs-test|python-test|java-test)" | awk '{print $1}' | xargs docker rm -f

# 删除所有测试镜像（可选）
docker rmi my-simple-web:1.0 nodejs-app:basic nodejs-app:optimized python-app:basic python-app:optimized java-app:basic java-app:optimized

# 清理未使用的镜像和构建缓存
docker system prune -a
```

---

## 📚 详细学习

完成快速体验后，请按照 `practice-steps.md` 中的详细步骤进行系统学习。

---

## ⚠️ 注意事项

1. **构建时间**：首次构建需要下载基础镜像和依赖，可能需要几分钟，请耐心等待
2. **端口冲突**：如果端口被占用，请修改 Dockerfile 中的 EXPOSE 或运行时的端口映射
3. **镜像大小**：优化版镜像通常比基础版小 50-80%，这是多阶段构建的优势
4. **缓存机制**：第二次构建会使用缓存，速度会快很多
5. **.dockerignore**：确保使用 .dockerignore 排除不必要的文件，减少构建上下文大小

---

## 🆘 遇到问题？

1. 检查 Docker 是否正常运行：`docker ps`
2. 查看构建日志：`docker build` 命令会显示详细输出
3. 查看容器日志：`docker logs <container_name>`
4. 查看详细实践步骤：`practice-steps.md`
5. 查看常见问题：`practice-steps.md` 中的"常见问题"部分

---

## ✅ 完成检查

完成快速体验后，你应该能够：
- [x] 使用 Docker 命令管理镜像
- [x] 编写简单的 Dockerfile
- [x] 构建自定义镜像
- [x] 理解多阶段构建的优势
- [x] 使用 .dockerignore 优化构建

现在可以开始系统学习 `practice-steps.md` 中的所有实验了！

