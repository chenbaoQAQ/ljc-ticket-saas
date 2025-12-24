# ljc-ticket-saas

一个基于 **Spring Boot + MyBatis-Plus** 的工单系统后端练手项目。  
当前为 **单租户阶段**，已预留 **多租户演进设计**，重点在后端结构与业务归属校验。

---

## 🎯 项目目的

- 后端能力练习
- 求职项目展示

---

## 🛠️ 技术栈

**语言**：Java 17  
**框架**：Spring Boot  
**ORM**：MyBatis-Plus  
**数据库**：MySQL  
**工具**：Lombok

---

## 🧠 核心设计关注点

- ✅ **清晰的分层架构**（Controller / Service）
- ✅ **强制分页查询**（不提供全量接口）
- ✅ **业务归属校验**（防越权访问）
- 🔄 **单租户 → 多租户扩展预留**

---
## 🗂️ 数据模型设计

```mermaid
    Company -->|has many| WorkOrder
    WorkOrder -->|has many| Ticket
    Ticket -->|belongs to| WorkOrder
```

> **设计说明**  
> Ticket 不直接关联 Company，而是通过 WorkOrder 间接归属，  
> 以此强制业务归属校验，避免跨公司越权访问。

---

## 🔐 业务归属与权限设计

### 📌 设计原则

- ❌ 禁止仅凭 **ID** 直接访问资源
- ✅ 所有关键查询 **必须携带 company 约束**
- 🧹 Controller 层不处理任何业务归属逻辑

### 🔧 Service 层统一约束方法

- `getByIdWithCompany(companyId, resourceId)` - 带公司归属校验的单条查询
- `pageWithCompany(companyId, ...)` - 带公司归属的分页查询

### 🎫 Ticket 访问规则

1. 校验 WorkOrder 是否存在
2. 校验 WorkOrder 是否属于当前 Company
3. 执行 Ticket 相关操作（防跨公司越权）

### 🧪 当前阶段说明

- 使用固定 `company_id = 1` 模拟单租户环境
- 计划在登录接入后，从安全上下文中动态获取 `companyId`

---

## 🧱 项目分层架构

### 🎮 Controller 层

**职责：**
- 接收 HTTP 请求参数
- 返回统一格式的响应

**禁止：**
- 实现业务规则
- 进行权限或归属校验

---

### ⚙️ Service 层

**职责：**
- 分页逻辑组装
- 查询条件筛选
- 业务归属校验
- 对外提供带约束的业务方法

---

### 🗃️ Entity

- 纯数据库表结构映射对象

---

### 📥 DTO（计划中）

- 方向：前端 → 后端
- 目的：限制并定义前端可传入的字段
- 示例：`WorkOrderCreateReq`

---

### 📤 VO（计划中）

- 方向：后端 → 前端
- 目的：控制返回给前端的字段范围

---

## 🌐 API 接口概览

### 📋 WorkOrder 相关

**创建工单**
- 方法：POST
- 路径：`/api/work-orders`
- 参数：`title`, `content`, `creatorId`

**分页查询**
- 方法：GET
- 路径：`/api/work-orders`
- 参数：`page`, `size`, `status`, `keyword`, `creatorId`, `handlerId`
- 注意：**强制分页**，不提供全量查询

**工单详情**
- 方法：GET
- 路径：`/api/work-orders/{id}`
- 注意：自动校验工单存在性及公司归属

**更新状态**
- 方法：PUT
- 路径：`/api/work-orders/{id}/status`
- 参数：`status`

### 🎟️ Ticket 相关

**创建反馈**
- 方法：POST
- 路径：`/api/tickets`
- 参数：`workOrderId`, `description`
- 注意：自动校验工单归属

**分页查询**
- 方法：GET
- 路径：`/api/tickets`
- 参数：`workOrderId`, `page`, `size`
- 注意：按时间倒序分页

---

## 📌 当前进度与计划

### ✅ 已完成

**WorkOrder 模块**
- 创建
- 分页
- 条件筛选
- 状态变更
- company 归属校验

**Ticket 模块**
- 创建
- 分页
- 基于工单的归属校验

### 📅 待进行

- 🔲 VO 输出对象设计
- 🔲 登录鉴权集成
- 🔲 真正的多租户数据隔离支持

---

## 🧭 项目定位

一个以 **"后端设计与结构能力"** 为核心的练手项目，  
重点体现对 **业务边界、数据归属、分页查询** 以及  
**单租户向多租户演进设计** 的理解与实践。

---

**📁 文件信息**  
最后更新：{{date:YYYY-MM-DD}}  
标签：`#project` `#backend` `#springboot` `#练手项目`