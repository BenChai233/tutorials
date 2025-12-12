#!/bin/bash

# 日志测试脚本
# 用于测试Docker日志配置和日志轮转

CONTAINER_NAME=${1:-"stage6-webapp"}

echo "=========================================="
echo "Docker 日志测试"
echo "=========================================="
echo "容器: $CONTAINER_NAME"
echo ""

# 检查容器是否存在
if ! docker ps --format "{{.Names}}" | grep -q "^${CONTAINER_NAME}$"; then
    echo "错误: 容器 $CONTAINER_NAME 未运行"
    exit 1
fi

echo "1. 查看日志配置..."
echo "----------------------------------------"
docker inspect --format='{{json .HostConfig.LogConfig}}' $CONTAINER_NAME | python3 -m json.tool 2>/dev/null || docker inspect --format='{{json .HostConfig.LogConfig}}' $CONTAINER_NAME

echo ""
echo "2. 查看日志文件位置..."
echo "----------------------------------------"
log_path=$(docker inspect --format='{{.LogPath}}' $CONTAINER_NAME)
echo "日志文件: $log_path"

if [ -f "$log_path" ]; then
    file_size=$(du -h "$log_path" | cut -f1)
    echo "文件大小: $file_size"
else
    echo "提示: 日志文件可能由Docker管理，无法直接访问"
fi

echo ""
echo "3. 查看最近的日志（最后50行）..."
echo "----------------------------------------"
docker logs --tail=50 $CONTAINER_NAME

echo ""
echo "4. 生成测试日志..."
echo "----------------------------------------"
if command -v curl &> /dev/null; then
    echo "发送请求生成日志..."
    curl -s http://localhost:8080/advanced/generate-logs > /dev/null
    echo "✓ 已生成日志"
    echo ""
    echo "查看新生成的日志:"
    docker logs --tail=20 $CONTAINER_NAME
else
    echo "提示: 安装 curl 以生成测试日志"
fi

echo ""
echo "5. 实时查看日志（按Ctrl+C退出）..."
echo "----------------------------------------"
echo "使用以下命令实时查看日志:"
echo "  docker logs -f $CONTAINER_NAME"
echo ""
read -p "是否现在查看实时日志? (y/n) " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    docker logs -f $CONTAINER_NAME
fi

echo ""
echo "=========================================="
echo "日志测试完成"
echo "=========================================="
echo ""
echo "提示:"
echo "  - 使用 'docker logs --tail=100 $CONTAINER_NAME' 查看最后100行"
echo "  - 使用 'docker logs --since 10m $CONTAINER_NAME' 查看最近10分钟的日志"
echo "  - 日志轮转配置在 docker-compose.yml 的 logging 部分"

