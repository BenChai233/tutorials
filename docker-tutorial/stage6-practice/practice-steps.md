# 第六阶段实践步骤详解

## 前置准备

确保你已经完成：
- [ ] Docker 已安装并运行
- [ ] Docker Compose 已安装
- [ ] 熟悉基本的 Docker 命令
- [ ] 了解 Docker 容器、镜像、网络和数据卷的基本概念
- [ ] 完成前五个阶段的学习
- [ ] 了解 Linux 基础命令

---

## 实验 1：容器资源限制

### 1.1 理解资源限制的重要性

**为什么需要资源限制？**
- 防止单个容器占用过多系统资源
- 确保多个容器能够公平共享资源
- 提高系统稳定性和可预测性
- 符合生产环境最佳实践

### 1.2 查看当前资源限制配置

```bash
# 进入项目目录
cd stage6-practice

# 查看 docker-compose.yml 中的资源限制配置
grep -A 10 "deploy:" docker-compose.yml
```

**关键配置项**：
- `limits`: 硬限制（容器不能超过）
- `reservations`: 软限制（容器保证获得）
- `cpus`: CPU限制（可以是小数，如 '0.5' 表示半个CPU）
- `memory`: 内存限制（支持单位：M、G等）

### 1.3 启动服务并查看资源限制

```bash
# 启动所有服务
docker-compose up -d

# 查看容器的资源限制
docker inspect stage6-webapp | grep -A 20 "Resources"
```

**预期输出**：
```json
"Resources": {
    "CpuQuota": 100000,
    "CpuPeriod": 100000,
    "Memory": 536870912,
    ...
}
```

### 1.4 监控资源使用情况

```bash
# 使用 docker stats 实时监控
docker stats

# 使用脚本监控
./scripts/monitor-resources.sh

# 监控特定容器
docker stats stage6-webapp
```

**观察要点**：
- CPU使用率（%）
- 内存使用量（绝对值和百分比）
- 网络I/O
- 磁盘I/O

### 1.5 测试CPU限制

```bash
# 访问资源监控页面
open http://localhost:8080/resources

# 或者使用curl触发CPU密集型任务
curl -X POST "http://localhost:8080/resources/cpu-test?iterations=50000000"
```

**实验步骤**：
1. 在资源监控页面，点击"执行CPU测试"
2. 同时运行 `docker stats stage6-webapp` 观察CPU使用率
3. 观察CPU使用率是否被限制在配置的范围内

### 1.6 测试内存限制

```bash
# 触发内存密集型任务（谨慎使用）
curl -X POST "http://localhost:8080/resources/memory-test?sizeMB=200"
```

**实验步骤**：
1. 先查看当前内存使用：`docker stats stage6-webapp`
2. 触发内存测试（分配200MB）
3. 观察内存使用是否被限制
4. 尝试分配超过限制的内存（如500MB），观察是否被拒绝

### 1.7 调整资源限制

```bash
# 编辑 docker-compose.yml，修改资源限制
# 例如，将webapp的CPU限制改为2.0，内存改为1G

# 重新创建容器以应用新配置
docker-compose up -d --force-recreate webapp

# 验证新配置
docker inspect stage6-webapp | grep -A 10 "Resources"
```

**练习**：
- 尝试不同的CPU和内存限制组合
- 观察对应用性能的影响
- 理解limits和reservations的区别

---

## 实验 2：容器健康检查

### 2.1 理解健康检查的作用

**健康检查的好处**：
- 自动检测容器是否正常运行
- 在容器不健康时自动重启
- 支持服务依赖（等待依赖服务健康后再启动）
- 提供容器状态的可视化

### 2.2 查看Dockerfile中的HEALTHCHECK

```bash
# 查看webapp的Dockerfile
cat webapp/Dockerfile | grep -A 3 HEALTHCHECK
```

**HEALTHCHECK指令**：
```dockerfile
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1
```

**参数说明**：
- `--interval`: 检查间隔（默认30秒）
- `--timeout`: 超时时间（默认30秒）
- `--start-period`: 启动宽限期（默认0秒）
- `--retries`: 连续失败次数后标记为不健康（默认3次）
- `CMD`: 健康检查命令

### 2.3 查看docker-compose中的健康检查配置

```bash
# 查看健康检查配置
grep -A 6 "healthcheck:" docker-compose.yml
```

**docker-compose健康检查**：
```yaml
healthcheck:
  test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", "http://localhost:8080/actuator/health || exit 1"]
  interval: 30s
  timeout: 10s
  retries: 3
  start_period: 60s
```

**注意**：docker-compose中的健康检查会覆盖Dockerfile中的HEALTHCHECK

### 2.4 查看容器健康状态

```bash
# 使用脚本检查健康状态
./scripts/check-health.sh

# 手动检查健康状态
docker inspect --format='{{json .State.Health}}' stage6-webapp | python3 -m json.tool

# 查看健康检查历史
docker inspect --format='{{range .State.Health.Log}}{{.Output}}{{end}}' stage6-webapp
```

**健康状态值**：
- `starting`: 启动中（在start-period内）
- `healthy`: 健康
- `unhealthy`: 不健康

### 2.5 测试健康检查

```bash
# 访问Spring Boot Actuator健康端点
curl http://localhost:8080/actuator/health

# 查看详细的健康信息
curl http://localhost:8080/actuator/health | python3 -m json.tool
```

**预期输出**：
```json
{
  "status": "UP",
  "components": {
    "db": {...},
    "diskSpace": {...},
    "ping": {...},
    "redis": {...}
  }
}
```

### 2.6 模拟健康检查失败

```bash
# 停止应用（模拟故障）
docker-compose stop webapp

# 观察健康检查状态变化
watch -n 1 'docker inspect --format="{{.State.Health.Status}}" stage6-webapp'

# 重启应用
docker-compose start webapp

# 观察健康检查恢复过程
```

### 2.7 使用健康检查实现服务依赖

查看 `docker-compose.yml` 中的 `depends_on` 配置：

```yaml
depends_on:
  mysql:
    condition: service_healthy
  redis:
    condition: service_healthy
```

**实验**：
1. 停止MySQL服务
2. 尝试重启webapp服务
3. 观察webapp是否会等待MySQL健康后再启动

---

## 实验 3：Docker 安全

### 3.1 理解容器安全最佳实践

**安全原则**：
- 最小权限原则
- 使用非root用户运行容器
- 限制容器能力（capabilities）
- 使用只读文件系统（尽可能）
- 定期更新基础镜像
- 扫描镜像漏洞

### 3.2 查看非root用户配置

```bash
# 查看Dockerfile中的用户配置
cat webapp/Dockerfile | grep -A 5 "USER"

# 检查容器运行用户
docker exec stage6-webapp whoami
```

**预期输出**：`appuser`（而不是root）

### 3.3 使用安全加固配置

```bash
# 使用安全加固版配置启动
docker-compose -f docker-compose.secure.yml up -d

# 检查容器用户
docker exec stage6-webapp-secure whoami

# 检查容器能力
docker inspect --format='{{.HostConfig.CapDrop}}' stage6-webapp-secure
docker inspect --format='{{.HostConfig.CapAdd}}' stage6-webapp-secure
```

### 3.4 测试只读文件系统

```bash
# 尝试在只读文件系统中创建文件
docker exec stage6-webapp-secure touch /app/test.txt

# 尝试在tmpfs中创建文件（应该成功）
docker exec stage6-webapp-secure touch /tmp/test.txt
```

### 3.5 镜像安全扫描

```bash
# 使用trivy扫描镜像（需要先安装trivy）
./scripts/scan-image.sh stage6-webapp:latest

# 或者使用Docker运行trivy
docker run --rm -v /var/run/docker.sock:/var/run/docker.sock \
    aquasec/trivy image stage6-webapp:latest
```

**扫描结果解读**：
- `CRITICAL`: 严重漏洞，需要立即修复
- `HIGH`: 高危漏洞，建议尽快修复
- `MEDIUM`: 中等风险
- `LOW`: 低风险

### 3.6 网络安全配置

```bash
# 查看网络配置
docker network inspect stage6-app-network

# 测试容器间通信
docker exec stage6-webapp ping -c 3 mysql
docker exec stage6-webapp ping -c 3 redis

# 测试外部访问（应该被限制）
docker exec stage6-webapp ping -c 3 8.8.8.8
```

---

## 实验 4：Docker 日志管理

### 4.1 理解日志驱动

**常用日志驱动**：
- `json-file`: 默认驱动，日志存储在JSON文件中
- `syslog`: 发送日志到syslog
- `journald`: 发送日志到systemd journal
- `gelf`: 发送日志到Graylog等
- `fluentd`: 发送日志到Fluentd

### 4.2 查看日志配置

```bash
# 查看容器的日志配置
docker inspect --format='{{json .HostConfig.LogConfig}}' stage6-webapp | python3 -m json.tool

# 查看docker-compose中的日志配置
grep -A 5 "logging:" docker-compose.yml
```

**日志配置示例**：
```yaml
logging:
  driver: "json-file"
  options:
    max-size: "10m"
    max-file: "5"
    compress: "true"
```

**参数说明**：
- `max-size`: 单个日志文件最大大小
- `max-file`: 保留的日志文件数量
- `compress`: 是否压缩旧日志文件

### 4.3 查看日志文件位置

```bash
# 查看日志文件路径
docker inspect --format='{{.LogPath}}' stage6-webapp

# 查看日志文件大小
sudo ls -lh $(docker inspect --format='{{.LogPath}}' stage6-webapp)
```

### 4.4 测试日志生成和轮转

```bash
# 使用脚本测试日志
./scripts/test-logging.sh stage6-webapp

# 手动生成日志
curl http://localhost:8080/advanced/generate-logs

# 查看日志
docker logs --tail=100 stage6-webapp

# 查看最近10分钟的日志
docker logs --since 10m stage6-webapp
```

### 4.5 测试日志轮转

```bash
# 生成大量日志（触发日志轮转）
for i in {1..1000}; do
    curl -s http://localhost:8080/advanced/generate-logs > /dev/null
    sleep 0.1
done

# 检查日志文件数量
sudo ls -lh $(dirname $(docker inspect --format='{{.LogPath}}' stage6-webapp)) | grep stage6-webapp
```

### 4.6 配置不同的日志驱动

**实验：使用syslog驱动**

```yaml
# 在docker-compose.yml中修改日志配置
logging:
  driver: "syslog"
  options:
    syslog-address: "tcp://localhost:514"
    tag: "{{.Name}}"
```

**注意**：需要先配置syslog服务器

### 4.7 集中式日志收集（高级）

**使用ELK Stack或Loki**：
- 配置日志驱动为 `gelf` 或 `fluentd`
- 将日志发送到集中式日志系统
- 实现日志的搜索、分析和可视化

---

## 实验 5：Docker 仓库管理

### 5.1 理解私有仓库的作用

**为什么需要私有仓库？**
- 保护内部镜像不被公开
- 加速镜像拉取（内网环境）
- 符合企业安全要求
- 支持镜像版本管理

### 5.2 启动私有仓库

```bash
# 使用脚本启动私有仓库
./scripts/setup-registry.sh

# 或手动启动
docker-compose -f docker-compose.registry.yml up -d

# 检查仓库状态
docker ps | grep registry
```

### 5.3 配置Docker客户端

**对于本地测试（HTTP）**：

```bash
# 编辑 /etc/docker/daemon.json（需要sudo权限）
sudo nano /etc/docker/daemon.json

# 添加以下内容：
{
  "insecure-registries": ["localhost:5000"]
}

# 重启Docker服务
sudo systemctl restart docker
```

**注意**：生产环境应使用HTTPS和认证

### 5.4 构建和标记镜像

```bash
# 进入webapp目录
cd webapp

# 构建镜像
docker build -t localhost:5000/stage6-webapp:1.0.0 .

# 查看镜像
docker images | grep stage6-webapp
```

### 5.5 推送镜像到私有仓库

```bash
# 推送镜像
docker push localhost:5000/stage6-webapp:1.0.0

# 查看推送结果
curl http://localhost:5000/v2/_catalog
```

**预期输出**：
```json
{"repositories":["stage6-webapp"]}
```

### 5.6 从私有仓库拉取镜像

```bash
# 先删除本地镜像
docker rmi localhost:5000/stage6-webapp:1.0.0

# 从私有仓库拉取
docker pull localhost:5000/stage6-webapp:1.0.0

# 验证镜像
docker images | grep stage6-webapp
```

### 5.7 查看仓库中的镜像

```bash
# 查看所有仓库
curl http://localhost:5000/v2/_catalog

# 查看特定镜像的标签
curl http://localhost:5000/v2/stage6-webapp/tags/list

# 查看镜像清单
curl http://localhost:5000/v2/stage6-webapp/manifests/1.0.0
```

### 5.8 使用Registry UI

```bash
# 访问Registry UI
open http://localhost:8082

# 在UI中可以：
# - 浏览所有镜像
# - 查看镜像标签
# - 删除镜像
# - 查看镜像详情
```

### 5.9 配置认证（高级）

**使用htpasswd创建认证文件**：

```bash
# 安装htpasswd（如果未安装）
sudo apt-get install apache2-utils  # Ubuntu/Debian
# 或
brew install httpd  # macOS

# 创建认证文件
mkdir -p registry/auth
htpasswd -Bbn admin password123 > registry/auth/htpasswd

# 修改docker-compose.registry.yml添加认证
```

---

## 综合实验：完整实践

### 实验目标

综合运用所有学到的知识，完成以下任务：

1. **配置完整的资源限制**
   - 为所有服务配置合理的CPU和内存限制
   - 监控资源使用情况
   - 验证限制是否生效

2. **实现健康检查**
   - 为所有服务配置健康检查
   - 实现基于健康检查的服务依赖
   - 测试健康检查的自动恢复

3. **应用安全加固**
   - 使用非root用户运行容器
   - 限制容器能力
   - 扫描镜像漏洞

4. **配置日志管理**
   - 配置日志轮转
   - 测试日志生成和查看
   - 验证日志文件大小限制

5. **搭建私有仓库**
   - 启动私有仓库
   - 推送和拉取镜像
   - 使用Registry UI管理镜像

### 实验步骤

```bash
# 1. 启动基础服务
docker-compose up -d

# 2. 验证资源限制
./scripts/monitor-resources.sh

# 3. 验证健康检查
./scripts/check-health.sh

# 4. 测试日志管理
./scripts/test-logging.sh

# 5. 启动私有仓库
docker-compose -f docker-compose.registry.yml up -d

# 6. 构建并推送镜像
cd webapp
docker build -t localhost:5000/stage6-webapp:1.0.0 .
docker push localhost:5000/stage6-webapp:1.0.0

# 7. 扫描镜像安全
cd ..
./scripts/scan-image.sh localhost:5000/stage6-webapp:1.0.0
```

---

## 常见问题排查

### 问题1：容器无法启动，提示资源不足

**解决方案**：
```bash
# 检查系统资源
docker system df
free -h
df -h

# 调整资源限制或释放资源
docker system prune -a
```

### 问题2：健康检查一直显示starting

**解决方案**：
```bash
# 检查健康检查命令是否正确
docker exec stage6-webapp wget --quiet --tries=1 --spider http://localhost:8080/actuator/health

# 增加start-period时间
# 检查应用是否真的启动完成
docker logs stage6-webapp
```

### 问题3：无法推送镜像到私有仓库

**解决方案**：
```bash
# 检查仓库是否运行
docker ps | grep registry

# 检查Docker daemon配置
cat /etc/docker/daemon.json

# 检查网络连接
curl http://localhost:5000/v2/
```

### 问题4：日志文件过大

**解决方案**：
```bash
# 检查日志配置
docker inspect --format='{{json .HostConfig.LogConfig}}' stage6-webapp

# 调整日志轮转配置
# 清理旧日志
docker system prune --volumes
```

---

## 学习检查点

完成本阶段学习后，你应该能够：

- [ ] 配置容器的CPU和内存限制
- [ ] 使用docker stats监控资源使用
- [ ] 在Dockerfile和docker-compose中配置健康检查
- [ ] 查看和理解容器健康状态
- [ ] 使用非root用户运行容器
- [ ] 配置容器安全选项（capabilities、只读文件系统）
- [ ] 使用工具扫描镜像安全漏洞
- [ ] 配置日志驱动和日志轮转
- [ ] 查看和管理容器日志
- [ ] 搭建和使用私有Docker仓库
- [ ] 推送和拉取镜像到私有仓库

---

## 下一步学习

完成本阶段后，建议：

1. **深入学习**：
   - 学习Kubernetes容器编排
   - 学习容器监控和告警
   - 学习CI/CD集成

2. **实践项目**：
   - 将实际项目容器化
   - 应用所有学到的安全最佳实践
   - 搭建生产级私有仓库

3. **参考资源**：
   - Docker官方文档
   - 容器安全最佳实践
   - 日志管理最佳实践

**祝你学习顺利！🚀**

