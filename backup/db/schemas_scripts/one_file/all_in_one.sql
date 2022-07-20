create sequence hibernate_sequence;

alter sequence hibernate_sequence owner to hfpmlazvgosijf;

create table databasechangelog
(
	id varchar(255) not null,
	author varchar(255) not null,
	filename varchar(255) not null,
	dateexecuted timestamp not null,
	orderexecuted integer not null,
	exectype varchar(10) not null,
	md5sum varchar(35),
	description varchar(255),
	comments varchar(255),
	tag varchar(255),
	liquibase varchar(20),
	contexts varchar(255),
	labels varchar(255),
	deployment_id varchar(10)
);

alter table databasechangelog owner to hfpmlazvgosijf;

create table databasechangeloglock
(
	id integer not null
		constraint databasechangeloglock_pkey
			primary key,
	locked boolean not null,
	lockgranted timestamp,
	lockedby varchar(255)
);

alter table databasechangeloglock owner to hfpmlazvgosijf;

create table documents
(
	id bigint generated by default as identity
		constraint documents_pkey
			primary key,
	is_accepted boolean,
	passport_data varchar(255),
	passport_issued varchar(255),
	passport_main_page varchar(255),
	passport_registration_page varchar(255)
);

alter table documents owner to hfpmlazvgosijf;

create table news
(
	id bigint generated by default as identity
		constraint news_pkey
			primary key,
	published_at timestamp,
	title varchar(255)
);

alter table news owner to hfpmlazvgosijf;

create table news_about
(
	news_id bigint not null
		constraint fk4a0oc5b4mjkcxt1bpp6c1pqkq
			references news,
	about varchar(255)
);

alter table news_about owner to hfpmlazvgosijf;

create table news_key_points
(
	news_id bigint not null
		constraint fk7vb2blsafwbierx68u6lred77
			references news,
	key_points text
);

alter table news_key_points owner to hfpmlazvgosijf;

create table portfolios
(
	id bigint generated by default as identity
		constraint portfolios_pkey
			primary key,
	currency varchar(255),
	total_balance_now numeric(19,2),
	total_balance_prev_day numeric(19,2),
	trade_history varchar(255)
);

alter table portfolios owner to hfpmlazvgosijf;

create table profiles
(
	id bigint generated by default as identity
		constraint profiles_pkey
			primary key,
	birth_date timestamp,
	first_name varchar(255),
	second_name varchar(255)
);

alter table profiles owner to hfpmlazvgosijf;

create table report
(
	id bigint generated by default as identity
		constraint report_pkey
			primary key,
	description varchar(200),
	tweet_id bigint,
	user_id bigint
);

alter table report owner to hfpmlazvgosijf;

create table roles
(
	id integer generated by default as identity
		constraint roles_pkey
			primary key,
	name varchar(20)
);

alter table roles owner to hfpmlazvgosijf;

create table stock_play_list
(
	id bigint generated by default as identity
		constraint stock_play_list_pkey
			primary key,
	ticker varchar(200),
	company varchar(200),
	sector varchar(200),
	industry varchar(200),
	market_cap varchar(200),
	recom varchar(200)
);

alter table stock_play_list owner to hfpmlazvgosijf;

create table stocks
(
	id bigint generated by default as identity
		constraint stocks_pkey
			primary key,
	bought_date timestamp,
	country varchar(255),
	currency varchar(255),
	description varchar(255),
	industry varchar(255),
	logo varchar(255),
	name varchar(255),
	phone varchar(255),
	share_outstanding double precision not null,
	ticker varchar(255),
	url varchar(255)
);

alter table stocks owner to hfpmlazvgosijf;

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

create table users
(
	user_id bigint generated by default as identity
		constraint users_pkey
			primary key,
	day_created timestamp,
	email varchar(255)
		constraint uk6dotkott2kjsp8vw4d0m25fb7
			unique,
	enable boolean not null,
	password varchar(255),
	phone varchar(255),
	username varchar(255)
		constraint ukr43af9ap4edm43mmtq01oddj6
			unique,
	portfolio_id bigint
		constraint fkonfi0p6h3fkvfq79otx2p5cc9
			references portfolios,
	profile_id bigint
		constraint fkq2e6rj0p6p1gec2cslmaxugw1
			references profiles,
	wallet_id bigint,
	first_name varchar(255),
	last_name varchar(255),
	activated boolean default false,
	activated_code varchar(255)
);

alter table users owner to hfpmlazvgosijf;

create table comment
(
	id bigint generated by default as identity
		constraint comment_pkey
			primary key,
	content varchar(200),
	published_at timestamp,
	author_user_id bigint
		constraint fki4xwyjsmhu648k3thg01nsqyw
			references users
);

alter table comment owner to hfpmlazvgosijf;

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

create table news_likes
(
	news_id bigint not null
		constraint fk_news_news_likes
			references news,
	user_id bigint not null
		constraint fk_news_users
			references users,
	constraint news_likes_pkey
		primary key (news_id, user_id)
);

alter table news_likes owner to hfpmlazvgosijf;

create table payments
(
	id bigint generated by default as identity
		constraint payments_pkey
			primary key,
	amount numeric(19,2),
	day_created timestamp not null,
	from_user_id bigint
		constraint fk74waay8m8qk4vdxse9h88gg2w
			references users,
	to_user_id bigint
		constraint fk5ygpkypx3434pgm7c2dlclfxw
			references users
);

alter table payments owner to hfpmlazvgosijf;

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

create table tweet
(
	id bigint generated by default as identity
		constraint tweet_pkey
			primary key,
	content varchar(255),
	published_at varchar(255),
	author_user_id bigint
		constraint fkow9n2oc1xipwvys0ielduf9o2
			references users
);

alter table tweet owner to hfpmlazvgosijf;

create table trading
(
	id bigint generated by default as identity
		constraint trading_pkey
			primary key,
	priority_post double precision,
	news_id bigint
		constraint fkqq7k3drxhtg9j5a6psqow60b6
			references news,
	tweet_id bigint
		constraint fkp031pr4f52abs86ehnuiukho0
			references tweet
);

alter table trading owner to hfpmlazvgosijf;

create table trading_tweet
(
	trading_id bigint not null
		constraint fk8d7t7k56u5x2wnjgmvpuqq7ss
			references trading,
	tweet_id bigint not null
		constraint uk_ks22n9xgc7qo5jj0jugovaph7
			unique
		constraint fkdmt0qah29r9ijqges7ovui1he
			references tweet
);

alter table trading_tweet owner to hfpmlazvgosijf;

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

create table user_roles
(
	user_id bigint not null
		constraint fkhfh9dx7w3ubf1co1vdev94g3f
			references users,
	role_id integer not null
		constraint fkh8ciramu9cc9q3qcqiv4ue8a6
			references roles,
	constraint user_roles_pkey
		primary key (user_id, role_id)
);

alter table user_roles owner to hfpmlazvgosijf;

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

alter table users
	add constraint fkcf6cgic6n7ek155uj81npsbcm
		foreign key (wallet_id) references wallets;

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

create table watchlist_item_description
(
	id bigint generated by default as identity
		constraint watchlist_item_description_pkey
			primary key,
	description text,
	ticker_name varchar(255)
);

alter table watchlist_item_description owner to hfpmlazvgosijf;

create table watchlist_item_order
(
	id bigint generated by default as identity
		constraint watchlist_item_order_pkey
			primary key,
	user_user_id bigint
		constraint fktr0d4o214cn3l8wdau6gng7yy
			references users
);

alter table watchlist_item_order owner to hfpmlazvgosijf;

create table watchlist_item_order_tickers
(
	watchlist_item_order_id bigint not null
		constraint fk1v3212sypknmecitn9ekukcuc
			references watchlist_item_order,
	tickers varchar(255)
);

alter table watchlist_item_order_tickers owner to hfpmlazvgosijf;

create table play_list
(
	id bigint generated by default as identity
		constraint play_list_pkey
			primary key,
	title varchar(255),
	description text
);

alter table play_list owner to hfpmlazvgosijf;

create table playlist_ticker
(
	playlist_id bigint not null,
	ticker varchar(255)
);

alter table playlist_ticker owner to hfpmlazvgosijf;

create table play_listc
(
	id bigint generated by default as identity
		constraint play_listc_pkey
			primary key,
	category varchar(255),
	imageurl varchar(255),
	title varchar(255)
);

alter table play_listc owner to hfpmlazvgosijf;

create table play_list_wrapper
(
	id bigint generated by default as identity
		constraint play_list_wrapper_pkey
			primary key,
	category varchar(255)
);

alter table play_list_wrapper owner to hfpmlazvgosijf;

create table play_list_wrapper_play_listc
(
	play_list_wrapper_id bigint not null
		constraint fkt0gv6m48p5xcljc46efw3ik82
			references play_list_wrapper,
	play_listc_id bigint not null
		constraint uk_d8kya2fa0yvj5wec7d5y6b0y3
			unique
		constraint fkjrqe0s8qpc2pfcw8e8txcdgie
			references play_listc
);

alter table play_list_wrapper_play_listc owner to hfpmlazvgosijf;

create table forusection
(
	id bigint generated by default as identity
		constraint forusection_pkey
			primary key,
	points integer,
	tweet_id bigint
		constraint fktweettweetid_forusectiontweetid
			references tweet
);

alter table forusection owner to hfpmlazvgosijf;

 