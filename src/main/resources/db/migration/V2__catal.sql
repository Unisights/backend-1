create table universities(
  id bigserial primary key,
  name text not null,
  country text,
  city text
);

create table programs(
  id bigserial primary key,
  university_id bigint references universities(id),
  title text,
  degree text,
  fee integer,
  deadlines text,
  min_gpa numeric(3,2),
  min_ielts numeric(3,1),
  min_toefl integer,
  reqs jsonb
);

insert into universities(name, country, city)
values ('MIT', 'USA', 'Cambridge'),
       ('Oxford', 'UK', 'Oxford');

insert into programs(university_id, title, degree, fee, deadlines, min_gpa, min_ielts, min_toefl, reqs)
values
  (1, 'Computer Science', 'MS', 50000, '2025-12-01', 3.5, 7.0, 100, '{"experience": "optional"}'),
  (2, 'Data Science', 'MSc', 30000, '2025-11-15', 3.2, 6.5, 90, '{"portfolio": "required"}');
