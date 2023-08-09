-- clear database
-- DROP DATABASE IF EXISTS vale_chat;

-- SOURCE ./vale_chat.sql; 不起作用 暂时先执行建表sql

USE vale_chat;

INSERT INTO organization (id, organization_name, email, organization_avatar) VALUES (1, "Vale", "Vale@gamil.com", "https://miningafrica.net/wp-content/uploads/2020/12/Vale-logo-2.jpg");
INSERT INTO organization (id, organization_name, email, organization_avatar) VALUES (2, "USYD", "USYD@uni.sydney.edu.au", "https://ih1.redbubble.net/image.204377643.7883/st,small,507x507-pad,600x600,f8f8f8.u7.jpg");
INSERT INTO organization (id, organization_name, email, organization_avatar) VALUES (3, "COMP", "COMP@uni.sydney.edu.au", "https://static.vecteezy.com/system/resources/previews/000/595/201/original/community-people-care-logo-and-symbols-vector.jpg");
INSERT INTO organization (id, organization_name, email, organization_avatar) VALUES (4, "INFO", "INFO@uni.sydney.edu.au", "https://pub-static.fotor.com/assets/projects/pages/4765c093-30c7-451f-a6f8-577db185c1b3/300w/cute-heart-shape-ngo-logo-ba179366-4067-4572-a24d-251414e93f78.jpg");

-- User: required
-- Password: Ethan-123
INSERT INTO user (id, user_name, user_account, user_password, email, user_role, organization_id) VALUES (1, "Ethan", "ethan", "3fbe90666a07064ea64a756d42efa75a", "Ethan@gmail.com",1,1);
-- Password: Tom-123456
INSERT INTO user (id, user_name, user_account, user_password, email, user_role, organization_id) VALUES (2, "Tommy", "tommy", "8ecf90caafb722a765679205a195ca58", "Tom@gmail.com",1,1);
INSERT INTO user (id, user_name, user_account, user_password, email, user_role, organization_id) VALUES (3, "Vale_Staff3", "vale_staff3", "8ecf90caafb722a765679205a195ca58", "Vale_Test@gmail.com",1,1);

-- Left team member account: Mutao for demonstration.
INSERT INTO user (id, user_name, user_account, user_password, email, user_role, organization_id) VALUES (4, "LiZheng", "lizheng", "8ecf90caafb722a765679205a195ca58", "lzhe7349@uni.sydney.edu.au",1,2);
INSERT INTO user (id, user_name, user_account, user_password, email, user_role, organization_id) VALUES (5, "Jiandong", "jiandong", "8ecf90caafb722a765679205a195ca58", "jche6441@uni.sydney.edu.au",1,2);
INSERT INTO user (id, user_name, user_account, user_password, email, user_role, organization_id) VALUES (6, "Jizhi", "jizhi", "8ecf90caafb722a765679205a195ca58", "jzha4939@uni.sydney.edu.au",1,2);
INSERT INTO user (id, user_name, user_account, user_password, email, user_role, organization_id) VALUES (7, "YueKong", "kongyue", "8ecf90caafb722a765679205a195ca58", "ykon8250@uni.sydney.edu.au",1,3);
INSERT INTO user (id, user_name, user_account, user_password, email, user_role, organization_id) VALUES (8, "Qianyu", "qianyu", "8ecf90caafb722a765679205a195ca58", "qliu7494@uni.sydney.edu.au",1,3);
INSERT INTO user (id, user_name, user_account, user_password, email, user_role, organization_id) VALUES (9, "YangLu", "yanglu", "8ecf90caafb722a765679205a195ca58", "test@uni.sydney.edu.au",1,2);

INSERT INTO user (id, user_name, user_account, user_password, email) VALUES (10, "James", "james", "3fbe90666a07064ea64a756d42efa75a", "James@gmail.com");
INSERT INTO user (id, user_name, user_account, user_password, email) VALUES (11, "Olivia", "olivia", "3fbe90666a07064ea64a756d42efa75a", "Olivia@qq.com");
INSERT INTO user (id, user_name, user_account, user_password, email) VALUES (12, "William", "william", "3fbe90666a07064ea64a756d42efa75a", "William@outlook.com");
INSERT INTO user (id, user_name, user_account, user_password, email) VALUES (13, "Michael", "michael", "3fbe90666a07064ea64a756d42efa75a", "Michael@gmail.com");
INSERT INTO user (id, user_name, user_account, user_password, email) VALUES (14, "Matthew", "matthew", "3fbe90666a07064ea64a756d42efa75a", "Matthew@outlook.com");
INSERT INTO user (id, user_name, user_account, user_password, email) VALUES (15, "Isabella", "isabella", "3fbe90666a07064ea64a756d42efa75a", "Isabella@gmail.com");
INSERT INTO user (id, user_name, user_account, user_password, email) VALUES (16, "Sofia", "sofia", "3fbe90666a07064ea64a756d42efa75a", "Sofia@outlook.com");
INSERT INTO user (id, user_name, user_account, user_password, email) VALUES (17, "Nicholas", "nicholas", "3fbe90666a07064ea64a756d42efa75a", "Nicholas@gmail.com");
INSERT INTO user (id, user_name, user_account, user_password, email) VALUES (18, "Evelyn", "evelyn", "3fbe90666a07064ea64a756d42efa75a", "Evelyn@gmail.com");
UPDATE user
SET user_avatar = "https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg"
WHERE id = 3;


-- workspace: required
INSERT INTO workspace (id, workspace_name, master_id) VALUES (1, "Vale", 1);
INSERT INTO workspace (id, workspace_name, master_id) VALUES (2, "USYD", 1);
INSERT INTO workspace (id, workspace_name, master_id) VALUES (3, "AUS", 1);

-- workspace: required


-- INSERT INTO user (id, user_name, user_account, user_password, email, user_role, organization_id) VALUES (4, "Test4", "test4", "8ecf90caafb722a765679205a195ca58", "Tom2@gmail.com", 1, 1);
-- INSERT INTO user (id, user_name, user_account, user_password, email, user_role, organization_id) VALUES (5, "Test5", "test5", "8ecf90caafb722a765679205a195ca58", "Tom3@gmail.com", 1, 2);

-- user workspace:
INSERT INTO user_workspace (user_id, workspace_id) VALUES (10, 1);
INSERT INTO user_workspace (user_id, workspace_id) VALUES (11, 1);
INSERT INTO user_workspace (user_id, workspace_id) VALUES (12, 1);
INSERT INTO user_workspace (user_id, workspace_id) VALUES (13, 1);
INSERT INTO user_workspace (user_id, workspace_id) VALUES (14, 1);
INSERT INTO user_workspace (user_id, workspace_id) VALUES (15, 1);
INSERT INTO user_workspace (user_id, workspace_id) VALUES (16, 1);
INSERT INTO user_workspace (user_id, workspace_id) VALUES (15, 2);
INSERT INTO user_workspace (user_id, workspace_id) VALUES (16, 2);
INSERT INTO user_workspace (user_id, workspace_id) VALUES (17, 2);
INSERT INTO user_workspace (user_id, workspace_id) VALUES (11, 3);
INSERT INTO user_workspace (user_id, workspace_id) VALUES (12, 3);
INSERT INTO user_workspace (user_id, workspace_id) VALUES (13, 3);
INSERT INTO user_workspace (user_id, workspace_id) VALUES (15, 3);
INSERT INTO user_workspace (user_id, workspace_id) VALUES (18, 3);

-- organization workspace:
INSERT INTO organization_workspace (organization_id, workspace_id) VALUES (1, 1);
INSERT INTO organization_workspace (organization_id, workspace_id) VALUES (2, 1);
INSERT INTO organization_workspace (organization_id, workspace_id) VALUES (3, 1);
INSERT INTO organization_workspace (organization_id, workspace_id) VALUES (4, 1);
INSERT INTO organization_workspace (organization_id, workspace_id) VALUES (1, 2);
INSERT INTO organization_workspace (organization_id, workspace_id) VALUES (3, 2);
INSERT INTO organization_workspace (organization_id, workspace_id) VALUES (1, 3);
INSERT INTO organization_workspace (organization_id, workspace_id) VALUES (2, 3);
INSERT INTO organization_workspace (organization_id, workspace_id) VALUES (4, 3);

-- channel: required
INSERT INTO channel (id, channel_name, workspace_id, master_id) VALUES (1, "Vale_Client", 1, 1);
INSERT INTO channel (id, channel_name, workspace_id, master_id) VALUES (2, "Vale_TechSupport", 1, 1);
INSERT INTO channel (id, channel_name, workspace_id, master_id) VALUES (3, "USYD_student", 2, 3);

-- user channel: optional
INSERT INTO user_channel (user_id, channel_id) VALUES (10, 1);
INSERT INTO user_channel (user_id, channel_id) VALUES (11, 1);
INSERT INTO user_channel (user_id, channel_id) VALUES (12, 1);
INSERT INTO user_channel (user_id, channel_id) VALUES (13, 1);
INSERT INTO user_channel (user_id, channel_id) VALUES (14, 1);
INSERT INTO user_channel (user_id, channel_id) VALUES (13, 2);
INSERT INTO user_channel (user_id, channel_id) VALUES (15, 2);
INSERT INTO user_channel (user_id, channel_id) VALUES (16, 3);
INSERT INTO user_channel (user_id, channel_id) VALUES (17, 3);
INSERT INTO user_channel (user_id, channel_id) VALUES (18, 3);

-- organization channel: optional
INSERT INTO organization_channel (organization_id, channel_id) VALUES (1, 1);
INSERT INTO organization_channel (organization_id, channel_id) VALUES (2, 1);
INSERT INTO organization_channel (organization_id, channel_id) VALUES (3, 1);
INSERT INTO organization_channel (organization_id, channel_id) VALUES (4, 1);
INSERT INTO organization_channel (organization_id, channel_id) VALUES (2, 2);
INSERT INTO organization_channel (organization_id, channel_id) VALUES (3, 2);
INSERT INTO organization_channel (organization_id, channel_id) VALUES (4, 2);
INSERT INTO organization_channel (organization_id, channel_id) VALUES (1, 3);
INSERT INTO organization_channel (organization_id, channel_id) VALUES (2, 3);

-- private_message: optional - can use frontend test page to generate
INSERT INTO private_message (id, content, sender_id, receiver_id, workspace_id) VALUES (1, "Hello Tom", 1, 2, 1);
INSERT INTO private_message (id, content, sender_id, receiver_id, workspace_id) VALUES (2, "Hi Ethan", 2, 1, 1);

-- channel_message:
INSERT INTO channel_message (id, content, sender_id, channel_id, workspace_id) VALUES (1, "Hello guys!", 1, 1, 1);
INSERT INTO channel_message (id, content, sender_id, channel_id, workspace_id) VALUES (2, "Hi Ethan, looks like just it's us two in this channel at the moment :)", 2, 1, 1);
INSERT INTO channel_message (id, content, sender_id, channel_id, workspace_id) VALUES (3, "hhh, we should really hire more people to the company", 1, 1, 1);
INSERT INTO channel_message (id, content, sender_id, channel_id, workspace_id) VALUES (4, "agreed", 2, 1, 1);