# 第二阶段实践项目：Docker 镜像管理

## 项目目标

通过本实践项目，你将学会：
1. 理解镜像的分层结构和存储机制
2. 使用 Docker 命令管理镜像（search, pull, tag, push）
3. 编写 Dockerfile 的基本指令
4. 为不同技术栈的应用编写 Dockerfile（静态 Web、Node.js、Python、Java）
5. 优化镜像大小（多阶段构建、.dockerignore）
6. 理解镜像缓存机制

## 项目结构

```
stage2-practice/
├── README.md                 # 本文件
├── QUICKSTART.md            # 快速开始指南
├── practice-steps.md        # 详细实践步骤
├── simple-web/              # 简单静态 Web 应用
│   ├── index.html
│   ├── Dockerfile           # 基础 Dockerfile
│   └── Dockerfile.optimized # 优化版 Dockerfile
├── nodejs-app/              # Node.js 应用示例
│   ├── package.json
│   ├── app.js
│   ├── Dockerfile           # 基础 Dockerfile
│   ├── Dockerfile.optimized # 多阶段构建优化版
│   └── .dockerignore
├── python-app/              # Python 应用示例
│   ├── requirements.txt
│   ├── app.py
│   ├── Dockerfile           # 基础 Dockerfile
│   ├── Dockerfile.optimized # 多阶段构建优化版
│   └── .dockerignore
└── java-app/                # Java 应用示例
    ├── pom.xml
    ├── src/
    ├── Dockerfile           # 基础 Dockerfile
    ├── Dockerfile.optimized # 多阶段构建优化版
    └── .dockerignore
```

## 技术栈

- **Nginx** - 静态 Web 服务器
- **Node.js** - JavaScript 运行时
- **Python** - Python 运行时
- **Java 17** - Java 编程语言
- **Maven** - Java 构建工具

## 实践内容

### 实验 1：镜像基础操作
- 搜索和拉取镜像
- 查看镜像信息
- 给镜像打标签
- 理解镜像分层结构

### 实验 2：编写第一个 Dockerfile
- 为静态 Web 应用编写 Dockerfile
- 理解 Dockerfile 基本指令
- 构建和运行自定义镜像

### 实验 3：Node.js 应用容器化
- 编写基础 Dockerfile
- 使用多阶段构建优化镜像
- 使用 .dockerignore 减少构建上下文

### 实验 4：Python 应用容器化
- 编写基础 Dockerfile
- 优化依赖安装
- 使用多阶段构建

### 实验 5：Java 应用容器化
- 编写基础 Dockerfile
- 使用多阶段构建分离构建和运行环境
- 优化镜像大小

### 实验 6：镜像优化实践
- 对比基础版和优化版镜像大小
- 理解镜像缓存机制
- 最佳实践总结

## 开始实践

请按照 `QUICKSTART.md` 快速体验，然后按照 `practice-steps.md` 中的步骤逐步完成每个实验。

