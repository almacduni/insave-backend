create table roles
(
	id integer generated by default as identity
		constraint roles_pkey
			primary key,
	name varchar(20)
);

alter table roles owner to hfpmlazvgosijf;

