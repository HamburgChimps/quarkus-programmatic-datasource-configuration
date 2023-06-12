create sequence testentity_seq start with 1 increment by 50;

    create table testentity (
       id bigint not null,
        primary key (id)
    );
