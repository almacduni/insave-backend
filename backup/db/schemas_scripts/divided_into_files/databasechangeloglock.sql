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

