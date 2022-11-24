
CREATE TABLE IF NOT EXISTS person (
	id serial PRIMARY KEY,
	name varchar(200) UNIQUE NOT NULL,
	password varchar(200) NOT NULL,
	created timestamp NOT NULL,
	role_id integer NOT NULL
);


CREATE TABLE IF NOT EXISTS role (
	id serial PRIMARY KEY,
	name varchar(200) UNIQUE NOT NULL,
	descri varchar(200),
	isdefault varchar(200)
);

CREATE TABLE IF NOT EXISTS user_role (
	id serial PRIMARY KEY,
	rolename varchar(200)  NOT NULL,
	author_id integer NOT NULL
);



insert into person (id,name,password,created,role_id) values (1,'wang','123456','2022-01-01',2);
insert into person (id,name,password,created,role_id) values (2,'zhang','123456','2022-01-01',2);
insert into role (id,name,descri,isdefault) values (1,'admin','系统管理','0');
insert into role (id,name,descri,isdefault) values (2,'user','普通用户','1');
insert into role (id,name,descri,isdefault) values (3,'customer','客户合作商','0');
insert into role (id,name,descri,isdefault) values (4,'manager','业务经理','0');
insert into user_role (id,rolename,author_id) values (1,'user',1);
insert into user_role (id,rolename,author_id) values (2,'user',2);


/*

delete from role;
delete from user_role;
delete from person;

drop table person;
drop table role;
drop table user_role;

*/

/*
select * from person p

select * from "role" r

select * from user_role ur
*/