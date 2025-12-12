# Spring Boot Web 应用

这是一个 Spring Boot 应用，用于演示 Docker 数据管理中的绑定挂载（Bind Mounts）功能。

## 技术栈

- Java 17
- Spring Boot 3.2.0
- Maven 3.9
- Thymeleaf（模板引擎）
- Spring Boot DevTools（热重载支持）
- MySQL Connector（数据库连接）

## 项目结构

```
webapp/
├── pom.xml                          # Maven 项目配置
├── Dockerfile                       # 生产环境 Dockerfile（多阶段构建）
├── Dockerfile.dev                   # 开发环境 Dockerfile
├── src/
│   └── main/
│       ├── java/com/docker/practice/
│       │   ├── WebappApplication.java      # Spring Boot 主类
│       │   ├── controller/
│       │   │   └── HomeController.java     # 控制器（主页和 API）
│       │   └── dto/
│       │       ├── ApiInfo.java            # API 信息 DTO
│       │       └── ApiData.java            # API 数据 DTO
│       └── resources/
│           ├── application.yml             # 应用配置文件
│           └── templates/
│               └── index.html              # 前端页面
└── README.md
```

## 本地运行（不使用 Docker）

### 前置要求

- JDK 17 或更高版本
- Maven 3.6 或更高版本
- MySQL 8.0（可选，用于数据库连接）

### 运行步骤

```bash
# 1. 编译项目
mvn clean package

# 2. 运行应用
mvn spring-boot:run

# 或者运行 JAR 文件
java -jar target/webapp-1.0.0.jar
```

应用将在 http://localhost:8080 启动

## Docker 运行

### 开发模式（使用绑定挂载）

```bash
# 使用 Dockerfile.dev
docker build -f Dockerfile.dev -t springboot-dev .
docker run -d \
  --name springboot-dev \
  -v ${PWD}:/app \
  -p 8080:8080 \
  -v maven-cache:/root/.m2 \
  springboot-dev
```

### 生产模式

```bash
# 使用 Dockerfile（多阶段构建）
docker build -t springboot-app .
docker run -d \
  --name springboot-app \
  -p 8080:8080 \
  springboot-app
```

## 使用 Docker Compose

```bash
# 在项目根目录运行
docker-compose up -d webapp
```

## API 端点

- `GET /` - 主页（Thymeleaf 模板）
- `GET /api/info` - 获取应用信息（JSON）
- `GET /api/data` - 获取示例数据（JSON）

## 开发提示

1. **热重载**：使用 Spring Boot DevTools，修改代码后会自动重启
2. **绑定挂载**：在开发模式下，代码修改会立即反映到容器中
3. **Maven 缓存**：使用数据卷缓存 Maven 依赖，加速构建

## 配置说明

应用配置在 `src/main/resources/application.yml` 中：

- 服务器端口：8080
- MySQL 连接：通过 Docker Compose 中的 mysql 服务
- Thymeleaf 缓存：开发模式下关闭

## 常见问题

### 1. 首次启动很慢？

首次运行需要下载 Maven 依赖，可能需要几分钟。使用 Maven 缓存数据卷可以加速后续构建。

### 2. 代码修改不生效？

确保使用开发模式（Dockerfile.dev）并启用了 Spring Boot DevTools。

### 3. 端口冲突？

修改 `application.yml` 中的 `server.port` 或 Docker 端口映射。

