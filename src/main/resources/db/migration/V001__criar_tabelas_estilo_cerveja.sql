create table estilo (
  codigo bigint(20) auto_increment,
  nome varchar(50) not null,
  
  constraint pk_estilo primary key (codigo)
  
) engine=innodb default charset=utf8;

create table cerveja (
  codigo bigint(20) auto_increment,
  nome varchar(80) not null,
  sky varchar(50) not null,
  descricao text not null,
  valor decimal(10, 2) not null,
  teor_acoolico decimal(10, 2) not null,
  comissao decimal(10, 2) not null,
  sabor varchar(50) not null,
  origem varchar(50) not null,
  codigo_estilo bigint(20) not null,
  
  constraint pk_cerveja primary key (codigo),
  constraint fk_cerveja_stilo foreign key (codigo_estilo) references estilo(codigo)
  
) engine=innodb default charset=utf8;

insert into estilo values (0, 'Amber Lager');