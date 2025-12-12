#!/bin/bash

# 数据库备份脚本

set -e

BACKUP_DIR="./mysql/backup"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="${BACKUP_DIR}/backup_${TIMESTAMP}.sql"
CONTAINER_NAME=${1:-stage7-mysql}
DB_NAME=${2:-testdb}
DB_USER=${3:-root}
DB_PASSWORD=${4:-123456}

echo "=========================================="
echo "数据库备份脚本"
echo "=========================================="
echo ""

# 检查容器是否存在
if ! docker ps --format "{{.Names}}" | grep -q "^${CONTAINER_NAME}$"; then
    echo "错误: 容器不存在: $CONTAINER_NAME"
    echo "可用容器:"
    docker ps --format "{{.Names}}"
    exit 1
fi

# 创建备份目录
mkdir -p "$BACKUP_DIR"

# 执行备份
echo "正在备份数据库: $DB_NAME"
echo "容器: $CONTAINER_NAME"
echo "备份文件: $BACKUP_FILE"
echo ""

docker exec "$CONTAINER_NAME" mysqldump \
    -u "$DB_USER" \
    -p"$DB_PASSWORD" \
    --single-transaction \
    --routines \
    --triggers \
    "$DB_NAME" > "$BACKUP_FILE"

# 压缩备份文件
if [ -f "$BACKUP_FILE" ]; then
    echo "压缩备份文件..."
    gzip "$BACKUP_FILE"
    BACKUP_FILE="${BACKUP_FILE}.gz"
    echo "备份完成: $BACKUP_FILE"
    echo "文件大小: $(du -h "$BACKUP_FILE" | cut -f1)"
    
    # 删除 7 天前的备份
    echo "清理旧备份（保留 7 天）..."
    find "$BACKUP_DIR" -name "backup_*.sql.gz" -mtime +7 -delete
    echo "完成"
else
    echo "错误: 备份失败"
    exit 1
fi

echo ""
echo "=========================================="
echo "备份完成"
echo "=========================================="

