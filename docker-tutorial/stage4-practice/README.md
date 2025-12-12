# 第四阶段实践项目：Docker 网络

## 项目目标

通过本实践项目，你将学会：
1. 理解 Docker 网络模型和网络类型
2. 创建和管理自定义网络
3. 实现容器间通信（使用容器名称）
4. 配置网络别名（aliases）
5. 理解端口映射和暴露的区别
6. 实践不同网络类型（bridge、host、none）
7. 实现网络隔离和多网络场景

## 项目结构

```
stage4-practice/
├── README.md                 # 本文件
├── QUICKSTART.md            # 快速开始指南
├── docker-compose.yml       # Docker Compose 配置（多服务、多网络）
├── docker-compose.networks.yml  # 网络实验专用配置
├── webapp/                   # Spring Boot Web 应用
│   ├── pom.xml              # Maven 项目配置
│   ├── Dockerfile           # 生产环境 Dockerfile
│   ├── Dockerfile.dev       # 开发环境 Dockerfile
│   └── src/
│       └── main/
│           ├── java/com/docker/practice/
│           │   ├── WebappApplication.java
│           │   ├── controller/               # 控制器
│           │   ├── service/                  # 服务层（调用其他服务）
│           │   └── dto/                      # 数据传输对象
│           └── resources/
│               ├── application.yml
│               └── templates/
│                   └── network.html          # 网络信息展示页面
├── api-service/             # 独立的 API 服务（演示服务间通信）
│   ├── Dockerfile
│   └── app.py               # Python Flask API
├── mysql/                   # MySQL 数据库
│   └── init.sql            # 数据库初始化脚本
├── redis/                   # Redis 缓存
│   └── redis.conf          # Redis 配置文件
├── scripts/                 # 网络测试脚本
│   ├── test-network.sh     # 网络连通性测试
│   ├── test-dns.sh         # DNS 解析测试
│   └── network-info.sh     # 网络信息查看
└── practice-steps.md       # 详细实践步骤
```

## 技术栈

- **Java 17** - Web 应用编程语言
- **Spring Boot 3.2** - Web 框架
- **Python 3** - API 服务
- **Flask** - Python Web 框架
- **MySQL 8.0** - 数据库
- **Redis 7** - 缓存
- **Nginx** - 反向代理（可选）

## 服务架构

```
┌─────────────────────────────────────────────────────────┐
│                    Docker Networks                       │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  ┌──────────────┐      ┌──────────────┐                │
│  │   frontend   │      │  api-service │                │
│  │   network    │      │   network    │                │
│  │              │      │              │                │
│  │  ┌────────┐  │      │  ┌────────┐  │                │
│  │  │ webapp │  │      │  │  api   │  │                │
│  │  │ :8080  │  │      │  │ :5000  │  │                │
│  │  └────────┘  │      │  └────────┘  │                │
│  │              │      │              │                │
│  └──────────────┘      └──────────────┘                │
│         │                      │                        │
│         └──────────┬───────────┘                        │
│                    │                                    │
│         ┌──────────▼──────────┐                         │
│         │   backend network   │                         │
│         │                     │                         │
│         │  ┌──────┐  ┌──────┐ │                         │
│         │  │mysql │  │redis │ │                         │
│         │  │ :3306│  │ :6379│ │                         │
│         │  └──────┘  └──────┘ │                         │
│         │                     │                         │
│         └─────────────────────┘                         │
│                                                          │
└─────────────────────────────────────────────────────────┘
```

## 实践内容

### 实验 1：Docker 网络基础操作
- 查看网络列表
- 创建自定义网络
- 查看网络详细信息
- 删除网络

### 实验 2：Bridge 网络实践
- 默认 bridge 网络
- 自定义 bridge 网络
- 容器间通信（使用容器名称）
- 网络隔离

### 实验 3：容器间通信
- 同一网络内容器通信
- 使用容器名称进行 DNS 解析
- 网络别名（aliases）的使用
- 跨网络通信

### 实验 4：端口映射和暴露
- 端口映射（-p）
- 端口暴露（EXPOSE）
- 动态端口映射
- 端口范围映射

### 实验 5：Host 和 None 网络
- Host 网络模式
- None 网络模式
- 使用场景对比

### 实验 6：多网络场景
- 容器加入多个网络
- 网络隔离和安全
- 服务发现

### 实验 7：综合实践
- 搭建完整的多服务应用
- 配置服务间网络通信
- 实现网络隔离

## 开始实践

请按照 `practice-steps.md` 中的步骤逐步完成每个实验。

## 前置要求

- Docker 已安装并运行
- Docker Compose 已安装
- 熟悉基本的 Docker 命令
- 了解 Docker 容器和镜像的基本概念
- 完成第三阶段的学习（了解数据卷和绑定挂载）

