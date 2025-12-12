#!/bin/bash

# Docker Compose 清理脚本

echo "=========================================="
echo "Docker Compose 清理工具"
echo "=========================================="
echo ""

read -p "是否停止并删除所有容器? (y/n) " -n 1 -r
echo ""
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo "停止并删除容器..."
    docker-compose down
    echo "完成!"
fi

echo ""

read -p "是否删除数据卷? (y/n) " -n 1 -r
echo ""
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo "删除数据卷..."
    docker-compose down -v
    echo "完成!"
fi

echo ""

read -p "是否清理未使用的 Docker 资源? (y/n) " -n 1 -r
echo ""
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo "清理未使用的资源..."
    docker system prune -f
    echo "完成!"
fi

echo ""
echo "=========================================="
echo "清理完成!"
echo "=========================================="

