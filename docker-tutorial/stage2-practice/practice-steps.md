# 第二阶段实践步骤详解：Docker 镜像管理

## 前置准备

确保你已经完成：
- [ ] Docker 已安装并运行
- [ ] 熟悉基本的 Docker 命令（run, ps, stop, rm 等）
- [ ] 了解 Docker 容器和镜像的基本概念

---

## 实验 1：镜像基础操作

### 1.1 搜索镜像

```bash
# 在 Docker Hub 上搜索镜像
docker search nginx

# 搜索特定标签的镜像
docker search python

# 查看搜索结果说明
# 结果包含：镜像名称、描述、星级、是否官方、是否自动构建
```

### 1.2 拉取镜像

```bash
# 拉取最新版本的镜像
docker pull nginx:latest

# 拉取特定版本的镜像
docker pull nginx:1.25-alpine

# 拉取 Python 镜像
docker pull python:3.11

# 查看本地镜像列表
docker images

# 或者使用简写
docker images nginx
```

### 1.3 查看镜像信息

```bash
# 查看镜像详细信息
docker inspect nginx:latest

# 查看镜像的特定信息（如架构、操作系统）
docker inspect nginx:latest | grep -i arch

# 查看镜像历史（分层结构）
docker history nginx:latest

# 查看镜像历史（不截断）
docker history --no-trunc nginx:latest

# 查看镜像大小
docker images --format "table {{.Repository}}\t{{.Tag}}\t{{.Size}}"
```

### 1.4 理解镜像分层结构

```bash
# 查看镜像的所有层
docker history nginx:latest

# 理解分层结构：
# - 每一行代表一个层（layer）
# - 层是只读的，多个镜像可以共享相同的层
# - 修改文件会创建新的层，而不是修改原有层
# - 这就是为什么 Docker 镜像可以高效共享和存储

# 查看镜像的层信息（JSON 格式）
docker inspect nginx:latest | grep -A 10 "Layers"
```

### 1.5 给镜像打标签

```bash
# 给现有镜像打新标签
docker tag nginx:latest my-nginx:v1.0

# 查看标签后的镜像
docker images | grep nginx

# 给镜像打多个标签
docker tag nginx:latest my-nginx:latest
docker tag nginx:latest my-nginx:1.0
docker tag nginx:latest my-nginx:stable

# 查看所有标签
docker images my-nginx
```

### 1.6 删除镜像

```bash
# 删除镜像（需要先删除使用该镜像的容器）
docker rmi my-nginx:v1.0

# 强制删除镜像（即使有容器在使用）
docker rmi -f my-nginx:v1.0

# 删除所有未使用的镜像
docker image prune

# 删除所有未使用的镜像（包括有标签的）
docker image prune -a
```

### 1.7 镜像存储位置

```bash
# 查看 Docker 系统信息
docker info

# 查看镜像存储位置（Linux）
# 默认位置：/var/lib/docker/image/

# 查看磁盘使用情况
docker system df

# 查看详细的磁盘使用情况
docker system df -v
```

---

## 实验 2：编写第一个 Dockerfile

### 2.1 创建简单 Web 应用

进入 `simple-web` 目录，查看项目结构：

```bash
cd simple-web
ls -la
```

### 2.2 查看基础 Dockerfile

```bash
cat Dockerfile
```

理解 Dockerfile 中的每个指令：
- `FROM`: 指定基础镜像
- `WORKDIR`: 设置工作目录
- `COPY`: 复制文件到镜像
- `EXPOSE`: 暴露端口（仅文档说明，实际不会打开端口）

### 2.3 构建镜像

```bash
# 构建镜像
docker build -t my-simple-web:1.0 .

# 构建过程说明：
# - `-t my-simple-web:1.0`: 指定镜像名称和标签
# - `.`: 指定构建上下文（当前目录）

# 查看构建过程
# 注意观察：
# 1. 每个指令都会创建一个新的层
# 2. 如果层已经存在（缓存），会显示 "CACHED"
# 3. 构建过程会显示每个步骤的执行结果
```

### 2.4 运行容器

```bash
# 运行容器
docker run -d --name web-test -p 8080:80 my-simple-web:1.0

# 参数说明：
# - `-d`: 后台运行
# - `--name web-test`: 指定容器名称
# - `-p 8080:80`: 端口映射（宿主机:容器）
# - `my-simple-web:1.0`: 镜像名称和标签

# 查看容器状态
docker ps

# 访问应用
# 浏览器打开：http://localhost:8080
# 或使用 curl：
curl http://localhost:8080
```

### 2.5 查看容器日志

```bash
# 查看容器日志
docker logs web-test

# 实时查看日志
docker logs -f web-test
```

### 2.6 对比优化版 Dockerfile

```bash
# 查看优化版 Dockerfile
cat Dockerfile.optimized

# 构建优化版镜像
docker build -f Dockerfile.optimized -t my-simple-web:optimized .

# 对比镜像大小
docker images | grep my-simple-web

# 运行优化版容器
docker run -d --name web-test-opt -p 8081:80 my-simple-web:optimized

# 访问：http://localhost:8081
```

### 2.7 清理

```bash
# 停止并删除容器
docker stop web-test web-test-opt
docker rm web-test web-test-opt

# 删除镜像（可选）
docker rmi my-simple-web:1.0 my-simple-web:optimized
```

---

## 实验 3：Node.js 应用容器化

### 3.1 查看项目结构

```bash
cd ../nodejs-app
ls -la
cat package.json
cat app.js
```

### 3.2 构建基础版镜像

```bash
# 查看基础 Dockerfile
cat Dockerfile

# 构建基础版镜像
docker build -f Dockerfile -t nodejs-app:basic .

# 观察构建过程：
# 1. 下载基础镜像 node:18
# 2. 复制 package.json
# 3. 运行 npm install（这一步可能较慢）
# 4. 复制应用代码
# 5. 设置启动命令

# 查看镜像大小
docker images | grep nodejs-app
```

### 3.3 运行基础版容器

```bash
# 运行容器
docker run -d --name nodejs-basic -p 3000:3000 nodejs-app:basic

# 等待几秒让应用启动
sleep 3

# 测试应用
curl http://localhost:3000

# 查看日志
docker logs nodejs-basic
```

### 3.4 理解 .dockerignore

```bash
# 查看 .dockerignore 文件
cat .dockerignore

# .dockerignore 的作用：
# - 排除不需要的文件，减少构建上下文大小
# - 加快构建速度
# - 避免将敏感信息（如 .env）复制到镜像中

# 测试：查看构建上下文大小
# 不使用 .dockerignore 时，node_modules 会被发送到 Docker daemon
# 使用 .dockerignore 后，node_modules 被排除
```

### 3.5 构建优化版镜像

```bash
# 查看优化版 Dockerfile
cat Dockerfile.optimized

# 理解多阶段构建：
# 阶段 1 (builder): 安装依赖
# 阶段 2 (最终镜像): 只复制运行时需要的文件

# 构建优化版镜像
docker build -f Dockerfile.optimized -t nodejs-app:optimized .

# 对比镜像大小
docker images | grep nodejs-app

# 优化版应该比基础版小很多（因为使用了 Alpine 和多阶段构建）
```

### 3.6 运行优化版容器

```bash
# 停止基础版容器
docker stop nodejs-basic
docker rm nodejs-basic

# 运行优化版容器
docker run -d --name nodejs-opt -p 3000:3000 nodejs-app:optimized

# 测试应用
curl http://localhost:3000

# 查看容器信息（注意用户是 nodejs，不是 root）
docker exec nodejs-opt whoami
```

### 3.7 理解镜像缓存

```bash
# 修改 app.js（例如修改返回消息）
# 然后重新构建

# 第一次构建（无缓存）
docker build -f Dockerfile.optimized -t nodejs-app:optimized .

# 观察：哪些步骤使用了缓存（CACHED），哪些重新执行

# 只修改 app.js，不修改 package.json
# 重新构建时，npm install 步骤应该使用缓存
```

### 3.8 清理

```bash
docker stop nodejs-opt
docker rm nodejs-opt
```

---

## 实验 4：Python 应用容器化

### 4.1 查看项目结构

```bash
cd ../python-app
ls -la
cat requirements.txt
cat app.py
```

### 4.2 构建基础版镜像

```bash
# 查看基础 Dockerfile
cat Dockerfile

# 构建基础版镜像
docker build -f Dockerfile -t python-app:basic .

# 查看镜像大小
docker images | grep python-app
```

### 4.3 运行基础版容器

```bash
# 运行容器
docker run -d --name python-basic -p 5000:5000 python-app:basic

# 等待应用启动
sleep 3

# 测试应用
curl http://localhost:5000

# 查看日志
docker logs python-basic
```

### 4.4 构建优化版镜像

```bash
# 查看优化版 Dockerfile
cat Dockerfile.optimized

# 构建优化版镜像
docker build -f Dockerfile.optimized -t python-app:optimized .

# 对比镜像大小
docker images | grep python-app

# 优化版使用 slim 版本，应该小很多
```

### 4.5 运行优化版容器

```bash
# 停止基础版
docker stop python-basic
docker rm python-basic

# 运行优化版
docker run -d --name python-opt -p 5000:5000 python-app:optimized

# 测试应用
curl http://localhost:5000

# 查看容器用户（应该是 appuser，不是 root）
docker exec python-opt whoami
```

### 4.6 清理

```bash
docker stop python-opt
docker rm python-opt
```

---

## 实验 5：Java 应用容器化

### 5.1 查看项目结构

```bash
cd ../java-app
ls -la
cat pom.xml
tree src/  # 如果没有 tree 命令，使用 find src/
```

### 5.2 构建基础版镜像

```bash
# 查看基础 Dockerfile
cat Dockerfile

# 注意：基础版已经使用了多阶段构建
# 但使用的是完整的 JRE 镜像

# 构建基础版镜像（首次构建需要下载 Maven 依赖，可能需要 2-5 分钟）
docker build -f Dockerfile -t java-app:basic .

# 观察构建过程：
# 1. 下载 Maven 和 JDK 镜像
# 2. 下载项目依赖（Maven）
# 3. 编译 Java 代码
# 4. 打包成 JAR
# 5. 复制到运行镜像

# 查看镜像大小
docker images | grep java-app
```

### 5.3 运行基础版容器

```bash
# 运行容器
docker run -d --name java-basic -p 8080:8080 java-app:basic

# 等待应用启动（Spring Boot 启动需要时间）
sleep 15

# 测试应用
curl http://localhost:8080

# 查看日志
docker logs java-basic
```

### 5.4 构建优化版镜像

```bash
# 查看优化版 Dockerfile
cat Dockerfile.optimized

# 优化点：
# 1. 使用 Alpine 版本的 JRE（更小）
# 2. 使用非 root 用户
# 3. 优化 JVM 参数

# 构建优化版镜像
docker build -f Dockerfile.optimized -t java-app:optimized .

# 对比镜像大小
docker images | grep java-app

# 优化版应该比基础版小 30-50MB
```

### 5.5 运行优化版容器

```bash
# 停止基础版
docker stop java-basic
docker rm java-basic

# 运行优化版
docker run -d --name java-opt -p 8080:8080 java-app:optimized

# 等待应用启动
sleep 15

# 测试应用
curl http://localhost:8080

# 查看容器用户
docker exec java-opt whoami
```

### 5.6 理解依赖缓存

```bash
# 修改 Java 源代码（例如修改返回消息）
# 但不修改 pom.xml

# 重新构建
docker build -f Dockerfile.optimized -t java-app:optimized .

# 观察：
# - dependency:go-offline 步骤应该使用缓存
# - 只有编译和打包步骤重新执行
```

### 5.7 清理

```bash
docker stop java-opt
docker rm java-opt
```

---

## 实验 6：镜像优化实践

### 6.1 对比所有镜像大小

```bash
# 查看所有构建的镜像
docker images | grep -E "(my-simple-web|nodejs-app|python-app|java-app)"

# 使用表格格式查看
docker images --format "table {{.Repository}}\t{{.Tag}}\t{{.Size}}" | grep -E "(REPOSITORY|my-simple-web|nodejs-app|python-app|java-app)"
```

### 6.2 分析镜像层

```bash
# 查看 Node.js 优化版镜像的层
docker history nodejs-app:optimized

# 查看 Java 优化版镜像的层
docker history java-app:optimized

# 理解：
# - 每个 RUN、COPY、ADD 指令都会创建一个新层
# - 层是只读的，可以共享
# - 优化 Dockerfile 的顺序可以最大化缓存利用
```

### 6.3 镜像大小优化技巧总结

1. **使用 Alpine 或 Slim 版本的基础镜像**
   ```dockerfile
   # 大
   FROM node:18
   # 小
   FROM node:18-alpine
   ```

2. **多阶段构建**
   ```dockerfile
   # 只保留运行时需要的文件
   FROM builder AS build
   # ...
   FROM runtime
   COPY --from=build /app/dist /app
   ```

3. **合并 RUN 指令**
   ```dockerfile
   # 不好：创建多个层
   RUN apt-get update
   RUN apt-get install -y package1
   RUN apt-get install -y package2
   
   # 好：一个层
   RUN apt-get update && \
       apt-get install -y package1 package2 && \
       apt-get clean
   ```

4. **使用 .dockerignore**
   - 排除 node_modules、.git、构建产物等

5. **清理缓存和临时文件**
   ```dockerfile
   RUN npm install && npm cache clean --force
   ```

### 6.4 构建上下文优化

```bash
# 查看构建上下文大小
# 在项目目录下运行
du -sh .

# 使用 .dockerignore 后
# 构建时发送到 Docker daemon 的数据会减少

# 查看 Docker daemon 日志中的构建上下文大小
# （需要启用详细日志）
```

### 6.5 镜像扫描和安全

```bash
# 查看镜像的详细信息
docker inspect java-app:optimized

# 检查镜像使用的用户
docker exec java-opt id

# 最佳实践：
# - 使用非 root 用户运行容器
# - 定期更新基础镜像
# - 扫描镜像漏洞（可以使用 docker scan 或第三方工具）
```

---

## 综合实践：完整应用部署

### 实践项目：构建和运行所有应用

```bash
# 回到项目根目录
cd /path/to/stage2-practice

# 构建所有优化版镜像
cd simple-web && docker build -f Dockerfile.optimized -t my-simple-web:latest .
cd ../nodejs-app && docker build -f Dockerfile.optimized -t nodejs-app:latest .
cd ../python-app && docker build -f Dockerfile.optimized -t python-app:latest .
cd ../java-app && docker build -f Dockerfile.optimized -t java-app:latest .

# 运行所有应用
docker run -d --name web -p 8080:80 my-simple-web:latest
docker run -d --name nodejs -p 3000:3000 nodejs-app:latest
docker run -d --name python -p 5000:5000 python-app:latest
docker run -d --name java -p 8081:8080 java-app:latest

# 测试所有应用
curl http://localhost:8080
curl http://localhost:3000
curl http://localhost:5000
curl http://localhost:8081

# 查看所有容器状态
docker ps

# 查看资源使用情况
docker stats
```

---

## 清理所有资源

```bash
# 停止所有容器
docker stop $(docker ps -aq)

# 删除所有容器
docker rm $(docker ps -aq)

# 删除所有测试镜像
docker rmi my-simple-web:1.0 my-simple-web:optimized my-simple-web:latest
docker rmi nodejs-app:basic nodejs-app:optimized nodejs-app:latest
docker rmi python-app:basic python-app:optimized python-app:latest
docker rmi java-app:basic java-app:optimized java-app:latest

# 清理未使用的镜像和构建缓存
docker system prune -a

# 查看清理后的磁盘使用情况
docker system df
```

---

## 学习检查清单

完成以下任务后，在 plan.md 中勾选对应的项目：

- [ ] 能够使用 `docker search` 搜索镜像
- [ ] 能够使用 `docker pull` 下载镜像
- [ ] 能够使用 `docker tag` 给镜像打标签
- [ ] 理解镜像的分层结构
- [ ] 能够编写简单的 Dockerfile
- [ ] 理解 Dockerfile 基本指令（FROM, RUN, COPY, WORKDIR, EXPOSE, ENV, CMD）
- [ ] 能够为静态 Web 应用编写 Dockerfile
- [ ] 能够为 Node.js 应用编写 Dockerfile
- [ ] 能够为 Python 应用编写 Dockerfile
- [ ] 能够为 Java 应用编写 Dockerfile
- [ ] 理解多阶段构建的优势
- [ ] 能够使用 .dockerignore 优化构建
- [ ] 理解镜像缓存机制
- [ ] 能够优化镜像大小

---

## 常见问题

### Q1: 为什么我的镜像构建很慢？

**A:** 可能的原因：
1. 首次构建需要下载基础镜像
2. 网络速度慢，下载依赖慢
3. 构建上下文太大
4. 没有使用缓存

**解决方案：**
- 使用国内镜像源
- 使用 .dockerignore 减少构建上下文
- 优化 Dockerfile 顺序以最大化缓存利用

### Q2: 如何查看镜像占用的磁盘空间？

**A:** 
```bash
# 查看所有镜像大小
docker images

# 查看详细磁盘使用情况
docker system df -v
```

### Q3: 多阶段构建真的能减小镜像大小吗？

**A:** 是的！多阶段构建可以：
- 排除构建工具（如 Maven、npm）
- 只保留运行时需要的文件
- 通常可以减少 50-80% 的镜像大小

### Q4: Alpine 镜像和标准镜像有什么区别？

**A:**
- **Alpine**: 基于 Alpine Linux，非常小（通常 < 5MB），使用 musl libc
- **标准镜像**: 基于 Debian/Ubuntu，较大（通常 50-200MB），功能更全

对于生产环境，Alpine 通常足够，除非有特殊兼容性要求。

### Q5: 如何推送镜像到 Docker Hub？

**A:**
```bash
# 登录 Docker Hub
docker login

# 给镜像打标签（格式：username/repository:tag）
docker tag my-app:latest username/my-app:latest

# 推送镜像
docker push username/my-app:latest
```

### Q6: 构建时如何传递构建参数？

**A:** 使用 ARG 指令：
```dockerfile
ARG VERSION=latest
FROM node:${VERSION}
```

构建时传递：
```bash
docker build --build-arg VERSION=18 -t my-app .
```

---

## 下一步

完成本阶段实践后，可以继续学习：
- 第三阶段：Docker 数据管理
- 第四阶段：Docker 网络
- 第五阶段：Docker Compose

---

## 扩展练习

1. **为你的实际项目编写 Dockerfile**
   - 选择一个你熟悉的技术栈
   - 编写基础版和优化版 Dockerfile
   - 对比镜像大小和构建时间

2. **探索更多优化技巧**
   - 研究 distroless 镜像
   - 学习使用 BuildKit 加速构建
   - 了解镜像安全扫描工具

3. **搭建私有镜像仓库**
   - 使用 Docker Registry
   - 配置镜像推送和拉取
   - 设置访问控制

