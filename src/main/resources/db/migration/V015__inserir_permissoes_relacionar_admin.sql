insert into permissao values ( 1, "CADASTRAR_CIDADE");
insert into permissao values ( 2, "CADASTRAR_USUARIO");

insert into grupo_permissao ( codigo_grupo, codigo_permissao) values ( 1, 1);
insert into grupo_permissao ( codigo_grupo, codigo_permissao) values ( 1, 2);

insert into usuario_grupo ( codigo_usuario, codigo_grupo ) 
               values ( ( select codigo from usuario where email = "admin@brewer.com") , 1 );
