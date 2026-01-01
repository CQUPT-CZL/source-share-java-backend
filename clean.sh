#!/bin/bash

# 1. 清理数据库
echo "正在清理数据库 (通过 Maven Test)..."
# 运行特定的清理测试，-q 减少日志输出
./mvnw test -Dtest=DatabaseCleanupTest -q

# 2. 清理上传的物理文件
echo "正在清理物理文件..."
rm -rf uploads/*
# 重新创建 uploads 目录以防万一
mkdir -p uploads

echo "清理完成！环境已重置。"
