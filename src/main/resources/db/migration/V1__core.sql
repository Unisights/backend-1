-- Roles as text for simplicity on Day-1
create table users(
                      id bigserial primary key,
                      email text unique not null,
                      password_hash text not null,
                      role text not null check (role in ('STUDENT','CONSULTANT','ADMIN')),
                      created_at timestamptz default now()
);

create table student_profiles(
                                 user_id bigint primary key references users(id) on delete cascade,
                                 full_name text,
                                 country text,
                                 gpa numeric(3,2),
                                 budget integer
);