#!/bin/bash

# 容器详情查看脚本

set -e

if [ -z "$1" ]; then
    echo "用法: $0 <container-name>"
    echo ""
    echo "可用容器:"
    docker ps --format "{{.Names}}"
    exit 1
fi

CONTAINER_NAME=$1

echo "=========================================="
echo "容器详情: $CONTAINER_NAME"
echo "=========================================="
echo ""

# 检查容器是否存在
if ! docker ps -a --format "{{.Names}}" | grep -q "^${CONTAINER_NAME}$"; then
    echo "错误: 容器不存在: $CONTAINER_NAME"
    exit 1
fi

# 基本信息
echo "=== 基本信息 ==="
docker inspect "$CONTAINER_NAME" --format '名称: {{.Name}}
状态: {{.State.Status}}
镜像: {{.Config.Image}}
创建时间: {{.Created}}
启动时间: {{.State.StartedAt}}'
echo ""

# 网络信息
echo "=== 网络信息 ==="
docker inspect "$CONTAINER_NAME" --format 'IP 地址: {{.NetworkSettings.IPAddress}}
MAC 地址: {{.NetworkSettings.MacAddress}}
网关: {{.NetworkSettings.Gateway}}'
echo ""

# 端口映射
echo "=== 端口映射 ==="
docker port "$CONTAINER_NAME" 2>/dev/null || echo "无端口映射"
echo ""

# 环境变量
echo "=== 环境变量 ==="
docker inspect "$CONTAINER_NAME" --format '{{range .Config.Env}}{{println .}}{{end}}' | head -20
echo ""

# 挂载卷
echo "=== 数据卷挂载 ==="
docker inspect "$CONTAINER_NAME" --format '{{range .Mounts}}{{.Type}}: {{.Source}} -> {{.Destination}}
{{end}}'
echo ""

# 资源限制
echo "=== 资源限制 ==="
NANO_CPUS=$(docker inspect "$CONTAINER_NAME" --format '{{.HostConfig.NanoCpus}}')
MEMORY=$(docker inspect "$CONTAINER_NAME" --format '{{.HostConfig.Memory}}')
if [ -n "$NANO_CPUS" ] && [ "$NANO_CPUS" != "0" ]; then
    CPU_LIMIT=$(echo "scale=2; $NANO_CPUS / 1000000000" | bc)
    echo "CPU 限制: ${CPU_LIMIT} CPUs"
else
    echo "CPU 限制: 无限制"
fi
if [ -n "$MEMORY" ] && [ "$MEMORY" != "0" ]; then
    MEMORY_MB=$(echo "scale=0; $MEMORY / 1024 / 1024" | bc)
    echo "内存限制: ${MEMORY_MB} MB"
else
    echo "内存限制: 无限制"
fi
echo ""

# 健康检查
echo "=== 健康检查 ==="
HEALTH_STATUS=$(docker inspect "$CONTAINER_NAME" --format '{{.State.Health.Status}}')
if [ -n "$HEALTH_STATUS" ] && [ "$HEALTH_STATUS" != "<no value>" ]; then
    FAILING_STREAK=$(docker inspect "$CONTAINER_NAME" --format '{{.State.Health.FailingStreak}}')
    echo "状态: $HEALTH_STATUS"
    echo "失败次数: $FAILING_STREAK"
else
    echo "未配置健康检查"
fi
echo ""

