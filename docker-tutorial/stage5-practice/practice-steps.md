# 第五阶段实践步骤详解

## 前置准备

确保你已经完成：
- [ ] Docker 已安装并运行
- [ ] Docker Compose 已安装（Docker Desktop 自带，或单独安装）
- [ ] 熟悉基本的 Docker 命令
- [ ] 了解 Docker 容器、镜像、网络和数据卷的基本概念
- [ ] 完成第三阶段和第四阶段的学习

---

## 实验 1：Docker Compose 基础

### 1.1 验证 Docker Compose 安装

```bash
# 检查 Docker Compose 版本（V1）
docker-compose --version

# 检查 Docker Compose 版本（V2，新版本）
docker compose version

# 查看帮助信息
docker-compose --help
```

**预期结果**：应该显示 Docker Compose 的版本信息，例如：
- `docker-compose version 1.29.2` 或
- `Docker Compose version v2.x.x`

### 1.2 理解 Docker Compose 的作用

**Docker Compose 的优势**：
- 使用 YAML 文件定义多容器应用
- 一键启动/停止所有服务
- 自动处理服务依赖关系
- 统一管理网络和数据卷
- 简化开发和生产环境部署

### 1.3 查看项目结构

```bash
# 进入项目目录
cd stage5-practice

# 查看项目结构
ls -la

# 查看主要的 Compose 配置文件
cat docker-compose.yml
```

**关键点**：
- `docker-compose.yml` 是默认的配置文件
- 可以使用 `-f` 参数指定其他配置文件
- Compose 文件使用 YAML 格式

---

## 实验 2：Compose 文件编写基础

### 2.1 理解 docker-compose.yml 文件结构

打开 `docker-compose.yml` 文件，观察以下结构：

```yaml
version: '3.8'        # Compose 文件版本

services:             # 服务定义
  service-name:       # 服务名称
    # 服务配置...

volumes:              # 数据卷定义
  volume-name:        # 数据卷配置...

networks:             # 网络定义
  network-name:       # 网络配置...
```

### 2.2 version 字段

```bash
# 查看当前使用的 Compose 文件版本
docker-compose config | head -5

# 了解不同版本的区别
# version '3.8' 是较新的版本，支持更多特性
```

**关键点**：
- `version` 指定 Compose 文件格式版本
- 不同版本支持不同的功能
- 建议使用 `3.8` 或更高版本

### 2.3 services 字段

```bash
# 查看所有定义的服务
docker-compose config --services

# 查看特定服务的配置
docker-compose config --services | grep mysql
```

**关键点**：
- `services` 定义所有要运行的服务
- 每个服务对应一个容器
- 服务名称可以作为网络中的主机名

### 2.4 networks 字段

```bash
# 查看网络配置
docker-compose config | grep -A 10 networks

# 启动服务后查看实际创建的网络
docker-compose up -d
docker network ls | grep compose
```

**关键点**：
- `networks` 定义自定义网络
- 默认使用 `bridge` 驱动
- 同一网络中的服务可以通过服务名通信

### 2.5 volumes 字段

```bash
# 查看数据卷配置
docker-compose config | grep -A 10 volumes

# 启动服务后查看实际创建的数据卷
docker volume ls | grep compose
```

**关键点**：
- `volumes` 定义命名数据卷
- 数据卷用于数据持久化
- 可以在多个服务间共享

---

## 实验 3：服务配置详解

### 3.1 environment - 环境变量

```bash
# 查看服务使用的环境变量
docker-compose config | grep -A 20 "environment:"

# 启动服务后查看容器的环境变量
docker-compose up -d mysql
docker-compose exec mysql env | grep MYSQL
```

**实践**：修改 `.env` 文件（如果存在）或直接在 `docker-compose.yml` 中修改环境变量，观察变化。

### 3.2 depends_on - 服务依赖

```bash
# 查看服务依赖关系
docker-compose config | grep -A 5 "depends_on"

# 观察服务启动顺序
docker-compose up

# 注意观察：
# 1. MySQL 先启动
# 2. Redis 启动
# 3. Webapp 等待 MySQL 健康检查通过后才启动
# 4. Nginx 等待 Webapp 启动后才启动
```

**关键点**：
- `depends_on` 定义服务启动顺序
- `condition: service_healthy` 等待健康检查通过
- `condition: service_started` 等待服务启动

### 3.3 ports - 端口映射

```bash
# 查看端口映射配置
docker-compose config | grep -A 3 "ports:"

# 启动服务后查看端口映射
docker-compose up -d
docker-compose ps

# 测试端口访问
curl http://localhost:8080
curl http://localhost:80
```

**关键点**：
- 格式：`"宿主机端口:容器端口"`
- 可以只指定容器端口（随机分配宿主机端口）
- 端口映射使容器服务可以从宿主机访问

### 3.4 build - 构建配置

```bash
# 查看构建配置
docker-compose config webapp | grep -A 10 "build:"

# 构建镜像
docker-compose build webapp

# 强制重新构建（不使用缓存）
docker-compose build --no-cache webapp

# 构建所有需要构建的服务
docker-compose build
```

**关键点**：
- `build` 指定如何构建镜像
- `context` 指定构建上下文目录
- `dockerfile` 指定 Dockerfile 文件

### 3.5 healthcheck - 健康检查

```bash
# 查看健康检查配置
docker-compose config mysql | grep -A 10 "healthcheck:"

# 启动服务后查看健康状态
docker-compose up -d
docker-compose ps

# 查看健康检查日志
docker inspect compose-mysql | grep -A 20 "Health"
```

**关键点**：
- 健康检查确保服务真正可用
- `depends_on` 可以等待健康检查通过
- 提高服务启动的可靠性

---

## 实验 4：Compose 命令实践

### 4.1 docker-compose up - 启动服务

```bash
# 启动所有服务（前台运行，查看日志）
docker-compose up

# 启动所有服务（后台运行）
docker-compose up -d

# 启动特定服务
docker-compose up -d mysql redis

# 启动服务并重新构建镜像
docker-compose up -d --build

# 启动服务并强制重新创建容器
docker-compose up -d --force-recreate
```

**实践**：
```bash
# 进入项目目录
cd stage5-practice

# 启动所有服务
docker-compose up -d

# 观察输出，理解启动过程
```

### 4.2 docker-compose down - 停止服务

```bash
# 停止并删除容器（保留数据卷）
docker-compose down

# 停止并删除容器和数据卷（完全清理）
docker-compose down -v

# 停止并删除容器、数据卷和网络
docker-compose down -v --remove-orphans
```

**实践**：
```bash
# 停止所有服务
docker-compose down

# 验证容器已删除
docker ps -a | grep compose
```

### 4.3 docker-compose ps - 查看服务状态

```bash
# 查看所有服务状态
docker-compose ps

# 查看特定服务状态
docker-compose ps webapp

# 查看详细信息
docker-compose ps -a
```

**实践**：
```bash
# 启动服务
docker-compose up -d

# 查看状态
docker-compose ps

# 观察输出中的：
# - 服务名称
# - 状态（Up/Down）
# - 端口映射
# - 健康状态
```

### 4.4 docker-compose logs - 查看日志

```bash
# 查看所有服务日志
docker-compose logs

# 查看特定服务日志
docker-compose logs webapp

# 实时查看日志（类似 tail -f）
docker-compose logs -f

# 查看最后 N 行日志
docker-compose logs --tail=100

# 查看带时间戳的日志
docker-compose logs -t

# 查看最近 N 分钟的日志
docker-compose logs --since 10m
```

**实践**：
```bash
# 实时查看所有服务日志
docker-compose logs -f

# 在另一个终端修改代码或重启服务，观察日志变化
```

### 4.5 docker-compose exec - 执行命令

```bash
# 在服务中执行命令
docker-compose exec webapp ls -la

# 进入服务容器（交互式）
docker-compose exec webapp sh

# 在 MySQL 中执行 SQL
docker-compose exec mysql mysql -uroot -p123456 -e "SHOW DATABASES;"

# 在 Redis 中执行命令
docker-compose exec redis redis-cli ping
```

**实践**：
```bash
# 进入 webapp 容器
docker-compose exec webapp sh

# 在容器内执行：
# - ls -la /app
# - env | grep SPRING
# - ping mysql
# - ping redis
# - exit
```

### 4.6 docker-compose build - 构建镜像

```bash
# 构建所有需要构建的服务
docker-compose build

# 构建特定服务
docker-compose build webapp

# 强制重新构建（不使用缓存）
docker-compose build --no-cache

# 构建并启动服务
docker-compose up -d --build
```

**实践**：
```bash
# 修改 webapp 的代码
# 然后重新构建
docker-compose build webapp

# 重启服务以使用新镜像
docker-compose up -d webapp
```

### 4.7 docker-compose restart - 重启服务

```bash
# 重启所有服务
docker-compose restart

# 重启特定服务
docker-compose restart webapp

# 重启多个服务
docker-compose restart webapp nginx
```

**实践**：
```bash
# 重启 webapp 服务
docker-compose restart webapp

# 观察日志，确认服务重新启动
docker-compose logs -f webapp
```

### 4.8 docker-compose config - 查看配置

```bash
# 查看解析后的完整配置
docker-compose config

# 查看特定服务的配置
docker-compose config webapp

# 验证配置文件语法
docker-compose config --quiet
```

**实践**：
```bash
# 查看完整配置
docker-compose config > full-config.yml

# 对比原始文件和解析后的文件，理解配置解析过程
```

---

## 实验 5：多服务编排实践

### 5.1 启动完整的 LNMP 应用栈

```bash
# 进入项目目录
cd stage5-practice

# 启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 等待所有服务启动完成（约 1-2 分钟）
```

**预期结果**：应该看到 4 个服务运行：
- `compose-mysql` - MySQL 数据库
- `compose-redis` - Redis 缓存
- `compose-webapp` - Spring Boot Web 应用
- `compose-nginx` - Nginx 反向代理

### 5.2 验证服务间通信

```bash
# 测试 webapp 访问 MySQL
docker-compose exec webapp ping -c 3 mysql

# 测试 webapp 访问 Redis
docker-compose exec webapp ping -c 3 redis

# 测试 Nginx 访问 webapp
docker-compose exec nginx ping -c 3 webapp

# 测试 HTTP 连接
docker-compose exec webapp wget -qO- http://localhost:8080/health
```

### 5.3 访问应用

```bash
# 通过 Nginx 访问（端口 80）
curl http://localhost

# 直接访问 Web 应用（端口 8080）
curl http://localhost:8080

# 在浏览器中访问
# http://localhost
# http://localhost:8080
# http://localhost/compose
```

### 5.4 观察服务依赖关系

```bash
# 停止所有服务
docker-compose down

# 只启动 MySQL
docker-compose up -d mysql

# 等待 MySQL 健康检查通过
docker-compose ps

# 启动 webapp（依赖 MySQL）
docker-compose up -d webapp

# 观察 webapp 等待 MySQL 就绪的过程
docker-compose logs -f webapp
```

**关键点**：
- `depends_on` 确保服务按正确顺序启动
- 健康检查确保依赖服务真正可用
- 避免服务启动时的连接错误

---

## 实验 6：环境变量和配置管理

### 6.1 使用 .env 文件

```bash
# 查看 .env.example 文件
cat .env.example

# 创建 .env 文件（如果不存在）
cp .env.example .env

# 编辑 .env 文件
# Windows: notepad .env
# Linux/Mac: nano .env

# 修改配置，例如：
# MYSQL_ROOT_PASSWORD=mynewpassword
# WEBAPP_PORT=9090
```

### 6.2 在 Compose 文件中使用环境变量

查看 `docker-compose.yml` 中的环境变量使用：

```yaml
environment:
  MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD:-123456}
  # ${变量名:-默认值} 语法
```

**实践**：
```bash
# 使用环境变量启动服务
docker-compose --env-file .env up -d

# 查看解析后的配置
docker-compose config | grep MYSQL_ROOT_PASSWORD
```

### 6.3 多环境配置

```bash
# 使用开发环境配置
docker-compose -f docker-compose.dev.yml up -d

# 使用生产环境配置
docker-compose -f docker-compose.prod.yml up -d

# 对比不同环境的配置差异
diff docker-compose.yml docker-compose.dev.yml
```

**关键点**：
- 使用不同的 Compose 文件管理不同环境
- 开发环境：绑定挂载、热重载、详细日志
- 生产环境：资源限制、日志轮转、安全配置

### 6.4 环境变量优先级

```bash
# 环境变量优先级（从高到低）：
# 1. Shell 环境变量
# 2. .env 文件
# 3. Compose 文件中的默认值

# 示例：使用 Shell 环境变量覆盖配置
export MYSQL_ROOT_PASSWORD=shellpassword
docker-compose up -d mysql
docker-compose exec mysql env | grep MYSQL_ROOT_PASSWORD
```

---

## 实验 7：网络和数据卷管理

### 7.1 自定义网络配置

```bash
# 查看网络配置
docker-compose config | grep -A 15 "networks:"

# 启动服务后查看网络
docker-compose up -d
docker network inspect compose-app-network

# 查看网络中的容器
docker network inspect compose-app-network | grep -A 5 "Containers"
```

**实践**：修改 `docker-compose.yml` 中的网络配置，观察变化。

### 7.2 数据卷管理

```bash
# 查看数据卷配置
docker-compose config | grep -A 10 "volumes:"

# 启动服务后查看数据卷
docker volume ls | grep compose

# 查看数据卷详细信息
docker volume inspect compose-mysql-data

# 查看数据卷中的数据
docker run --rm -v compose-mysql-data:/data alpine ls -la /data
```

### 7.3 数据持久化验证

```bash
# 启动服务并创建一些数据
docker-compose up -d
docker-compose exec mysql mysql -uroot -p123456 -e "USE testdb; INSERT INTO users (name, email) VALUES ('Test', 'test@example.com');"

# 停止并删除容器（保留数据卷）
docker-compose down

# 重新启动服务
docker-compose up -d

# 验证数据仍然存在
docker-compose exec mysql mysql -uroot -p123456 -e "USE testdb; SELECT * FROM users;"
```

**关键点**：
- 数据卷独立于容器生命周期
- 删除容器不会删除数据卷
- 使用 `docker-compose down -v` 才会删除数据卷

### 7.4 绑定挂载 vs 数据卷

```bash
# 查看 docker-compose.dev.yml 中的绑定挂载
cat docker-compose.dev.yml | grep -A 3 "volumes:"

# 使用开发配置启动（使用绑定挂载）
docker-compose -f docker-compose.dev.yml up -d

# 修改本地代码文件
# 观察容器内的变化（热重载）
```

**对比**：
- **数据卷**：Docker 管理，适合生产环境
- **绑定挂载**：直接挂载宿主机目录，适合开发环境

---

## 实验 8：扩展和扩展服务

### 8.1 扩展服务实例

```bash
# 扩展 webapp 服务（启动 3 个实例）
docker-compose up -d --scale webapp=3

# 查看扩展后的服务
docker-compose ps

# 注意：需要配置 Nginx 负载均衡才能有效利用多个实例
```

**限制**：直接扩展有端口映射的服务会有端口冲突。需要：
1. 移除端口映射，或
2. 使用负载均衡器（如 Nginx）

### 8.2 配置负载均衡

修改 `nginx/nginx.conf` 添加负载均衡配置：

```nginx
upstream webapp {
    server webapp:8080;
}

server {
    location / {
        proxy_pass http://webapp;
    }
}
```

### 8.3 使用 Docker Swarm 模式（高级）

```bash
# 初始化 Swarm 模式
docker swarm init

# 使用 stack 部署（支持真正的服务扩展）
docker stack deploy -c docker-compose.yml myapp

# 扩展服务
docker service scale myapp_webapp=3

# 查看服务
docker service ls
```

---

## 实验 9：故障排查和调试

### 9.1 服务无法启动

```bash
# 查看服务日志
docker-compose logs webapp

# 查看服务状态
docker-compose ps

# 查看容器详细信息
docker inspect compose-webapp

# 进入容器调试
docker-compose exec webapp sh
```

### 9.2 网络连接问题

```bash
# 查看网络信息
docker network inspect compose-app-network

# 测试容器间连接
docker-compose exec webapp ping mysql
docker-compose exec webapp ping redis

# 查看 DNS 解析
docker-compose exec webapp nslookup mysql
```

### 9.3 数据卷问题

```bash
# 查看数据卷
docker volume ls
docker volume inspect compose-mysql-data

# 检查数据卷挂载
docker-compose exec mysql df -h
docker-compose exec mysql ls -la /var/lib/mysql
```

### 9.4 资源使用问题

```bash
# 查看资源使用情况
docker stats

# 查看特定容器的资源使用
docker stats compose-webapp

# 检查系统资源
docker system df
```

---

## 实验 10：最佳实践总结

### 10.1 Compose 文件最佳实践

1. **使用版本控制**：将 `docker-compose.yml` 纳入版本控制
2. **环境变量管理**：使用 `.env` 文件管理敏感信息
3. **健康检查**：为所有服务配置健康检查
4. **资源限制**：在生产环境设置资源限制
5. **日志管理**：配置日志轮转和大小限制

### 10.2 服务编排最佳实践

1. **服务依赖**：正确配置 `depends_on` 和健康检查
2. **网络隔离**：使用自定义网络，避免使用默认 bridge
3. **数据持久化**：使用命名数据卷，不要使用匿名卷
4. **安全配置**：使用非 root 用户运行容器
5. **多环境管理**：使用不同的 Compose 文件管理不同环境

### 10.3 开发工作流

```bash
# 开发环境
docker-compose -f docker-compose.dev.yml up -d

# 修改代码（自动热重载）

# 测试
docker-compose -f docker-compose.dev.yml logs -f

# 构建生产镜像
docker-compose build webapp

# 生产环境
docker-compose -f docker-compose.prod.yml up -d
```

---

## 综合练习

### 练习 1：创建自己的 Compose 应用

1. 创建一个新的目录 `my-compose-app`
2. 编写 `docker-compose.yml`，包含：
   - 一个 Web 服务（Nginx）
   - 一个数据库服务（MySQL）
   - 自定义网络
   - 数据卷
3. 启动并测试应用

### 练习 2：多环境配置

1. 创建 `docker-compose.dev.yml` 和 `docker-compose.prod.yml`
2. 配置不同的环境变量
3. 测试在不同环境下的运行

### 练习 3：服务扩展

1. 配置 Nginx 负载均衡
2. 扩展 Web 服务到多个实例
3. 验证负载均衡是否工作

---

## 清理和总结

### 清理资源

```bash
# 停止并删除所有容器
docker-compose down

# 删除数据卷
docker-compose down -v

# 清理未使用的资源
docker system prune -f
```

### 学习检查点

完成本阶段后，你应该能够：
- [ ] 编写 `docker-compose.yml` 文件
- [ ] 使用 Compose 命令管理多容器应用
- [ ] 配置服务依赖和健康检查
- [ ] 管理环境变量和配置
- [ ] 使用网络和数据卷
- [ ] 进行故障排查和调试
- [ ] 理解 Compose 最佳实践

---

**恭喜完成第五阶段的学习！🎉**

下一步：继续学习第六阶段 - Docker 高级主题

