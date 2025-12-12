# Docker Registry 私有仓库

本目录包含私有Docker镜像仓库的配置和说明。

## 快速开始

### 启动私有仓库

```bash
# 从项目根目录启动
docker-compose -f docker-compose.registry.yml up -d

# 或使用脚本
../scripts/setup-registry.sh
```

### 访问仓库

- **Registry API**: http://localhost:5000
- **Registry UI**: http://localhost:8082

## 配置说明

### 基本配置

私有仓库使用 `registry:2` 镜像，配置了：
- 本地文件系统存储
- 允许删除镜像
- 健康检查
- 资源限制

### 存储位置

镜像数据存储在Docker卷 `stage6-registry-data` 中。

查看存储位置：
```bash
docker volume inspect stage6-registry-data
```

## 使用方法

### 1. 配置Docker客户端

对于本地测试（HTTP），需要配置Docker daemon：

编辑 `/etc/docker/daemon.json`：
```json
{
  "insecure-registries": ["localhost:5000"]
}
```

重启Docker服务：
```bash
sudo systemctl restart docker
```

### 2. 构建和标记镜像

```bash
# 构建镜像
docker build -t localhost:5000/myapp:1.0.0 .

# 标记现有镜像
docker tag myapp:latest localhost:5000/myapp:1.0.0
```

### 3. 推送镜像

```bash
docker push localhost:5000/myapp:1.0.0
```

### 4. 拉取镜像

```bash
docker pull localhost:5000/myapp:1.0.0
```

### 5. 查看仓库内容

```bash
# 查看所有仓库
curl http://localhost:5000/v2/_catalog

# 查看镜像标签
curl http://localhost:5000/v2/myapp/tags/list
```

## Registry UI

Registry UI提供了可视化的镜像管理界面：

- 浏览所有镜像
- 查看镜像标签和大小
- 删除镜像
- 查看镜像详情

访问地址：http://localhost:8082

## 生产环境配置

### HTTPS配置

生产环境应使用HTTPS。需要：
1. 配置SSL证书
2. 修改docker-compose配置
3. 更新Docker daemon配置

### 认证配置

使用htpasswd创建认证：

```bash
# 创建认证目录
mkdir -p auth

# 创建用户
htpasswd -Bbn admin password123 > auth/htpasswd

# 修改docker-compose.registry.yml添加认证卷
```

### 存储后端

可以配置不同的存储后端：
- S3
- Azure Blob Storage
- Google Cloud Storage
- 等

## 备份和恢复

### 备份

```bash
# 备份数据卷
docker run --rm -v stage6-registry-data:/data -v $(pwd):/backup \
    alpine tar czf /backup/registry-backup.tar.gz -C /data .
```

### 恢复

```bash
# 恢复数据卷
docker run --rm -v stage6-registry-data:/data -v $(pwd):/backup \
    alpine tar xzf /backup/registry-backup.tar.gz -C /data
```

## 故障排查

### 无法推送镜像

1. 检查仓库是否运行：`docker ps | grep registry`
2. 检查Docker daemon配置
3. 检查网络连接：`curl http://localhost:5000/v2/`

### 镜像无法删除

确保配置了 `REGISTRY_STORAGE_DELETE_ENABLED: "true"`

### UI无法访问

1. 检查registry-ui容器是否运行
2. 检查端口是否被占用
3. 查看容器日志：`docker logs stage6-registry-ui`

## 参考资源

- [Docker Registry 官方文档](https://docs.docker.com/registry/)
- [Registry UI 项目](https://github.com/Joxit/docker-registry-ui)

