# 第七阶段实践项目：Docker 生产实践

## 项目目标

通过本实践项目，你将学会：
1. 使用 Docker Swarm 进行容器编排
2. 集成 CI/CD 流程（Jenkins、GitLab CI）
3. 实现容器监控和调试
4. 应用生产级最佳实践（多环境配置、备份恢复）
5. 构建微服务架构应用

## 项目结构

```
stage7-practice/
├── README.md                      # 本文件
├── QUICKSTART.md                 # 快速开始指南
├── practice-steps.md             # 详细实践步骤
├── docker-compose.yml            # Docker Compose 配置（开发环境）
├── docker-compose.prod.yml       # Docker Compose 生产环境配置
├── docker-stack.yml              # Docker Swarm Stack 配置
├── .env.example                  # 环境变量示例文件
├── .gitlab-ci.yml                # GitLab CI/CD 配置
├── Jenkinsfile                   # Jenkins Pipeline 配置
├── user-service/                 # 用户服务（Spring Boot 微服务）
│   ├── pom.xml
│   ├── Dockerfile                # 生产级 Dockerfile
│   ├── Dockerfile.dev            # 开发环境 Dockerfile
│   ├── .dockerignore
│   └── src/
│       └── main/
│           ├── java/com/docker/practice/userservice/
│           │   ├── UserServiceApplication.java
│           │   ├── controller/
│           │   ├── service/
│           │   ├── repository/
│           │   └── model/
│           └── resources/
│               ├── application.yml
│               └── application-prod.yml
├── order-service/                # 订单服务（Spring Boot 微服务）
│   ├── pom.xml
│   ├── Dockerfile
│   ├── Dockerfile.dev
│   ├── .dockerignore
│   └── src/
│       └── main/
│           ├── java/com/docker/practice/orderservice/
│           │   ├── OrderServiceApplication.java
│           │   ├── controller/
│           │   ├── service/
│           │   ├── repository/
│           │   └── model/
│           └── resources/
│               ├── application.yml
│               └── application-prod.yml
├── gateway-service/              # API 网关（Spring Cloud Gateway）
│   ├── pom.xml
│   ├── Dockerfile
│   ├── Dockerfile.dev
│   ├── .dockerignore
│   └── src/
│       └── main/
│           ├── java/com/docker/practice/gateway/
│           │   ├── GatewayApplication.java
│           │   └── config/
│           └── resources/
│               ├── application.yml
│               └── application-prod.yml
├── mysql/                        # MySQL 数据库
│   ├── init.sql                 # 数据库初始化脚本
│   └── backup/                  # 备份目录
├── scripts/                      # 实用脚本
│   ├── swarm-init.sh            # 初始化 Docker Swarm
│   ├── swarm-deploy.sh          # 部署 Swarm Stack
│   ├── monitor-containers.sh    # 容器监控脚本
│   ├── inspect-container.sh     # 容器详情查看脚本
│   ├── backup-database.sh       # 数据库备份脚本
│   ├── restore-database.sh      # 数据库恢复脚本
│   └── troubleshoot.sh          # 故障排查脚本
└── monitoring/                   # 监控配置
    ├── prometheus.yml            # Prometheus 配置（可选）
    └── grafana/                  # Grafana 配置（可选）
```

## 技术栈

- **Java 17** - 编程语言
- **Spring Boot 3.2** - Web 框架
- **Spring Cloud Gateway** - API 网关
- **Spring Boot Actuator** - 监控和管理
- **Maven** - 构建工具
- **MySQL 8.0** - 数据库
- **Docker Swarm** - 容器编排
- **Jenkins** - CI/CD 工具
- **GitLab CI** - CI/CD 工具

## 实践内容

### 实验 1：Docker Swarm 容器编排

- 初始化 Docker Swarm 集群
- 创建和管理 Swarm 服务
- 实现服务扩展和负载均衡
- 配置服务更新策略（滚动更新）
- 使用 Docker Stack 部署多服务应用

### 实验 2：CI/CD 集成

- 配置 Jenkins Pipeline 自动化构建和部署
- 配置 GitLab CI/CD 自动化流程
- 实现自动化镜像构建和推送
- 实现自动化测试和部署
- 配置多环境部署（开发、测试、生产）

### 实验 3：监控和调试

- 使用 `docker inspect` 查看容器详情
- 配置容器性能监控
- 使用 Spring Boot Actuator 监控应用
- 实现日志聚合和查看
- 故障排查技巧和实践

### 实验 4：生产最佳实践

- 编写生产级 Dockerfile（多阶段构建、安全加固）
- 实现多环境配置管理
- 配置数据库备份和恢复策略
- 实现健康检查和自动恢复
- 配置资源限制和监控

## 快速开始

详细步骤请参考 [QUICKSTART.md](QUICKSTART.md)

### 使用 Docker Compose（开发环境）

```bash
# 1. 进入项目目录
cd stage7-practice

# 2. 复制环境变量文件
cp .env.example .env

# 3. 启动所有服务
docker-compose up -d

# 4. 查看服务状态
docker-compose ps

# 5. 访问应用
# API 网关: http://localhost:8080
# 用户服务: http://localhost:8081
# 订单服务: http://localhost:8082
```

### 使用 Docker Swarm（生产环境）

```bash
# 1. 初始化 Swarm（单节点模式，用于学习）
./scripts/swarm-init.sh

# 2. 部署 Stack
./scripts/swarm-deploy.sh

# 3. 查看服务状态
docker service ls

# 4. 扩展服务
docker service scale user-service=3
```

## 学习要点

1. **容器编排**：使用 Docker Swarm 实现服务的高可用和负载均衡
2. **CI/CD**：自动化构建、测试和部署流程，提高开发效率
3. **监控调试**：及时发现和解决问题，保障服务稳定运行
4. **生产实践**：应用最佳实践，确保生产环境的安全和稳定

## 注意事项

- Docker Swarm 需要 Docker 1.12+ 版本
- CI/CD 配置需要根据实际环境调整
- 生产环境需要配置 HTTPS 和认证
- 备份策略需要根据业务需求制定
- 监控告警需要根据实际情况配置

## 参考资源

- [Docker Swarm 官方文档](https://docs.docker.com/engine/swarm/)
- [Jenkins Pipeline 文档](https://www.jenkins.io/doc/book/pipeline/)
- [GitLab CI/CD 文档](https://docs.gitlab.com/ee/ci/)
- [Spring Boot Actuator 文档](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Docker 生产最佳实践](https://docs.docker.com/develop/dev-best-practices/)

