create table news
(
	id bigint generated by default as identity
		constraint news_pkey
			primary key,
	published_at timestamp,
	title varchar(255)
);

alter table news owner to hfpmlazvgosijf;

