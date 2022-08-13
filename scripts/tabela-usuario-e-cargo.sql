create table usuario(
	id_usuario numeric(11) not null,
	nome text not null,
	email text unique not null,
	senha text not null,
	foto bytea,
	primary key(id_usuario)
);

create table cargo(
	id_cargo numeric not null,
	nome text unique not null,
	primary key (id_cargo)
);

create table usuario_cargo(
	id_usuario numeric not null,
	id_cargo numeric not null,
	primary key(id_usuario, id_cargo),
	constraint fk_usuario_cargo_cargo foreign key (id_cargo) references cargo (id_cargo),
  	constraint fk_usuario_cargo_usuario foreign key (id_usuario) references usuario (id_usuario)
);

create sequence seq_usuario
increment 1
start 1;
	
create sequence seq_cargo
increment 1
start 1;

insert into cargo(id_cargo, nome)
values (nextVal('seq_cargo'), 'ROLE_COLABORADOR');

insert into cargo(id_cargo, nome)
values(nextVal('seq_cargo'), 'ROLE_COMPRADOR');

insert into cargo(id_cargo, nome)
values(nextVal('seq_cargo'), 'ROLE_GESTOR');

insert into cargo(id_cargo, nome)
values(nextVal('seq_cargo'), 'ROLE_FINANCEIRO');

insert into cargo(id_cargo, nome)
values(nextVal('seq_cargo'), 'ROLE_ADMINISTRADOR');