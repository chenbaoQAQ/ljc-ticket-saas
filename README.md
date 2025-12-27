# ljc-ticket-saas

一个基于 **Spring Boot + MyBatis-Plus** 的多租户工单系统后端练手项目  
当前阶段：**JWT 登录已完成，单租户运行，多租户结构已打通**

---

## ✨ 项目简介

本项目实现了一个基础的工单系统（Ticket SaaS 后端雏形），包含：

- **WorkOrder（工单）**
- **Ticket（工单流转 / 处理记录）**
- **JWT 登录与拦截器鉴权**

项目重点不在功能堆砌，而在于：

- 接口职责清晰（Controller / Service / Mapper）
- DTO / VO 严格区分
- 业务校验集中在 Service 层
- 登录上下文与业务解耦
- 为单租户 → 多租户平滑演进做好结构设计

---

## 🧱 技术栈

- Java 17
- Spring Boot 2.7.x
- MyBatis-Plus
- MySQL
- JWT（jjwt）
- Lombok

---

## 🧩 核心模块

### 1️⃣ 登录与鉴权（JWT）

#### 登录流程

1. 调用登录接口：POST /api/login
2. 校验用户名 / 密码（BCrypt）
3. 生成 JWT（包含 companyId / employeeId / role / name）
4. 返回 token 给前端
5. 前端后续请求统一携带：Authorization: Bearer <token>

#### 拦截器职责

- 拦截所有 `/api/**` 请求
- 放行：`/api/login`
      `OPTIONS`（预检请求）
- 校验 JWT 合法性
- 解析 claims
- 写入 `AuthContext（ThreadLocal）`
- 请求结束后自动 `clear()`

### 2⃣ AuthContext（登录上下文）

通过 `ThreadLocal` 保存当前请求的登录信息：

- companyId
- employeeId
- role
- name

使用方式：

```java
Long companyId = AuthContext.getCompanyId();
Long employeeId = AuthContext.getEmployeeId();
```
###  WorkOrder（工单）

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

### Ticket（工单记录）

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

      系统已接入 JWT 登录
      companyId / employeeId 从 token 中解析
      业务层统一使用 AuthContext 获取租户信息


- 关键设计点：

      companyId 校验全部在 Service 层完成
      Ticket 不直接校验 companyId，而是：
      ```
      Ticket → WorkOrder → companyId
      ```
      Controller 层不写业务规则，只负责参数接收与返回



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
```
🚀 当前进度

✅ JWT 登录完成
✅ 拦截器统一鉴权
✅ AuthContext 登录上下文
✅ WorkOrder / Ticket 全链路跑通
✅ companyId 统一校验
✅ VO / DTO 结构稳定
✅ 接口测试通过


---

## 🧭 项目定位

一个以 **"后端设计与结构能力"** 为核心的练手项目，  
重点体现对 **业务边界、数据归属、分页查询** 以及  
**单租户向多租户演进设计** 的理解与实践。

## 并发与扩展性设计说明

- 登录采用 JWT，无服务端 Session，支持多实例水平扩展
- 所有业务接口为无状态接口
- 分页查询避免大结果集
- companyId 作为强制条件，保证数据隔离
- 后续可引入：
  - Redis 缓存热点工单
  - 消息队列处理异步通知


---

**📁 文件信息**  
最后更新：{{date:YYYY-MM-DD}}  
标签：`#project` `#backend` `#springboot` `#练手项目`