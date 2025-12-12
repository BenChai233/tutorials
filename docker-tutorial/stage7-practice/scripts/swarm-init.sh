#!/bin/bash

# Docker Swarm 初始化脚本
# 用于初始化 Docker Swarm 集群（单节点模式，用于学习）

set -e

echo "=========================================="
echo "Docker Swarm 初始化脚本"
echo "=========================================="

# 检查 Docker 是否安装
if ! command -v docker &> /dev/null; then
    echo "错误: Docker 未安装，请先安装 Docker"
    exit 1
fi

# 检查 Docker 版本
DOCKER_VERSION=$(docker version --format '{{.Server.Version}}')
echo "Docker 版本: $DOCKER_VERSION"

# 检查是否已经是 Swarm 节点
if docker info | grep -q "Swarm: active"; then
    echo "警告: Docker Swarm 已经初始化"
    read -p "是否要离开当前 Swarm 并重新初始化? (y/n) " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        echo "离开当前 Swarm..."
        docker swarm leave --force 2>/dev/null || true
    else
        echo "取消操作"
        exit 0
    fi
fi

# 初始化 Swarm
echo "初始化 Docker Swarm..."
docker swarm init

# 获取 join token
echo ""
echo "=========================================="
echo "Swarm 初始化完成！"
echo "=========================================="
echo ""
echo "Manager Token:"
docker swarm join-token manager -q
echo ""
echo "Worker Token:"
docker swarm join-token worker -q
echo ""
echo "要添加更多节点，使用以下命令："
echo "  docker swarm join --token <TOKEN> <IP>:2377"
echo ""

