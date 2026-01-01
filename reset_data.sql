-- 清理所有业务数据
TRUNCATE TABLE operation_logs RESTART IDENTITY CASCADE;
TRUNCATE TABLE resource_nodes RESTART IDENTITY CASCADE;
TRUNCATE TABLE users RESTART IDENTITY CASCADE;

-- 如果你需要保留管理员账户，请手动重新插入，或者使用下面的语句 (假设你要保留一个默认 admin)
-- INSERT INTO users (id, username, password, real_name, role) VALUES (1, 'admin', '123456', '管理员', 'ADMIN');
