# 第五阶段实践项目：Docker Compose

## 项目目标

通过本实践项目，你将学会：
1. 理解 Docker Compose 的作用和优势
2. 编写 docker-compose.yml 文件
3. 使用 Compose 编排多容器应用
4. 配置服务依赖、网络和数据卷
5. 使用环境变量管理配置
6. 掌握 Compose 常用命令
7. 搭建完整的 LNMP 应用栈
8. 实现多环境配置管理

## 项目结构

```
stage5-practice/
├── README.md                      # 本文件
├── QUICKSTART.md                 # 快速开始指南
├── practice-steps.md             # 详细实践步骤
├── docker-compose.yml            # 主 Compose 配置文件（完整 LNMP 栈）
├── docker-compose.simple.yml     # 简化版 Compose 配置（学习用）
├── docker-compose.prod.yml       # 生产环境配置示例
├── docker-compose.dev.yml        # 开发环境配置示例
├── .env.example                  # 环境变量示例文件
├── webapp/                       # Spring Boot Web 应用
│   ├── pom.xml
│   ├── Dockerfile
│   ├── Dockerfile.dev
│   └── src/
│       └── main/
│           ├── java/com/docker/practice/
│           │   ├── WebappApplication.java
│           │   ├── controller/
│           │   │   └── ComposeController.java
│           │   └── service/
│           │       └── ComposeService.java
│           └── resources/
│               ├── application.yml
│               └── templates/
│                   └── compose.html
├── mysql/                        # MySQL 数据库
│   └── init.sql                 # 数据库初始化脚本
├── nginx/                        # Nginx 反向代理
│   ├── nginx.conf               # Nginx 配置文件
│   └── html/                    # 静态文件目录
├── redis/                        # Redis 缓存
│   └── redis.conf               # Redis 配置文件
└── scripts/                      # 辅助脚本
    ├── compose-info.sh          # Compose 信息查看脚本
    ├── service-logs.sh          # 服务日志查看脚本
    └── cleanup.sh               # 清理脚本
```

## 技术栈

- **Java 17** - Web 应用编程语言
- **Spring Boot 3.2** - Web 框架
- **MySQL 8.0** - 关系型数据库
- **Redis 7** - 缓存数据库
- **Nginx** - 反向代理和负载均衡
- **Docker Compose** - 容器编排工具

## 服务架构

```
┌─────────────────────────────────────────────────────────┐
│              Docker Compose 编排的应用栈                  │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  ┌──────────────────────────────────────────────┐       │
│  │         Nginx (反向代理) :80                  │       │
│  │  ┌────────────────────────────────────────┐  │       │
│  │  │  /api/*  →  Webapp :8080               │  │       │
│  │  │  /static →  静态文件                    │  │       │
│  │  └────────────────────────────────────────┘  │       │
│  └──────────────────────────────────────────────┘       │
│                    │                                     │
│         ┌──────────┴──────────┐                         │
│         │                     │                         │
│  ┌──────▼──────┐      ┌──────▼──────┐                  │
│  │   Webapp    │      │   Redis     │                  │
│  │  :8080      │      │   :6379     │                  │
│  │             │      │             │                  │
│  └──────┬──────┘      └─────────────┘                  │
│         │                                               │
│         │                                               │
│  ┌──────▼──────┐                                        │
│  │   MySQL     │                                        │
│  │   :3306     │                                        │
│  └─────────────┘                                        │
│                                                          │
└─────────────────────────────────────────────────────────┘
```

## 实践内容

### 实验 1：Docker Compose 基础
- 理解 Compose 的作用和优势
- 安装和验证 Docker Compose
- 理解 docker-compose.yml 文件结构

### 实验 2：Compose 文件编写基础
- `version` - 版本声明
- `services` - 服务定义
- `networks` - 网络配置
- `volumes` - 数据卷配置

### 实验 3：服务配置详解
- `environment` - 环境变量
- `depends_on` - 服务依赖
- `ports` - 端口映射
- `build` - 构建配置
- `healthcheck` - 健康检查

### 实验 4：Compose 命令实践
- `docker-compose up` - 启动服务
- `docker-compose down` - 停止服务
- `docker-compose ps` - 查看服务状态
- `docker-compose logs` - 查看日志
- `docker-compose exec` - 执行命令
- `docker-compose build` - 构建镜像
- `docker-compose restart` - 重启服务

### 实验 5：多服务编排实践
- 使用 Compose 编排 LNMP 应用栈
- 配置服务间依赖关系
- 实现服务健康检查
- 配置服务重启策略

### 实验 6：环境变量和配置管理
- 使用 `.env` 文件管理环境变量
- 多环境配置（开发/生产）
- 使用 `environment` 和 `env_file`

### 实验 7：网络和数据卷管理
- 自定义网络配置
- 数据卷的创建和管理
- 绑定挂载的使用

### 实验 8：扩展和扩展服务
- 使用 `docker-compose scale` 扩展服务
- 配置服务副本
- 负载均衡配置

## 开始实践

请按照 `practice-steps.md` 中的步骤逐步完成每个实验。

## 前置要求

- Docker 已安装并运行
- Docker Compose 已安装（Docker Desktop 自带）
- 熟悉基本的 Docker 命令
- 了解 Docker 容器、镜像、网络和数据卷的基本概念
- 完成第三阶段和第四阶段的学习

## 快速开始

如果你想快速体验，请查看 `QUICKSTART.md` 文件。

