create table report
(
	id bigint generated by default as identity
		constraint report_pkey
			primary key,
	description varchar(200),
	tweet_id bigint,
	user_id bigint
);

alter table report owner to hfpmlazvgosijf;

