# Stage 1 - 单体订单示例（Spring Boot）

这个目录对应 `plan.md` 中 **阶段一：预热与基础概念** 的实践部分，目标是：

- 围绕「商品 + 订单」这个简单业务场景，先做一个「**单体应用**」。
- 通过一个完整但尽量简单的 Spring Boot 项目，熟悉基础结构、REST API、分层设计。
- 为后续阶段的「服务拆分」打基础（后面可以在此项目基础上拆成多个服务）。

## 功能概要

- 商品管理：
  - 查询商品列表 `GET /api/products`
  - 新建商品 `POST /api/products`
- 订单管理：
  - 查询订单列表 `GET /api/orders`
  - 查询单个订单 `GET /api/orders/{id}`
  - 创建订单 `POST /api/orders`

数据保存在**内存中**（`InMemoryProductRepository` / `InMemoryOrderRepository`），方便你专注在业务和分层，不引入数据库和中间件。

## 代码结构

- `Stage1MonolithOrderApplication`：应用入口，启动一个单体 Spring Boot 应用。
- `model`：领域模型，包含 `Product`、`Order`、`OrderItem`、`OrderStatus`。
- `repository`：仓储层，这里是简单的内存实现。
- `service`：业务服务层，封装下单、校验的业务逻辑。
- `web`：接口层，`Controller` + `DTO` + 全局异常处理。

建议你在看代码时，思考：

- 哪些东西属于「领域模型」，哪些属于「接口层」？
- 控制器、服务、仓储之间是如何协作的？
- 如果未来要拆成「商品服务」「订单服务」，哪些类/包会被移动到哪里？

## 运行方式

在本目录下执行：

```bash
mvn spring-boot:run
```

然后访问：

- `GET http://localhost:8080/api/products`
- `POST http://localhost:8080/api/orders`

你也可以使用 IDEA / Eclipse 直接运行 `Stage1MonolithOrderApplication`。

## 与 plan.md 的对应关系

- 对应 **第 1 周：搞清微服务是什么、适用场景** 里的实践任务：
  - 「选定一个简单业务场景（如商品订单），用单体 Spring Boot 实现一个最简版」。
- 本项目可以作为后续阶段（单体拆分为多个服务）的起点。

建议你在 `docs/` 下自己再写一份学习笔记，总结：

- 单体应用和微服务的区别（结合你写的/看到的代码）。
- 如果拆成微服务，粗略划分一下「用户 / 商品 / 订单」的边界。

