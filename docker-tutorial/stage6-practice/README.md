# 第六阶段实践项目：Docker 高级主题

## 项目目标

通过本实践项目，你将学会：
1. 配置容器资源限制（CPU、内存、磁盘I/O）
2. 实现容器健康检查（HEALTHCHECK指令、监控）
3. 应用Docker安全最佳实践（非root用户、安全扫描）
4. 配置Docker日志管理（日志驱动、日志轮转、集中式日志）
5. 搭建和管理私有Docker仓库

## 项目结构

```
stage6-practice/
├── README.md                      # 本文件
├── QUICKSTART.md                 # 快速开始指南
├── practice-steps.md             # 详细实践步骤
├── docker-compose.yml            # 主 Compose 配置（资源限制、健康检查、日志）
├── docker-compose.secure.yml     # 安全加固版配置
├── docker-compose.registry.yml   # 私有仓库配置
├── webapp/                       # Spring Boot Web 应用
│   ├── pom.xml
│   ├── Dockerfile                # 生产环境 Dockerfile（安全加固）
│   ├── Dockerfile.dev            # 开发环境 Dockerfile
│   ├── .dockerignore
│   └── src/
│       └── main/
│           ├── java/com/docker/practice/
│           │   ├── WebappApplication.java
│           │   ├── controller/
│           │   │   ├── AdvancedController.java    # 高级特性演示控制器
│           │   │   └── ResourceController.java    # 资源监控控制器
│           │   ├── service/
│           │   │   └── ResourceService.java       # 资源监控服务
│           │   └── config/
│           │       └── SecurityConfig.java        # 安全配置
│           └── resources/
│               ├── application.yml
│               └── templates/
│                   ├── advanced.html              # 高级特性展示页面
│                   └── resources.html             # 资源监控页面
├── registry/                     # 私有仓库配置
│   ├── docker-compose.registry.yml
│   └── README.md
├── scripts/                      # 实用脚本
│   ├── monitor-resources.sh      # 资源监控脚本
│   ├── check-health.sh           # 健康检查脚本
│   ├── scan-image.sh             # 镜像安全扫描脚本
│   ├── setup-registry.sh         # 私有仓库设置脚本
│   └── test-logging.sh           # 日志测试脚本
└── logs/                         # 日志目录（用于日志轮转演示）
    └── .gitkeep
```

## 技术栈

- **Java 17** - 编程语言
- **Spring Boot 3.2** - Web 框架
- **Spring Boot Actuator** - 健康检查和监控
- **Maven** - 构建工具
- **MySQL 8.0** - 数据库
- **Redis 7** - 缓存
- **Docker Registry** - 私有镜像仓库

## 实践内容

### 实验 1：容器资源限制

- 配置CPU限制（cpus、cpu-shares）
- 配置内存限制（memory、memory-swap）
- 配置磁盘I/O限制（blkio-config）
- 使用 `docker stats` 监控资源使用
- 测试资源限制的效果

### 实验 2：容器健康检查

- 在Dockerfile中使用HEALTHCHECK指令
- 在docker-compose.yml中配置健康检查
- 实现Spring Boot Actuator健康检查端点
- 监控容器健康状态
- 实现基于健康检查的服务依赖

### 实验 3：Docker 安全

- 使用非root用户运行容器
- 配置只读文件系统
- 限制容器能力（capabilities）
- 使用安全扫描工具（如trivy）
- 网络安全配置（网络隔离）

### 实验 4：Docker 日志管理

- 配置不同的日志驱动（json-file、syslog、journald）
- 配置日志轮转（max-size、max-file）
- 实现集中式日志收集（使用日志驱动）
- 查看和分析容器日志

### 实验 5：Docker 仓库管理

- 搭建私有Docker Registry
- 配置Registry认证
- 推送和拉取镜像到私有仓库
- 使用Harbor（可选，高级）

## 快速开始

详细步骤请参考 [QUICKSTART.md](QUICKSTART.md)

```bash
# 1. 进入项目目录
cd stage6-practice

# 2. 启动所有服务（包含资源限制、健康检查、日志配置）
docker-compose up -d

# 3. 查看服务状态和健康检查
docker-compose ps

# 4. 监控资源使用
./scripts/monitor-resources.sh

# 5. 访问应用
# Web应用: http://localhost:8080
# 健康检查: http://localhost:8080/actuator/health
```

## 学习要点

1. **资源限制**：合理配置资源限制可以防止容器占用过多系统资源
2. **健康检查**：确保容器正常运行，自动重启不健康的容器
3. **安全加固**：使用最小权限原则，降低安全风险
4. **日志管理**：合理配置日志可以方便问题排查和监控
5. **私有仓库**：保护镜像安全，加速镜像拉取

## 注意事项

- 资源限制需要根据实际需求调整
- 健康检查的间隔和超时时间需要合理设置
- 安全配置可能会影响某些功能，需要测试验证
- 日志轮转配置需要根据日志量调整
- 私有仓库需要配置HTTPS和认证（生产环境）

## 参考资源

- [Docker 官方文档 - 资源限制](https://docs.docker.com/config/containers/resource_constraints/)
- [Docker 官方文档 - 健康检查](https://docs.docker.com/engine/reference/builder/#healthcheck)
- [Docker 官方文档 - 安全](https://docs.docker.com/engine/security/)
- [Docker 官方文档 - 日志驱动](https://docs.docker.com/config/containers/logging/)
- [Docker Registry 文档](https://docs.docker.com/registry/)

