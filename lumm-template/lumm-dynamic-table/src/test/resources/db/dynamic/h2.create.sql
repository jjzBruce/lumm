CREATE TABLE IF NOT EXISTS biz_001_dynamic_user
(
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    position             VARCHAR(255) DEFAULT '',
    dept                 VARCHAR(255) DEFAULT ''
);

insert into biz_001_dynamic_user(position, dept) values ('001', '001');
insert into biz_001_dynamic_user(position, dept) values ('002', '002');

CREATE TABLE IF NOT EXISTS biz_011_dynamic_user
(
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    position             VARCHAR(255) DEFAULT '',
    dept                 VARCHAR(255) DEFAULT ''
);

insert into biz_011_dynamic_user(position, dept) values ('011', '011');
