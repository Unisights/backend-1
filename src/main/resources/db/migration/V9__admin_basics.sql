-- mark users active/inactive (soft)
alter table users
    add column if not exists is_active boolean default true,
    add column if not exists created_at timestamptz default now();

-- minimal audit log (optional, used for exports/ops visibility)
create table if not exists audit_log(
                                        id bigserial primary key,
                                        actor_user_id bigint,
                                        action text not null,         -- e.g., 'USER_DEACTIVATE', 'PROGRAM_CREATE'
                                        details jsonb,
                                        created_at timestamptz default now()
    );

-- seed an admin user for Day 9 (password handled by Day 8/enc later)
insert into users(email,password_hash,role,is_active)
select 'admin@demo.com','noop','ADMIN',true
    where not exists (select 1 from users where email='admin@demo.com');