#!/bin/bash

# 镜像安全扫描脚本
# 使用 trivy 扫描 Docker 镜像的安全漏洞

IMAGE_NAME=${1:-"stage6-webapp:latest"}

echo "=========================================="
echo "Docker 镜像安全扫描"
echo "=========================================="
echo "镜像: $IMAGE_NAME"
echo ""

# 检查 trivy 是否安装
if ! command -v trivy &> /dev/null; then
    echo "错误: trivy 未安装"
    echo ""
    echo "安装方法:"
    echo "  macOS:   brew install trivy"
    echo "  Linux:   sudo apt-get install wget apt-transport-https gnupg lsb-release"
    echo "           wget -qO - https://aquasecurity.github.io/trivy-repo/deb/public.key | sudo apt-key add -"
    echo "           echo deb https://aquasecurity.github.io/trivy-repo/deb $(lsb_release -sc) main | sudo tee -a /etc/apt/sources.list.d/trivy.list"
    echo "           sudo apt-get update"
    echo "           sudo apt-get install trivy"
    echo ""
    echo "或者使用 Docker 运行:"
    echo "  docker run --rm -v /var/run/docker.sock:/var/run/docker.sock aquasec/trivy image $IMAGE_NAME"
    exit 1
fi

# 检查镜像是否存在
if ! docker image inspect $IMAGE_NAME &> /dev/null; then
    echo "错误: 镜像 $IMAGE_NAME 不存在"
    echo ""
    echo "请先构建镜像:"
    echo "  cd webapp && docker build -t $IMAGE_NAME ."
    exit 1
fi

echo "开始扫描..."
echo "----------------------------------------"
echo ""

# 执行扫描
trivy image --severity HIGH,CRITICAL $IMAGE_NAME

echo ""
echo "=========================================="
echo "扫描完成"
echo "=========================================="
echo ""
echo "提示: 使用 'trivy image $IMAGE_NAME' 查看所有漏洞（包括低危）"

