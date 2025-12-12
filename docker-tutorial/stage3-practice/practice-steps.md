# 第三阶段实践步骤详解

## 前置准备

确保你已经完成：
- [ ] Docker 已安装并运行
- [ ] 熟悉基本的 Docker 命令
- [ ] 了解 Docker 容器和镜像的基本概念

---

## 实验 1：数据卷（Volumes）基础操作

### 1.1 创建数据卷

```bash
# 创建一个名为 mydata 的数据卷
docker volume create mydata

# 查看所有数据卷
docker volume ls

# 查看数据卷详细信息
docker volume inspect mydata
```

### 1.2 使用数据卷运行容器

```bash
# 运行一个 Nginx 容器，挂载数据卷到 /usr/share/nginx/html
docker run -d \
  --name nginx-test \
  -v mydata:/usr/share/nginx/html \
  -p 8080:80 \
  nginx:latest

# 进入容器查看挂载点
docker exec -it nginx-test ls -la /usr/share/nginx/html
```

### 1.3 在数据卷中创建文件

```bash
# 创建一个临时容器，挂载同一个数据卷，写入测试文件
docker run --rm \
  -v mydata:/data \
  alpine sh -c "echo 'Hello from Volume' > /data/test.txt"

# 再次查看 Nginx 容器中的文件
docker exec -it nginx-test cat /usr/share/nginx/html/test.txt
```

### 1.4 验证数据持久化

```bash
# 停止并删除容器
docker stop nginx-test
docker rm nginx-test

# 数据卷仍然存在
docker volume ls

# 重新运行容器，数据仍然存在
docker run -d \
  --name nginx-test2 \
  -v mydata:/usr/share/nginx/html \
  -p 8080:80 \
  nginx:latest

docker exec -it nginx-test2 cat /usr/share/nginx/html/test.txt
```

### 1.5 清理

```bash
docker stop nginx-test2
docker rm nginx-test2
docker volume rm mydata
```

---

## 实验 2：MySQL 数据库数据持久化

### 2.1 运行 MySQL 容器（无数据卷）

```bash
# 运行 MySQL 容器，不挂载数据卷
docker run -d \
  --name mysql-no-volume \
  -e MYSQL_ROOT_PASSWORD=123456 \
  -e MYSQL_DATABASE=testdb \
  -p 3306:3306 \
  mysql:8.0

# 等待几秒让 MySQL 启动
sleep 10

# 创建一些测试数据
docker exec -it mysql-no-volume mysql -uroot -p123456 -e \
  "use testdb; \
   CREATE TABLE users (id INT PRIMARY KEY, name VARCHAR(50)); \
   INSERT INTO users VALUES (1, 'Alice'), (2, 'Bob'); \
   SELECT * FROM users;"
```

### 2.2 删除容器并重新创建（数据丢失）

```bash
# 停止并删除容器
docker stop mysql-no-volume
docker rm mysql-no-volume

# 重新运行容器
docker run -d \
  --name mysql-no-volume2 \
  -e MYSQL_ROOT_PASSWORD=123456 \
  -e MYSQL_DATABASE=testdb \
  -p 3306:3306 \
  mysql:8.0

sleep 10

# 查看数据（应该不存在）
docker exec -it mysql-no-volume2 mysql -uroot -p123456 -e \
  "USE testdb; SHOW TABLES;"
# 结果：Empty set（数据已丢失）

docker stop mysql-no-volume2
docker rm mysql-no-volume2
```

### 2.3 使用数据卷运行 MySQL

```bash
# 创建数据卷
docker volume create mysql-data

# 运行 MySQL 容器，挂载数据卷
docker run -d \
  --name mysql-with-volume \
  -e MYSQL_ROOT_PASSWORD=123456 \
  -e MYSQL_DATABASE=testdb \
  -v mysql-data:/var/lib/mysql \
  -p 3307:3306 \
  mysql:8.0

sleep 10

# 创建测试数据
docker exec -it mysql-with-volume mysql -uroot -p123456 -e \
  "USE testdb; \
   CREATE TABLE users (id INT PRIMARY KEY, name VARCHAR(50)); \
   INSERT INTO users VALUES (1, 'Alice'), (2, 'Bob'), (3, 'Charlie'); \
   SELECT * FROM users;"
```

### 2.4 验证数据持久化

```bash
# 停止并删除容器
docker stop mysql-with-volume
docker rm mysql-with-volume

# 重新运行容器，使用相同的数据卷
docker run -d \
  --name mysql-with-volume2 \
  -e MYSQL_ROOT_PASSWORD=123456 \
  -e MYSQL_DATABASE=testdb \
  -v mysql-data:/var/lib/mysql \
  -p 3307:3306 \
  mysql:8.0

sleep 10

# 查看数据（应该仍然存在）
docker exec -it mysql-with-volume2 mysql -uroot -p123456 -e \
  "USE testdb; SELECT * FROM users;"
# 结果：应该能看到之前插入的 3 条记录
```

### 2.5 查看数据卷位置

```bash
# 查看数据卷详细信息
docker volume inspect mysql-data

# 在 Windows 上，数据卷通常存储在：
# \\wsl$\docker-desktop-data\data\docker\volumes\mysql-data\_data
```

---

## 实验 3：绑定挂载（Bind Mounts）开发实践

### 3.1 准备 Web 应用代码

首先创建项目文件（已在本项目中提供）。这是一个 Spring Boot 应用，使用 Java 17 和 Maven。

### 3.2 使用绑定挂载运行开发环境

```bash
# 进入 webapp 目录
cd webapp

# 使用绑定挂载运行 Spring Boot 应用（开发模式）
docker run -d \
  --name springboot-dev \
  -v ${PWD}:/app \
  -w /app \
  -p 8080:8080 \
  -v maven-cache:/root/.m2 \
  maven:3.9-eclipse-temurin-17 \
  mvn spring-boot:run

# 或者使用 Docker Compose（推荐）
cd ..
docker-compose up -d webapp
```

### 3.3 修改代码验证热更新

```bash
# 查看容器日志
docker logs -f springboot-dev

# 在另一个终端，修改 Java 代码文件
# 例如：修改 HomeController.java 中的返回消息
# Spring Boot DevTools 会自动检测更改并重启应用

# 访问 http://localhost:8080 查看更改
# 注意：首次启动需要下载 Maven 依赖，可能需要几分钟
```

### 3.4 挂载配置文件

```bash
# 创建 Nginx 配置文件
mkdir -p nginx-config
cat > nginx-config/nginx.conf <<EOF
server {
    listen 80;
    server_name localhost;
    
    location / {
        proxy_pass http://host.docker.internal:8080;
    }
}
EOF

# 使用绑定挂载运行 Nginx
docker run -d \
  --name nginx-config-test \
  -v ${PWD}/nginx-config/nginx.conf:/etc/nginx/conf.d/default.conf:ro \
  -p 8081:80 \
  nginx:latest

# 修改配置文件，重启容器查看效果
docker restart nginx-config-test
```

### 3.5 清理

```bash
docker stop springboot-dev nginx-config-test
docker rm springboot-dev nginx-config-test
```

---

## 实验 4：数据备份和恢复

### 4.1 备份 MySQL 数据

```bash
# 确保 MySQL 容器正在运行（使用之前创建的 mysql-with-volume2）
# 如果不存在，先创建：
docker run -d \
  --name mysql-backup-test \
  -e MYSQL_ROOT_PASSWORD=123456 \
  -e MYSQL_DATABASE=testdb \
  -v mysql-data:/var/lib/mysql \
  -p 3308:3306 \
  mysql:8.0

sleep 10

# 创建一些测试数据
docker exec -it mysql-backup-test mysql -uroot -p123456 -e \
  "USE testdb; \
   INSERT INTO users VALUES (4, 'David'), (5, 'Eve'); \
   SELECT * FROM users;"

# 方法1：使用 mysqldump 备份
docker exec mysql-backup-test mysqldump -uroot -p123456 testdb > backup.sql

# 方法2：直接备份数据卷
docker run --rm \
  -v mysql-data:/data \
  -v ${PWD}:/backup \
  alpine tar czf /backup/mysql-backup-$(date +%Y%m%d).tar.gz -C /data .
```

### 4.2 恢复数据（方法1：使用 mysqldump）

```bash
# 停止并删除原容器
docker stop mysql-backup-test
docker rm mysql-backup-test

# 删除数据卷（模拟数据丢失）
docker volume rm mysql-data

# 创建新的数据卷和容器
docker volume create mysql-data
docker run -d \
  --name mysql-restored \
  -e MYSQL_ROOT_PASSWORD=123456 \
  -e MYSQL_DATABASE=testdb \
  -v mysql-data:/var/lib/mysql \
  -p 3308:3306 \
  mysql:8.0

sleep 10

# 恢复数据
docker exec -i mysql-restored mysql -uroot -p123456 testdb < backup.sql

# 验证数据
docker exec -it mysql-restored mysql -uroot -p123456 -e \
  "USE testdb; SELECT * FROM users;"
```

### 4.3 恢复数据（方法2：从数据卷备份恢复）

```bash
# 停止容器
docker stop mysql-restored
docker rm mysql-restored
docker volume rm mysql-data

# 创建新数据卷
docker volume create mysql-data

# 恢复数据卷
docker run --rm \
  -v mysql-data:/data \
  -v ${PWD}:/backup \
  alpine sh -c "cd /data && tar xzf /backup/mysql-backup-*.tar.gz"

# 启动新容器
docker run -d \
  --name mysql-restored2 \
  -e MYSQL_ROOT_PASSWORD=123456 \
  -e MYSQL_DATABASE=testdb \
  -v mysql-data:/var/lib/mysql \
  -p 3308:3306 \
  mysql:8.0

sleep 10

# 验证数据
docker exec -it mysql-restored2 mysql -uroot -p123456 -e \
  "USE testdb; SELECT * FROM users;"
```

### 4.4 使用备份脚本

```bash
# 使用提供的备份脚本
cd mysql
chmod +x backup.sh
./backup.sh mysql-backup-test testdb 123456
```

---

## 实验 5：tmpfs 临时文件系统

### 5.1 使用 tmpfs 运行容器

```bash
# 运行容器，使用 tmpfs 挂载到 /tmp
docker run -d \
  --name tmpfs-test \
  --tmpfs /tmp:rw,noexec,nosuid,size=100m \
  alpine sh -c "while true; do sleep 3600; done"

# 进入容器创建文件
docker exec -it tmpfs-test sh -c "echo 'test' > /tmp/test.txt && ls -lh /tmp/"

# 查看 tmpfs 挂载信息
docker exec -it tmpfs-test df -h /tmp
```

### 5.2 验证 tmpfs 特性

```bash
# tmpfs 中的数据在容器停止后会丢失
docker stop tmpfs-test
docker start tmpfs-test

# 文件已不存在
docker exec -it tmpfs-test ls -la /tmp/test.txt
# 结果：No such file or directory
```

### 5.3 使用场景示例：Redis 缓存

```bash
# Redis 可以使用 tmpfs 存储临时数据（不推荐用于生产环境）
docker run -d \
  --name redis-tmpfs \
  --tmpfs /data:rw,noexec,nosuid,size=200m \
  -p 6379:6379 \
  redis:latest \
  redis-server --appendonly no

# 测试 Redis
docker exec -it redis-tmpfs redis-cli set test "hello"
docker exec -it redis-tmpfs redis-cli get test

# 重启容器，数据丢失（因为 appendonly=no 且使用 tmpfs）
docker restart redis-tmpfs
docker exec -it redis-tmpfs redis-cli get test
# 结果：(nil)
```

---

## 练手项目建议

### 项目 1：Spring Boot + MySQL 数据卷实战

1. **目标**：搭建 Spring Boot 用户服务 + MySQL，重点演练命名数据卷、备份与恢复。
2. **项目结构**：
   - `user-service/`：Spring Boot 3、Spring Data JPA、Flyway 初始化 schema。
   - `docker/`：存放 `Dockerfile`、`docker-compose.yml`、备份脚本。
3. **实施步骤**：
   ```bash
   # 构建应用镜像
   cd user-service
   docker build -t user-service:stage3 .

   # 创建数据卷并用 Compose 启动
   cd ../docker
   docker volume create user-mysql-data
   docker compose up -d

   # 验证 CRUD
   curl -X POST http://localhost:8080/users -d '{"name":"Alice"}' -H "Content-Type: application/json"
   curl http://localhost:8080/users

   # 模拟宕机并恢复
   docker compose down
   docker compose up -d
   curl http://localhost:8080/users   # 数据仍在

   # 使用 mysqldump + 数据卷备份双保险
   ./scripts/backup.sh mysql user_mgmt 123456
   ```
4. **关键检查点**：
   - Compose 中 `volumes` 区块分别声明命名卷与本地备份目录。
   - Spring Boot 配置中使用 `SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/user_mgmt`，确保服务名解析。
   - 记录一次真实的恢复流程：删除容器 -> 重新拉起 -> 导入备份。

### 项目 2：日志聚合与 tmpfs 缓冲

1. **目标**：实现 "应用容器 -> Fluent Bit -> 对象存储" 的日志通道，演练绑定挂载与 tmpfs。
2. **项目结构**：
   - `order-service/`：Spring Boot，向 STDOUT 打结构化日志。
   - `logging/`：Fluent Bit 配置，包含 `fluent-bit.conf`、`parsers.conf`。
   - `docker-compose.yml`：定义 `order-service`、`fluent-bit`、`minio` 三个服务。
3. **实施步骤**：
   ```bash
   # 启动
   docker compose up -d

   # order-service 绑定宿主机源码目录，便于热更新
   # Fluent Bit 使用 tmpfs 作为缓冲，避免日志泄露到磁盘
   docker compose exec order-service curl -X POST http://localhost:8081/orders

   # 验证 Fluent Bit 将日志写入 MinIO
   mc alias set local http://localhost:9000 minioadmin minioadmin
   mc ls local/log-bucket
   ```
4. **Docker Compose 关键片段**：
   ```yaml
   services:
     order-service:
       build: ./order-service
       volumes:
         - ./order-service:/app
       environment:
         - JAVA_TOOL_OPTIONS=-XX:InitialRAMPercentage=40 -XX:MaxRAMPercentage=60
       ports:
         - "8081:8080"
     fluent-bit:
       image: fluent/fluent-bit:2.2
       volumes:
         - ./logging/fluent-bit.conf:/fluent-bit/etc/fluent-bit.conf:ro
         - type: tmpfs
           target: /buffers
           tmpfs:
             size: 64m
       command: ["/fluent-bit/bin/fluent-bit","-c","/fluent-bit/etc/fluent-bit.conf"]
   ```
5. **关键检查点**：
   - 绑定挂载的配置文件能热更新，修改 `fluent-bit.conf` 后 `docker compose restart fluent-bit` 即可。
   - tmpfs 在容器重启后自动清空，验证日志不会残留。
   - MinIO 数据目录可以使用命名卷 `minio-data`，确保对象数据具备持久性。

---

## 综合实践：完整应用部署

### 使用 Docker Compose 部署完整应用

```bash
# 使用项目提供的 docker-compose.yml
cd ..
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f

# 访问应用
# Web: http://localhost:8080
# Nginx 代理: http://localhost:80
# MySQL: localhost:3306
```

---

## 清理所有资源

```bash
# 停止所有容器
docker stop $(docker ps -aq)

# 删除所有容器
docker rm $(docker ps -aq)

# 删除所有数据卷（谨慎操作！）
docker volume rm $(docker volume ls -q)

# 或使用 Docker Compose 清理
docker-compose down -v
```

---

## 学习检查清单

完成以下任务后，在 plan.md 中勾选对应的项目：

- [ ] 能够创建和管理数据卷
- [ ] 理解数据卷与绑定挂载的区别
- [ ] 能够为数据库容器配置数据卷
- [ ] 能够进行数据备份和恢复
- [ ] 理解 tmpfs 的使用场景
- [ ] 能够使用绑定挂载进行开发
- [ ] 能够挂载配置文件到容器

---

## 常见问题

### Q1: 数据卷在哪里存储？
**A:** 在 Windows 上，Docker Desktop 使用 WSL2，数据卷存储在：
`\\wsl$\docker-desktop-data\data\docker\volumes\<volume-name>\_data`

### Q2: 绑定挂载和数据卷有什么区别？
**A:** 
- **数据卷**：由 Docker 管理，存储在 Docker 的存储目录中，可以跨容器共享
- **绑定挂载**：直接挂载宿主机的文件或目录，路径由用户指定

### Q3: 如何查看数据卷占用的空间？
**A:** 使用 `docker system df -v` 查看详细信息

### Q4: 可以同时使用多个数据卷吗？
**A:** 可以，一个容器可以挂载多个数据卷到不同的路径

---

## 下一步

完成本阶段实践后，可以继续学习：
- 第四阶段：Docker 网络
- 第五阶段：Docker Compose（本项目中已包含基础示例）
