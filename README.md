# Source Share - 校园资源共享平台

Source Share 是一个基于 Spring Boot 和 PostgreSQL 开发的资源管理与共享平台。支持文件上传、多级目录管理、权限控制以及操作审计。

## ✨ 主要功能

*   **用户系统**
    *   用户注册与登录（JWT 认证）
    *   角色管理（管理员 vs 普通用户）
    *   管理员可管理所有资源，普通用户仅管理自己的资源
*   **资源管理**
    *   支持多级文件夹结构（树形存储）
    *   文件上传与下载
    *   资源分类（课程作业、开题报告、中期考核、毕业设计、综合资源）
    *   全文搜索与分类检索
*   **审计与统计**
    *   记录关键操作日志（上传、删除等）
    *   资源数据统计看板

## 🛠️ 技术栈

*   **后端**: Java 21, Spring Boot 3.4.13
*   **数据库**: PostgreSQL (使用 `ltree` 插件处理树形结构)
*   **ORM**: Spring Data JPA
*   **认证**: JWT (JSON Web Token)

## 🚀 快速开始

### 1. 环境准备

*   JDK 21 或更高版本
*   PostgreSQL 14+

### 2. 数据库设置

首先创建一个 PostgreSQL 数据库（例如 `source_share`），然后**必须**开启 `ltree` 扩展：

```sql
-- 在你的数据库查询窗口执行
CREATE EXTENSION IF NOT EXISTS ltree;
```

### 3. 修改配置

打开 `src/main/resources/application.properties`，修改数据库连接信息：

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/你的数据库名
spring.datasource.username=你的用户名
spring.datasource.password=你的密码
```

### 4. 运行项目

**方式一：使用 Maven 插件直接运行（开发环境）**

```bash
./mvnw spring-boot:run
```

实际上我在IDE中使用下面的命令跑的！
```
cd /home/czl/project/source-share ; /usr/bin/env /home/czl/.jdks/temurin-24/bin/java @/tmp/cp_ecigami1c83ztpogy3ronttcz.argfile com.example.source_share.SourceShareApplication
```


**方式二：打包运行（生产环境）**

```bash
# 1. 打包
./mvnw clean package -DskipTests

# 2. 运行 jar 包
java -jar target/source-share-0.0.1-SNAPSHOT.jar
```

---

## 🖥️ 后台运行与开机自启 (Linux)

如果你部署在服务器上，推荐使用 `systemd` 来管理服务，这样可以实现后台运行、开机自启以及自动重启。

### 1. 简单后台运行 (nohup)

如果只是临时跑一下，不想用 systemd：

```bash
nohup java -jar target/source-share-0.0.1-SNAPSHOT.jar > output.log 2>&1 &
```
*   `output.log` 是日志文件。
*   `&` 表示后台运行。

### 2. 配置开机自启 (Systemd 推荐)

**第一步：创建服务文件**

创建一个新的服务文件 `/etc/systemd/system/source-share.service`：

```bash
sudo nano /etc/systemd/system/source-share.service
```

**第二步：填入以下内容**

请根据你的实际路径修改 `User`（运行用户）和 `ExecStart`（Jar包路径）。

```ini
[Unit]
Description=Source Share Backend Service
After=syslog.target network.target postgresql.service

[Service]
# 修改为你的用户名 (例如: root 或 ubuntu)
User=root

# 环境变量 (如果有需要)
# Environment=JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64

# 运行命令 (注意修改 Jar 包的绝对路径)
ExecStart=/usr/bin/java -jar /home/czl/project/source-share/target/source-share-0.0.1-SNAPSHOT.jar

# 总是自动重启 (如果崩溃)
Restart=always
RestartSec=10

# 日志输出 (systemd 会自动管理，也可以查看 journalctl)
StandardOutput=syslog
StandardError=syslog
SyslogIdentifier=source-share

[Install]
WantedBy=multi-user.target
```

**第三步：启动并设置开机自启**

```bash
# 1. 重新加载配置
sudo systemctl daemon-reload

# 2. 启动服务
sudo systemctl start source-share

# 3. 设置开机自启
sudo systemctl enable source-share

# 4. 查看状态
sudo systemctl status source-share
```

### 3. 如何查看 Systemd 日志？

如果服务启动失败，或者想看运行日志：

```bash
# 查看实时日志
sudo journalctl -u source-share -f

# 查看最后 100 行日志
sudo journalctl -u source-share -n 100
```
