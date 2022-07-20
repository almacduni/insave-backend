create table comments_likes
(
	comment_id bigint not null
		constraint fkeqs3he7d0ysbw4lup5dyfntnx
			references comment,
	user_id bigint not null
		constraint fkiwf3mhli7ej3pgf9ktj6vv08p
			references users,
	constraint comments_likes_pkey
		primary key (comment_id, user_id)
);

alter table comments_likes owner to hfpmlazvgosijf;

