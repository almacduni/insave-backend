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

