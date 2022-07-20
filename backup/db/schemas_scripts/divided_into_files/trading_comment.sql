create table trading_comment
(
	trading_id bigint not null,
	comment_id bigint not null
		constraint uk_qemgb9uim8bgxx9r7i9ww0pkp
			unique
		constraint fke7wo6beww37ergg29jr1xtxt6
			references comment
);

alter table trading_comment owner to hfpmlazvgosijf;

