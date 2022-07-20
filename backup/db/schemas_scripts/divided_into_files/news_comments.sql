create table news_comments
(
	news_id bigint not null
		constraint fk_news_news_comments
			references news,
	comments_id bigint not null
		constraint fk_news_comments_comment
			references comment
);

alter table news_comments owner to hfpmlazvgosijf;

