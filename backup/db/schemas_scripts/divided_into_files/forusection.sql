create table forusection
(
	id bigint generated by default as identity
		constraint forusection_pkey
			primary key,
	points integer,
	tweet_id bigint
		constraint fktweettweetid_forusectiontweetid
			references tweet
);

alter table forusection owner to hfpmlazvgosijf;

