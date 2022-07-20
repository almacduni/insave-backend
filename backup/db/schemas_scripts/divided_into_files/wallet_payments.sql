create table wallet_payments
(
	wallet_id bigint not null
		constraint fkhpgfoxd7lr9rb05722kb824nl
			references wallets,
	payment_id bigint not null
		constraint fkb2epxnluk143ikm3hhxbhj1bj
			references payments
);

alter table wallet_payments owner to hfpmlazvgosijf;

