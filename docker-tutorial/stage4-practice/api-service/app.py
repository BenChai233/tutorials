#!/usr/bin/env python3
"""
Docker 网络实践 - Python Flask API 服务
演示容器间网络通信
"""

from flask import Flask, jsonify
import redis
import pymysql
import socket
import os
import time

app = Flask(__name__)

# 配置
REDIS_HOST = os.getenv('REDIS_HOST', 'redis')
REDIS_PORT = int(os.getenv('REDIS_PORT', 6379))
MYSQL_HOST = os.getenv('MYSQL_HOST', 'mysql')
MYSQL_PORT = int(os.getenv('MYSQL_PORT', 3306))
MYSQL_DATABASE = os.getenv('MYSQL_DATABASE', 'testdb')
MYSQL_USER = os.getenv('MYSQL_USER', 'appuser')
MYSQL_PASSWORD = os.getenv('MYSQL_PASSWORD', 'apppass')

# 初始化 Redis 连接
try:
    redis_client = redis.Redis(host=REDIS_HOST, port=REDIS_PORT, decode_responses=True)
    redis_client.ping()
    redis_connected = True
except Exception as e:
    print(f"Redis connection failed: {e}")
    redis_connected = False

# 获取 MySQL 连接
def get_mysql_connection():
    try:
        return pymysql.connect(
            host=MYSQL_HOST,
            port=MYSQL_PORT,
            user=MYSQL_USER,
            password=MYSQL_PASSWORD,
            database=MYSQL_DATABASE,
            cursorclass=pymysql.cursors.DictCursor
        )
    except Exception as e:
        print(f"MySQL connection failed: {e}")
        return None

@app.route('/health', methods=['GET'])
def health():
    """健康检查端点"""
    return jsonify({
        'status': 'healthy',
        'service': 'api-service',
        'hostname': socket.gethostname(),
        'timestamp': time.time()
    })

@app.route('/api/info', methods=['GET'])
def api_info():
    """API 信息端点"""
    return jsonify({
        'service': 'api-service',
        'version': '1.0.0',
        'hostname': socket.gethostname(),
        'networks': {
            'frontend-network': True,
            'backend-network': True
        },
        'connected_services': {
            'redis': redis_connected,
            'mysql': get_mysql_connection() is not None
        }
    })

@app.route('/api/redis/test', methods=['GET'])
def redis_test():
    """测试 Redis 连接"""
    if not redis_connected:
        return jsonify({'error': 'Redis not connected'}), 500
    
    try:
        # 设置和获取值
        redis_client.set('test_key', 'test_value', ex=60)
        value = redis_client.get('test_key')
        
        # 获取 Redis 信息
        info = redis_client.info()
        
        return jsonify({
            'status': 'success',
            'test_key': value,
            'redis_host': REDIS_HOST,
            'redis_port': REDIS_PORT,
            'redis_info': {
                'connected_clients': info.get('connected_clients', 0),
                'used_memory_human': info.get('used_memory_human', 'N/A')
            }
        })
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@app.route('/api/mysql/test', methods=['GET'])
def mysql_test():
    """测试 MySQL 连接"""
    conn = get_mysql_connection()
    if not conn:
        return jsonify({'error': 'MySQL not connected'}), 500
    
    try:
        with conn.cursor() as cursor:
            # 查询用户数量
            cursor.execute("SELECT COUNT(*) as count FROM users")
            result = cursor.fetchone()
            
            # 查询所有用户
            cursor.execute("SELECT * FROM users LIMIT 10")
            users = cursor.fetchall()
            
            return jsonify({
                'status': 'success',
                'mysql_host': MYSQL_HOST,
                'mysql_port': MYSQL_PORT,
                'database': MYSQL_DATABASE,
                'user_count': result['count'],
                'users': users
            })
    except Exception as e:
        return jsonify({'error': str(e)}), 500
    finally:
        conn.close()

@app.route('/api/network/info', methods=['GET'])
def network_info():
    """获取网络信息"""
    hostname = socket.gethostname()
    
    # 获取容器 IP 地址
    try:
        ip_address = socket.gethostbyname(hostname)
    except:
        ip_address = 'unknown'
    
    return jsonify({
        'hostname': hostname,
        'ip_address': ip_address,
        'redis_host': REDIS_HOST,
        'mysql_host': MYSQL_HOST,
        'services': {
            'redis': {
                'host': REDIS_HOST,
                'port': REDIS_PORT,
                'connected': redis_connected
            },
            'mysql': {
                'host': MYSQL_HOST,
                'port': MYSQL_PORT,
                'connected': get_mysql_connection() is not None
            }
        }
    })

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)

