-- XXX how does spring boot run this with mysql ? h2 seems ok
create table users(
	id					varchar(255) primary key,
	first_name			varchar(255) not null,
	last_name			varchar(255) not null,
	email				varchar(255) not null,
	encoded_password	varchar(128) not null
);