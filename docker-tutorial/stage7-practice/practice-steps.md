# 第七阶段实践步骤详解

## 前置准备

确保你已经完成：
- [ ] Docker 20.10+ 已安装并运行
- [ ] Docker Compose 2.0+ 已安装
- [ ] Java 17 和 Maven 3.8+（用于本地开发）
- [ ] 熟悉 Docker 基础命令和 Docker Compose
- [ ] 完成前六个阶段的学习
- [ ] 了解微服务架构的基本概念

---

## 实验 1：Docker Swarm 容器编排

### 1.1 了解 Docker Swarm

**Docker Swarm 是什么**：
- Docker 原生的容器编排工具
- 可以将多个 Docker 主机组成一个集群
- 提供高可用、负载均衡、服务发现等功能
- 适合中小型应用的生产部署

**Docker Swarm vs Docker Compose**：
- Compose：单机多容器编排，适合开发环境
- Swarm：多机集群编排，适合生产环境

### 1.2 初始化 Docker Swarm

```bash
# 进入项目目录
cd stage7-practice

# 使用脚本初始化 Swarm（推荐）
./scripts/swarm-init.sh

# 或手动初始化
docker swarm init

# 查看 Swarm 状态
docker info | grep Swarm
```

**预期结果**：
- Swarm 状态显示为 `active`
- 获得 Manager Token 和 Worker Token

**关键点**：
- `docker swarm init` 将当前节点初始化为 Swarm Manager
- 单节点模式也可以用于学习和测试
- 生产环境需要多个节点（Manager + Worker）

### 1.3 查看 Swarm 节点

```bash
# 查看 Swarm 节点列表
docker node ls

# 查看节点详细信息
docker node inspect self
```

### 1.4 创建和管理 Swarm 服务

```bash
# 创建服务（示例）
docker service create \
  --name test-service \
  --replicas 2 \
  --publish 8080:80 \
  nginx:alpine

# 查看服务列表
docker service ls

# 查看服务详情
docker service ps test-service

# 查看服务日志
docker service logs test-service

# 扩展服务
docker service scale test-service=5

# 更新服务
docker service update --image nginx:latest test-service

# 删除服务
docker service rm test-service
```

### 1.5 使用 Docker Stack 部署应用

```bash
# 构建镜像（先构建镜像）
docker build -t user-service:latest ./user-service
docker build -t order-service:latest ./order-service
docker build -t gateway-service:latest ./gateway-service

# 使用脚本部署 Stack
./scripts/swarm-deploy.sh

# 或手动部署
docker stack deploy -c docker-stack.yml stage7

# 查看 Stack 服务
docker stack services stage7

# 查看服务详情
docker service ps stage7_user-service
docker service ps stage7_order-service
docker service ps stage7_gateway-service
```

**关键点**：
- Stack 是 Swarm 中一组相关服务的集合
- `docker-stack.yml` 类似于 `docker-compose.yml`，但用于 Swarm
- Stack 中的服务会自动进行负载均衡

### 1.6 服务扩展和负载均衡

```bash
# 扩展用户服务到 3 个副本
docker service scale stage7_user-service=3

# 扩展订单服务到 2 个副本
docker service scale stage7_order-service=2

# 查看服务状态
docker service ps stage7_user-service

# 测试负载均衡（多次访问）
curl http://localhost:8080/api/users
```

**预期结果**：
- 服务有多个副本运行
- 请求会被分发到不同的副本
- 服务具有高可用性

### 1.7 服务更新策略

```bash
# 滚动更新（逐步更新）
docker service update \
  --update-parallelism 1 \
  --update-delay 10s \
  --image user-service:new-version \
  stage7_user-service

# 查看更新进度
docker service ps stage7_user-service

# 回滚服务
docker service rollback stage7_user-service
```

**关键点**：
- `update-parallelism`：同时更新的副本数
- `update-delay`：更新间隔时间
- 滚动更新确保服务不中断

### 1.8 移除 Stack

```bash
# 移除整个 Stack
docker stack rm stage7

# 查看服务状态（等待完全停止）
docker stack services stage7
```

---

## 实验 2：CI/CD 集成

### 2.1 了解 CI/CD

**CI/CD 的作用**：
- **CI (Continuous Integration)**：持续集成，自动构建和测试
- **CD (Continuous Deployment)**：持续部署，自动部署到生产环境
- 提高开发效率，减少人工错误

### 2.2 配置 Jenkins Pipeline

#### 2.2.1 安装 Jenkins

```bash
# 使用 Docker 运行 Jenkins
docker run -d \
  --name jenkins \
  -p 8080:8080 \
  -p 50000:50000 \
  -v jenkins_home:/var/jenkins_home \
  jenkins/jenkins:lts

# 获取初始密码
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

#### 2.2.2 配置 Jenkins

1. 访问 http://localhost:8080
2. 输入初始密码
3. 安装推荐插件
4. 创建管理员账户

#### 2.2.3 创建 Pipeline 任务

1. 点击 "新建任务"
2. 输入任务名称，选择 "Pipeline"
3. 在 Pipeline 配置中：
   - Definition: Pipeline script from SCM
   - SCM: Git
   - Repository URL: 你的 Git 仓库地址
   - Script Path: Jenkinsfile

#### 2.2.4 运行 Pipeline

```bash
# 查看 Jenkinsfile
cat Jenkinsfile

# 在 Jenkins 中手动触发构建
# 或推送到 Git 仓库自动触发
```

**关键点**：
- Jenkinsfile 定义了构建流程
- Pipeline 支持多阶段构建
- 可以集成 Docker 构建和部署

### 2.3 配置 GitLab CI/CD

#### 2.3.1 查看 GitLab CI 配置

```bash
# 查看 .gitlab-ci.yml
cat .gitlab-ci.yml
```

#### 2.3.2 配置 GitLab Runner

```bash
# 安装 GitLab Runner（在服务器上）
# 参考：https://docs.gitlab.com/runner/install/

# 注册 Runner
gitlab-runner register
```

#### 2.3.3 推送代码触发 CI/CD

```bash
# 提交代码
git add .
git commit -m "Add CI/CD configuration"
git push origin main

# 在 GitLab 中查看 Pipeline
# 项目 -> CI/CD -> Pipelines
```

**关键点**：
- `.gitlab-ci.yml` 定义了 CI/CD 流程
- GitLab Runner 执行构建任务
- 可以配置自动部署

### 2.4 自动化构建和部署

**构建流程**：
1. 代码提交触发 CI/CD
2. 自动运行测试
3. 构建 Docker 镜像
4. 推送到镜像仓库
5. 部署到生产环境

**实践**：
- 修改代码并提交
- 观察 CI/CD 流程
- 验证自动部署

---

## 实验 3：监控和调试

### 3.1 使用 docker inspect 查看容器详情

```bash
# 使用脚本查看容器详情
./scripts/inspect-container.sh stage7-user-service

# 或手动查看
docker inspect stage7-user-service

# 查看特定信息
docker inspect stage7-user-service --format '{{.State.Status}}'
docker inspect stage7-user-service --format '{{.NetworkSettings.IPAddress}}'
docker inspect stage7-user-service --format '{{json .Config.Env}}' | jq
```

**关键点**：
- `docker inspect` 显示容器的完整配置
- 可以查看网络、挂载、环境变量等
- 使用 `--format` 可以格式化输出

### 3.2 容器性能监控

```bash
# 使用监控脚本
./scripts/monitor-containers.sh

# 或使用 docker stats
docker stats

# 查看特定容器
docker stats stage7-user-service

# 持续监控
docker stats --no-stream
```

**关键指标**：
- CPU 使用率
- 内存使用量
- 网络 I/O
- 磁盘 I/O

### 3.3 使用 Spring Boot Actuator 监控应用

```bash
# 访问健康检查端点
curl http://localhost:8081/actuator/health

# 查看应用信息
curl http://localhost:8081/actuator/info

# 查看指标
curl http://localhost:8081/actuator/metrics

# 查看 Prometheus 指标
curl http://localhost:8081/actuator/prometheus
```

**关键点**：
- Actuator 提供应用监控端点
- 可以集成 Prometheus 和 Grafana
- 支持自定义健康检查

### 3.4 日志查看和分析

```bash
# 查看容器日志
docker logs stage7-user-service

# 实时跟踪日志
docker logs -f stage7-user-service

# 查看最近 100 行日志
docker logs --tail 100 stage7-user-service

# 查看 Swarm 服务日志
docker service logs stage7_user-service

# 查看特定时间段的日志
docker logs --since 1h stage7-user-service
```

### 3.5 故障排查

```bash
# 使用故障排查脚本
./scripts/troubleshoot.sh

# 检查容器状态
docker ps -a

# 检查服务状态（Swarm）
docker service ls
docker service ps stage7_user-service

# 进入容器调试
docker exec -it stage7-user-service sh

# 查看容器资源使用
docker stats stage7-user-service
```

**常见问题**：
1. **容器无法启动**：查看日志，检查配置
2. **服务无法连接**：检查网络配置
3. **性能问题**：监控资源使用，调整限制
4. **健康检查失败**：检查应用状态

---

## 实验 4：生产最佳实践

### 4.1 编写生产级 Dockerfile

**查看生产级 Dockerfile**：

```bash
# 查看用户服务的 Dockerfile
cat user-service/Dockerfile
```

**关键特性**：
- 多阶段构建（减小镜像大小）
- 使用非 root 用户（安全）
- 配置健康检查
- 优化 JVM 参数
- 使用 Alpine 基础镜像

**实践**：
```bash
# 构建生产镜像
docker build -t user-service:prod ./user-service

# 查看镜像大小
docker images user-service

# 运行容器
docker run -d --name test-user-service user-service:prod
```

### 4.2 多环境配置管理

**查看配置文件**：

```bash
# 查看应用配置
cat user-service/src/main/resources/application.yml
cat user-service/src/main/resources/application-prod.yml
```

**环境变量配置**：

```bash
# 使用环境变量
docker run -e SPRING_PROFILES_ACTIVE=prod user-service:latest

# 在 docker-compose.yml 中配置
# environment:
#   - SPRING_PROFILES_ACTIVE=prod
```

**实践**：
- 开发环境：使用 `application.yml`
- 生产环境：使用 `application-prod.yml`
- 通过环境变量切换配置

### 4.3 数据库备份和恢复

#### 4.3.1 备份数据库

```bash
# 使用备份脚本
./scripts/backup-database.sh

# 或手动备份
docker exec stage7-mysql mysqldump \
  -u root -p123456 testdb > backup.sql
```

#### 4.3.2 恢复数据库

```bash
# 使用恢复脚本
./scripts/restore-database.sh ./mysql/backup/backup_20240101_120000.sql.gz

# 或手动恢复
docker exec -i stage7-mysql mysql \
  -u root -p123456 testdb < backup.sql
```

**备份策略**：
- 定期自动备份（使用 cron）
- 保留多个备份版本
- 测试恢复流程

### 4.4 健康检查和自动恢复

**查看健康检查配置**：

```bash
# Dockerfile 中的健康检查
grep HEALTHCHECK user-service/Dockerfile

# docker-compose.yml 中的健康检查
grep -A 5 healthcheck docker-compose.yml
```

**测试健康检查**：

```bash
# 查看容器健康状态
docker inspect stage7-user-service --format '{{json .State.Health}}' | jq

# 模拟故障（停止应用）
docker exec stage7-user-service kill 1

# 观察自动恢复
docker ps
```

### 4.5 资源限制和监控

**查看资源限制配置**：

```bash
# docker-compose.yml 中的资源限制
grep -A 10 deploy docker-compose.yml
```

**监控资源使用**：

```bash
# 实时监控
docker stats

# 查看资源限制
docker inspect stage7-user-service --format '{{json .HostConfig}}' | jq '.Resources'
```

**实践**：
- 设置合理的 CPU 和内存限制
- 监控资源使用情况
- 根据实际情况调整限制

---

## 综合实践

### 实践 1：完整的部署流程

1. **开发阶段**：
   ```bash
   # 使用 Docker Compose 启动开发环境
   docker-compose up -d
   ```

2. **测试阶段**：
   ```bash
   # 运行测试
   mvn test
   ```

3. **构建阶段**：
   ```bash
   # 构建镜像
   docker build -t user-service:latest ./user-service
   ```

4. **部署阶段**：
   ```bash
   # 使用 Swarm 部署
   docker stack deploy -c docker-stack.yml stage7
   ```

### 实践 2：服务扩展和更新

1. **扩展服务**：
   ```bash
   docker service scale stage7_user-service=5
   ```

2. **更新服务**：
   ```bash
   docker service update --image user-service:v2 stage7_user-service
   ```

3. **回滚服务**：
   ```bash
   docker service rollback stage7_user-service
   ```

### 实践 3：故障恢复

1. **模拟故障**：
   ```bash
   docker service update --replicas 0 stage7_user-service
   ```

2. **恢复服务**：
   ```bash
   docker service update --replicas 2 stage7_user-service
   ```

3. **验证恢复**：
   ```bash
   curl http://localhost:8080/api/users
   ```

---

## 总结

通过本阶段的实践，你应该掌握：

1. ✅ **Docker Swarm 容器编排**
   - 初始化和管理 Swarm 集群
   - 使用 Stack 部署多服务应用
   - 服务扩展和负载均衡
   - 滚动更新和回滚

2. ✅ **CI/CD 集成**
   - 配置 Jenkins Pipeline
   - 配置 GitLab CI/CD
   - 自动化构建和部署

3. ✅ **监控和调试**
   - 使用 docker inspect 查看容器详情
   - 监控容器性能
   - 使用 Actuator 监控应用
   - 故障排查技巧

4. ✅ **生产最佳实践**
   - 编写生产级 Dockerfile
   - 多环境配置管理
   - 数据库备份和恢复
   - 健康检查和自动恢复
   - 资源限制和监控

---

## 下一步学习

- 深入学习 Kubernetes（更强大的容器编排工具）
- 学习服务网格（Service Mesh）技术
- 探索容器安全最佳实践
- 学习云原生应用开发
- 实践大规模微服务架构

**祝你学习顺利！🚀**

