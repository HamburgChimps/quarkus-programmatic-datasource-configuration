create sequence hibernate_sequence start with 1 increment by 1;

    create table TestEntity (
       id bigint not null,
        primary key (id)
    );
