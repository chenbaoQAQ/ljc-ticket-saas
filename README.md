# ljc-ticket-saas

一个基于 **Spring Boot + MyBatis-Plus** 的多租户工单系统后端练手项目  
当前阶段：**单租户实现，已预留多租户扩展设计**

---

## ✨ 项目简介

本项目实现了一个基础的工单系统，包含：

- **WorkOrder（工单）**
- **Ticket（工单记录 / 流转记录）**

重点不在“功能堆砌”，而在于：
- 接口分层清晰
- 返回结构稳定（VO）
- 业务校验集中在 Service 层
- 为后续多租户、登录系统预留扩展空间

---

## 🧱 技术栈

- Java 17
- Spring Boot 2.7.x
- MyBatis-Plus
- MySQL
- Lombok

---

## 🧩 模块设计

### 1️⃣ WorkOrder（工单）

**职责**：
- 表示一条完整的工单
- 归属于某个公司（companyId）
- 状态流转（OPEN / IN_PROGRESS / CLOSED）

**已实现接口**：

- `GET /api/work-orders`  
  工单分页查询（支持筛选）

- `POST /api/work-orders`  
  创建工单

- `GET /api/work-orders/{id}`  
  工单详情（VO）

- `PUT /api/work-orders/{id}`  
  更新工单内容

- `PUT /api/work-orders/{id}/status`  
  更新工单状态

---

### 2️⃣ Ticket（工单记录）

**职责**：
- 表示工单下的一条操作/沟通记录
- **必须依附于某个 WorkOrder**
- 不单独归属公司，而是通过工单做归属校验

**已实现接口**：

- `POST /api/tickets`  
  新建工单记录

- `GET /api/tickets?workOrderId=xxx`  
  按工单分页查询记录（时间倒序）

- `GET /api/tickets/{id}`  
  记录详情（VO）

---

## 🔐 多租户设计说明（当前为单租户实现）

- 当前阶段：
    - 使用 `companyId = 1L` 写死在 Controller 中
    - 所有核心业务方法 **已预留 companyId 参数**

- 关键设计点：
    - **工单归属校验统一在 Service 层完成**
    - Ticket 不直接校验 companyId，而是：
      ```
      Ticket → WorkOrder → companyId
      ```

后续只需：
- 接入登录系统
- 从上下文获取 companyId  
  即可无缝升级为真正的多租户系统。

---

## 📦 DTO / VO 设计约定

- **DTO（Request）**
    - 仅用于接收前端输入
    - 只包含必要字段

- **VO（Response）**
    - 所有查询接口统一返回 VO
    - 前端不直接依赖 Entity
    - 字段稳定，可演进

示例：
```text
DTO：前端 → 后端
VO：后端 → 前端
🚀 当前进度

✅ Service 接口统一
✅ companyId 统一
✅ WorkOrder / Ticket VO 完整
✅ 接口测试通过
✅ 异常链路正确

### 📅 待进行

⏳ 登录 & 动态 companyId 待实现
---

## 🧭 项目定位

一个以 **"后端设计与结构能力"** 为核心的练手项目，  
重点体现对 **业务边界、数据归属、分页查询** 以及  
**单租户向多租户演进设计** 的理解与实践。

---

**📁 文件信息**  
最后更新：{{date:YYYY-MM-DD}}  
标签：`#project` `#backend` `#springboot` `#练手项目`