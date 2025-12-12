#!/bin/bash

# Docker 网络实践 - DNS 解析测试脚本

echo "=========================================="
echo "Docker DNS 解析测试"
echo "=========================================="
echo ""

CONTAINER=${1:-webapp-network-practice}

if ! docker ps | grep -q $CONTAINER; then
    echo "错误: 容器 $CONTAINER 未运行"
    exit 1
fi

echo "测试容器: $CONTAINER"
echo ""

# 测试服务名称解析
services=("api-service" "mysql" "redis" "db" "cache" "api" "web")

for service in "${services[@]}"; do
    echo "测试 DNS 解析: $service"
    docker exec $CONTAINER nslookup $service 2>/dev/null || echo "  解析失败"
    echo ""
done

echo "查看容器网络配置:"
docker inspect $CONTAINER | grep -A 20 "Networks"

