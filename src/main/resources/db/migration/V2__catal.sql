create table universities(
  id bigserial primary key,
  name text not null,
  country text,
  city text
);

create table programs(
  id bigserial primary key,
  name text not null,
  university_id bigint references universities(id)
);
