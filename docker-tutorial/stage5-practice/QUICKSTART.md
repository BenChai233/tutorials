# 快速开始指南

## 🚀 5 分钟快速体验

### 步骤 1：验证 Docker Compose 环境

```bash
# 检查 Docker Compose 是否安装
docker-compose --version

# 或者使用新版本的命令（Docker Compose V2）
docker compose version

# 检查 Docker 是否运行
docker ps
```

### 步骤 2：进入项目目录

```bash
cd stage5-practice
```

### 步骤 3：启动完整应用栈（一键启动）

```bash
# 使用默认的 docker-compose.yml 启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看服务日志
docker-compose logs -f
```

**预期结果**：应该看到以下服务启动：
- `mysql` - MySQL 数据库
- `redis` - Redis 缓存
- `webapp` - Spring Boot Web 应用
- `nginx` - Nginx 反向代理

### 步骤 4：访问应用

```bash
# 访问 Web 应用（通过 Nginx）
# 浏览器打开：http://localhost

# 或者直接访问 Web 应用
# 浏览器打开：http://localhost:8080

# 查看 Compose 信息页面
# 浏览器打开：http://localhost/compose
```

### 步骤 5：查看服务信息

```bash
# 查看所有服务状态
docker-compose ps

# 查看特定服务的日志
docker-compose logs webapp
docker-compose logs mysql

# 查看服务资源使用情况
docker stats

# 进入容器执行命令
docker-compose exec webapp sh
docker-compose exec mysql mysql -uroot -p123456
```

### 步骤 6：停止和清理

```bash
# 停止所有服务（保留容器和数据）
docker-compose stop

# 停止并删除容器（保留数据卷）
docker-compose down

# 停止并删除容器和数据卷（完全清理）
docker-compose down -v
```

---

## 📚 分步学习路径

### 实验 1：最简单的 Compose 应用（5分钟）

```bash
# 使用简化版配置
docker-compose -f docker-compose.simple.yml up -d

# 查看服务
docker-compose -f docker-compose.simple.yml ps

# 访问应用
# 浏览器打开：http://localhost:8080

# 停止服务
docker-compose -f docker-compose.simple.yml down
```

### 实验 2：理解服务依赖（10分钟）

```bash
# 启动服务并观察启动顺序
docker-compose up

# 注意观察：
# 1. MySQL 先启动
# 2. Redis 启动
# 3. Webapp 等待 MySQL 健康检查通过后才启动
# 4. Nginx 等待 Webapp 启动后才启动

# 查看服务依赖关系
docker-compose config
```

### 实验 3：环境变量管理（10分钟）

```bash
# 复制环境变量示例文件
cp .env.example .env

# 编辑 .env 文件，修改配置
# Windows: notepad .env
# Linux/Mac: nano .env

# 使用环境变量启动服务
docker-compose --env-file .env up -d

# 查看解析后的配置
docker-compose config
```

### 实验 4：服务扩展（10分钟）

```bash
# 扩展 Web 应用服务（启动 3 个实例）
docker-compose up -d --scale webapp=3

# 查看扩展后的服务
docker-compose ps

# 注意：需要配置 Nginx 负载均衡才能有效利用多个实例
```

### 实验 5：开发模式（15分钟）

```bash
# 使用开发环境配置
docker-compose -f docker-compose.dev.yml up -d

# 开发模式特点：
# - 代码热重载（绑定挂载）
# - 详细的日志输出
# - 开发工具启用

# 修改 webapp/src 下的代码，观察自动重载
```

---

## 🔧 常用命令速查

### 服务管理

```bash
# 启动服务（后台运行）
docker-compose up -d

# 启动服务（前台运行，查看日志）
docker-compose up

# 停止服务
docker-compose stop

# 停止并删除容器
docker-compose down

# 重启服务
docker-compose restart

# 重启特定服务
docker-compose restart webapp
```

### 查看信息

```bash
# 查看服务状态
docker-compose ps

# 查看服务日志
docker-compose logs

# 查看特定服务日志
docker-compose logs webapp

# 实时查看日志
docker-compose logs -f

# 查看最后 N 行日志
docker-compose logs --tail=100

# 查看配置（解析后的）
docker-compose config

# 查看服务资源使用
docker stats
```

### 执行命令

```bash
# 在服务中执行命令
docker-compose exec webapp sh
docker-compose exec mysql mysql -uroot -p123456

# 在服务中执行一次性命令
docker-compose exec webapp ls -la
docker-compose exec mysql mysql -uroot -p123456 -e "SHOW DATABASES;"
```

### 构建和镜像

```bash
# 构建镜像
docker-compose build

# 强制重新构建
docker-compose build --no-cache

# 构建特定服务
docker-compose build webapp

# 拉取镜像
docker-compose pull
```

### 扩展服务

```bash
# 扩展服务实例数
docker-compose up -d --scale webapp=3

# 注意：需要先停止现有服务
docker-compose down
docker-compose up -d --scale webapp=3
```

---

## 🐛 故障排查

### 服务无法启动

```bash
# 查看服务日志
docker-compose logs [service-name]

# 查看服务状态
docker-compose ps

# 检查端口占用
# Windows: netstat -ano | findstr :8080
# Linux/Mac: lsof -i :8080

# 检查 Docker 资源
docker system df
docker system prune  # 清理未使用的资源
```

### 服务依赖问题

```bash
# 查看服务依赖关系
docker-compose config | grep -A 5 depends_on

# 手动启动依赖服务
docker-compose up -d mysql redis

# 等待依赖服务就绪后再启动其他服务
docker-compose up -d webapp
```

### 网络连接问题

```bash
# 查看网络信息
docker network ls
docker network inspect stage5-practice_app-network

# 测试容器间连接
docker-compose exec webapp ping mysql
docker-compose exec webapp ping redis
```

### 数据卷问题

```bash
# 查看数据卷
docker volume ls
docker volume inspect stage5-practice_mysql-data

# 清理数据卷（注意：会删除数据）
docker-compose down -v
```

---

## 📖 下一步

完成快速体验后，建议：

1. **阅读 README.md** - 了解项目整体结构
2. **按照 practice-steps.md** - 逐步完成所有实验
3. **尝试修改配置** - 理解各个配置项的作用
4. **创建自己的项目** - 将学到的知识应用到实际项目

---

## 💡 提示

- 使用 `docker-compose config` 查看解析后的完整配置
- 使用 `docker-compose logs -f` 实时查看日志，方便调试
- 开发时使用 `docker-compose.dev.yml`，生产环境使用 `docker-compose.prod.yml`
- 定期清理未使用的资源：`docker system prune`
- 使用 `.env` 文件管理敏感信息和环境特定配置

**祝你学习顺利！🚀**

