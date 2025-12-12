#!/bin/bash

# 健康检查脚本
# 用于检查Docker容器的健康状态

echo "=========================================="
echo "Docker 容器健康检查"
echo "=========================================="
echo ""

# 检查docker命令是否可用
if ! command -v docker &> /dev/null; then
    echo "错误: Docker 未安装或不在PATH中"
    exit 1
fi

# 检查特定容器
containers=("stage6-webapp" "stage6-mysql" "stage6-redis")

for container in "${containers[@]}"; do
    if docker ps --format "{{.Names}}" | grep -q "^${container}$"; then
        echo "容器: $container"
        echo "----------------------------------------"
        
        # 检查容器状态
        status=$(docker inspect --format='{{.State.Status}}' $container)
        health=$(docker inspect --format='{{.State.Health.Status}}' $container 2>/dev/null || echo "N/A")
        
        echo "  状态: $status"
        if [ "$health" != "N/A" ]; then
            echo "  健康状态: $health"
            
            # 显示健康检查详情
            if [ "$health" != "healthy" ]; then
                echo "  健康检查日志:"
                docker inspect --format='{{range .State.Health.Log}}{{.Output}}{{end}}' $container | tail -3 | sed 's/^/    /'
            fi
        else
            echo "  健康检查: 未配置"
        fi
        
        # 检查端口
        ports=$(docker port $container 2>/dev/null | awk '{print $1}' | tr '\n' ' ')
        if [ -n "$ports" ]; then
            echo "  端口映射: $ports"
        fi
        
        echo ""
    else
        echo "容器 $container 未运行"
        echo ""
    fi
done

echo "=========================================="
echo "应用健康检查端点:"
echo "=========================================="
echo ""

# 检查Web应用健康端点
if docker ps --format "{{.Names}}" | grep -q "^stage6-webapp$"; then
    echo "检查 Spring Boot Actuator 健康端点..."
    health_url="http://localhost:8080/actuator/health"
    
    if command -v curl &> /dev/null; then
        response=$(curl -s -o /dev/null -w "%{http_code}" $health_url 2>/dev/null)
        if [ "$response" = "200" ]; then
            echo "✓ 健康端点可访问: $health_url"
            curl -s $health_url | python3 -m json.tool 2>/dev/null || curl -s $health_url
        else
            echo "✗ 健康端点不可访问 (HTTP $response): $health_url"
        fi
    else
        echo "提示: 安装 curl 以检查HTTP端点"
    fi
fi

echo ""
echo "=========================================="

