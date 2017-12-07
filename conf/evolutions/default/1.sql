# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table posts (
  id                            bigint auto_increment not null,
  user_id                       bigint not null,
  post_description              varchar(255),
  number_of_likes               int default 0,
  number_of_comments            int default 0,
  created_date                  bigint,
  constraint pk_posts primary key (id)
);

create table user_session (
  id                            bigint auto_increment not null,
  user_id                       bigint not null,
  session_token                 varchar(255) not null,
  login_time                    bigint not null,
  constraint uq_user_session_session_token unique (session_token),
  constraint pk_user_session primary key (id)
);

create table users (
  id                            bigint auto_increment not null,
  user_name                     varchar(255),
  password                      varchar(255) not null,
  email                         varchar(255) not null,
  created_date                  bigint,
  constraint uq_users_email unique (email),
  constraint pk_users primary key (id)
);

alter table posts add constraint fk_posts_user_id foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_posts_user_id on posts (user_id);

alter table user_session add constraint fk_user_session_user_id foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_user_session_user_id on user_session (user_id);


# --- !Downs

alter table posts drop foreign key fk_posts_user_id;
drop index ix_posts_user_id on posts;

alter table user_session drop foreign key fk_user_session_user_id;
drop index ix_user_session_user_id on user_session;

drop table if exists posts;

drop table if exists user_session;

drop table if exists users;

