#!/bin/bash

# 容器监控脚本
# 显示容器的资源使用情况

set -e

echo "=========================================="
echo "容器资源监控"
echo "=========================================="
echo ""

# 检查是否使用 Compose 或 Swarm
if docker info | grep -q "Swarm: active"; then
    echo "Docker Swarm 模式"
    echo ""
    echo "服务列表:"
    docker service ls
    echo ""
    echo "资源使用情况:"
    docker stats --no-stream --format "table {{.Name}}\t{{.CPUPerc}}\t{{.MemUsage}}\t{{.NetIO}}\t{{.BlockIO}}"
else
    echo "Docker Compose 模式"
    echo ""
    echo "容器列表:"
    docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
    echo ""
    echo "资源使用情况:"
    docker stats --no-stream --format "table {{.Name}}\t{{.CPUPerc}}\t{{.MemUsage}}\t{{.NetIO}}\t{{.BlockIO}}"
fi

echo ""
echo "持续监控 (按 Ctrl+C 退出):"
read -p "是否启动持续监控? (y/n) " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    if docker info | grep -q "Swarm: active"; then
        docker stats
    else
        docker stats
    fi
fi

