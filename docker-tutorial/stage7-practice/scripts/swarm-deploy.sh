#!/bin/bash

# Docker Swarm Stack 部署脚本

set -e

STACK_NAME=${1:-stage7}
COMPOSE_FILE=${2:-docker-stack.yml}

echo "=========================================="
echo "Docker Swarm Stack 部署脚本"
echo "=========================================="

# 检查是否在 Swarm 模式
if ! docker info | grep -q "Swarm: active"; then
    echo "错误: Docker Swarm 未初始化"
    echo "请先运行: ./scripts/swarm-init.sh"
    exit 1
fi

# 检查 compose 文件是否存在
if [ ! -f "$COMPOSE_FILE" ]; then
    echo "错误: Compose 文件不存在: $COMPOSE_FILE"
    exit 1
fi

# 构建镜像（如果需要）
echo "构建 Docker 镜像..."
docker build -t user-service:latest ./user-service
docker build -t order-service:latest ./order-service
docker build -t gateway-service:latest ./gateway-service

# 部署 Stack
echo "部署 Stack: $STACK_NAME"
docker stack deploy -c "$COMPOSE_FILE" "$STACK_NAME"

# 等待服务启动
echo "等待服务启动..."
sleep 10

# 显示服务状态
echo ""
echo "=========================================="
echo "服务状态:"
echo "=========================================="
docker stack services "$STACK_NAME"

echo ""
echo "查看服务详情:"
echo "  docker stack services $STACK_NAME"
echo "  docker service ps $STACK_NAME_<service-name>"
echo "  docker service logs $STACK_NAME_<service-name>"
echo ""

