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

