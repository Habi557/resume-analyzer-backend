create table education_entity_seq (
    next_val bigint
) engine=InnoDB;

insert into education_entity_seq values ( 1 );

create table education_entity (
    id bigint not null,
    resume_id bigint,
    degree varchar(255),
    duration varchar(255),
    institute varchar(255),
    score varchar(255),
    primary key (id)
) engine=InnoDB;

create table resume (
    scan_all_resumes bit,
    years_of_experience float(53),
    id bigint not null auto_increment,
    upload_time datetime(6),
    user_id bigint,
    address varchar(255),
    email varchar(255),
    file_name varchar(255),
    file_path varchar(255),
    name varchar(255),
    phone varchar(255),
    resume_hash varchar(255),
    extracted_text LONGTEXT,
    red_flags LONGTEXT,
    status enum ('ANALYZED','FAILED','PARSED','PROCESSING','UPLOADED'),
    primary key (id)
) engine=InnoDB;

create table resume_analysis_failures (
    retry_count integer not null,
    failed_at datetime(6),
    id bigint not null,
    last_retry_at datetime(6),
    resume_id bigint,
    error_message varchar(255),
    job_id varchar(255),
    status enum ('PENDING_RETRY','RESOLVED','RETRY_EXHAUSTED'),
    primary key (id)
) engine=InnoDB;

create table resume_analysis_failures_seq (
    next_val bigint
) engine=InnoDB;

insert into resume_analysis_failures_seq values ( 1 );

create table resume_jobs (
    failed_resumes integer not null,
    processed_resumes integer not null,
    total_resumes integer not null,
    completed_at datetime(6),
    created_at datetime(6),
    id bigint not null auto_increment,
    job_id varchar(255) not null,
    job_role LONGTEXT,
    status enum ('AI_SERVICE_DOWN','COMPLETED','COMPLETED_WITH_FAILURES','FAILED','PENDING','RUNNING') not null,
    primary key (id)
) engine=InnoDB;

create table resume_analysis_data (
    match_percentage integer,
    analysizedtime datetime(6),
    id bigint not null auto_increment,
    resume_id bigint,
    interview_date varchar(255),
    interview_mode varchar(255),
    interview_time varchar(255),
    selected_status varchar(255),
    analysis LONGTEXT,
    suggestions LONGTEXT,
    top_matching_skills LONGTEXT,
    primary key (id)
) engine=InnoDB;

create table roles (
    id bigint not null auto_increment,
    role_name varchar(50) not null,
    primary key (id)
) engine=InnoDB;

create table skill (
    id bigint not null auto_increment,
    resume_id bigint,
    name varchar(255),
    primary key (id)
) engine=InnoDB;

create table test (
    id bigint not null,
    name varchar(255),
    primary key (id)
) engine=InnoDB;

create table test_seq (
    next_val bigint
) engine=InnoDB;

insert into test_seq values ( 1 );

create table token (
    expired bit not null,
    revoked bit not null,
    id bigint not null auto_increment,
    user_id bigint,
    token varchar(500),
    token_type enum ('BEARER'),
    primary key (id)
) engine=InnoDB;

create table user_roles (
    role_id bigint not null,
    user_id bigint not null,
    primary key (role_id, user_id)
) engine=InnoDB;

create table users (
    enabled bit not null,
    id bigint not null auto_increment,
    username varchar(50) not null,
    email varchar(100) not null,
    password varchar(255),
    provider varchar(255) not null,
    provider_id varchar(255),
    primary key (id)
) engine=InnoDB;

alter table resume
   add constraint UKqo68fn6nb7q7yx4k4f58459di unique (resume_hash);

create index idx_job_id
   on resume_jobs (job_id);

create index idx_job_status
   on resume_jobs (status);

alter table resume_jobs
   add constraint UK4tpylquwaxyl5beipjqpm9726 unique (job_id);

alter table roles
   add constraint UK716hgxp60ym1lifrdgp67xt5k unique (role_name);

alter table token
   add constraint UKpddrhgwxnms2aceeku9s2ewy5 unique (token);

alter table users
   add constraint UKr43af9ap4edm43mmtq01oddj6 unique (username);

alter table users
   add constraint UK6dotkott2kjsp8vw4d0m25fb7 unique (email);

alter table users
   add constraint UK6jdo1l976be85wv43w6x6e6x2 unique (provider_id);

alter table education_entity
   add constraint FKpbm3ewxb3wlf9cigebmd7qhy
   foreign key (resume_id)
   references resume (id);

alter table resume
   add constraint FKpv0whudowxosfu792veo6s2c0
   foreign key (user_id)
   references users (id);

alter table resume_analysis_failures
   add constraint FKm55971ypjm0nut0q0t72l6fiy
   foreign key (resume_id)
   references resume (id);

alter table resume_analysis_data
   add constraint FKbwim0hbyfe2vwv3eo364h5val
   foreign key (resume_id)
   references resume (id);

alter table skill
   add constraint FKahgxm3rt1nahhdy66taa4xjrv
   foreign key (resume_id)
   references resume (id);

alter table token
   add constraint FKj8rfw4x0wjjyibfqq566j4qng
   foreign key (user_id)
   references users (id);

alter table user_roles
   add constraint FKh8ciramu9cc9q3qcqiv4ue8a6
   foreign key (role_id)
   references roles (id);

alter table user_roles
   add constraint FKhfh9dx7w3ubf1co1vdev94g3f
   foreign key (user_id)
   references users (id);