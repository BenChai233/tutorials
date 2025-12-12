#!/bin/bash

# MySQL 数据备份脚本
# 使用方法: ./backup.sh <container_name> <database_name> <root_password>

CONTAINER_NAME=${1:-mysql-container}
DATABASE_NAME=${2:-testdb}
ROOT_PASSWORD=${3:-123456}
BACKUP_DIR="./backups"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="${BACKUP_DIR}/backup_${DATABASE_NAME}_${TIMESTAMP}.sql"

# 创建备份目录
mkdir -p ${BACKUP_DIR}

echo "开始备份数据库..."
echo "容器名称: ${CONTAINER_NAME}"
echo "数据库名称: ${DATABASE_NAME}"
echo "备份文件: ${BACKUP_FILE}"

# 执行备份
docker exec ${CONTAINER_NAME} mysqldump -uroot -p${ROOT_PASSWORD} ${DATABASE_NAME} > ${BACKUP_FILE}

if [ $? -eq 0 ]; then
    echo "备份成功！"
    echo "备份文件位置: ${BACKUP_FILE}"
    ls -lh ${BACKUP_FILE}
else
    echo "备份失败！"
    exit 1
fi

# 压缩备份文件（可选）
echo "压缩备份文件..."
gzip ${BACKUP_FILE}
echo "压缩完成: ${BACKUP_FILE}.gz"

