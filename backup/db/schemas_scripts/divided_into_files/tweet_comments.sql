create table tweet_comments
(
	tweet_id bigint not null
		constraint fkn4qjuwum5n2hfvbhpvq4arpqe
			references tweet,
	comments_id bigint not null
		constraint fkpn4pm8beebfxg0dqgq2pkipo7
			references comment
);

alter table tweet_comments owner to hfpmlazvgosijf;

