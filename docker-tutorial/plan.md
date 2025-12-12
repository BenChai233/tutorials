# Docker 学习计划

## 学习目标
掌握 Docker 容器化技术，能够使用 Docker 进行应用开发、部署和管理。

## 学习阶段

### 第一阶段：Docker 基础入门（1-2周）

#### 1. Docker 概述
- [ ] 了解什么是 Docker 和容器化技术
- [ ] 理解 Docker 与虚拟机的区别
- [ ] 了解 Docker 的核心概念：镜像、容器、仓库
- [ ] 了解 Docker 的架构和组件

#### 2. Docker 安装与环境配置
- [ ] 在 Windows 上安装 Docker Desktop
- [ ] 验证 Docker 安装是否成功
- [ ] 配置 Docker 镜像加速器（国内用户）
- [ ] 熟悉 Docker 命令行工具

#### 3. Docker 基础命令
- [ ] `docker version` - 查看版本信息
- [ ] `docker info` - 查看系统信息
- [ ] `docker pull` - 拉取镜像
- [ ] `docker images` - 查看本地镜像
- [ ] `docker run` - 运行容器
- [ ] `docker ps` - 查看运行中的容器
- [ ] `docker stop/start/restart` - 容器生命周期管理
- [ ] `docker rm` - 删除容器
- [ ] `docker rmi` - 删除镜像

#### 4. 容器操作实践
- [ ] 运行第一个容器（Hello World）
- [ ] 运行交互式容器
- [ ] 后台运行容器
- [ ] 查看容器日志
- [ ] 进入运行中的容器
- [ ] 容器与宿主机文件复制

---

### 第二阶段：Docker 镜像管理（1-2周）

#### 1. 镜像基础
- [ ] 理解镜像的分层结构
- [ ] 了解镜像的存储机制
- [ ] 使用 `docker search` 搜索镜像
- [ ] 使用 `docker pull` 下载镜像
- [ ] 使用 `docker tag` 给镜像打标签
- [ ] 使用 `docker push` 推送镜像到仓库

#### 2. Dockerfile 编写
- [ ] 理解 Dockerfile 的作用
- [ ] 学习 Dockerfile 基本指令：
  - `FROM` - 指定基础镜像
  - `RUN` - 执行命令
  - `COPY/ADD` - 复制文件
  - `WORKDIR` - 设置工作目录
  - `EXPOSE` - 暴露端口
  - `ENV` - 设置环境变量
  - `CMD/ENTRYPOINT` - 容器启动命令
- [ ] 编写第一个 Dockerfile
- [ ] 使用 `docker build` 构建镜像
- [ ] 理解构建上下文的概念

#### 3. 镜像优化
- [ ] 了解多阶段构建（multi-stage build）
- [ ] 减少镜像大小的方法
- [ ] 使用 `.dockerignore` 文件
- [ ] 镜像缓存机制的理解

#### 4. 实践项目
- [ ] 为简单的 Web 应用编写 Dockerfile
- [ ] 为 Node.js/Python/Java 应用编写 Dockerfile
- [ ] 构建并运行自定义镜像

---

### 第三阶段：Docker 数据管理（1周）

#### 1. 数据卷（Volumes）
- [ ] 理解数据卷的作用
- [ ] 创建和管理数据卷
- [ ] 挂载数据卷到容器
- [ ] 数据卷的持久化

#### 2. 绑定挂载（Bind Mounts）
- [ ] 理解绑定挂载与数据卷的区别
- [ ] 使用绑定挂载进行开发
- [ ] 挂载配置文件

#### 3. 临时文件系统（tmpfs）
- [ ] 了解 tmpfs 的使用场景
- [ ] 使用 tmpfs 挂载

#### 4. 实践项目
- [ ] 为数据库容器配置数据卷
- [ ] 使用数据卷进行数据备份和恢复

---

### 第四阶段：Docker 网络（1-2周）

#### 1. Docker 网络基础
- [ ] 理解 Docker 网络模型
- [ ] 查看网络列表
- [ ] 创建自定义网络
- [ ] 删除网络

#### 2. 网络类型
- [ ] bridge 网络（默认网络）
- [ ] host 网络
- [ ] none 网络
- [ ] overlay 网络（用于 Swarm）

#### 3. 容器间通信
- [ ] 同一网络内容器通信
- [ ] 使用容器名称进行通信
- [ ] 使用网络别名（aliases）
- [ ] 端口映射和暴露

#### 4. 实践项目
- [ ] 搭建多容器应用（如 Web + 数据库）
- [ ] 配置容器间的网络连接

---

### 第五阶段：Docker Compose（1-2周）

#### 1. Docker Compose 基础
- [ ] 理解 Docker Compose 的作用
- [ ] 安装 Docker Compose
- [ ] 理解 docker-compose.yml 文件结构

#### 2. Compose 文件编写
- [ ] `version` - 版本声明
- [ ] `services` - 服务定义
- [ ] `networks` - 网络配置
- [ ] `volumes` - 数据卷配置
- [ ] `environment` - 环境变量
- [ ] `depends_on` - 服务依赖
- [ ] `ports` - 端口映射
- [ ] `build` - 构建配置

#### 3. Compose 命令
- [ ] `docker-compose up` - 启动服务
- [ ] `docker-compose down` - 停止服务
- [ ] `docker-compose ps` - 查看服务状态
- [ ] `docker-compose logs` - 查看日志
- [ ] `docker-compose exec` - 执行命令
- [ ] `docker-compose build` - 构建镜像
- [ ] `docker-compose restart` - 重启服务

#### 4. 实践项目
- [ ] 使用 Compose 编排多容器应用
- [ ] 搭建 LAMP/LNMP 环境
- [ ] 搭建微服务应用栈

---

### 第六阶段：Docker 高级主题（2-3周）

#### 1. 容器资源限制
- [ ] CPU 限制
- [ ] 内存限制
- [ ] 磁盘 I/O 限制
- [ ] 使用 `docker stats` 监控资源

#### 2. 容器健康检查
- [ ] 配置健康检查
- [ ] 使用 HEALTHCHECK 指令
- [ ] 监控容器健康状态

#### 3. Docker 安全
- [ ] 理解容器安全最佳实践
- [ ] 使用非 root 用户运行容器
- [ ] 镜像安全扫描
- [ ] 网络安全配置

#### 4. Docker 日志管理
- [ ] 配置日志驱动
- [ ] 日志轮转
- [ ] 集中式日志收集

#### 5. Docker 仓库管理
- [ ] 使用 Docker Hub
- [ ] 搭建私有仓库（Docker Registry）
- [ ] 使用 Harbor 等企业级仓库

---

### 第七阶段：Docker 生产实践（2-3周）

#### 1. 容器编排
- [ ] 了解 Docker Swarm
- [ ] 了解 Kubernetes 基础（可选）
- [ ] 服务发现和负载均衡

#### 2. CI/CD 集成
- [ ] Docker 与 Jenkins 集成
- [ ] Docker 与 GitLab CI 集成
- [ ] 自动化构建和部署流程

#### 3. 监控和调试
- [ ] 使用 `docker inspect` 查看容器详情
- [ ] 容器性能监控
- [ ] 故障排查技巧

#### 4. 最佳实践
- [ ] 编写生产级 Dockerfile
- [ ] 容器化应用的设计原则
- [ ] 多环境配置管理
- [ ] 备份和恢复策略

---

## 学习资源推荐

### 官方文档
- Docker 官方文档：https://docs.docker.com/
- Docker Hub：https://hub.docker.com/

### 实践项目建议
1. **简单 Web 应用容器化**
   - 使用 Nginx 或 Apache 部署静态网站
   - 使用 Node.js/Python/Java 部署动态应用

2. **数据库容器化**
   - MySQL/PostgreSQL 容器化
   - Redis 容器化
   - 数据持久化配置

3. **微服务应用**
   - 使用 Docker Compose 编排多个服务
   - 前后端分离应用部署

4. **CI/CD 实践**
   - 自动化构建镜像
   - 自动化部署流程

---

## 学习检查点

### 基础检查点
- [ ] 能够独立安装和配置 Docker
- [ ] 熟练使用常用 Docker 命令
- [ ] 能够编写简单的 Dockerfile
- [ ] 能够构建和运行自定义镜像

### 进阶检查点
- [ ] 能够使用 Docker Compose 编排多容器应用
- [ ] 理解 Docker 网络和数据管理
- [ ] 能够进行容器资源限制和监控
- [ ] 能够搭建私有镜像仓库

### 高级检查点
- [ ] 能够设计生产级容器化方案
- [ ] 能够进行容器安全加固
- [ ] 能够集成 Docker 到 CI/CD 流程
- [ ] 能够进行容器故障排查和优化

---

## 学习建议

1. **理论与实践结合**：每学习一个概念，立即动手实践
2. **循序渐进**：不要跳跃式学习，打好基础很重要
3. **多做项目**：通过实际项目加深理解
4. **记录笔记**：记录遇到的问题和解决方案
5. **参与社区**：遇到问题可以查阅文档或社区讨论
6. **持续学习**：Docker 技术更新较快，保持学习新特性

---

## 学习时间规划

- **总时长**：8-12 周（根据个人时间安排调整）
- **每日学习时间**：建议 1-2 小时
- **每周实践时间**：至少 4-6 小时

---

## 下一步行动

1. 完成第一阶段的学习和练习
2. 每完成一个阶段，在对应的复选框打勾
3. 遇到问题及时记录和解决
4. 定期回顾和总结

**祝你学习顺利！🚀**

