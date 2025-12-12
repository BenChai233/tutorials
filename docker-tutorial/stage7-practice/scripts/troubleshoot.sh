#!/bin/bash

# 故障排查脚本

set -e

echo "=========================================="
echo "Docker 故障排查工具"
echo "=========================================="
echo ""

# 1. 检查 Docker 服务状态
echo "=== 1. Docker 服务状态 ==="
if systemctl is-active --quiet docker; then
    echo "✓ Docker 服务运行中"
else
    echo "✗ Docker 服务未运行"
    echo "  启动命令: sudo systemctl start docker"
fi
echo ""

# 2. 检查容器状态
echo "=== 2. 容器状态 ==="
if docker info | grep -q "Swarm: active"; then
    echo "Swarm 模式:"
    docker service ls
    echo ""
    echo "服务任务状态:"
    docker service ps $(docker service ls -q) --no-trunc 2>/dev/null | head -20
else
    echo "Compose 模式:"
    docker ps -a --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
fi
echo ""

# 3. 检查网络
echo "=== 3. 网络状态 ==="
docker network ls
echo ""

# 4. 检查数据卷
echo "=== 4. 数据卷状态 ==="
docker volume ls
echo ""

# 5. 检查资源使用
echo "=== 5. 资源使用情况 ==="
docker stats --no-stream --format "table {{.Name}}\t{{.CPUPerc}}\t{{.MemUsage}}\t{{.NetIO}}"
echo ""

# 6. 检查最近的日志错误
echo "=== 6. 最近的错误日志 ==="
for container in $(docker ps --format "{{.Names}}"); do
    echo "容器: $container"
    docker logs --tail 5 "$container" 2>&1 | grep -i error || echo "  无错误"
    echo ""
done

# 7. 检查磁盘空间
echo "=== 7. 磁盘空间 ==="
df -h | grep -E "Filesystem|/var/lib/docker"
echo ""

# 8. 检查端口占用
echo "=== 8. 端口占用情况 ==="
netstat -tuln | grep -E ":(8080|8081|8082|3306)" || echo "  无相关端口占用"
echo ""

echo "=========================================="
echo "排查完成"
echo "=========================================="

