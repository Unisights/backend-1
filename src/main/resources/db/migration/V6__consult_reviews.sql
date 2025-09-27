-- which consultant reviews which app
create table if not exists reviews(
  id bigserial primary key,
  application_id bigint not null references applications(id) on delete cascade,
  consultant_id bigint not null,
  decision text not null,      -- APPROVE, REJECT, NEEDS_FIX
  feedback text,
  created_at timestamptz default now()
);

/* optional: future use for routing to specific consultants */
create table if not exists assignments(
  id bigserial primary key,
  consultant_id bigint not null,
  student_id bigint not null,
  created_at timestamptz default now()
);
