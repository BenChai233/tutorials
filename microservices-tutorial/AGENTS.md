# Repository Guidelines

## 项目结构与模块说明

- 根目录：高层文档（`readme.md`、`plan.md`、`AGENTS.md`）。
- `docs/`：学习与阶段文档（如 `phase1-knowledge.md`、`phase1-lab-guide.md`）。
- `stage1-monolith-order/`：Java Spring Boot 单体示例（商品 + 订单领域）。
  - `src/main/java/`：应用入口、Controller、Service、Repository、Model 等源码。
  - `src/main/resources/`：配置文件（如 `application.yml`）。
  - `src/test/java/`：测试代码，包结构应与 `main` 对应。

## 构建、测试与本地运行

以下命令默认在 `stage1-monolith-order/` 目录内执行：

- `mvn clean package`：清理、编译并运行测试，打包应用。
- `mvn test`：仅运行单元测试 / 集成测试。
- `mvn spring-boot:run`：本地启动 API 服务，默认端口 `8080`。

## 代码风格与命名约定

- 技术栈：Java 17、Maven、Spring Boot 3.x。
- 缩进使用 4 个空格，禁止使用 Tab。
- 类名：`PascalCase`；方法和字段：`camelCase`；常量：`UPPER_SNAKE_CASE`。
- 包结构：`com.example.stage1monolithorder.<layer>`（`web`、`service`、`repository`、`model`、`dto` 等）。
- 优先通过小而清晰的类/方法表达意图，避免冗长注释。

## 基础设施连接约定

- 所有本地 / 学习环境的中间件统一使用同一 IP：`192.168.200.128`。
- 端口号使用各组件的**默认端口**，例如：
  - 数据库（MySQL）：`192.168.200.128:3306`，用户名 `root`，密码 `root`。
  - Redis：`192.168.200.128:6379`（如需账号密码，默认使用 `root` / `root`）。
  - Nacos：`192.168.200.128:8848`（如启用登录，默认使用 `root` / `root`）。
- 如需新增其他组件（如 MQ、配置中心等），请默认采用 IP `192.168.200.128` + 组件默认端口；账号密码如无特殊说明统一采用 `root` / `root`。

## 测试规范

- 使用 Spring Boot 默认测试依赖（JUnit 等）。
- 测试位于 `src/test/java`，包路径与被测类保持一致。
- 测试类命名：`<ClassName>Test`（如 `OrderServiceTest`）。
- 新增特性应至少覆盖核心正常流程和关键边界情况。

## 提交与 Pull Request 规范

- 提交信息使用简洁的祈使句英文或中文，例如：`Add order search endpoint` / `新增订单搜索接口`。
- 同一提交只做一类变更，避免将重构与功能修改混在一起。
- PR（如使用）需说明：变更动机、主要改动点、是否有兼容性影响，并附带测试命令与执行结果。

## 面向 Agent 的说明

- 本仓库所有文件的修改均应遵循本 `AGENTS.md`。
- 尽量进行最小化、聚焦的改动，保持现有结构与命名风格。
- 如需新增阶段或服务，请沿用 `stage1-...` 目录命名模式，并同步更新 `docs/` 中相关文档。

