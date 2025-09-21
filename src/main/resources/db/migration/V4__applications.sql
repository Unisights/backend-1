-- Applications
create table if not exists applications(
                                           id bigserial primary key,
                                           student_id bigint not null,
                                           program_id bigint not null references programs(id),
    status text not null default 'DRAFT', -- DRAFT, SUBMITTED, REVIEW, ACCEPTED, REJECTED
    created_at timestamptz default now()
    );

-- Checklist items for each application
create table if not exists checklist_items(
                                              id bigserial primary key,
                                              application_id bigint references applications(id) on delete cascade,
    name text not null,
    required boolean default true,
    status text not null default 'PENDING', -- PENDING, DONE
    comment text
    );

-- Timeline of events
create table if not exists app_events(
                                         id bigserial primary key,
                                         application_id bigint references applications(id) on delete cascade,
    event text not null,
    created_at timestamptz default now()
    );


UPDATE programs SET reqs = '{"docs": ["cv", "transcript"], "experience": "optional"}' WHERE id = 1;
