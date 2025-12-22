# ljc-ticket-saas

一个工单系统的最小可运行骨架（Spring Boot + MyBatis-Plus + MySQL）

## 本地运行

1. 创建数据库
```sql
CREATE DATABASE IF NOT EXISTS ljc_ticket_saas DEFAULT CHARSET utf8mb4;
```

2. 配置数据库账号密码（推荐用环境变量）
- Windows (PowerShell)
```powershell
$env:DB_USER="root"
$env:DB_PASS="你的密码"
```
- macOS / Linux
```bash
export DB_USER=root
export DB_PASS='你的密码'
```

3. 启动项目  
启动后会自动执行 `schema.sql` 和 `data.sql`

## 现有接口（兼容旧路径 + 新路径）

- 工单列表  
  - `GET /workorder`
  - `GET /api/work-orders`

- 员工列表  
  - `GET /employee`
  - `GET /api/employees`

- 工单处理记录列表  
  - `GET /tickets`
  - `GET /api/tickets`
