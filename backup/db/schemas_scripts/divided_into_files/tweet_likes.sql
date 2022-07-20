create table tweet_likes
(
	tweet_id bigint not null
		constraint fk_tweet_news_likes
			references tweet,
	user_id bigint not null
		constraint fk_tweet_users
			references users,
	constraint tweet_likes_pkey
		primary key (tweet_id, user_id)
);

alter table tweet_likes owner to hfpmlazvgosijf;

