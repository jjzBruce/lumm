DROP TABLE IF EXISTS `user`;

-- org.h2.jdbc.JdbcSQLSyntaxErrorException: `id` BIGINT[*](11) NOT NULL COMMENT '主键ID'
-- https://stackoverflow.com/questions/70695039/org-h2-jdbc-jdbcsqlsyntaxerrorexception-after-h2-version-upgrade
-- INT(11) is allowed only in MySQL and MariaDB compatibility modes, but the specified precision is ignored by H2.
-- This definition is rejected in all other compatibility modes in H2 2.0, you need to use INT or INTEGER.
CREATE TABLE `user`
(
    `id`    BIGINT(11)  NOT NULL COMMENT '主键ID',
    `name`  VARCHAR(30) NULL DEFAULT NULL COMMENT '姓名',
    `age`   INT         NULL DEFAULT NULL COMMENT '年龄',
    `email` VARCHAR(50) NULL DEFAULT NULL COMMENT '邮箱',
    PRIMARY KEY (`id`)
);