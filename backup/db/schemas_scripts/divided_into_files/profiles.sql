create table profiles
(
	id bigint generated by default as identity
		constraint profiles_pkey
			primary key,
	birth_date timestamp,
	first_name varchar(255),
	second_name varchar(255)
);

alter table profiles owner to hfpmlazvgosijf;

