# 第四阶段实践步骤详解

## 前置准备

确保你已经完成：
- [ ] Docker 已安装并运行
- [ ] Docker Compose 已安装
- [ ] 熟悉基本的 Docker 命令
- [ ] 了解 Docker 容器和镜像的基本概念
- [ ] 完成第三阶段的学习（了解数据卷和绑定挂载）

---

## 实验 1：Docker 网络基础操作

### 1.1 查看网络列表

```bash
# 查看所有网络
docker network ls

# 查看网络详细信息
docker network inspect bridge

# 查看默认网络配置
docker network inspect bridge | grep -A 10 "IPAM"
```

**预期结果**：应该看到至少 3 个默认网络：
- `bridge` - 默认桥接网络
- `host` - 主机网络
- `none` - 无网络

### 1.2 创建自定义网络

```bash
# 创建一个自定义 bridge 网络
docker network create my-custom-network

# 查看创建的网络
docker network ls

# 查看网络详细信息
docker network inspect my-custom-network
```

**关键点**：
- 默认使用 `bridge` 驱动
- Docker 会自动分配子网（通常是 172.x.x.x）
- 可以指定子网和网关

### 1.3 创建带自定义配置的网络

```bash
# 创建指定子网和网关的网络
docker network create \
  --driver bridge \
  --subnet=172.25.0.0/16 \
  --gateway=172.25.0.1 \
  my-custom-network-v2

# 查看网络配置
docker network inspect my-custom-network-v2
```

### 1.4 删除网络

```bash
# 删除网络（注意：网络必须没有被任何容器使用）
docker network rm my-custom-network

# 如果网络中有容器，需要先删除容器
docker network rm my-custom-network-v2

# 清理未使用的网络
docker network prune
```

---

## 实验 2：Bridge 网络实践

### 2.1 默认 Bridge 网络

```bash
# 运行两个容器，使用默认 bridge 网络
docker run -d --name container1 alpine sleep 3600
docker run -d --name container2 alpine sleep 3600

# 查看容器网络配置
docker inspect container1 | grep -A 20 "Networks"
docker inspect container2 | grep -A 20 "Networks"

# 尝试使用容器名称通信（会失败）
docker exec container1 ping -c 3 container2
# 结果：ping: bad address 'container2'

# 使用 IP 地址通信（可以成功）
docker exec container1 ping -c 3 $(docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' container2)
```

**关键点**：
- 默认 bridge 网络中的容器**不能**通过容器名称通信
- 只能通过 IP 地址通信
- 容器名称不会自动注册到 DNS

### 2.2 自定义 Bridge 网络

```bash
# 创建自定义网络
docker network create my-bridge-network

# 在自定义网络中运行容器
docker run -d --name container3 --network my-bridge-network alpine sleep 3600
docker run -d --name container4 --network my-bridge-network alpine sleep 3600

# 测试容器名称通信（可以成功）
docker exec container3 ping -c 3 container4
# 结果：可以成功 ping 通

# 测试反向通信
docker exec container4 ping -c 3 container3
```

**关键点**：
- 自定义 bridge 网络中的容器**可以**通过容器名称通信
- Docker 内置 DNS 服务器自动解析容器名称
- 这是推荐的生产环境做法

### 2.3 网络隔离

```bash
# 创建两个不同的网络
docker network create network-a
docker network create network-b

# 在不同网络中运行容器
docker run -d --name container-a --network network-a alpine sleep 3600
docker run -d --name container-b --network network-b alpine sleep 3600

# 测试跨网络通信（会失败）
docker exec container-a ping -c 3 container-b
# 结果：无法解析或连接失败

# 将容器加入多个网络
docker network connect network-a container-b

# 现在可以通信了
docker exec container-a ping -c 3 container-b
```

### 2.4 清理

```bash
# 停止并删除容器
docker stop container1 container2 container3 container4 container-a container-b
docker rm container1 container2 container3 container4 container-a container-b

# 删除网络
docker network rm my-custom-network my-bridge-network network-a network-b
```

---

## 实验 3：容器间通信

### 3.1 同一网络内容器通信

```bash
# 使用项目提供的 docker-compose.yml
cd stage4-practice
docker-compose up -d

# 等待服务启动
sleep 10

# 测试 webapp 访问 api-service
docker-compose exec webapp ping -c 3 api-service

# 测试 HTTP 通信
docker-compose exec webapp curl http://api-service:5000/health
```

### 3.2 使用容器名称进行 DNS 解析

```bash
# 进入 webapp 容器
docker-compose exec webapp sh

# 在容器内测试 DNS 解析
nslookup api-service
nslookup mysql
nslookup redis

# 测试 ping
ping -c 3 api-service
ping -c 3 mysql
ping -c 3 redis

# 退出容器
exit
```

### 3.3 网络别名（Aliases）的使用

```bash
# 查看 docker-compose.yml 中的别名配置
# mysql 服务有别名：db, database
# redis 服务有别名：cache

# 测试使用别名访问
docker-compose exec webapp ping -c 3 db
docker-compose exec webapp ping -c 3 cache
docker-compose exec webapp ping -c 3 database

# 查看网络中的别名
docker network inspect frontend-network | grep -A 5 "Aliases"
docker network inspect backend-network | grep -A 5 "Aliases"
```

**关键点**：
- 别名允许一个容器有多个名称
- 可以在不同网络中使用不同的别名
- 提高配置的灵活性

### 3.4 跨网络通信

```bash
# webapp 和 api-service 在 frontend-network
# mysql 和 redis 在 backend-network
# webapp 和 api-service 同时连接 backend-network

# 测试 webapp 访问 frontend-network 中的服务
docker-compose exec webapp ping -c 3 api-service

# 测试 webapp 访问 backend-network 中的服务
docker-compose exec webapp ping -c 3 mysql
docker-compose exec webapp ping -c 3 redis

# 测试 api-service 访问 backend-network 中的服务
docker-compose exec api-service ping -c 3 mysql
docker-compose exec api-service ping -c 3 redis
```

---

## 实验 4：端口映射和暴露

### 4.1 端口映射（-p）

```bash
# 运行容器并映射端口
docker run -d \
  --name nginx-test \
  -p 8080:80 \
  nginx:latest

# 查看端口映射
docker port nginx-test

# 测试访问
curl http://localhost:8080

# 映射多个端口
docker run -d \
  --name nginx-multi \
  -p 8081:80 \
  -p 8443:443 \
  nginx:latest

# 动态端口映射（让 Docker 自动分配）
docker run -d \
  --name nginx-dynamic \
  -p 80 \
  nginx:latest

# 查看动态分配的端口
docker port nginx-dynamic
```

### 4.2 端口暴露（EXPOSE）

```bash
# 查看 Dockerfile 中的 EXPOSE 指令
cat webapp/Dockerfile | grep EXPOSE
cat api-service/Dockerfile | grep EXPOSE

# EXPOSE 只是文档说明，不会实际映射端口
# 必须使用 -p 或 --publish 才能从宿主机访问
```

**关键点**：
- `EXPOSE` 只是文档说明，告诉用户容器监听的端口
- `-p` 或 `--publish` 才是真正的端口映射
- 端口映射格式：`宿主机端口:容器端口`

### 4.3 端口范围映射

```bash
# 映射端口范围
docker run -d \
  --name nginx-range \
  -p 8000-8010:8000-8010 \
  nginx:latest

# 查看映射的端口
docker port nginx-range
```

### 4.4 清理

```bash
docker stop nginx-test nginx-multi nginx-dynamic nginx-range
docker rm nginx-test nginx-multi nginx-dynamic nginx-range
```

---

## 实验 5：Host 和 None 网络

### 5.1 Host 网络模式

```bash
# 使用 host 网络模式运行容器
docker run -d \
  --name nginx-host \
  --network host \
  nginx:latest

# 注意：host 模式下，-p 参数无效
# 容器直接使用宿主机的网络栈

# 查看容器网络
docker inspect nginx-host | grep -A 10 "NetworkMode"

# 测试访问（直接使用宿主机端口）
curl http://localhost:80

# 查看端口占用
# Windows: netstat -ano | findstr :80
# Linux: netstat -tulpn | grep :80
```

**使用场景**：
- 需要最佳网络性能
- 容器需要访问宿主机网络服务
- 不需要端口隔离

### 5.2 None 网络模式

```bash
# 使用 none 网络模式运行容器
docker run -d \
  --name alpine-none \
  --network none \
  alpine sleep 3600

# 进入容器测试网络
docker exec alpine-none ping -c 3 8.8.8.8
# 结果：网络不可用

# 查看容器网络配置
docker inspect alpine-none | grep -A 10 "NetworkMode"
```

**使用场景**：
- 完全隔离的网络环境
- 安全要求极高的场景
- 自定义网络配置

### 5.3 对比测试

```bash
# 创建三个不同网络模式的容器
docker run -d --name test-bridge --network bridge alpine sleep 3600
docker run -d --name test-host --network host alpine sleep 3600
docker run -d --name test-none --network none alpine sleep 3600

# 查看网络配置
docker inspect test-bridge | grep -A 5 "NetworkMode"
docker inspect test-host | grep -A 5 "NetworkMode"
docker inspect test-none | grep -A 5 "NetworkMode"

# 清理
docker stop test-bridge test-host test-none
docker rm test-bridge test-host test-none
```

---

## 实验 6：多网络场景

### 6.1 容器加入多个网络

```bash
# 使用 docker-compose 启动服务
docker-compose up -d

# 查看 webapp 容器的网络
docker inspect webapp-network-practice | grep -A 30 "Networks"

# webapp 同时连接了 frontend-network 和 backend-network

# 测试从不同网络访问
docker-compose exec webapp ping -c 3 api-service  # frontend-network
docker-compose exec webapp ping -c 3 mysql        # backend-network
```

### 6.2 网络隔离和安全

```bash
# 创建一个隔离的网络
docker network create isolated-network

# 在隔离网络中运行容器
docker run -d \
  --name isolated-container \
  --network isolated-network \
  alpine sleep 3600

# 测试从其他网络访问（应该失败）
docker-compose exec webapp ping -c 3 isolated-container
# 结果：无法访问

# 将容器连接到多个网络
docker network connect frontend-network isolated-container

# 现在可以访问了
docker-compose exec webapp ping -c 3 isolated-container
```

### 6.3 服务发现

```bash
# Docker 内置 DNS 服务器提供服务发现
# 容器可以通过服务名称自动发现其他容器

# 查看 DNS 配置
docker-compose exec webapp cat /etc/resolv.conf

# 测试 DNS 解析
docker-compose exec webapp nslookup api-service
docker-compose exec webapp nslookup mysql
docker-compose exec webapp nslookup redis

# 使用网络别名
docker-compose exec webapp nslookup db      # mysql 的别名
docker-compose exec webapp nslookup cache   # redis 的别名
```

---

## 实验 7：综合实践

### 7.1 搭建完整的多服务应用

```bash
# 启动所有服务
cd stage4-practice
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看网络
docker network ls
docker network inspect frontend-network
docker network inspect backend-network
```

### 7.2 配置服务间网络通信

```bash
# 测试 webapp 访问 api-service
curl http://localhost:8080

# 测试 api-service 健康检查
curl http://localhost:5000/health

# 测试 api-service 访问 MySQL
curl http://localhost:5000/api/mysql/test

# 测试 api-service 访问 Redis
curl http://localhost:5000/api/redis/test
```

### 7.3 使用测试脚本

```bash
# 运行网络测试脚本
chmod +x scripts/test-network.sh
./scripts/test-network.sh

# 运行 DNS 测试脚本
chmod +x scripts/test-dns.sh
./scripts/test-dns.sh webapp-network-practice

# 查看网络信息
chmod +x scripts/network-info.sh
./scripts/network-info.sh
```

### 7.4 验证网络隔离

```bash
# 创建一个新的隔离网络
docker network create test-isolated-network

# 在隔离网络中运行测试容器
docker run -d \
  --name test-isolated \
  --network test-isolated-network \
  alpine sleep 3600

# 测试从 webapp 访问（应该失败）
docker-compose exec webapp ping -c 3 test-isolated
# 结果：无法访问

# 清理
docker stop test-isolated
docker rm test-isolated
docker network rm test-isolated-network
```

---

## 使用网络实验配置文件

### 实验不同的网络类型

```bash
# 使用专门的网络实验配置
docker-compose -f docker-compose.networks.yml up -d

# 查看不同网络模式的容器
docker ps

# 测试不同网络模式
docker exec test-bridge-custom ping -c 3 8.8.8.8
docker exec test-host ping -c 3 8.8.8.8
docker exec test-none ping -c 3 8.8.8.8

# 测试多网络容器
docker exec test-multi-network ping -c 3 8.8.8.8

# 测试网络别名
docker exec test-with-alias hostname
docker network inspect alias-network | grep -A 5 "Aliases"

# 清理
docker-compose -f docker-compose.networks.yml down
```

---

## 常见问题排查

### 问题 1：容器无法互相访问

**排查步骤**：

```bash
# 1. 检查容器是否在同一网络
docker network inspect <network-name> | grep Containers

# 2. 检查容器网络配置
docker inspect <container-name> | grep -A 20 "Networks"

# 3. 测试 DNS 解析
docker exec <container-name> nslookup <target-service>

# 4. 测试网络连通性
docker exec <container-name> ping -c 3 <target-service>
```

### 问题 2：端口无法访问

**排查步骤**：

```bash
# 1. 检查端口映射
docker port <container-name>

# 2. 检查容器是否监听端口
docker exec <container-name> netstat -tulpn

# 3. 检查防火墙设置
# Windows: 检查 Windows Defender 防火墙
# Linux: sudo ufw status

# 4. 检查端口是否被占用
# Windows: netstat -ano | findstr :8080
# Linux: netstat -tulpn | grep :8080
```

### 问题 3：DNS 解析失败

**排查步骤**：

```bash
# 1. 检查 DNS 配置
docker exec <container-name> cat /etc/resolv.conf

# 2. 手动测试 DNS
docker exec <container-name> nslookup <service-name>

# 3. 检查网络中的服务
docker network inspect <network-name> | grep -A 10 "Containers"

# 4. 检查网络别名
docker network inspect <network-name> | grep -A 5 "Aliases"
```

---

## 学习检查清单

完成以下任务后，在 plan.md 中勾选对应的项目：

- [ ] 能够查看和管理 Docker 网络
- [ ] 理解 bridge、host、none 网络的区别
- [ ] 能够创建自定义网络
- [ ] 理解容器间通信机制
- [ ] 能够使用容器名称进行服务发现
- [ ] 理解网络别名的作用
- [ ] 能够配置端口映射
- [ ] 理解端口映射和暴露的区别
- [ ] 能够实现网络隔离
- [ ] 能够配置多网络场景

---

## 下一步

完成本阶段实践后，可以继续学习：
- 第五阶段：Docker Compose（本项目中已包含基础示例）
- 第六阶段：Docker 高级主题

---

## 清理所有资源

```bash
# 停止所有容器
docker-compose down

# 删除所有网络（谨慎操作！）
docker network prune

# 删除所有未使用的资源
docker system prune -a
```

