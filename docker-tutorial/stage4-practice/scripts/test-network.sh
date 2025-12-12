#!/bin/bash

# Docker 网络实践 - 网络连通性测试脚本

echo "=========================================="
echo "Docker 网络连通性测试"
echo "=========================================="
echo ""

# 颜色定义
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 测试函数
test_connection() {
    local container=$1
    local target=$2
    local port=${3:-80}
    
    echo -n "测试 $container -> $target:$port ... "
    
    if docker exec $container ping -c 1 $target > /dev/null 2>&1; then
        echo -e "${GREEN}✓ 连通${NC}"
        return 0
    else
        echo -e "${RED}✗ 失败${NC}"
        return 1
    fi
}

test_http() {
    local container=$1
    local url=$2
    
    echo -n "测试 HTTP $container -> $url ... "
    
    if docker exec $container curl -s -f $url > /dev/null 2>&1; then
        echo -e "${GREEN}✓ 成功${NC}"
        return 0
    else
        echo -e "${RED}✗ 失败${NC}"
        return 1
    fi
}

# 检查容器是否运行
check_container() {
    if ! docker ps | grep -q $1; then
        echo -e "${RED}错误: 容器 $1 未运行${NC}"
        return 1
    fi
    return 0
}

echo "1. 检查容器状态"
echo "----------------------------------------"
containers=("webapp-network-practice" "api-service-network-practice" "mysql-network-practice" "redis-network-practice")
for container in "${containers[@]}"; do
    if check_container $container; then
        echo -e "${GREEN}✓${NC} $container 正在运行"
    fi
done
echo ""

echo "2. 测试网络连通性"
echo "----------------------------------------"

# 测试 webapp 到其他服务
if check_container "webapp-network-practice"; then
    echo "从 webapp 测试:"
    test_connection "webapp-network-practice" "api-service"
    test_connection "webapp-network-practice" "mysql"
    test_connection "webapp-network-practice" "redis"
    test_http "webapp-network-practice" "http://api-service:5000/health"
    echo ""
fi

# 测试 api-service 到其他服务
if check_container "api-service-network-practice"; then
    echo "从 api-service 测试:"
    test_connection "api-service-network-practice" "mysql"
    test_connection "api-service-network-practice" "redis"
    echo ""
fi

echo "3. 测试 DNS 解析"
echo "----------------------------------------"
if check_container "webapp-network-practice"; then
    echo "从 webapp 测试 DNS:"
    docker exec webapp-network-practice nslookup api-service 2>/dev/null | grep -q "Name:" && echo -e "${GREEN}✓${NC} api-service DNS 解析成功" || echo -e "${RED}✗${NC} api-service DNS 解析失败"
    docker exec webapp-network-practice nslookup mysql 2>/dev/null | grep -q "Name:" && echo -e "${GREEN}✓${NC} mysql DNS 解析成功" || echo -e "${RED}✗${NC} mysql DNS 解析失败"
    docker exec webapp-network-practice nslookup redis 2>/dev/null | grep -q "Name:" && echo -e "${GREEN}✓${NC} redis DNS 解析成功" || echo -e "${RED}✗${NC} redis DNS 解析失败"
    echo ""
fi

echo "4. 查看网络信息"
echo "----------------------------------------"
echo "网络列表:"
docker network ls | grep -E "(frontend|backend|network-practice)"
echo ""

echo "测试完成！"

