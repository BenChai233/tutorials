#!/bin/bash

# 数据库恢复脚本

set -e

if [ -z "$1" ]; then
    echo "用法: $0 <backup-file> [container-name] [db-name] [db-user] [db-password]"
    echo ""
    echo "示例:"
    echo "  $0 ./mysql/backup/backup_20240101_120000.sql.gz"
    exit 1
fi

BACKUP_FILE=$1
CONTAINER_NAME=${2:-stage7-mysql}
DB_NAME=${3:-testdb}
DB_USER=${4:-root}
DB_PASSWORD=${5:-123456}

echo "=========================================="
echo "数据库恢复脚本"
echo "=========================================="
echo ""

# 检查备份文件是否存在
if [ ! -f "$BACKUP_FILE" ]; then
    echo "错误: 备份文件不存在: $BACKUP_FILE"
    exit 1
fi

# 检查容器是否存在
if ! docker ps --format "{{.Names}}" | grep -q "^${CONTAINER_NAME}$"; then
    echo "错误: 容器不存在: $CONTAINER_NAME"
    exit 1
fi

# 确认操作
echo "警告: 此操作将覆盖数据库 $DB_NAME 的所有数据！"
read -p "确认继续? (yes/no) " -r
echo
if [[ ! $REPLY =~ ^[Yy][Ee][Ss]$ ]]; then
    echo "操作已取消"
    exit 0
fi

# 解压备份文件（如果是压缩的）
TEMP_FILE=""
if [[ "$BACKUP_FILE" == *.gz ]]; then
    echo "解压备份文件..."
    TEMP_FILE=$(mktemp)
    gunzip -c "$BACKUP_FILE" > "$TEMP_FILE"
    BACKUP_FILE="$TEMP_FILE"
fi

# 执行恢复
echo "正在恢复数据库: $DB_NAME"
echo "容器: $CONTAINER_NAME"
echo "备份文件: $1"
echo ""

docker exec -i "$CONTAINER_NAME" mysql \
    -u "$DB_USER" \
    -p"$DB_PASSWORD" \
    "$DB_NAME" < "$BACKUP_FILE"

# 清理临时文件
if [ -n "$TEMP_FILE" ] && [ -f "$TEMP_FILE" ]; then
    rm "$TEMP_FILE"
fi

echo ""
echo "=========================================="
echo "恢复完成"
echo "=========================================="

