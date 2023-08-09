-- Create database
DROP DATABASE IF EXISTS vale_chat;
create database vale_chat;

-- Change database
use vale_chat;

-- Organization table
create table organization
(
    id                bigint auto_increment comment 'organization id'
        primary key,
    organization_name varchar(256) not null comment 'organization name',
    is_deleted    tinyint  default 0                 not null comment 'delete status (0-not deleted, 1-deleted)',
    create_time   datetime default CURRENT_TIMESTAMP not null comment 'creation date',
    update_time   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'update date',
    email           varchar(256)                       null comment 'email address',
    organization_avatar     varchar(1024)                      null comment 'organization avatar url',
    constraint organization_organization_name_uindex
        unique (organization_name)
);

-- User information table
-- auto-generated definition
create table user
(
    id              bigint auto_increment comment 'id'
        primary key,
    user_name       varchar(256)                                                       null comment 'user nickname',
    user_account    varchar(256)                                                       not null comment 'user account',
    user_password   varchar(512)                                                       null comment 'password',
    gender          tinyint                                                            null comment 'gender (0-male, 1-female)',
    phone           varchar(128)                                                       null comment 'phone number',
    email           varchar(256)                                                       null comment 'email address',
    user_avatar     varchar(1024) default 'https://i.pinimg.com/originals/ff/a0/9a/ffa09aec412db3f54deadf1b3781de2a.png'
                                                                                       null comment 'user avatar url',
    user_status     int           default 0                                            not null comment 'user login status (0-offline, 1-online)',
    user_role       int           default 0                                            not null comment 'user role (0-common User, 1-institute)',
    is_deleted      tinyint       default 0                                            not null comment 'delete status (0-not deleted, 1-deleted)',
    create_time     datetime      default CURRENT_TIMESTAMP                            not null comment 'creation date',
    update_time     datetime      default CURRENT_TIMESTAMP                            not null on update CURRENT_TIMESTAMP comment 'update date',
    organization_id bigint                                                             null comment 'If user is everyday people, this column is null. Otherwise is organization id',
    constraint user1_email_uindex
        unique (email),
    constraint user1_user_account_uindex
        unique (user_account),
    constraint user_organization_id_fk
        foreign key (organization_id) references organization (id)
            on update cascade
);
/*create table user
(
    id            bigint auto_increment comment 'id'
        primary key,
    user_name     varchar(256)                       null comment 'user nickname',
    user_account  varchar(256)                       not null comment 'user account',
    user_password varchar(512)                       not null comment 'password',
    gender        tinyint                            null comment 'gender (0-male, 1-female)',
    phone         varchar(128)                       null comment 'phone number',
    email         varchar(256)                       null comment 'email address',
    user_avatar   varchar(1024)                      null comment 'user avatar url',
    user_status   int      default 0                 not null comment 'user login status (0-offline, 1-online)',
    user_role     int      default 0                 not null comment 'user role (0-common User, 1-institute)',
    is_deleted    tinyint  default 0                 not null comment 'delete status (0-not deleted, 1-deleted)',
    create_time   datetime default CURRENT_TIMESTAMP not null comment 'creation date',
    update_time   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'update date',
    constraint user1_email_uindex
        unique (email),
    constraint user1_user_account_uindex
        unique (user_account)
);*/

-- Workspace table
create table workspace
(
    id             bigint auto_increment
        primary key,
    workspace_name varchar(256) not null comment 'workspace name',
    master_id      bigint       not null comment 'the id of the user who created this workspace.',
    is_deleted    tinyint  default 0                 null comment 'delete status (0-not deleted, 1-deleted)',
    create_time   datetime default CURRENT_TIMESTAMP not null comment 'creation date',
    update_time   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'update date',
    constraint workspace_user_id_fk
        foreign key (master_id) references user (id)
);

-- Table: include the user and corresponding workspace
create table user_workspace
(
    user_id      bigint                             not null,
    workspace_id bigint                             not null,
    join_time    datetime default CURRENT_TIMESTAMP not null comment 'The time when the user joined the workspace',
    is_deleted   tinyint  default 0                 not null comment 'delete status (0-not deleted, 1-deleted)',
    primary key (user_id, workspace_id),
    constraint user_workspace_user_id_fk
        foreign key (user_id) references user (id),
    constraint user_workspace_workspace_id_fk
        foreign key (workspace_id) references workspace (id)
);

create table organization_workspace
(
    organization_id bigint                             not null,
    workspace_id    bigint                             not null,
    join_time       datetime default CURRENT_TIMESTAMP not null comment 'The time when the user joined the workspace',
    is_deleted      tinyint  default 0                 not null comment 'delete status (0-not deleted, 1-deleted)',
    primary key (organization_id, workspace_id),
    constraint organization_workspace_organization_id_fk
        foreign key (organization_id) references organization (id),
    constraint organization_workspace_workspace_id_fk
        foreign key (workspace_id) references workspace (id)
);

-- Channel of multiple users
create table channel
(
    id           bigint auto_increment
        primary key,
    channel_name varchar(256)                       not null comment 'Group name',
    workspace_id bigint                             not null,
    master_id    bigint                             not null comment 'the id of the creator.',
    is_deleted   tinyint  default 0                 not null comment 'delete status (0-not deleted, 1-deleted)',
    create_time  datetime default CURRENT_TIMESTAMP not null comment 'Creation date',
    update_time  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'update date',
    channel_type   tinyint  default 0                 not null comment 'type status (0-common channel, 1-user org private channel)',
    constraint channel_user_id_fk
        foreign key (master_id) references user (id),
    constraint channel_workspace_id_fk
        foreign key (workspace_id) references workspace (id)
);

-- Table: include multiple users and corresponding workspace
create table user_channel
(
    user_id    bigint                             not null,
    channel_id bigint                             not null,
    join_time  datetime default CURRENT_TIMESTAMP not null comment 'The time when the user joined the workspace',
    is_deleted tinyint  default 0                 not null comment 'delete status (0-not deleted, 1-deleted)',
    primary key (user_id, channel_id),
    constraint user_channel_channel_id_fk
        foreign key (channel_id) references channel (id),
    constraint user_channel_user_id_fk
        foreign key (user_id) references user (id)
);

create table organization_channel
(
    organization_id bigint                             not null,
    channel_id      bigint                             not null,
    join_time       datetime default CURRENT_TIMESTAMP not null comment 'The time when the user joined the workspace',
    is_deleted      tinyint  default 0                 not null comment 'delete status (0-not deleted, 1-deleted)',
    primary key (organization_id, channel_id),
    constraint organization_channel_channel_id_fk
        foreign key (channel_id) references channel (id),
    constraint organization_channel_organization_id_fk
        foreign key (organization_id) references organization (id)
);

-- Chat messages between two people
create table private_message
(
    id           bigint auto_increment comment 'id'
        primary key,
    content      varchar(4096)                      null comment 'Message content',
    is_read      tinyint  default 0                 not null comment 'Read status(0-unread, 1-read)',
    sender_id    bigint                             not null comment 'sender id',
    receiver_id  bigint                             not null comment 'receiver id',
    workspace_id bigint                             not null comment 'corresponding workspace',
    is_deleted   tinyint  default 0                 not null comment 'delete status (0-not deleted, 1-deleted)',
    create_time  datetime default CURRENT_TIMESTAMP not null comment 'Creation date',
    update_time  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'update date',
    constraint private_message_user_id_fk
        foreign key (sender_id) references user (id),
    constraint private_message_user_id_fk_2
        foreign key (receiver_id) references user (id),
    constraint private_message_workspace_id_fk
        foreign key (workspace_id) references workspace (id)
);
/*create table private_message
(
    id           bigint auto_increment comment 'id'
        primary key,
    content      text                               not null comment 'Message content (non-text display url)',
    is_read      tinyint  default 0                 not null comment 'Read status(0-unread, 1-read)',
    sender_id    bigint                             not null comment 'sender id',
    receiver_id  bigint                             not null comment 'receiver id',
    workspace_id bigint                             not null comment 'corresponding workspace',
    is_deleted   tinyint  default 0                 not null comment 'delete status (0-not deleted, 1-deleted)',
    create_time  datetime default CURRENT_TIMESTAMP not null comment 'Creation date',
    update_time  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'update date',
    constraint private_message_user_id_fk
        foreign key (sender_id) references user (id),
    constraint private_message_user_id_fk_2
        foreign key (receiver_id) references user (id),
    constraint private_message_workspace_id_fk
        foreign key (workspace_id) references workspace (id)
);*/

-- Chat messages of channels
create table channel_message
(
    id           bigint auto_increment comment 'id'
        primary key,
    content      varchar(4096)                      null comment 'Message content',
    is_read      tinyint  default 0                 not null comment 'Read status(0-unread, 1-read)',
    sender_id    bigint                             not null comment 'sender id',
    channel_id   bigint                             not null comment 'receiver id',
    workspace_id bigint                             not null comment 'corresponding workspace',
    is_deleted   tinyint  default 0                 not null comment 'delete status (0-not deleted, 1-deleted)',
    create_time  datetime default CURRENT_TIMESTAMP not null comment 'Creation date',
    update_time  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'update date',
    constraint channel_message_channel_id_fk
        foreign key (channel_id) references channel (id),
    constraint channel_message_user_id_fk
        foreign key (sender_id) references user (id),
    constraint channel_message_workspace_id_fk
        foreign key (workspace_id) references workspace (id)
);

/*-- File in private chat
create table private_file
(
    id           bigint auto_increment comment 'id'
        primary key,
    file_name    varchar(512)                       not null comment 'File name',
    file_path    varchar(1024)                      not null comment 'File storage address-url',
    message_id   bigint                             not null comment 'The message corresponding to this file (the file is sent in chat)',
    workspace_id bigint                             not null comment 'The workspace_id corresponding to this file',
    is_deleted   tinyint  default 0                 not null comment 'delete status (0-not deleted, 1-deleted)',
    create_time  datetime default CURRENT_TIMESTAMP not null comment 'Creation date',
    update_time  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'update date',
    constraint private_file_private_message_id_fk
        foreign key (message_id) references private_message (id),
    constraint private_file_workspace_id_fk
        foreign key (workspace_id) references workspace (id)
);*/

create table private_file
(
    id          bigint auto_increment comment 'id',
    file_type   varchar(128)                       not null comment 'File type(0-file, 1-picture, 2-audio, 3-video)',
    unique_name varchar(512)                       not null comment 'Unique file name',
    file_name   varchar(512)                       not null comment 'File name',
    file_path   varchar(1024)                      not null comment 'File storage address-url',
    message_id  bigint                             not null comment 'The message corresponding to this file (the file is sent in chat)',
    is_deleted  tinyint  default 0                 not null comment 'delete status (0-not deleted, 1-deleted)',
    create_time datetime default CURRENT_TIMESTAMP not null comment 'Creation date',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'update date',
    file_url    varchar(512)                       not null comment 'file url to access the file in server',
    primary key (id, message_id),
    constraint private_file_private_message_id_fk
        foreign key (message_id) references private_message (id)
);

create table channel_file
(
    id          bigint auto_increment comment 'id',
    file_type   varchar(128)                       not null comment 'File type(0-file, 1-picture, 2-audio, 3-video)',
    unique_name varchar(512)                       not null comment 'Unique file name',
    file_name   varchar(512)                       not null comment 'File name',
    file_url    varchar(512)                       not null comment 'file URL to access the file',
    file_path   varchar(1024)                      not null comment 'File storage address-url',
    message_id  bigint                             not null comment 'The message corresponding to this file (the file is sent in chat)',
    is_deleted  tinyint  default 0                 not null comment 'delete status (0-not deleted, 1-deleted)',
    create_time datetime default CURRENT_TIMESTAMP not null comment 'Creation date',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'update date',
    primary key (id, message_id),
    constraint channel_file_channel_message_id_fk
        foreign key (message_id) references channel_message (id)
);

create table user_star
(
    id           bigint auto_increment comment 'id'
        primary key,
    user_id      bigint                             not null comment 'The user(id) who create the star',
    star_type    int                                not null comment '3 Types: 0-user, 1-channel, 2-orgChannel',
    starred_id   bigint                             not null comment 'If star_type=0, starred_id=user_id, otherwise channel_id',
    workspace_id bigint                             not null comment 'corresponding workspace',
    create_time  datetime default CURRENT_TIMESTAMP not null comment 'The time star',
    update_time  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment 'update date'
);

-- workspace: required
# INSERT INTO workspace (workspace_name, master_id) VALUES ('vale', 1);
#
# INSERT INTO user_workspace (user_id, workspace_id) VALUES (1, 1);
# INSERT INTO user_workspace (user_id, workspace_id) VALUES (2, 1);
#
# insert into organization(organization_name) values ('Tencent');
# insert into organization(organization_name) values ('Law Firm');
#
# INSERT INTO user_channel (user_id, channel_id) VALUES (1, 1);
# INSERT INTO user_channel (user_id, channel_id) VALUES (2, 1);
#
# INSERT INTO vale_chat.organization_channel (organization_id, channel_id, join_time, is_deleted) VALUES (1, 1, DEFAULT, DEFAULT);