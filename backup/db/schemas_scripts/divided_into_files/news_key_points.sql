create table news_key_points
(
	news_id bigint not null
		constraint fk7vb2blsafwbierx68u6lred77
			references news,
	key_points text
);

alter table news_key_points owner to hfpmlazvgosijf;

