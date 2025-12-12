# 快速开始指南 - Docker 高级主题实践

本指南将帮助你快速启动和体验阶段6的实践项目。

## 前置要求

- Docker 20.10+
- Docker Compose 2.0+
- Java 17（本地开发需要）
- Maven 3.6+（本地开发需要）

## 快速启动

### 1. 启动基础服务（资源限制、健康检查、日志）

```bash
cd stage6-practice
docker-compose up -d
```

这将启动：
- MySQL（带资源限制和健康检查）
- Redis（带资源限制和健康检查）
- Spring Boot Web应用（带资源限制、健康检查、日志配置）

### 2. 查看服务状态

```bash
# 查看所有服务状态（包括健康检查状态）
docker-compose ps

# 查看服务日志
docker-compose logs -f webapp
```

### 3. 访问应用

- **Web应用首页**: http://localhost:8080
- **高级特性页面**: http://localhost:8080/advanced
- **资源监控页面**: http://localhost:8080/resources
- **健康检查端点**: http://localhost:8080/actuator/health
- **应用信息**: http://localhost:8080/actuator/info

### 4. 监控资源使用

```bash
# 使用脚本监控资源
./scripts/monitor-resources.sh

# 或使用docker stats命令
docker stats
```

### 5. 检查容器健康状态

```bash
# 使用脚本检查健康状态
./scripts/check-health.sh

# 或手动检查
docker inspect --format='{{.State.Health.Status}}' stage6-webapp
```

## 实验场景

### 场景1：资源限制测试

1. 启动服务：
```bash
docker-compose up -d
```

2. 查看资源限制配置：
```bash
docker inspect stage6-webapp | grep -A 10 "Resources"
```

3. 监控资源使用：
```bash
docker stats stage6-webapp
```

4. 访问资源监控页面，触发一些CPU密集型操作：
```bash
curl http://localhost:8080/resources/cpu-test
```

### 场景2：健康检查测试

1. 查看健康检查配置：
```bash
docker inspect stage6-webapp | grep -A 10 "Health"
```

2. 模拟服务故障（停止应用）：
```bash
docker-compose stop webapp
```

3. 观察健康检查状态变化：
```bash
watch -n 1 'docker inspect --format="{{.State.Health.Status}}" stage6-webapp'
```

4. 重启服务：
```bash
docker-compose start webapp
```

### 场景3：日志管理测试

1. 查看日志配置：
```bash
docker inspect stage6-webapp | grep -A 10 "LogConfig"
```

2. 生成一些日志：
```bash
# 访问应用，触发日志输出
curl http://localhost:8080/advanced
```

3. 查看日志：
```bash
# 查看最近100行日志
docker-compose logs --tail=100 webapp

# 实时查看日志
docker-compose logs -f webapp
```

4. 测试日志轮转：
```bash
# 生成大量日志
for i in {1..1000}; do curl http://localhost:8080/advanced; done

# 检查日志文件大小和数量
docker inspect stage6-webapp | grep -A 5 "LogPath"
```

### 场景4：安全加固测试

1. 使用安全加固配置启动：
```bash
docker-compose -f docker-compose.secure.yml up -d
```

2. 检查容器是否以非root用户运行：
```bash
docker exec stage6-webapp-secure whoami
# 应该输出: appuser (而不是 root)
```

3. 尝试写入只读文件系统：
```bash
docker exec stage6-webapp-secure touch /tmp/test.txt
# 应该成功（/tmp通常是可写的）

docker exec stage6-webapp-secure touch /app/test.txt
# 可能失败（如果/app是只读的）
```

### 场景5：私有仓库测试

1. 启动私有仓库：
```bash
docker-compose -f docker-compose.registry.yml up -d
```

2. 构建并标记镜像：
```bash
cd webapp
docker build -t localhost:5000/stage6-webapp:1.0.0 .
```

3. 推送镜像到私有仓库：
```bash
docker push localhost:5000/stage6-webapp:1.0.0
```

4. 从私有仓库拉取镜像：
```bash
docker pull localhost:5000/stage6-webapp:1.0.0
```

5. 查看仓库中的镜像：
```bash
curl http://localhost:5000/v2/_catalog
```

## 常用命令

### 服务管理

```bash
# 启动服务
docker-compose up -d

# 停止服务
docker-compose down

# 重启服务
docker-compose restart

# 查看服务状态
docker-compose ps

# 查看服务日志
docker-compose logs -f [service-name]
```

### 资源监控

```bash
# 实时监控所有容器资源
docker stats

# 监控特定容器
docker stats stage6-webapp

# 查看容器详细信息
docker inspect stage6-webapp
```

### 健康检查

```bash
# 查看健康检查状态
docker inspect --format='{{json .State.Health}}' stage6-webapp | jq

# 使用脚本检查
./scripts/check-health.sh
```

### 日志管理

```bash
# 查看日志
docker-compose logs [service-name]

# 查看最近N行日志
docker-compose logs --tail=100 [service-name]

# 实时查看日志
docker-compose logs -f [service-name]

# 查看日志文件位置
docker inspect --format='{{.LogPath}}' stage6-webapp
```

### 镜像安全扫描

```bash
# 使用trivy扫描镜像（需要先安装trivy）
./scripts/scan-image.sh stage6-webapp:latest
```

## 故障排查

### 服务无法启动

1. 检查端口是否被占用：
```bash
netstat -tulpn | grep -E '8080|3306|6379'
```

2. 查看详细错误日志：
```bash
docker-compose logs [service-name]
```

3. 检查资源限制是否合理：
```bash
docker stats
```

### 健康检查失败

1. 检查健康检查端点：
```bash
curl http://localhost:8080/actuator/health
```

2. 查看容器日志：
```bash
docker-compose logs webapp
```

3. 进入容器检查：
```bash
docker exec -it stage6-webapp sh
```

### 资源使用过高

1. 查看资源使用情况：
```bash
docker stats
```

2. 调整资源限制（编辑docker-compose.yml）：
```yaml
deploy:
  resources:
    limits:
      cpus: '2.0'  # 增加CPU限制
      memory: 1G   # 增加内存限制
```

3. 重启服务：
```bash
docker-compose up -d --force-recreate
```

## 清理环境

```bash
# 停止并删除所有容器、网络
docker-compose down

# 删除数据卷（注意：会删除数据）
docker-compose down -v

# 删除镜像
docker rmi stage6-webapp:latest

# 清理未使用的资源
docker system prune -a
```

## 下一步

完成快速开始后，建议：
1. 阅读 [practice-steps.md](practice-steps.md) 了解详细实践步骤
2. 尝试修改配置，观察效果
3. 阅读 [README.md](README.md) 了解项目结构和技术栈
4. 参考官方文档深入学习各个主题

