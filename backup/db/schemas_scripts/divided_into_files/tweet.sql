create table tweet
(
	id bigint generated by default as identity
		constraint tweet_pkey
			primary key,
	content varchar(255),
	published_at varchar(255),
	author_user_id bigint
		constraint fkow9n2oc1xipwvys0ielduf9o2
			references users
);

alter table tweet owner to hfpmlazvgosijf;

