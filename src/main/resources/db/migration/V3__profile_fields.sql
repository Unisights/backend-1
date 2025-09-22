alter table student_profiles
    add column if not exists ielts numeric(3,1),
    add column if not exists toefl integer,
    add column if not exists tests jsonb,
    add column if not exists updated_at timestamptz default now();

insert into student_profiles(user_id, full_name, country, gpa, budget)
values (1, 'Demo Student', 'India', 3.0, 30000);