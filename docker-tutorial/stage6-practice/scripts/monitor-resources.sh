#!/bin/bash

# 资源监控脚本
# 用于监控Docker容器的资源使用情况

echo "=========================================="
echo "Docker 容器资源监控"
echo "=========================================="
echo ""

# 检查docker stats命令是否可用
if ! command -v docker &> /dev/null; then
    echo "错误: Docker 未安装或不在PATH中"
    exit 1
fi

# 显示所有容器的资源使用情况
echo "所有容器的资源使用情况:"
echo "----------------------------------------"
docker stats --no-stream --format "table {{.Container}}\t{{.CPUPerc}}\t{{.MemUsage}}\t{{.MemPerc}}\t{{.NetIO}}\t{{.BlockIO}}"

echo ""
echo "=========================================="
echo "特定容器的详细信息:"
echo "=========================================="

# 检查特定容器
containers=("stage6-webapp" "stage6-mysql" "stage6-redis")

for container in "${containers[@]}"; do
    if docker ps --format "{{.Names}}" | grep -q "^${container}$"; then
        echo ""
        echo "容器: $container"
        echo "----------------------------------------"
        docker stats --no-stream --format "CPU: {{.CPUPerc}} | 内存: {{.MemUsage}} ({{.MemPerc}}) | 网络: {{.NetIO}} | 磁盘: {{.BlockIO}}" $container
        
        # 显示资源限制
        echo "资源限制:"
        docker inspect $container --format '  CPU限制: {{.HostConfig.CpuQuota}}/{{.HostConfig.CpuPeriod}} | 内存限制: {{.HostConfig.Memory}}'
    fi
done

echo ""
echo "=========================================="
echo "使用 'docker stats' 查看实时监控"
echo "=========================================="

