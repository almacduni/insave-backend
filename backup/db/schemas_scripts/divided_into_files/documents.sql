create table documents
(
	id bigint generated by default as identity
		constraint documents_pkey
			primary key,
	is_accepted boolean,
	passport_data varchar(255),
	passport_issued varchar(255),
	passport_main_page varchar(255),
	passport_registration_page varchar(255)
);

alter table documents owner to hfpmlazvgosijf;

