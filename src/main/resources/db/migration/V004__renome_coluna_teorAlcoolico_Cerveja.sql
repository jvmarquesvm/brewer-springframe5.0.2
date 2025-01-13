alter table cerveja drop column teor_acoolico;
alter table cerveja add column teor_alcoolico decimal(10, 2) not null;
