# Stage 01 实验工程设计（Week 1）

目标：用最小可运行示例体会线程创建、调度、上下文切换成本与竞态现象，为后续同步与内存模型打基础。所有实验均可用纯 `javac`/`java` 或 Maven/Gradle（任选一种），保持代码精简、日志清晰。

## 推荐目录结构
```
docs/
  stage-01/
    week1-concepts.md
    experiments.md   <-- 本文件
src/
  main/java/
    com/example/concurrency/stage01/
      ThreadBasics.java
      ContextSwitchCost.java
      SharedCounterRace.java
      ThreadDumpHelper.java (可选)
```

## 实验 1：线程基础与调度观察（ThreadBasics）
- 目的：熟悉线程创建、命名、启动、join，观察输出交错。
- 要求：
  - 创建 3–5 个线程，线程名含序号，循环打印标识与计数。
  - 主线程 `join` 等待，结束前打印 “main done”。
  - 日志包含时间戳与线程名（可用 `SimpleDateFormat` 或 `DateTimeFormatter`）。
- 验收：输出交错明显，所有线程均正常结束，无卡死。

## 实验 2：上下文切换成本感知（ContextSwitchCost）
- 目的：感知多线程切换带来的额外开销。
- 要求：
  - 基准 1：单线程循环累加（如 `long counter++` 1e8 次），记录耗时。
  - 基准 2：使用 N 个线程（N=CPU 核数或 2×核数），各自累加分片或共享变量（可先分片避免锁），记录总耗时。
  - 日志打印：线程数、每段耗时（纳秒或毫秒）、总计。
- 验收：
  - 能看到线程数上升后耗时趋势（一般多线程不一定更快）。
  - 说明原因（切换、缓存失效、竞争）并在代码注释或笔记中记录。

## 实验 3：共享变量竞态演示（SharedCounterRace）
- 目的：直观看到竞态与丢失更新，为后续同步铺垫。
- 要求：
  - 全局 `int counter = 0;`
  - 启动 N 个线程，每个自增 M 次（总期望 N×M）。
  - 版本 A：无任何同步；版本 B：用 `synchronized` 或 `AtomicInteger` 修正。
  - 打印最终计数与耗时，对比 A/B 结果。
- 验收：版本 A 计数显著小于期望值；版本 B 计数正确。记录差异与原因。

## 可选实验 4：线程 dump 观察（ThreadDumpHelper）
- 目的：初步熟悉 `jstack`/`jcmd Thread.print`。
- 要求：
  - 在代码中 `sleep` 保持线程存活，留出时间抓取 dump。
  - 使用命令（手动执行）：
    - `jcmd <pid> Thread.print`
    - 或 `jstack <pid>`
  - 观察线程状态、持锁情况（此阶段仅做认识）。
- 验收：保存一份 dump 文本，标注 RUNNABLE、TIMED_WAITING 等状态含义。

## 运行与记录建议
- 日志：统一 `Thread.currentThread().getName()`，输出简洁行。
- 参数化：可通过程序参数调整线程数、循环次数，便于多组对比。
- 记录：在 `docs/stage-01` 写一份实验笔记，包含：
  - 场景/参数
  - 现象/数据
  - 原因分析
  - 改进/结论

## 额外提示
- 若用 Maven：`mvn -q compile exec:java -Dexec.mainClass=...`
- 若用 Gradle：`./gradlew run --args="..."`（需设置 `application` 插件）。
- 若直接 `javac`：在项目根执行 `javac -d out src/main/java/com/example/concurrency/stage01/*.java`，再 `java -cp out com.example.concurrency.stage01.ThreadBasics`。
