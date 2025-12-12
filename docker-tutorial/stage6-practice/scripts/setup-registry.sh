#!/bin/bash

# 私有仓库设置脚本
# 用于设置和配置私有Docker Registry

echo "=========================================="
echo "Docker 私有仓库设置"
echo "=========================================="
echo ""

# 检查docker命令是否可用
if ! command -v docker &> /dev/null; then
    echo "错误: Docker 未安装或不在PATH中"
    exit 1
fi

REGISTRY_PORT=${REGISTRY_PORT:-5000}
REGISTRY_HOST="localhost:${REGISTRY_PORT}"

echo "1. 启动私有仓库..."
echo "----------------------------------------"
docker-compose -f docker-compose.registry.yml up -d

echo ""
echo "等待仓库启动..."
sleep 5

echo ""
echo "2. 检查仓库状态..."
echo "----------------------------------------"
if docker ps --format "{{.Names}}" | grep -q "^stage6-registry$"; then
    echo "✓ 仓库容器运行中"
    
    # 检查健康状态
    health=$(docker inspect --format='{{.State.Health.Status}}' stage6-registry 2>/dev/null || echo "N/A")
    echo "  健康状态: $health"
else
    echo "✗ 仓库容器未运行"
    exit 1
fi

echo ""
echo "3. 测试仓库连接..."
echo "----------------------------------------"
if command -v curl &> /dev/null; then
    response=$(curl -s -o /dev/null -w "%{http_code}" http://${REGISTRY_HOST}/v2/ 2>/dev/null)
    if [ "$response" = "200" ] || [ "$response" = "401" ]; then
        echo "✓ 仓库可访问 (HTTP $response)"
    else
        echo "✗ 仓库不可访问 (HTTP $response)"
    fi
else
    echo "提示: 安装 curl 以测试HTTP连接"
fi

echo ""
echo "4. 配置Docker客户端（如果需要）..."
echo "----------------------------------------"
echo "注意: 如果使用HTTPS或需要认证，需要配置Docker daemon"
echo ""
echo "对于本地测试（HTTP），可以配置 /etc/docker/daemon.json:"
echo "{"
echo "  \"insecure-registries\": [\"${REGISTRY_HOST}\"]"
echo "}"
echo ""
echo "然后重启Docker服务:"
echo "  sudo systemctl restart docker"

echo ""
echo "5. 使用示例..."
echo "----------------------------------------"
echo "构建并标记镜像:"
echo "  cd webapp"
echo "  docker build -t ${REGISTRY_HOST}/stage6-webapp:1.0.0 ."
echo ""
echo "推送镜像:"
echo "  docker push ${REGISTRY_HOST}/stage6-webapp:1.0.0"
echo ""
echo "拉取镜像:"
echo "  docker pull ${REGISTRY_HOST}/stage6-webapp:1.0.0"
echo ""
echo "查看仓库中的镜像:"
echo "  curl http://${REGISTRY_HOST}/v2/_catalog"

echo ""
echo "6. 访问Registry UI（如果已启动）..."
echo "----------------------------------------"
echo "  http://localhost:8082"

echo ""
echo "=========================================="
echo "设置完成"
echo "=========================================="

