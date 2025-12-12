#!/bin/bash

# Docker 网络实践 - 网络信息查看脚本

echo "=========================================="
echo "Docker 网络信息"
echo "=========================================="
echo ""

echo "1. 网络列表"
echo "----------------------------------------"
docker network ls
echo ""

echo "2. 网络详细信息"
echo "----------------------------------------"
networks=("frontend-network" "backend-network" "bridge" "host" "none")

for network in "${networks[@]}"; do
    if docker network ls | grep -q $network; then
        echo "网络: $network"
        docker network inspect $network | grep -E "(Name|Driver|Subnet|Gateway|Containers)" | head -20
        echo ""
    fi
done

echo "3. 容器网络配置"
echo "----------------------------------------"
containers=("webapp-network-practice" "api-service-network-practice" "mysql-network-practice" "redis-network-practice")

for container in "${containers[@]}"; do
    if docker ps | grep -q $container; then
        echo "容器: $container"
        docker inspect $container | grep -A 30 "Networks" | head -35
        echo ""
    fi
done

echo "4. 网络统计"
echo "----------------------------------------"
echo "网络数量: $(docker network ls | wc -l)"
echo "运行中的容器: $(docker ps | wc -l)"

