#!/bin/bash

# 服务日志查看脚本
# 用法: ./service-logs.sh [service-name] [lines]

SERVICE=${1:-""}
LINES=${2:-100}

if [ -z "$SERVICE" ]; then
    echo "查看所有服务日志（最后 $LINES 行）:"
    docker-compose logs --tail=$LINES
else
    echo "查看 $SERVICE 服务日志（最后 $LINES 行）:"
    docker-compose logs --tail=$LINES $SERVICE
fi

echo ""
echo "提示: 使用 'docker-compose logs -f $SERVICE' 实时查看日志"

