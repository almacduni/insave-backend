create table watchlist_item_order_tickers
(
	watchlist_item_order_id bigint not null
		constraint fk1v3212sypknmecitn9ekukcuc
			references watchlist_item_order,
	tickers varchar(255)
);

alter table watchlist_item_order_tickers owner to hfpmlazvgosijf;

