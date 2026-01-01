# Source Share 接口文档

本文档详细描述了后端提供的所有 API 接口。

## 🌐 基础信息

*   **基础 URL**: `http://localhost:8080`
*   **认证方式**: Bearer Token (JWT)
*   **统一响应格式**:
    ```json
    {
      "code": 200,          // 200: 成功, 其他: 失败
      "message": "操作成功", // 提示信息
      "data": { ... }       // 具体数据
    }
    ```

---

## 🔑 1. 认证模块 (Auth)

### 1.1 用户登录

*   **URL**: `/api/tokens`
*   **Method**: `POST`
*   **Auth**: 无需认证
*   **描述**: 用户登录并获取 Token。

**请求参数 (Body):**

```json
{
  "username": "admin",
  "password": "123"
}
```

**响应示例:**

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userId": 1,
    "username": "admin",
    "realName": "管理员",
    "role": "admin"
  }
}
```

---

## 👤 2. 用户管理 (User)

### 2.1 注册新用户

*   **URL**: `/api/users`
*   **Method**: `POST`
*   **Auth**: **仅管理员**
*   **描述**: 创建一个新的后台用户。

**请求参数 (Body):**

```json
{
  "username": "zhangsan",
  "password": "123",
  "realName": "张三",
  "email": "zhangsan@example.com",
  "grade": "2024",
  "role": "user"  // user 或 admin
}
```

**响应示例:**

```json
{
  "code": 200,
  "message": "用户创建成功",
  "data": {
    "id": 2,
    "username": "zhangsan",
    "realName": "张三",
    "role": "user"
  }
}
```

---

## 📂 3. 资源管理 (Resource)

### 3.1 获取板块根目录 ID

*   **URL**: `/api/resources/root-id`
*   **Method**: `GET`
*   **Auth**: 需登录
*   **描述**: 根据业务板块代码，获取该板块的根文件夹 ID。

**Query 参数:**

| 参数名 | 类型 | 必填 | 描述 | 示例 |
| :--- | :--- | :--- | :--- | :--- |
| `category` | String | 是 | 板块代码 | `COURSEWORK` (课程作业), `PROPOSAL` (开题), `MIDTERM` (中期), `THESIS` (毕设), `OTHERS` (综合) |

**响应示例:**

```json
{
  "code": 200,
  "data": 10
}
```

### 3.2 获取文件夹内容 (子资源)

*   **URL**: `/api/resources/{parentId}/children`
*   **Method**: `GET`
*   **Auth**: 需登录
*   **描述**: 获取指定文件夹下的所有子文件和子文件夹。

**Path 参数:**

| 参数名 | 描述 |
| :--- | :--- |
| `parentId` | 父文件夹的 ID |

**响应示例:**

```json
{
  "code": 200,
  "data": [
    {
      "id": 12,
      "nodeName": "第一章作业",
      "resourceType": "DIRECTORY",
      "ownerName": "张三",
      "updatedAt": "2024-01-01T10:00:00"
    },
    {
      "id": 13,
      "nodeName": "实验报告.pdf",
      "resourceType": "FILE",
      "ownerName": "张三",
      "properties": {
        "size": 1024,
        "extension": "pdf",
        "url": "http://..."
      }
    }
  ]
}
```

### 3.3 搜索资源

*   **URL**: `/api/resources`
*   **Method**: `GET`
*   **Auth**: 需登录
*   **描述**: 全局搜索或在指定文件夹内搜索资源。

**Query 参数:**

| 参数名 | 必填 | 描述 |
| :--- | :--- | :--- |
| `keyword` | 是 | 搜索关键词 |
| `folderId` | 否 | 限制搜索范围的文件夹ID (不传则搜全库) |

### 3.4 添加资源 (新建文件夹/保存文件信息)

*   **URL**: `/api/resources`
*   **Method**: `POST`
*   **Auth**: 需登录
*   **描述**: 在指定目录下创建一个新文件夹，或者保存一个已上传文件的元数据。

**请求参数 (Body):**

**场景 A: 新建文件夹**

```json
{
  "parentId": 10,
  "nodeName": "新的文件夹",
  "resourceType": "DIRECTORY"
}
```

**场景 B: 保存文件 (上传物理文件后调用)**

```json
{
  "parentId": 10,
  "nodeName": "我的作业.docx",
  "resourceType": "FILE",
  "properties": {
    "filePath": "files/uuid.docx",
    "url": "http://localhost:8080/uploads/uuid.docx",
    "size": 2048
  }
}
```

### 3.5 删除资源

*   **URL**: `/api/resources/{resourceId}`
*   **Method**: `DELETE`
*   **Auth**: 需登录
*   **描述**: 删除指定的文件或文件夹（非空文件夹无法删除）。
    *   普通用户只能删除自己的资源。
    *   管理员可以删除任何资源。

---

## 📤 4. 文件上传 (File)

### 4.1 上传文件

*   **URL**: `/api/files/upload`
*   **Method**: `POST`
*   **Auth**: 需登录 (建议)
*   **Content-Type**: `multipart/form-data`
*   **描述**: 上传物理文件到服务器。上传成功后，需调用 **3.4 添加资源** 接口保存文件信息。

**Form Data:**

| Key | Value |
| :--- | :--- |
| `file` | (二进制文件) |

**响应示例:**

```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "originalName": "我的作业.docx",
    "storedName": "uuid-generated-name.docx",
    "url": "http://localhost:8080/uploads/uuid-generated-name.docx",
    "size": "2048"
  }
}
```

---

## 📊 5. 统计与日志 (Admin)

### 5.1 获取资源统计

*   **URL**: `/api/statistics`
*   **Method**: `GET`
*   **Auth**: 需登录
*   **描述**: 获取各类资源的数量统计。

**响应示例:**

```json
{
  "code": 200,
  "data": {
    "fileTypeCounts": {
      "pdf": 12,
      "docx": 5,
      "jpg": 3,
      "unknown": 1
    },
    "totalSize": 1024000,
    "totalCount": 21
  }
}
```

### 5.2 获取操作日志

*   **URL**: `/api/logs`
*   **Method**: `GET`
*   **Auth**: 需登录 (通常仅管理员)
*   **Query 参数**: `page` (页码), `size` (每页条数)

---

## ❤️ 6. 系统 (System)

### 6.1 健康检查

*   **URL**: `/hi`
*   **Method**: `GET`
*   **Auth**: 无需认证
*   **描述**: 检查后端服务是否存活。
