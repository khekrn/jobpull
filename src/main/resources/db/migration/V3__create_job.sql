CREATE SEQUENCE IF NOT EXISTS job_state_id_seq;

create table job (
    "id" bigint default nextval('job_state_id_seq') not null,
    "job_name" varchar(255) not null,
    "job_type" varchar(255) not null,
    "payload" text not null,
    "status" varchar(255) not null,
    "retries" int4 NOT NULL DEFAULT 0,
    "created_at" timestamp,
    "updated_at" timestamp,
    primary key (id)
);