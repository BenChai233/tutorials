# 阶段一：预热与基础概念 - 知识点清单

本阶段的目标：**搞清楚“为什么要微服务”和“单体应用长什么样”**，并能用一个简单的 Spring Boot 单体项目实现「商品 + 订单」业务场景。

建议你把下面每一条都至少“理解 + 在代码里找到对应位置”：

## 1. 架构与场景

- 单体架构（Monolith）
  - 概念：一个应用打成一个包，部署在一个进程里（本项目就是）。
  - 特点：开发简单、部署简单、调试方便；后期容易变成“大泥球”。
  - 适用场景：团队小、业务简单、需求变化不剧烈。
- 微服务架构（Microservices）
  - 概念：一组小而自治的服务，通过网络协作完成业务。
  - 优点：按业务拆分、按服务独立扩缩容、技术栈可多样化。
  - 代价：分布式带来的复杂度（网络、数据一致性、观测、部署等）。
- 如何判断要不要上微服务
  - 先从单体开始：当团队、业务、性能、协作复杂度达到一定程度，再考虑拆分。
  - 阶段一重点：**先把单体写清楚**，为以后“怎么拆”打基础。

## 2. 分布式基础概念（只需要入门级理解）

- CAP 理论（Consistency / Availability / Partition tolerance）
  - 网络不可靠时，在“强一致性”和“可用性”之间要做权衡。
  - 微服务场景下，常见策略是牺牲强一致性，换取高可用。
- BASE 思想（Basically Available / Soft state / Eventual consistency）
  - “最终一致性”而不是“时时刻刻完全一致”。
  - 阶段一只要理解：**单体里通常是强一致（同库事务），微服务里经常是最终一致**。

## 3. DDD 入门（结合当前项目理解即可）

- 领域（Domain）与子域：电商可以粗分为「用户 / 商品 / 订单 / 支付 / 库存」等。
- 实体（Entity）与值对象（Value Object）
  - 实体：有唯一标识，会随时间变化，本项目中的 `Product`、`Order`。
  - 值对象：代表一个值、没有单独标识，可复用，本项目可把 `OrderItem` 看成一种“值对象风格”的结构。
- 聚合与聚合根
  - 一个订单包含多个 OrderItem，可以视作一个聚合；`Order` 是聚合根。
- 界限上下文（Bounded Context）
  - 将来拆微服务时，“商品服务 / 订单服务”就是两个不同的上下文。
  - 阶段一任务：能在代码里画出粗略的边界即可。

## 4. Spring Boot 基础

- Spring Boot 的角色
  - 简化 Spring 配置，约定优于配置。
  - 通过 “starter + 自动配置” 快速搭建 Web 应用。
- 项目结构（以 `stage1-monolith-order` 为例）
  - `Stage1MonolithOrderApplication`：启动类，`@SpringBootApplication`。
  - `model`：领域模型类。
  - `repository`：数据访问层（本项目是内存实现，后续可替换为数据库）。
  - `service`：业务逻辑层。
  - `web`：Controller、DTO、异常处理。
- 配置文件
  - `application.yml` 的作用：配置端口、日志等级、数据源等。
  - profiles 基本概念：`application-dev.yml` / `application-prod.yml` 等（本阶段了解概念即可）。

## 5. REST API 与 Web 基础

- HTTP 方法语义
  - `GET`：查；`POST`：新建；`PUT`：整体更新；`PATCH`：部分更新；`DELETE`：删除。
  - 本项目中：`GET /api/products`、`POST /api/orders` 等。
- 状态码
  - 200 OK：成功。
  - 201 Created：创建成功（本项目 `@ResponseStatus(HttpStatus.CREATED)`）。
  - 400 Bad Request：参数错误或业务校验失败（本项目异常处理会返回 400）。
- JSON 与 DTO
  - Controller 不要直接暴露复杂领域对象时，可使用 DTO（本项目中的 `CreateOrderRequest`、`OrderItemRequest`）。
  - DTO 的作用：控制入参/出参结构，避免直接暴露内部模型。
- 参数校验
  - 使用注解：`@NotBlank`、`@NotEmpty`、`@Min`、`@NotNull` 等。
  - 全局异常处理：集中处理校验失败 / 业务异常，并返回统一结构（本项目 `GlobalExceptionHandler`）。

## 6. 开发与调试基本功

- 工具链
  - JDK 17 基本安装与环境变量。
  - Maven 基本命令：`mvn clean package`、`mvn spring-boot:run`。
  - IDE 使用：导入 Maven 项目、运行 main 方法、设置断点调试。
- 本地调试习惯
  - 启动服务后，用 Postman 或 curl 调接口。
  - 通过日志观察请求 / 响应（本项目 `logging.level.org.springframework.web=debug`）。
  - 在 Service 层打断点，理清调用链（Controller → Service → Repository）。

## 7. 学习建议

- 概念与实践结合
  - 看完一个概念，就在项目中找到对应位置（例如：REST API → 找 Controller；实体 → 找 `model` 包）。
  - 不追求一次记住所有术语，重点是能“说清楚 + 在代码中指认出来”。
- 阶段一结束时，你应该能做到：
  - 口头说明单体架构和微服务的区别、大致优缺点。
  - 画出当前项目的简单架构图（Controller / Service / Repository / Model）。
  - 在现有单体项目上做简单需求修改（如增加字段、增加一个新接口）。

