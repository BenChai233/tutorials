# 第三阶段实践项目：Docker 数据管理

## 项目目标

通过本实践项目，你将学会：
1. 使用数据卷（Volumes）实现数据持久化
2. 使用绑定挂载（Bind Mounts）进行开发
3. 为数据库容器配置数据卷
4. 进行数据备份和恢复
5. 理解 tmpfs 的使用场景

## 项目结构

```
stage3-practice/
├── README.md                 # 本文件
├── QUICKSTART.md            # 快速开始指南
├── mysql/                    # MySQL 数据库相关
│   ├── init.sql             # 数据库初始化脚本
│   └── backup.sh            # 数据备份脚本
├── webapp/                   # Spring Boot Web 应用（演示绑定挂载）
│   ├── pom.xml              # Maven 项目配置
│   ├── Dockerfile           # 生产环境 Dockerfile
│   ├── Dockerfile.dev       # 开发环境 Dockerfile
│   └── src/
│       └── main/
│           ├── java/com/docker/practice/
│           │   ├── WebappApplication.java    # Spring Boot 主类
│           │   ├── controller/               # 控制器
│           │   └── dto/                      # 数据传输对象
│           └── resources/
│               ├── application.yml           # 应用配置
│               └── templates/
│                   └── index.html            # 前端页面
├── nginx/                    # Nginx 配置
│   ├── nginx.conf           # Nginx 配置文件
│   └── html/                # 静态文件
├── docker-compose.yml       # Docker Compose 配置
└── practice-steps.md        # 详细实践步骤
```

## 技术栈

- **Java 17** - 编程语言
- **Spring Boot 3.2** - Web 框架
- **Maven** - 构建工具
- **MySQL 8.0** - 数据库
- **Nginx** - 反向代理
- **Thymeleaf** - 模板引擎

## 实践内容

### 实验 1：数据卷（Volumes）基础操作

### 实验 2：MySQL 数据库数据持久化

### 实验 3：绑定挂载（Bind Mounts）开发实践

### 实验 4：数据备份和恢复

### 实验 5：tmpfs 临时文件系统

## 开始实践

请按照 `practice-steps.md` 中的步骤逐步完成每个实验。

