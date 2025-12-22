# ljc-ticket-saas

一个 Spring Boot + MyBatis-Plus 的练手后端项目（当前已完成 WorkOrder 工单的基础骨架：创建、分页查询、筛选、改状态）。

## Tech Stack
- Java 17
- Spring Boot
- MyBatis-Plus
- MySQL
- Lombok

## Quick Start

### 1. 配置数据库环境变量（推荐）
在 IntelliJ：运行配置（Edit Configurations）里设置环境变量：

- `DB_USER=root`
- `DB_PASS=020222`

### 2. 数据库连接
默认连接在 `application.properties`：

- DB: `ljc_ticket_saas`
- URL: `jdbc:mysql://localhost:3306/ljc_ticket_saas?serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=utf-8`

项目启动时会执行：
- `classpath:schema.sql`
- `classpath:data.sql`

### 3. 启动项目
运行 `LjcTicketSaasApplication`

默认端口：
- `http://localhost:8080`

---

## API（Work Orders）

### 1) 创建工单
**POST** `/api/work-orders`

Body(JSON)：
