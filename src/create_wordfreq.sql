create database wordfreq;

use wordfreq;

create table site(
	id bigint(10) unsigned auto_increment,
	domain varchar(50) not null,
	url varchar(130) not null unique,
	primary key (id)
);

create table page(
	id bigint(10) unsigned,
	parent bigint(10) unsigned,
	site_id bigint(10) unsigned not null,
	depth mediumint(10) unsigned not null,
	title varchar(30),
	url varchar(130) not null unique,
	primary key (id),
	foreign key (parent) references page (id)
	on delete set null on update cascade,
	foreign key (site_id) references site(id)
	on delete cascade on update cascade
);

create table word(
	page_id bigint(10) unsigned,
	word varchar(30),
	freq bigint(10) unsigned not null,
	primary key (page_id, word),
	foreign key (page_id) references page(id)
	on delete cascade on update cascade
);

create index IndWord on word(word);

grant select, insert, delete, update on wordfreq.* to mai1223@localhost identified by 'mai1223';

grant select on wordfreq.* to commonUser@localhost identified by 'commonPassword';


insert into site (id, domain, url) values ('1', 'www.nintendo.gr/', 'http://www.nintendo.gr');
insert into site (id, domain, url) values ('2', 'www.serenesforest.net/', 'http://www.serenesforest.net');