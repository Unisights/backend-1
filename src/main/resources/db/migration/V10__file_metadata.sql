create table if not exists app_files(
                                        id bigserial primary key,
                                        application_id bigint not null references applications(id) on delete cascade,
    storage_key text not null,          -- e.g. s3 object key
    original_name text not null,
    mime text,
    size_bytes bigint,
    uploaded_by bigint,                 -- user id
    created_at timestamptz default now()
    );