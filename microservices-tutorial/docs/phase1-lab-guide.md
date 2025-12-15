# 阶段一实验：单体「商品 + 订单」系统实践指南

目标：**从 0 到 1 跑通一个简单的单体 Spring Boot 项目，并在此过程中把阶段一的知识点“落地到代码”**。

建议你按照下面步骤依次完成，可以分两三天做完，每次预留 1～2 小时的完整时间块。

---

## 第 0 步：环境准备

- 安装并确认：
  - JDK 17：`java -version`
  - Maven：`mvn -v`
  - 一个 IDE：IntelliJ IDEA（推荐）、Eclipse 或 VS Code（带 Java 插件）。
- 克隆 / 打开本仓库：
  - 在终端进入 `D:\codexProjects`，确认能看到：
    - `plan.md`
    - `readme.md`
    - `stage1-monolith-order/`

---

## 第 1 步：感知业务与架构

- 用 10 分钟在纸上或 Markdown 里写下你对“电商下单流程”的理解：
  - 用户 → 浏览商品 → 选择商品和数量 → 提交订单。
- 思考并写下来：
  - 如果只用一个系统（单体），这个流程需要哪些核心对象？（例如：商品、订单、订单项、用户）
  - 这些对象在代码里大概会长什么样？（先不看代码，凭直觉写）

---

## 第 2 步：运行单体项目

在命令行中：

- 进入目录：`cd stage1-monolith-order`
- 启动应用：
  - `mvn spring-boot:run`
  - 看到类似 “Started Stage1MonolithOrderApplication” 的日志表示启动成功，监听在 `8080` 端口。
- 用 Postman / curl 测试接口：
  - 查看商品列表：
    - `GET http://localhost:8080/api/products`
  - 创建订单（示例请求体）：
    - `POST http://localhost:8080/api/orders`
    - JSON：
      ```json
      {
        "customerName": "张三",
        "items": [
          { "productId": 1, "quantity": 2 },
          { "productId": 2, "quantity": 1 }
        ]
      }
      ```
  - 再次查看订单列表：
    - `GET http://localhost:8080/api/orders`

做到这里，你已经完成了阶段一实践里最核心的要求之一：**跑通一个简单的单体 Demo**。

---

## 第 3 步：结构导读（Controller → Service → Repository → Model）

建议在 IDE 中打开 `stage1-monolith-order`，按下面顺序看代码：

1. **入口与总体结构**
   - 打开 `Stage1MonolithOrderApplication`，确认它是 Spring Boot 启动类。
   - 在项目视图中浏览包结构：`model`、`repository`、`service`、`web`。
2. **接口层（Controller）**
   - 看 `web/ProductController` 和 `web/OrderController`：
     - 弄清楚每个接口的 URL、HTTP 方法、入参和出参。
     - 暂时先“不管实现细节”，只搞清楚它们对外暴露了什么能力。
3. **业务层（Service）**
   - 看 `service/ProductService` 和 `service/OrderService`：
     - 哪些逻辑在 Service 层做？（例如：下单时的校验）
     - 哪些事情交给 Repository 层？（例如：保存订单）
4. **数据访问层（Repository）与模型（Model）**
   - 看 `repository/InMemoryProductRepository` 和 `InMemoryOrderRepository`：
     - 理解内存存储的实现（Map + 自增 ID），思考：换成数据库时，哪些地方会变？
   - 看 `model` 包：
     - `Product`、`Order`、`OrderItem`、`OrderStatus` 分别是什么责任？
5. **异常与校验**
   - 看 `web/GlobalExceptionHandler` 和 DTO（`CreateOrderRequest`、`OrderItemRequest`）：
     - 找出所有校验注解：`@NotBlank`、`@NotEmpty`、`@Min`、`@NotNull`。
     - 想一想：如果参数不符合要求，会发生什么？返回什么样子的错误 JSON？

建议你一边看，一边在 `docs/` 下自己建一个 `notes-phase1.md`，把：

- 每个包的职责；
- 每个主要类的一句话总结；
- 你觉得“奇怪/不懂”的地方；

都记录下来。

---

## 第 4 步：小改动练习（巩固理解）

下面是几个循序渐进的小练习，帮助你“从读懂到能改动”：

### 练习 1：给订单增加一个字段（例如收货地址）

目标：感受从 Model → DTO → Controller 的完整改动链。

建议步骤：

1. 在 `model/Order` 中新增字段，例如 `private String shippingAddress;`。
2. 在 `web/dto/CreateOrderRequest` 中新增同名字段，并加上适当的校验注解（如 `@NotBlank`）。
3. 在 `OrderService#createOrder` 中，从请求对象中取出该字段，设置到新建的 `Order` 上。
4. 重新启动服务，调用 `POST /api/orders` 时带上 `shippingAddress`，确认：
   - 能创建成功；
   - `GET /api/orders` 返回的数据中能看到这个字段。

### 练习 2：增加一个查询接口（按顾客姓名查询订单）

目标：自己独立“从 Controller 往下设计一条调用链”。

提示步骤：

1. 在 `OrderRepository` 接口中新增方法签名，例如：`List<Order> findByCustomerName(String customerName);`
2. 在 `InMemoryOrderRepository` 中实现该方法（遍历 Map 过滤）。
3. 在 `OrderService` 中新增业务方法，调用 Repository。
4. 在 `OrderController` 中新增接口，例如：
   - `GET /api/orders/search?customerName=张三`
5. 测试接口是否按预期工作。

如果能完成这两个练习，说明你已经真正掌握了“一个简单单体应用的分层和改动路径”。

---

## 第 5 步：对照知识点自查

打开 `docs/phase1-knowledge.md`，做一次自查：

- 能不能用自己的话说明：
  - 单体 vs 微服务的区别与适用场景？
  - 当前项目中的领域模型有哪些？边界大致在哪？
  - Controller / Service / Repository 各自管什么？
- 在项目中找到以下对应关系：
  - REST API → `web` 包；
  - 领域模型 → `model` 包；
  - 数据持久化 → `repository` 包；
  - 业务规则 → `service` 包。

建议你写一份简短的总结（哪怕只有几百字），内容包括：

- 本阶段你实际做了什么（跑项目、看代码、做了哪些改动）；
- 对单体应用的理解；
- 你对“未来拆成微服务”的直觉思路（不用太严谨，写出自己的想法即可）。

做到这里，阶段一的“实践 + 基础概念”就算完成得比较扎实了，可以进入后续“服务拆分”的学习。

