# 快速开始指南

## 🚀 5 分钟快速体验

### 步骤 1：验证 Docker 环境

```bash
# 检查 Docker 是否运行
docker --version
docker ps
```

### 步骤 2：实验 1 - 数据卷基础（5分钟）

```bash
# 创建数据卷
docker volume create mydata

# 运行容器并挂载数据卷
docker run -d --name nginx-test -v mydata:/usr/share/nginx/html -p 8080:80 nginx:latest

# 在数据卷中创建文件
docker run --rm -v mydata:/data alpine sh -c "echo 'Hello Docker Volume!' > /data/test.txt"

# 验证数据
docker exec nginx-test cat /usr/share/nginx/html/test.txt

# 访问 http://localhost:8080/test.txt 查看文件
```

### 步骤 3：实验 2 - MySQL 数据持久化（10分钟）

```bash
# 创建数据卷并运行 MySQL
docker volume create mysql-data
docker run -d \
  --name mysql-test \
  -e MYSQL_ROOT_PASSWORD=123456 \
  -e MYSQL_DATABASE=testdb \
  -v mysql-data:/var/lib/mysql \
  -p 3306:3306 \
  mysql:8.0

# 等待 MySQL 启动（约 10 秒）
timeout /t 10

# 创建测试数据
docker exec mysql-test mysql -uroot -p123456 -e \
  "USE testdb; CREATE TABLE users (id INT, name VARCHAR(50)); \
   INSERT INTO users VALUES (1, 'Alice'), (2, 'Bob'); \
   SELECT * FROM users;"

# 停止并删除容器
docker stop mysql-test
docker rm mysql-test

# 重新运行容器，数据仍然存在
docker run -d \
  --name mysql-test2 \
  -e MYSQL_ROOT_PASSWORD=123456 \
  -e MYSQL_DATABASE=testdb \
  -v mysql-data:/var/lib/mysql \
  -p 3306:3306 \
  mysql:8.0

timeout /t 10

# 验证数据持久化
docker exec mysql-test2 mysql -uroot -p123456 -e "USE testdb; SELECT * FROM users;"
```

### 步骤 4：实验 3 - 绑定挂载（15分钟）

```bash
# 进入 webapp 目录
cd webapp

# 使用绑定挂载运行 Spring Boot 应用（开发模式）
#（Windows 风格）：
docker run -d \
  --name springboot-dev \
  -v %CD%:/app \
  -w /app \
  -p 8080:8080 \
  -v maven-cache:/root/.m2 \
  maven:3.9-eclipse-temurin-17 \
  mvn spring-boot:run

# (Linux 环境）：
docker run -d \
  --name springboot-dev \
  -v $(pwd):/app \
  -w /app \
  -p 8080:8080 \
  -v maven-cache:/root/.m2 \
  maven:3.9-eclipse-temurin-17 \
  mvn spring-boot:run

# 等待应用启动（首次启动需要下载 Maven 依赖，可能需要 2-3 分钟）
timeout /t 30

# 查看日志确认启动成功
docker logs springboot-dev

# 访问 http://localhost:8080 查看应用

# 修改 Java 代码文件（例如修改 HomeController.java），Spring Boot DevTools 会自动检测并重启
```

### 步骤 5：使用 Docker Compose 部署完整应用（5分钟）

```bash
# 返回项目根目录
cd ..

# 启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f

# 访问应用
# Web 应用: http://localhost:8080
# Nginx 代理: http://localhost:80
# MySQL: localhost:3306
```

### 步骤 6：清理资源

```bash
# 停止所有容器
docker-compose down

# 删除数据卷（可选）
docker volume rm mysql-data mydata maven-cache
```

---

## 📚 详细学习

完成快速体验后，请按照 `practice-steps.md` 中的详细步骤进行系统学习。

---

## ⚠️ 注意事项

1. **Windows 路径**：在 Windows 上使用 PowerShell 时，路径变量是 `$PWD`，在 CMD 中是 `%CD%`
2. **端口冲突**：如果端口被占用，请修改 docker-compose.yml 中的端口映射
3. **数据持久化**：删除数据卷会永久删除数据，请谨慎操作
4. **权限问题**：某些操作可能需要管理员权限
5. **Maven 依赖**：首次运行 Spring Boot 应用需要下载 Maven 依赖，可能需要几分钟，请耐心等待
6. **Java 版本**：本项目使用 Java 17，确保 Docker 镜像支持

---

## 🆘 遇到问题？

1. 检查 Docker 是否正常运行：`docker ps`
2. 查看容器日志：`docker logs <container_name>`
3. 查看详细实践步骤：`practice-steps.md`
4. 查看常见问题：`practice-steps.md` 中的"常见问题"部分

---

## ✅ 完成检查

完成快速体验后，你应该能够：
- [x] 创建和使用数据卷
- [x] 为 MySQL 配置数据持久化
- [x] 使用绑定挂载进行开发
- [x] 使用 Docker Compose 部署多容器应用

现在可以开始系统学习 `practice-steps.md` 中的所有实验了！

