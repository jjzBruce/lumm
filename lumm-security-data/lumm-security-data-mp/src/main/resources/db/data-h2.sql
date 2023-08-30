DELETE FROM `user`;

INSERT INTO `user` (id, code, name, age, email, dept_code, perm_scope) VALUES
                    (1, 'Jone', 'Jone', 18, 'test1@baomidou.com', 'dev', 'DEPT'),
                    (2, 'Jack', 'Jack', 20, 'test2@baomidou.com', 'dev', 'MYSELF'),
                    (3, 'Tom', 'Tom', 28, 'test3@baomidou.com', 'product', 'DEPT'),
                    (4, 'Sandy', 'Sandy', 21, 'test4@baomidou.com', 'product', 'MYSELF'),
                    (5, 'Billie', 'Billie', 24, 'test5@baomidou.com', 'product', 'ALL');


DELETE FROM `task`;

INSERT INTO `task` (id, code, dept_code, create_code) VALUES
                    (1, 'task-1', 'dev', 'Jone'),
                    (2, 'task-2', 'dev', 'Jack'),
                    (3, 'task-3', 'product', 'Tom'),
                    (4, 'task-4', 'product', 'Sandy'),
                    (5, 'task-5', 'product', 'Sandy');
