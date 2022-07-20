create table portfolio_stocks
(
	portfolio_id bigint not null
		constraint fk9key7lkhq6rt42flierknviad
			references portfolios,
	stock_id bigint not null
		constraint fkfj6ref2viw08erqb73vwybbod
			references stocks
);

alter table portfolio_stocks owner to hfpmlazvgosijf;

