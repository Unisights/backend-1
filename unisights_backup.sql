--
-- PostgreSQL database dump
--

\restrict iszfC2ZBVATIjQ7PoTgzFCl7rohcebppisZOBT9zvyioKC8UFB4LKSh41fu63P3

-- Dumped from database version 16.10 (Debian 16.10-1.pgdg13+1)
-- Dumped by pg_dump version 17.6

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: app_events; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.app_events (
    id bigint NOT NULL,
    application_id bigint,
    event text NOT NULL,
    created_at timestamp with time zone DEFAULT now()
);


ALTER TABLE public.app_events OWNER TO postgres;

--
-- Name: app_events_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.app_events_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.app_events_id_seq OWNER TO postgres;

--
-- Name: app_events_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.app_events_id_seq OWNED BY public.app_events.id;


--
-- Name: applications; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.applications (
    id bigint NOT NULL,
    student_id bigint NOT NULL,
    program_id bigint NOT NULL,
    status text DEFAULT 'DRAFT'::text NOT NULL,
    created_at timestamp with time zone DEFAULT now()
);


ALTER TABLE public.applications OWNER TO postgres;

--
-- Name: applications_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.applications_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.applications_id_seq OWNER TO postgres;

--
-- Name: applications_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.applications_id_seq OWNED BY public.applications.id;


--
-- Name: assignments; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.assignments (
    id bigint NOT NULL,
    consultant_id bigint NOT NULL,
    student_id bigint NOT NULL,
    created_at timestamp with time zone DEFAULT now()
);


ALTER TABLE public.assignments OWNER TO postgres;

--
-- Name: assignments_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.assignments_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.assignments_id_seq OWNER TO postgres;

--
-- Name: assignments_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.assignments_id_seq OWNED BY public.assignments.id;


--
-- Name: checklist_items; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.checklist_items (
    id bigint NOT NULL,
    application_id bigint,
    name text NOT NULL,
    required boolean DEFAULT true,
    status text DEFAULT 'PENDING'::text NOT NULL,
    comment text
);


ALTER TABLE public.checklist_items OWNER TO postgres;

--
-- Name: checklist_items_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.checklist_items_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.checklist_items_id_seq OWNER TO postgres;

--
-- Name: checklist_items_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.checklist_items_id_seq OWNED BY public.checklist_items.id;


--
-- Name: flyway_schema_history; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.flyway_schema_history (
    installed_rank integer NOT NULL,
    version character varying(50),
    description character varying(200) NOT NULL,
    type character varying(20) NOT NULL,
    script character varying(1000) NOT NULL,
    checksum integer,
    installed_by character varying(100) NOT NULL,
    installed_on timestamp without time zone DEFAULT now() NOT NULL,
    execution_time integer NOT NULL,
    success boolean NOT NULL
);


ALTER TABLE public.flyway_schema_history OWNER TO postgres;

--
-- Name: programs; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.programs (
    id bigint NOT NULL,
    university_id bigint,
    title text,
    degree text,
    fee integer,
    deadlines text,
    min_gpa numeric(3,2),
    min_ielts numeric(3,1),
    min_toefl integer,
    reqs jsonb
);


ALTER TABLE public.programs OWNER TO postgres;

--
-- Name: programs_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.programs_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.programs_id_seq OWNER TO postgres;

--
-- Name: programs_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.programs_id_seq OWNED BY public.programs.id;


--
-- Name: reviews; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.reviews (
    id bigint NOT NULL,
    application_id bigint NOT NULL,
    consultant_id bigint NOT NULL,
    decision text NOT NULL,
    feedback text,
    created_at timestamp with time zone DEFAULT now()
);


ALTER TABLE public.reviews OWNER TO postgres;

--
-- Name: reviews_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.reviews_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.reviews_id_seq OWNER TO postgres;

--
-- Name: reviews_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.reviews_id_seq OWNED BY public.reviews.id;


--
-- Name: student_profiles; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.student_profiles (
    user_id bigint NOT NULL,
    full_name text,
    country text,
    gpa numeric(3,2),
    budget integer,
    ielts numeric(3,1),
    toefl integer,
    tests jsonb,
    updated_at timestamp with time zone DEFAULT now()
);


ALTER TABLE public.student_profiles OWNER TO postgres;

--
-- Name: universities; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.universities (
    id bigint NOT NULL,
    name text NOT NULL,
    country text,
    city text
);


ALTER TABLE public.universities OWNER TO postgres;

--
-- Name: universities_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.universities_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.universities_id_seq OWNER TO postgres;

--
-- Name: universities_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.universities_id_seq OWNED BY public.universities.id;


--
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    id bigint NOT NULL,
    email text NOT NULL,
    password_hash text NOT NULL,
    role text NOT NULL,
    created_at timestamp with time zone DEFAULT now(),
    CONSTRAINT users_role_check CHECK ((role = ANY (ARRAY['STUDENT'::text, 'CONSULTANT'::text, 'ADMIN'::text])))
);


ALTER TABLE public.users OWNER TO postgres;

--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.users_id_seq OWNER TO postgres;

--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- Name: app_events id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_events ALTER COLUMN id SET DEFAULT nextval('public.app_events_id_seq'::regclass);


--
-- Name: applications id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.applications ALTER COLUMN id SET DEFAULT nextval('public.applications_id_seq'::regclass);


--
-- Name: assignments id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.assignments ALTER COLUMN id SET DEFAULT nextval('public.assignments_id_seq'::regclass);


--
-- Name: checklist_items id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.checklist_items ALTER COLUMN id SET DEFAULT nextval('public.checklist_items_id_seq'::regclass);


--
-- Name: programs id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.programs ALTER COLUMN id SET DEFAULT nextval('public.programs_id_seq'::regclass);


--
-- Name: reviews id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.reviews ALTER COLUMN id SET DEFAULT nextval('public.reviews_id_seq'::regclass);


--
-- Name: universities id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.universities ALTER COLUMN id SET DEFAULT nextval('public.universities_id_seq'::regclass);


--
-- Name: users id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- Data for Name: app_events; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.app_events (id, application_id, event, created_at) FROM stdin;
\.


--
-- Data for Name: applications; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.applications (id, student_id, program_id, status, created_at) FROM stdin;
\.


--
-- Data for Name: assignments; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.assignments (id, consultant_id, student_id, created_at) FROM stdin;
\.


--
-- Data for Name: checklist_items; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.checklist_items (id, application_id, name, required, status, comment) FROM stdin;
\.


--
-- Data for Name: flyway_schema_history; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) FROM stdin;
1	1	core	SQL	V1__core.sql	1802813209	postgres	2025-09-24 22:40:08.494362	36	t
2	2	catal	SQL	V2__catal.sql	-542687076	postgres	2025-09-24 22:40:08.578513	33	t
3	3	profile fields	SQL	V3__profile_fields.sql	428774504	postgres	2025-09-24 22:40:08.639906	9	t
4	4	applications	SQL	V4__applications.sql	-584201357	postgres	2025-09-24 22:40:08.671432	36	t
5	6	consult reviews	SQL	V6__consult_reviews.sql	1373760039	postgres	2025-09-24 22:52:49.636083	28	t
\.


--
-- Data for Name: programs; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.programs (id, university_id, title, degree, fee, deadlines, min_gpa, min_ielts, min_toefl, reqs) FROM stdin;
2	2	Data Science	MSc	30000	2025-11-15	3.20	6.5	90	{"portfolio": "required"}
1	1	Computer Science	MS	50000	2025-12-01	3.50	7.0	100	{"docs": ["cv", "transcript"], "experience": "optional"}
\.


--
-- Data for Name: reviews; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.reviews (id, application_id, consultant_id, decision, feedback, created_at) FROM stdin;
\.


--
-- Data for Name: student_profiles; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.student_profiles (user_id, full_name, country, gpa, budget, ielts, toefl, tests, updated_at) FROM stdin;
1	Demo Student	India	3.00	30000	\N	\N	\N	2025-09-24 17:10:08.646218+00
\.


--
-- Data for Name: universities; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.universities (id, name, country, city) FROM stdin;
1	MIT	USA	Cambridge
2	Oxford	UK	Oxford
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (id, email, password_hash, role, created_at) FROM stdin;
1	demo@example.com	hashed_password_here	STUDENT	2025-09-24 17:10:08.646218+00
\.


--
-- Name: app_events_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.app_events_id_seq', 1, false);


--
-- Name: applications_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.applications_id_seq', 1, false);


--
-- Name: assignments_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.assignments_id_seq', 1, false);


--
-- Name: checklist_items_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.checklist_items_id_seq', 1, false);


--
-- Name: programs_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.programs_id_seq', 2, true);


--
-- Name: reviews_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.reviews_id_seq', 1, false);


--
-- Name: universities_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.universities_id_seq', 2, true);


--
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.users_id_seq', 1, false);


--
-- Name: app_events app_events_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_events
    ADD CONSTRAINT app_events_pkey PRIMARY KEY (id);


--
-- Name: applications applications_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.applications
    ADD CONSTRAINT applications_pkey PRIMARY KEY (id);


--
-- Name: assignments assignments_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.assignments
    ADD CONSTRAINT assignments_pkey PRIMARY KEY (id);


--
-- Name: checklist_items checklist_items_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.checklist_items
    ADD CONSTRAINT checklist_items_pkey PRIMARY KEY (id);


--
-- Name: flyway_schema_history flyway_schema_history_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.flyway_schema_history
    ADD CONSTRAINT flyway_schema_history_pk PRIMARY KEY (installed_rank);


--
-- Name: programs programs_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.programs
    ADD CONSTRAINT programs_pkey PRIMARY KEY (id);


--
-- Name: reviews reviews_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.reviews
    ADD CONSTRAINT reviews_pkey PRIMARY KEY (id);


--
-- Name: student_profiles student_profiles_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.student_profiles
    ADD CONSTRAINT student_profiles_pkey PRIMARY KEY (user_id);


--
-- Name: universities universities_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.universities
    ADD CONSTRAINT universities_pkey PRIMARY KEY (id);


--
-- Name: users users_email_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_email_key UNIQUE (email);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: flyway_schema_history_s_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX flyway_schema_history_s_idx ON public.flyway_schema_history USING btree (success);


--
-- Name: app_events app_events_application_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.app_events
    ADD CONSTRAINT app_events_application_id_fkey FOREIGN KEY (application_id) REFERENCES public.applications(id) ON DELETE CASCADE;


--
-- Name: applications applications_program_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.applications
    ADD CONSTRAINT applications_program_id_fkey FOREIGN KEY (program_id) REFERENCES public.programs(id);


--
-- Name: checklist_items checklist_items_application_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.checklist_items
    ADD CONSTRAINT checklist_items_application_id_fkey FOREIGN KEY (application_id) REFERENCES public.applications(id) ON DELETE CASCADE;


--
-- Name: programs programs_university_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.programs
    ADD CONSTRAINT programs_university_id_fkey FOREIGN KEY (university_id) REFERENCES public.universities(id);


--
-- Name: reviews reviews_application_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.reviews
    ADD CONSTRAINT reviews_application_id_fkey FOREIGN KEY (application_id) REFERENCES public.applications(id) ON DELETE CASCADE;


--
-- Name: student_profiles student_profiles_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.student_profiles
    ADD CONSTRAINT student_profiles_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

\unrestrict iszfC2ZBVATIjQ7PoTgzFCl7rohcebppisZOBT9zvyioKC8UFB4LKSh41fu63P3

