create table news_likes
(
	news_id bigint not null
		constraint fk_news_news_likes
			references news,
	user_id bigint not null
		constraint fk_news_users
			references users,
	constraint news_likes_pkey
		primary key (news_id, user_id)
);

alter table news_likes owner to hfpmlazvgosijf;

