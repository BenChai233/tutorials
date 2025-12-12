# 快速开始指南

本指南将帮助你快速启动和运行第七阶段的实践项目。

## 前置要求

- Docker 20.10+ 和 Docker Compose 2.0+
- Java 17 和 Maven 3.8+（用于本地开发）
- 至少 4GB 可用内存
- 至少 10GB 可用磁盘空间

## 方式一：使用 Docker Compose（推荐用于开发和学习）

### 1. 准备环境

```bash
# 进入项目目录
cd stage7-practice

# 复制环境变量文件
cp .env.example .env

# 根据需要编辑 .env 文件
# 默认配置通常可以直接使用
```

### 2. 构建和启动服务

```bash
# 构建并启动所有服务
docker-compose up -d --build

# 查看服务状态
docker-compose ps

# 查看服务日志
docker-compose logs -f
```

### 3. 验证服务

```bash
# 检查 API 网关
curl http://localhost:8080/actuator/health

# 检查用户服务
curl http://localhost:8081/actuator/health

# 检查订单服务
curl http://localhost:8082/actuator/health

# 测试 API（通过网关）
curl http://localhost:8080/api/users
curl http://localhost:8080/api/orders
```

### 4. 访问应用

- **API 网关**: http://localhost:8080
- **用户服务**: http://localhost:8081
- **订单服务**: http://localhost:8082
- **MySQL**: localhost:3306
- **健康检查端点**: http://localhost:8080/actuator/health

### 5. 停止服务

```bash
# 停止所有服务
docker-compose down

# 停止并删除数据卷（注意：会删除数据）
docker-compose down -v
```

## 方式二：使用 Docker Swarm（生产环境模拟）

### 1. 初始化 Swarm

```bash
# 使用脚本初始化（单节点模式，用于学习）
./scripts/swarm-init.sh

# 或手动初始化
docker swarm init

# 查看 Swarm 状态
docker info | grep Swarm
```

### 2. 部署 Stack

```bash
# 使用脚本部署
./scripts/swarm-deploy.sh

# 或手动部署
docker stack deploy -c docker-stack.yml stage7

# 查看 Stack 服务
docker stack services stage7

# 查看服务详情
docker service ls
docker service ps stage7_user-service
```

### 3. 扩展服务

```bash
# 扩展用户服务到 3 个副本
docker service scale stage7_user-service=3

# 扩展订单服务到 2 个副本
docker service scale stage7_order-service=2

# 查看服务状态
docker service ps stage7_user-service
```

### 4. 更新服务

```bash
# 更新服务镜像
docker service update --image user-service:new-version stage7_user-service

# 滚动更新（逐步更新）
docker service update --update-parallelism 1 --update-delay 10s stage7_user-service
```

### 5. 查看日志

```bash
# 查看服务日志
docker service logs stage7_user-service

# 实时跟踪日志
docker service logs -f stage7_user-service

# 查看特定任务的日志
docker service logs stage7_user-service --task-id <task-id>
```

### 6. 移除 Stack

```bash
# 移除整个 Stack
docker stack rm stage7

# 等待服务完全停止
docker stack services stage7
```

## 方式三：本地开发（不使用 Docker）

### 1. 启动 MySQL

```bash
# 使用 Docker 启动 MySQL
docker run -d \
  --name mysql-dev \
  -e MYSQL_ROOT_PASSWORD=123456 \
  -e MYSQL_DATABASE=testdb \
  -e MYSQL_USER=appuser \
  -e MYSQL_PASSWORD=apppass \
  -p 3306:3306 \
  mysql:8.0
```

### 2. 运行服务

```bash
# 启动用户服务
cd user-service
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 启动订单服务（新终端）
cd order-service
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 启动网关服务（新终端）
cd gateway-service
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## 监控和调试

### 查看容器详情

```bash
# 使用脚本查看容器详情
./scripts/inspect-container.sh <container-name>

# 或手动查看
docker inspect <container-name>
docker inspect <container-name> | grep -A 20 "State"
```

### 监控资源使用

```bash
# 使用脚本监控
./scripts/monitor-containers.sh

# 或使用 docker stats
docker stats

# 查看特定容器
docker stats <container-name>
```

### 故障排查

```bash
# 使用故障排查脚本
./scripts/troubleshoot.sh

# 查看容器日志
docker-compose logs <service-name>
docker service logs <service-name>

# 进入容器调试
docker-compose exec <service-name> sh
docker exec -it <container-name> sh
```

## 数据库备份和恢复

### 备份数据库

```bash
# 使用备份脚本
./scripts/backup-database.sh

# 手动备份
docker-compose exec mysql mysqldump -u root -p123456 testdb > backup.sql
```

### 恢复数据库

```bash
# 使用恢复脚本
./scripts/restore-database.sh backup.sql

# 手动恢复
docker-compose exec -T mysql mysql -u root -p123456 testdb < backup.sql
```

## CI/CD 使用

### Jenkins Pipeline

```bash
# 1. 安装 Jenkins（使用 Docker）
docker run -d \
  --name jenkins \
  -p 8080:8080 \
  -p 50000:50000 \
  -v jenkins_home:/var/jenkins_home \
  jenkins/jenkins:lts

# 2. 访问 Jenkins
# http://localhost:8080
# 获取初始密码：docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword

# 3. 创建 Pipeline 任务
# - 新建任务 -> Pipeline
# - Pipeline 定义选择 "Pipeline script from SCM"
# - SCM 选择 Git，配置仓库地址
# - Script Path 设置为 Jenkinsfile
```

### GitLab CI

```bash
# 1. 将项目推送到 GitLab 仓库

# 2. 配置 GitLab Runner（如果还没有）
# 参考：https://docs.gitlab.com/runner/install/

# 3. 提交代码后，GitLab CI 会自动运行
# 查看 Pipeline：项目 -> CI/CD -> Pipelines
```

## 常见问题

### 1. 端口冲突

如果端口被占用，修改 `.env` 文件中的端口配置：

```bash
WEBAPP_PORT=8080
USER_SERVICE_PORT=8081
ORDER_SERVICE_PORT=8082
```

### 2. 服务启动失败

```bash
# 查看服务日志
docker-compose logs <service-name>

# 检查服务健康状态
docker-compose ps

# 重启服务
docker-compose restart <service-name>
```

### 3. 数据库连接失败

```bash
# 检查 MySQL 是否启动
docker-compose ps mysql

# 检查数据库日志
docker-compose logs mysql

# 测试数据库连接
docker-compose exec mysql mysql -u appuser -papppass testdb
```

### 4. Swarm 初始化失败

```bash
# 检查 Docker 版本（需要 1.12+）
docker version

# 检查是否已有 Swarm
docker info | grep Swarm

# 离开现有 Swarm（如果需要）
docker swarm leave --force
```

## 下一步

- 阅读 [practice-steps.md](practice-steps.md) 了解详细的实践步骤
- 学习 Docker Swarm 的高级特性
- 配置 CI/CD 自动化流程
- 实现监控和告警
- 优化生产环境配置

## 获取帮助

- 查看项目 README.md
- 查看 Docker 官方文档
- 查看 Spring Boot 官方文档
- 查看项目 issue（如果有）

