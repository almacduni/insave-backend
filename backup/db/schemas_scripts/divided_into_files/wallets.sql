create table wallets
(
	id bigint generated by default as identity
		constraint wallets_pkey
			primary key,
	amount numeric(19,2),
	type varchar(255),
	user_user_id bigint
		constraint fk4conf8f30sdxi93mnv8bnadvi
			references users
);

alter table wallets owner to hfpmlazvgosijf;

