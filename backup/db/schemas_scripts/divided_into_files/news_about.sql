create table news_about
(
	news_id bigint not null
		constraint fk4a0oc5b4mjkcxt1bpp6c1pqkq
			references news,
	about varchar(255)
);

alter table news_about owner to hfpmlazvgosijf;

