# Docker 网络实践 - 快速开始指南

## 快速启动

### 1. 启动所有服务

```bash
# 进入项目目录
cd stage4-practice

# 启动所有服务（包括 webapp、api-service、mysql、redis）
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

### 2. 访问应用

- **Web 应用**: http://localhost:8080
- **API 服务**: http://localhost:5000
- **MySQL**: localhost:3306
- **Redis**: localhost:6379

### 3. 测试网络连通性

```bash
# 测试 webapp 能否访问 api-service
docker-compose exec webapp curl http://api-service:5000/health

# 测试 webapp 能否访问 mysql
docker-compose exec webapp ping -c 3 mysql

# 测试 webapp 能否访问 redis
docker-compose exec webapp ping -c 3 redis

# 查看网络信息
docker network ls
docker network inspect frontend-network
docker network inspect backend-network
```

### 4. 停止服务

```bash
# 停止所有服务
docker-compose down

# 停止服务并删除网络
docker-compose down --remove-orphans
```

## 网络架构说明

本项目包含两个自定义网络：

1. **frontend-network** - 前端网络
   - webapp (Spring Boot 应用)
   - api-service (Python Flask API)

2. **backend-network** - 后端网络
   - mysql (MySQL 数据库)
   - redis (Redis 缓存)
   - webapp (同时加入后端网络)
   - api-service (同时加入后端网络)

## 常用命令

### 查看网络

```bash
# 列出所有网络
docker network ls

# 查看网络详细信息
docker network inspect <network-name>

# 查看容器网络配置
docker inspect <container-name> | grep -A 20 "Networks"
```

### 容器网络操作

```bash
# 将容器连接到网络
docker network connect <network-name> <container-name>

# 断开容器与网络的连接
docker network disconnect <network-name> <container-name>

# 查看容器网络
docker inspect <container-name> | grep NetworkMode
```

### 测试容器间通信

```bash
# 进入容器
docker-compose exec webapp sh

# 在容器内测试 DNS 解析
nslookup api-service
nslookup mysql
nslookup redis

# 测试网络连通性
ping api-service
ping mysql
ping redis

# 测试 HTTP 连接
curl http://api-service:5000/health
```

## 实验场景

### 场景 1：测试同一网络内容器通信

```bash
# 启动服务
docker-compose up -d

# webapp 和 api-service 在同一网络（frontend-network）
# 它们可以通过容器名称互相访问
docker-compose exec webapp curl http://api-service:5000/api/info
```

### 场景 2：测试跨网络通信

```bash
# webapp 同时连接 frontend-network 和 backend-network
# 可以访问两个网络中的服务
docker-compose exec webapp curl http://api-service:5000/api/info  # frontend-network
docker-compose exec webapp ping mysql  # backend-network
```

### 场景 3：测试网络隔离

```bash
# api-service 只连接 frontend-network
# 无法直接访问 backend-network 中的 mysql 和 redis
docker-compose exec api-service ping mysql
# 结果：无法解析或连接失败（取决于网络配置）
```

### 场景 4：使用网络别名

```bash
# 在 docker-compose.yml 中配置了网络别名
# 可以通过别名访问服务
docker-compose exec webapp ping db  # db 是 mysql 的别名
docker-compose exec webapp ping cache  # cache 是 redis 的别名
```

## 故障排查

### 问题 1：容器无法互相访问

```bash
# 检查容器是否在同一网络
docker network inspect <network-name> | grep Containers

# 检查容器网络配置
docker inspect <container-name> | grep -A 10 Networks

# 检查 DNS 解析
docker-compose exec <container-name> nslookup <service-name>
```

### 问题 2：端口无法访问

```bash
# 检查端口映射
docker-compose ps
docker port <container-name>

# 检查防火墙设置
# Windows: 检查 Windows Defender 防火墙
# Linux: sudo ufw status
```

### 问题 3：服务启动失败

```bash
# 查看详细日志
docker-compose logs <service-name>

# 检查网络是否存在
docker network ls

# 重新创建网络
docker-compose down
docker network prune
docker-compose up -d
```

## 下一步

完成快速体验后，请按照 `practice-steps.md` 中的详细步骤进行深入学习。

