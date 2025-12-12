#!/bin/bash

# Docker Compose 信息查看脚本

echo "=========================================="
echo "Docker Compose 信息查看"
echo "=========================================="
echo ""

echo "1. 服务状态:"
docker-compose ps
echo ""

echo "2. 网络信息:"
docker network ls | grep compose
echo ""

echo "3. 数据卷信息:"
docker volume ls | grep compose
echo ""

echo "4. 服务资源使用:"
docker stats --no-stream --format "table {{.Container}}\t{{.CPUPerc}}\t{{.MemUsage}}"
echo ""

echo "5. 配置信息（前50行）:"
docker-compose config | head -50
echo ""

echo "=========================================="

