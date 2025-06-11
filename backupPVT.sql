--
-- PostgreSQL database dump
--

-- Dumped from database version 17.5
-- Dumped by pg_dump version 17.2

-- Started on 2025-06-08 21:49:59

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

--
-- TOC entry 233 (class 1255 OID 17093)
-- Name: get_participant_job_position_by_id(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_participant_job_position_by_id(p_id integer) RETURNS character varying
    LANGUAGE plpgsql
    AS $$
DECLARE
    job_pos VARCHAR(100);
BEGIN
    SELECT job_position INTO job_pos FROM participants WHERE participant_id = p_id;
    RETURN job_pos;
END;
$$;


ALTER FUNCTION public.get_participant_job_position_by_id(p_id integer) OWNER TO postgres;

--
-- TOC entry 231 (class 1255 OID 17091)
-- Name: random_between(integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.random_between(low integer, high integer) RETURNS integer
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN floor(random() * (high - low + 1) + low);
END;
$$;


ALTER FUNCTION public.random_between(low integer, high integer) OWNER TO postgres;

--
-- TOC entry 232 (class 1255 OID 17092)
-- Name: random_float_between(real, real); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.random_float_between(low real, high real) RETURNS real
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN random() * (high - low) + low;
END;
$$;


ALTER FUNCTION public.random_float_between(low real, high real) OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 217 (class 1259 OID 16973)
-- Name: companies; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.companies (
    company_id integer NOT NULL,
    name character varying(255) NOT NULL,
    address text,
    date_registered date NOT NULL,
    contact_person character varying(255),
    mobile_phone character varying(50),
    email_address character varying(100)
);


ALTER TABLE public.companies OWNER TO postgres;

--
-- TOC entry 218 (class 1259 OID 16978)
-- Name: companies_company_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.companies_company_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.companies_company_id_seq OWNER TO postgres;

--
-- TOC entry 4944 (class 0 OID 0)
-- Dependencies: 218
-- Name: companies_company_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.companies_company_id_seq OWNED BY public.companies.company_id;


--
-- TOC entry 219 (class 1259 OID 16979)
-- Name: measurement_data; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.measurement_data (
    measurement_id integer NOT NULL,
    participant_id integer NOT NULL,
    criterion_id integer NOT NULL,
    measurement_datetime timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    job_position character varying(100),
    severity_level character varying(50),
    severity_score integer,
    measurement_location point,
    measured_scores real[],
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.measurement_data OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 16986)
-- Name: measurement_data_measurement_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.measurement_data_measurement_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.measurement_data_measurement_id_seq OWNER TO postgres;

--
-- TOC entry 4945 (class 0 OID 0)
-- Dependencies: 220
-- Name: measurement_data_measurement_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.measurement_data_measurement_id_seq OWNED BY public.measurement_data.measurement_id;


--
-- TOC entry 221 (class 1259 OID 16987)
-- Name: observers; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.observers (
    observer_id integer NOT NULL,
    user_id integer NOT NULL,
    company_id integer NOT NULL,
    name character varying(255) NOT NULL,
    address text,
    date_created timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    mobile_phone character varying(50),
    email_address character varying(100),
    gender character varying(10) DEFAULT 'Unknown'::character varying,
    "position" character varying(100) DEFAULT 'Staff'::character varying
);


ALTER TABLE public.observers OWNER TO postgres;

--
-- TOC entry 222 (class 1259 OID 16993)
-- Name: observers_observer_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.observers_observer_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.observers_observer_id_seq OWNER TO postgres;

--
-- TOC entry 4946 (class 0 OID 0)
-- Dependencies: 222
-- Name: observers_observer_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.observers_observer_id_seq OWNED BY public.observers.observer_id;


--
-- TOC entry 223 (class 1259 OID 16994)
-- Name: participants; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.participants (
    participant_id integer NOT NULL,
    user_id integer NOT NULL,
    company_id integer NOT NULL,
    name character varying(255) NOT NULL,
    department character varying(100),
    job_position character varying(100),
    email_address character varying(100),
    mobile_phone character varying(50),
    date_of_last_measurement timestamp without time zone,
    last_fatique_level integer
);


ALTER TABLE public.participants OWNER TO postgres;

--
-- TOC entry 224 (class 1259 OID 16999)
-- Name: participants_participant_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.participants_participant_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.participants_participant_id_seq OWNER TO postgres;

--
-- TOC entry 4947 (class 0 OID 0)
-- Dependencies: 224
-- Name: participants_participant_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.participants_participant_id_seq OWNED BY public.participants.participant_id;


--
-- TOC entry 225 (class 1259 OID 17000)
-- Name: performance_criteria; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.performance_criteria (
    criterion_id integer NOT NULL,
    name character varying(255) NOT NULL,
    description text,
    max_score integer NOT NULL,
    is_active boolean DEFAULT true,
    CONSTRAINT performance_criteria_max_score_check CHECK ((max_score > 0))
);


ALTER TABLE public.performance_criteria OWNER TO postgres;

--
-- TOC entry 226 (class 1259 OID 17007)
-- Name: performance_criteria_criterion_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.performance_criteria_criterion_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.performance_criteria_criterion_id_seq OWNER TO postgres;

--
-- TOC entry 4948 (class 0 OID 0)
-- Dependencies: 226
-- Name: performance_criteria_criterion_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.performance_criteria_criterion_id_seq OWNED BY public.performance_criteria.criterion_id;


--
-- TOC entry 227 (class 1259 OID 17008)
-- Name: user_manager; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.user_manager (
    manager_id integer NOT NULL,
    user_id integer NOT NULL,
    name character varying(255) NOT NULL,
    email character varying(100),
    mobile_phone character varying(50),
    date_created timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.user_manager OWNER TO postgres;

--
-- TOC entry 228 (class 1259 OID 17012)
-- Name: user_manager_manager_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.user_manager_manager_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.user_manager_manager_id_seq OWNER TO postgres;

--
-- TOC entry 4949 (class 0 OID 0)
-- Dependencies: 228
-- Name: user_manager_manager_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.user_manager_manager_id_seq OWNED BY public.user_manager.manager_id;


--
-- TOC entry 229 (class 1259 OID 17013)
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    user_id integer NOT NULL,
    username character varying(50) NOT NULL,
    password character varying(255) NOT NULL,
    role character varying(20) NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT users_role_check CHECK (((role)::text = ANY (ARRAY[('participant'::character varying)::text, ('observer'::character varying)::text, ('user_manager'::character varying)::text])))
);


ALTER TABLE public.users OWNER TO postgres;

--
-- TOC entry 230 (class 1259 OID 17018)
-- Name: users_user_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.users_user_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.users_user_id_seq OWNER TO postgres;

--
-- TOC entry 4950 (class 0 OID 0)
-- Dependencies: 230
-- Name: users_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.users_user_id_seq OWNED BY public.users.user_id;


--
-- TOC entry 4728 (class 2604 OID 17019)
-- Name: companies company_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.companies ALTER COLUMN company_id SET DEFAULT nextval('public.companies_company_id_seq'::regclass);


--
-- TOC entry 4729 (class 2604 OID 17020)
-- Name: measurement_data measurement_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.measurement_data ALTER COLUMN measurement_id SET DEFAULT nextval('public.measurement_data_measurement_id_seq'::regclass);


--
-- TOC entry 4732 (class 2604 OID 17021)
-- Name: observers observer_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.observers ALTER COLUMN observer_id SET DEFAULT nextval('public.observers_observer_id_seq'::regclass);


--
-- TOC entry 4736 (class 2604 OID 17022)
-- Name: participants participant_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.participants ALTER COLUMN participant_id SET DEFAULT nextval('public.participants_participant_id_seq'::regclass);


--
-- TOC entry 4737 (class 2604 OID 17023)
-- Name: performance_criteria criterion_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.performance_criteria ALTER COLUMN criterion_id SET DEFAULT nextval('public.performance_criteria_criterion_id_seq'::regclass);


--
-- TOC entry 4739 (class 2604 OID 17024)
-- Name: user_manager manager_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_manager ALTER COLUMN manager_id SET DEFAULT nextval('public.user_manager_manager_id_seq'::regclass);


--
-- TOC entry 4741 (class 2604 OID 17025)
-- Name: users user_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users ALTER COLUMN user_id SET DEFAULT nextval('public.users_user_id_seq'::regclass);


--
-- TOC entry 4925 (class 0 OID 16973)
-- Dependencies: 217
-- Data for Name: companies; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.companies (company_id, name, address, date_registered, contact_person, mobile_phone, email_address) FROM stdin;
2	PT Siap Antar Jemput	Jl. Raya Bogor KM 20, Depok	2018-05-10	Bela Kusuma	085611223344	kontak@siapantar.co.id
3	PT Bangun Bumi Bersama	Jl. Thamrin No. 50, Surabaya	2019-09-15	Cakra Dirgantara	087855667788	admin@bangunbumi.com
1	PT Jago Coding Indonesia	Jl. Merdeka No. 1, Jakarta Pusat	2020-01-01	Ardi Wijayanto	081234567890	info@jagocoding.id
4	PT Sukses Makmur Bersama	Jl. Dahlia No.10, Blitar	2025-06-07	Alex Subandi	0835764375435	customerservice@sukses.com
5	PT Minyak dan Bumi	Jl. Kamboja No.12, Jombang	2025-06-07	Dewi Wijaya	081747582984	csbumi@gigimail.com
\.


--
-- TOC entry 4927 (class 0 OID 16979)
-- Dependencies: 219
-- Data for Name: measurement_data; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.measurement_data (measurement_id, participant_id, criterion_id, measurement_datetime, job_position, severity_level, severity_score, measurement_location, measured_scores, created_at) FROM stdin;
1	1	3	2025-05-30 00:03:36.306081	staff backend	Rendah	2	(-6.123114109039307,106.76319885253906)	{8.153284,9.106307}	2025-06-06 11:38:36.306081
2	1	1	2025-05-24 09:44:36.306081	staff backend	Tinggi	5	(-6.740608215332031,109.24246978759766)	{4.096625,2.1819177}	2025-06-06 11:38:36.306081
3	1	5	2025-05-12 16:27:36.306081	staff backend	Tinggi	1	(-6.488343715667725,111.01620483398438)	{9.76279,6.3777437}	2025-06-06 11:38:36.306081
4	1	1	2025-05-26 21:51:36.306081	staff backend	Sedang	4	(-6.2872538566589355,106.83139038085938)	{5.6692214,4.1681747}	2025-06-06 11:38:36.306081
5	1	3	2025-05-25 01:03:36.306081	staff backend	Tinggi	5	(-6.162768363952637,107.95320892333984)	{2.5799649,5.3215632}	2025-06-06 11:38:36.306081
6	2	2	2025-05-11 13:43:36.306081	staff UI/UX	Sedang	2	(-6.084387302398682,111.3040542602539)	{4.659877,1.4262507}	2025-06-06 11:38:36.306081
7	2	3	2025-06-03 09:13:36.306081	staff UI/UX	Tinggi	2	(-6.216287612915039,111.69020080566406)	{2.4935977,2.1233547}	2025-06-06 11:38:36.306081
8	2	1	2025-06-01 19:35:36.306081	staff UI/UX	Tinggi	2	(-6.069147109985352,108.88522338867188)	{9.611571,4.2587366}	2025-06-06 11:38:36.306081
9	2	4	2025-05-14 06:35:36.306081	staff UI/UX	Rendah	3	(-6.773860454559326,108.16901397705078)	{5.64044,2.2702549}	2025-06-06 11:38:36.306081
10	2	5	2025-05-23 21:34:36.306081	staff UI/UX	Rendah	3	(-6.465439796447754,110.91598510742188)	{6.2060847,6.7905717}	2025-06-06 11:38:36.306081
11	3	1	2025-05-24 07:17:36.306081	staff Cyber Security	Sedang	5	(-6.018788814544678,106.05340576171875)	{3.172898,5.494863}	2025-06-06 11:38:36.306081
12	3	5	2025-05-21 18:32:36.306081	staff Cyber Security	Rendah	1	(-6.052303314208984,107.05963897705078)	{2.497134,3.4110708}	2025-06-06 11:38:36.306081
13	3	3	2025-05-19 06:39:36.306081	staff Cyber Security	Tinggi	4	(-6.954506874084473,110.89307403564453)	{3.071101,9.033846}	2025-06-06 11:38:36.306081
14	3	3	2025-05-11 17:51:36.306081	staff Cyber Security	Sedang	2	(-6.578612327575684,106.82865142822266)	{5.6522593,2.6734278}	2025-06-06 11:38:36.306081
15	3	5	2025-05-12 07:44:36.306081	staff Cyber Security	Rendah	2	(-6.601580619812012,106.58905792236328)	{4.6244435,2.6081495}	2025-06-06 11:38:36.306081
16	4	4	2025-05-06 19:42:36.306081	staff backend	Rendah	2	(-6.02193021774292,106.21326446533203)	{2.4460003,5.3474946}	2025-06-06 11:38:36.306081
17	4	5	2025-05-24 17:58:36.306081	staff backend	Rendah	4	(-6.668169975280762,110.2800064086914)	{7.053913,4.6320963}	2025-06-06 11:38:36.306081
18	4	1	2025-06-01 19:19:36.306081	staff backend	Rendah	5	(-6.959262371063232,110.48846435546875)	{7.338279,9.661345}	2025-06-06 11:38:36.306081
19	4	5	2025-05-24 11:17:36.306081	staff backend	Rendah	1	(-6.152253150939941,110.51826477050781)	{9.449121,4.448219}	2025-06-06 11:38:36.306081
20	4	5	2025-05-15 01:16:36.306081	staff backend	Sedang	2	(-6.902599334716797,108.42359161376953)	{2.2331624,6.288683}	2025-06-06 11:38:36.306081
21	5	5	2025-05-30 12:22:36.306081	staff UI/UX	Tinggi	1	(-6.679330825805664,110.64324188232422)	{9.525087,2.7358425}	2025-06-06 11:38:36.306081
22	5	1	2025-05-19 05:40:36.306081	staff UI/UX	Tinggi	4	(-6.959665775299072,109.07158660888672)	{7.4407644,2.1931145}	2025-06-06 11:38:36.306081
23	5	1	2025-05-20 19:54:36.306081	staff UI/UX	Tinggi	5	(-6.218525409698486,106.72119140625)	{5.826086,4.335868}	2025-06-06 11:38:36.306081
24	5	5	2025-05-07 13:30:36.306081	staff UI/UX	Tinggi	2	(-6.580587387084961,108.29755401611328)	{3.3483899,8.956361}	2025-06-06 11:38:36.306081
25	5	3	2025-05-20 11:10:36.306081	staff UI/UX	Tinggi	2	(-6.8402299880981445,107.23990631103516)	{9.04489,6.923164}	2025-06-06 11:38:36.306081
26	6	1	2025-05-11 22:38:36.306081	driver motor	Rendah	4	(-6.944755554199219,110.5859146118164)	{3.4217463,7.283473}	2025-06-06 11:38:36.306081
27	6	1	2025-06-05 22:23:36.306081	driver motor	Sedang	1	(-6.514914035797119,107.8642578125)	{7.723503,5.794007}	2025-06-06 11:38:36.306081
28	6	5	2025-05-18 12:19:36.306081	driver motor	Tinggi	5	(-6.854454040527344,106.56983184814453)	{6.3059607,1.9484212}	2025-06-06 11:38:36.306081
29	6	1	2025-05-06 15:33:36.306081	driver motor	Tinggi	5	(-6.770469665527344,106.02713012695312)	{5.101031,1.7579728}	2025-06-06 11:38:36.306081
30	6	1	2025-05-18 08:38:36.306081	driver motor	Rendah	3	(-6.518715858459473,109.4566879272461)	{4.9269767,4.46126}	2025-06-06 11:38:36.306081
31	7	1	2025-05-15 03:59:36.306081	driver mobil	Rendah	4	(-6.880579948425293,111.91475677490234)	{7.421687,2.5817049}	2025-06-06 11:38:36.306081
32	7	2	2025-05-22 23:03:36.306081	driver mobil	Tinggi	1	(-6.099215984344482,111.39151000976562)	{4.5967655,3.045678}	2025-06-06 11:38:36.306081
33	7	1	2025-06-03 15:35:36.306081	driver mobil	Rendah	1	(-6.28568172454834,108.49050903320312)	{3.5774724,8.928162}	2025-06-06 11:38:36.306081
34	7	1	2025-05-20 10:50:36.306081	driver mobil	Rendah	5	(-6.267744064331055,108.33920288085938)	{2.6552885,2.4738047}	2025-06-06 11:38:36.306081
35	7	1	2025-05-13 07:07:36.306081	driver mobil	Tinggi	2	(-6.290684700012207,110.75439453125)	{4.1582494,9.723352}	2025-06-06 11:38:36.306081
36	8	3	2025-06-01 01:15:36.306081	sorter	Rendah	2	(-6.5437798500061035,107.24076080322266)	{6.9170203,7.4895697}	2025-06-06 11:38:36.306081
37	8	3	2025-05-06 21:54:36.306081	sorter	Sedang	3	(-6.974897384643555,107.50193786621094)	{1.3170726,6.4478025}	2025-06-06 11:38:36.306081
38	8	1	2025-05-29 03:49:36.306081	sorter	Rendah	5	(-6.104442596435547,109.8857192993164)	{7.7193694,2.445516}	2025-06-06 11:38:36.306081
39	8	5	2025-05-13 15:22:36.306081	sorter	Rendah	2	(-6.624639511108398,107.03688049316406)	{2.8424954,5.5614505}	2025-06-06 11:38:36.306081
40	8	4	2025-05-25 08:32:36.306081	sorter	Tinggi	5	(-6.451078414916992,108.82890319824219)	{4.4036746,3.39194}	2025-06-06 11:38:36.306081
41	9	2	2025-05-10 07:03:36.306081	security	Rendah	3	(-6.165265083312988,110.37557983398438)	{3.2520401,9.73235}	2025-06-06 11:38:36.306081
42	9	3	2025-05-29 07:37:36.306081	security	Sedang	3	(-6.099468231201172,108.2939682006836)	{6.863686,8.942736}	2025-06-06 11:38:36.306081
43	9	4	2025-05-11 23:36:36.306081	security	Rendah	4	(-6.783792495727539,107.84382629394531)	{4.830758,5.398902}	2025-06-06 11:38:36.306081
44	9	3	2025-06-06 08:23:36.306081	security	Rendah	5	(-6.500592231750488,109.15821838378906)	{9.202622,3.4448674}	2025-06-06 11:38:36.306081
45	9	3	2025-05-20 21:41:36.306081	security	Tinggi	5	(-6.786047458648682,110.7402114868164)	{8.054425,6.2613797}	2025-06-06 11:38:36.306081
46	10	3	2025-05-24 19:57:36.306081	driver truk	Tinggi	3	(-6.456830024719238,109.3280258178711)	{4.780314,9.792717}	2025-06-06 11:38:36.306081
47	10	5	2025-05-19 11:08:36.306081	driver truk	Tinggi	1	(-6.256146430969238,106.99549865722656)	{8.516561,1.5193521}	2025-06-06 11:38:36.306081
48	10	1	2025-05-10 20:22:36.306081	driver truk	Rendah	3	(-6.970463752746582,109.85560607910156)	{9.377101,9.019249}	2025-06-06 11:38:36.306081
49	10	2	2025-06-02 09:58:36.306081	driver truk	Rendah	3	(-6.994573593139648,107.33698272705078)	{5.34494,7.7995353}	2025-06-06 11:38:36.306081
50	10	3	2025-05-09 17:56:36.306081	driver truk	Sedang	4	(-6.9436750411987305,108.95716094970703)	{2.0639555,6.2258186}	2025-06-06 11:38:36.306081
51	11	3	2025-05-29 19:40:36.306081	Project Planner	Sedang	2	(-6.045452117919922,106.00346374511719)	{4.4497323,8.321748}	2025-06-06 11:38:36.306081
52	11	5	2025-05-11 05:23:36.306081	Project Planner	Tinggi	2	(-6.958221435546875,108.2217025756836)	{6.4111342,5.518324}	2025-06-06 11:38:36.306081
53	11	3	2025-05-22 10:46:36.306081	Project Planner	Tinggi	5	(-6.303378105163574,107.20166778564453)	{1.4120102,5.0879784}	2025-06-06 11:38:36.306081
54	11	1	2025-05-26 14:42:36.306081	Project Planner	Rendah	4	(-6.657989501953125,106.41366577148438)	{5.011707,8.247569}	2025-06-06 11:38:36.306081
55	11	4	2025-05-24 09:39:36.306081	Project Planner	Tinggi	5	(-6.21220064163208,111.79203033447266)	{5.543604,1.9023376}	2025-06-06 11:38:36.306081
56	12	5	2025-05-08 18:32:36.306081	Site Engineer	Tinggi	1	(-6.542443752288818,109.00120544433594)	{5.7247987,5.5323076}	2025-06-06 11:38:36.306081
57	12	5	2025-05-07 17:19:36.306081	Site Engineer	Sedang	2	(-6.646286964416504,109.1075210571289)	{2.3177242,1.9053878}	2025-06-06 11:38:36.306081
58	12	5	2025-05-26 15:24:36.306081	Site Engineer	Rendah	4	(-6.604301929473877,108.94955444335938)	{1.301472,5.2470875}	2025-06-06 11:38:36.306081
59	12	3	2025-05-28 08:35:36.306081	Site Engineer	Tinggi	2	(-6.458458423614502,110.5018081665039)	{8.214775,6.9877467}	2025-06-06 11:38:36.306081
60	12	5	2025-05-24 16:03:36.306081	Site Engineer	Sedang	1	(-6.077003002166748,109.78216552734375)	{9.00076,8.505268}	2025-06-06 11:38:36.306081
61	13	3	2025-05-16 21:39:36.306081	QA/QC Inspector	Tinggi	5	(-6.528536319732666,109.95498657226562)	{7.900326,2.0188088}	2025-06-06 11:38:36.306081
62	13	4	2025-05-13 14:37:36.306081	QA/QC Inspector	Sedang	5	(-6.157008171081543,108.41360473632812)	{9.374334,5.5966988}	2025-06-06 11:38:36.306081
63	13	4	2025-05-13 18:26:36.306081	QA/QC Inspector	Rendah	1	(-6.4777302742004395,110.98604583740234)	{8.457571,1.8619372}	2025-06-06 11:38:36.306081
64	13	4	2025-06-02 01:24:36.306081	QA/QC Inspector	Sedang	1	(-6.568883895874023,109.97119140625)	{8.269147,9.4414625}	2025-06-06 11:38:36.306081
65	13	4	2025-05-12 13:08:36.306081	QA/QC Inspector	Tinggi	3	(-6.802828788757324,109.28516387939453)	{8.441276,3.443519}	2025-06-06 11:38:36.306081
66	14	3	2025-06-05 09:54:36.306081	Project Planner	Tinggi	4	(-6.86040735244751,108.38421630859375)	{7.7278857,2.8014984}	2025-06-06 11:38:36.306081
67	14	4	2025-05-11 14:30:36.306081	Project Planner	Rendah	3	(-6.584989547729492,106.48373413085938)	{3.022967,6.8750467}	2025-06-06 11:38:36.306081
68	14	2	2025-05-21 05:44:36.306081	Project Planner	Sedang	1	(-6.021289348602295,107.91084289550781)	{9.589302,7.3019047}	2025-06-06 11:38:36.306081
69	14	3	2025-05-12 08:06:36.306081	Project Planner	Sedang	3	(-6.695703029632568,110.73695373535156)	{2.127247,7.977138}	2025-06-06 11:38:36.306081
70	14	1	2025-06-02 09:55:36.306081	Project Planner	Sedang	3	(-6.042702674865723,109.0698471069336)	{8.428466,4.7501316}	2025-06-06 11:38:36.306081
71	15	2	2025-05-09 20:23:36.306081	Site Engineer	Rendah	2	(-6.335181713104248,107.48843383789062)	{3.0697627,9.941814}	2025-06-06 11:38:36.306081
72	15	1	2025-05-15 19:58:36.306081	Site Engineer	Tinggi	1	(-6.279111862182617,110.1607437133789)	{3.8381212,7.189228}	2025-06-06 11:38:36.306081
73	15	5	2025-05-17 07:28:36.306081	Site Engineer	Rendah	2	(-6.341500759124756,108.54438781738281)	{4.4444833,6.52806}	2025-06-06 11:38:36.306081
74	15	5	2025-05-24 07:22:36.306081	Site Engineer	Rendah	1	(-6.470509052276611,107.67630004882812)	{2.4169314,9.7837105}	2025-06-06 11:38:36.306081
75	15	2	2025-05-11 01:40:36.306081	Site Engineer	Rendah	3	(-6.080677032470703,108.95855712890625)	{5.579355,6.009349}	2025-06-06 11:38:36.306081
76	1	1	2025-06-06 15:20:00	staff backend	Sedang	5	(10.3,534.9)	{10,10}	2025-06-06 15:28:51.665885
77	6	5	2025-05-03 23:05:00.001542	driver motor	Tinggi	1	(-6.457116603851318,109.31026458740234)	{6.0126076,6.0686817}	2025-06-08 21:44:00.001542
78	14	4	2025-04-29 07:34:00.001542	Project Planner	Rendah	1	(-7.157613277435303,110.13143920898438)	{6.9777427,7.236302}	2025-06-08 21:44:00.001542
79	2	5	2025-05-21 11:23:00.001542	staff UI/UX	Sedang	3	(-6.150976657867432,108.22525787353516)	{3.160132,4.368185}	2025-06-08 21:44:00.001542
80	1	5	2025-04-24 17:00:00.001542	staff backend	Sedang	3	(-7.361656188964844,109.45703125)	{2.3261447,2.0100112}	2025-06-08 21:44:00.001542
81	7	4	2025-06-03 23:48:00.001542	driver mobil	Sedang	4	(-6.7543158531188965,111.45743560791016)	{8.583375,6.376278}	2025-06-08 21:44:00.001542
82	7	4	2025-05-25 03:49:00.001542	driver mobil	Rendah	3	(-6.873301029205322,109.48092651367188)	{8.936757,3.1901944}	2025-06-08 21:44:00.001542
83	11	2	2025-06-05 12:50:00.001542	Project Planner	Rendah	1	(-7.065784931182861,108.43119812011719)	{7.183403,3.4021199}	2025-06-08 21:44:00.001542
84	7	2	2025-05-10 00:23:00.001542	driver mobil	Sedang	3	(-6.159621238708496,108.91632080078125)	{6.34158,4.123459}	2025-06-08 21:44:00.001542
85	13	5	2025-04-27 06:26:00.001542	QA/QC Inspector	Tinggi	3	(-6.619957447052002,111.26891326904297)	{7.8211894,4.361131}	2025-06-08 21:44:00.001542
86	12	4	2025-04-10 09:55:00.001542	Site Engineer	Rendah	5	(-6.446601867675781,111.56991577148438)	{8.179622,1.0721506}	2025-06-08 21:44:00.001542
87	7	5	2025-05-22 15:21:00.001542	driver mobil	Tinggi	4	(-6.804837703704834,109.19807434082031)	{4.621088,8.069788}	2025-06-08 21:44:00.001542
88	1	5	2025-04-27 21:03:00.001542	staff backend	Sedang	4	(-7.1231913566589355,109.09854125976562)	{8.028004,5.4890966}	2025-06-08 21:44:00.001542
89	3	3	2025-04-16 17:21:00.001542	staff Cyber Security	Sedang	4	(-6.8486328125,109.87320709228516)	{7.454784,9.990178}	2025-06-08 21:44:00.001542
90	11	5	2025-05-20 00:25:00.001542	Project Planner	Tinggi	2	(-7.26912260055542,108.6038818359375)	{9.113234,7.979712}	2025-06-08 21:44:00.001542
91	3	2	2025-05-20 14:09:00.001542	staff Cyber Security	Tinggi	3	(-7.111536026000977,109.98300170898438)	{5.982962,5.429727}	2025-06-08 21:44:00.001542
92	11	3	2025-05-29 13:01:00.001542	Project Planner	Rendah	3	(-6.058234691619873,107.08160400390625)	{1.0384021,2.1282263}	2025-06-08 21:44:00.001542
93	1	2	2025-04-11 18:45:00.001542	staff backend	Rendah	4	(-7.459392070770264,106.98236846923828)	{9.203839,4.764113}	2025-06-08 21:44:00.001542
94	11	1	2025-04-22 18:02:00.001542	Project Planner	Tinggi	3	(-6.1693596839904785,108.33185577392578)	{3.6270013,2.2471712}	2025-06-08 21:44:00.001542
95	7	2	2025-04-29 05:30:00.001542	driver mobil	Tinggi	1	(-6.768612861633301,106.98577880859375)	{2.7575905,1.2576867}	2025-06-08 21:44:00.001542
96	10	5	2025-06-04 16:06:00.001542	driver truk	Sedang	3	(-7.4739155769348145,108.5369873046875)	{1.2795825,9.518273}	2025-06-08 21:44:00.001542
97	4	3	2025-04-29 15:29:00.001542	staff backend	Sedang	5	(-6.108922481536865,106.4317626953125)	{6.1515403,6.0467944}	2025-06-08 21:44:00.001542
98	14	2	2025-05-05 15:31:00.001542	Project Planner	Rendah	5	(-7.474521636962891,106.49767303466797)	{2.7386358,3.9474268}	2025-06-08 21:44:00.001542
99	2	5	2025-04-23 21:39:00.001542	staff UI/UX	Sedang	3	(-6.109462261199951,110.37995147705078)	{3.3373585,1.0939207}	2025-06-08 21:44:00.001542
100	12	1	2025-04-22 03:05:00.001542	Site Engineer	Rendah	2	(-7.302956581115723,110.3404541015625)	{7.848179,5.2688456}	2025-06-08 21:44:00.001542
101	15	2	2025-05-28 03:54:00.001542	Site Engineer	Tinggi	3	(-6.872003078460693,108.44306182861328)	{1.4824569,5.508946}	2025-06-08 21:44:00.001542
102	13	3	2025-05-26 03:51:00.001542	QA/QC Inspector	Sedang	3	(-6.071288585662842,106.21297454833984)	{1.0273287,7.361728}	2025-06-08 21:44:00.001542
103	2	3	2025-06-04 07:57:00.001542	staff UI/UX	Sedang	2	(-7.367630958557129,107.22740173339844)	{4.717783,4.9476414}	2025-06-08 21:44:00.001542
104	9	1	2025-04-17 20:03:00.001542	security	Sedang	3	(-7.494260311126709,108.43769836425781)	{5.032865,8.201235}	2025-06-08 21:44:00.001542
105	11	1	2025-06-07 07:19:00.001542	Project Planner	Rendah	2	(-6.778156280517578,111.2254638671875)	{8.692104,1.1402683}	2025-06-08 21:44:00.001542
106	3	5	2025-05-25 15:31:00.001542	staff Cyber Security	Rendah	2	(-7.235445022583008,110.05464935302734)	{2.3632312,9.867155}	2025-06-08 21:44:00.001542
107	11	4	2025-06-04 16:23:00.001542	Project Planner	Tinggi	5	(-7.403657913208008,109.37361145019531)	{4.7923274,7.308122}	2025-06-08 21:44:00.001542
108	7	4	2025-04-17 19:52:00.001542	driver mobil	Rendah	2	(-7.244923114776611,110.03902435302734)	{7.7485533,2.193227}	2025-06-08 21:44:00.001542
109	11	5	2025-05-20 09:33:00.001542	Project Planner	Rendah	2	(-6.4248270988464355,109.99835205078125)	{7.8797574,1.2384039}	2025-06-08 21:44:00.001542
110	5	3	2025-05-03 18:20:00.001542	staff UI/UX	Tinggi	3	(-7.116837024688721,111.29854583740234)	{2.0244465,6.0183063}	2025-06-08 21:44:00.001542
111	12	5	2025-05-22 01:50:00.001542	Site Engineer	Rendah	2	(-6.300599098205566,111.4568862915039)	{1.3167926,1.4354413}	2025-06-08 21:44:00.001542
112	14	2	2025-05-08 10:03:00.001542	Project Planner	Rendah	1	(-6.116751194000244,110.70714569091797)	{7.2959123,6.3757834}	2025-06-08 21:44:00.001542
113	1	1	2025-06-06 12:46:00.001542	staff backend	Sedang	3	(-7.236203193664551,106.50800323486328)	{4.7925634,9.3572235}	2025-06-08 21:44:00.001542
114	14	2	2025-04-17 18:08:00.001542	Project Planner	Sedang	5	(-6.013382434844971,106.14030456542969)	{6.131181,2.2926097}	2025-06-08 21:44:00.001542
115	11	1	2025-05-31 12:22:00.001542	Project Planner	Sedang	3	(-6.962932586669922,109.1788558959961)	{6.8574495,6.0369134}	2025-06-08 21:44:00.001542
116	10	2	2025-05-23 20:46:00.001542	driver truk	Rendah	5	(-6.821178913116455,107.85322570800781)	{7.417124,7.7600403}	2025-06-08 21:44:00.001542
117	10	1	2025-05-20 05:20:00.001542	driver truk	Sedang	2	(-6.416896820068359,111.94283294677734)	{7.951849,3.468918}	2025-06-08 21:44:00.001542
118	1	1	2025-06-05 01:35:00.001542	staff backend	Sedang	5	(-7.286367893218994,109.86105346679688)	{4.5427804,4.6468105}	2025-06-08 21:44:00.001542
119	6	2	2025-05-10 17:37:00.001542	driver motor	Rendah	4	(-6.213916778564453,109.95757293701172)	{2.4239233,9.01896}	2025-06-08 21:44:00.001542
120	1	2	2025-05-01 11:43:00.001542	staff backend	Rendah	4	(-6.5601348876953125,106.80506134033203)	{6.1521134,9.739208}	2025-06-08 21:44:00.001542
121	3	3	2025-05-24 03:07:00.001542	staff Cyber Security	Sedang	5	(-7.2923712730407715,111.79418182373047)	{2.4831913,9.932788}	2025-06-08 21:44:00.001542
122	7	1	2025-04-27 07:56:00.001542	driver mobil	Rendah	3	(-7.100760459899902,108.72332763671875)	{2.3891764,1.8967803}	2025-06-08 21:44:00.001542
123	15	2	2025-04-21 12:35:00.001542	Site Engineer	Tinggi	5	(-6.325868606567383,107.02304077148438)	{3.7604094,8.573546}	2025-06-08 21:44:00.001542
124	10	5	2025-04-28 08:05:00.001542	driver truk	Tinggi	2	(-6.830194473266602,110.4489517211914)	{4.33355,6.2049017}	2025-06-08 21:44:00.001542
125	6	1	2025-05-28 09:43:00.001542	driver motor	Tinggi	4	(-6.918565273284912,111.01103210449219)	{2.598466,4.0930305}	2025-06-08 21:44:00.001542
126	13	2	2025-04-21 20:19:00.001542	QA/QC Inspector	Rendah	4	(-6.922009468078613,109.6751480102539)	{3.8647282,9.938774}	2025-06-08 21:44:00.001542
\.


--
-- TOC entry 4929 (class 0 OID 16987)
-- Dependencies: 221
-- Data for Name: observers; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.observers (observer_id, user_id, company_id, name, address, date_created, mobile_phone, email_address, gender, "position") FROM stdin;
2	5	1	Fajar Kresna	Jl. Mawar No. 2, Jakarta	2025-06-06 11:38:36.306081	081344556677	fajar.k@jagocoding.id	Male	Staff
3	6	2	Galih Pratama	Jl. Tulip No. 3, Depok	2025-06-06 11:38:36.306081	085678901234	galih.p@siapantar.co.id	Male	Manager
4	7	2	Hana Rahayu	Jl. Sakura No. 4, Depok	2025-06-06 11:38:36.306081	081987654321	hana.r@siapantar.co.id	Female	Staff
5	8	3	Indra Jaya	Jl. Kenanga No. 5, Surabaya	2025-06-06 11:38:36.306081	081122334455	indra.j@bangunbumi.com	Male	Manager
6	9	3	Juwita Sari	Jl. Cempaka No. 6, Surabaya	2025-06-06 11:38:36.306081	085767890123	juwita.s@bangunbumi.com	Female	Staff
7	30	2	Eko Susilo	Jl. Melati No.7, Malang	2025-06-07 11:12:04.967583	083453829583	ekosusilo@siapantar.co.id	Male	Staff
8	31	3	Indah Ekowati	Jl. Kamboja No.8, Gresik	2025-06-07 11:12:04.967583	084827583475	indah.e@bangunbumi.com	Female	Staff
9	32	1	Eko Wahyudi	Jl. Edelweis No. 9, Tuban	2025-06-07 11:12:04.967583	085428475834	eko.wahyu@jagocoding.id	Male	Staff
1	4	1	Eko Budianto	Jl. Anggrek No. 1, Jakarta	2025-06-06 11:38:36.306081	081357304780	eka.b@jagocoding.id	Male	Manager
10	33	3	Egy Maulana	Jl. Asoka No.14, Madura	2025-06-07 12:35:53.479134	083573849573	egy.m@bangunbumi.com	Male	Manager
11	36	4	Yayat Sudrajat	Jl. Harum No.19, Kertosono	2025-06-08 13:45:05.870703	05844758937584	yayat@suksesmakmur.com	Male	Staff
\.


--
-- TOC entry 4931 (class 0 OID 16994)
-- Dependencies: 223
-- Data for Name: participants; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.participants (participant_id, user_id, company_id, name, department, job_position, email_address, mobile_phone, date_of_last_measurement, last_fatique_level) FROM stdin;
11	20	3	Vina Oktaviani	Perencanaan	Project Planner	vina.o@bangunbumi.com	087811000011	2025-06-07 07:19:00.001542	2
9	18	2	Tiara Kusuma	Keamanan	security	tiara.k@siapantar.co.id	081290000009	2025-06-06 08:23:36.306081	5
15	24	3	Zahra Amelia	Operator	Site Engineer	zahra.a@bangunbumi.com	085215000015	2025-05-28 03:54:00.001542	\N
3	12	1	Miko Julianto	Cyber Security	staff Cyber Security	miko.j@jagocoding.id	085730000003	2025-05-25 15:31:00.001542	5
5	14	1	Oscar Wijaya	UI/UX	staff UI/UX	oscar.w@jagocoding.id	089950000005	2025-05-30 12:22:36.306081	2
4	13	1	Nia Puspitasari	Backend Developer	staff backend	nia.p@jagocoding.id	087840000004	2025-06-01 19:19:36.306081	5
10	19	2	Umar Bakri	Driver	driver truk	umar.b@siapantar.co.id	081310000010	2025-06-04 16:06:00.001542	3
6	15	2	Putra Nugraha	Driver	driver motor	putra.n@siapantar.co.id	081160000006	2025-06-05 22:23:36.306081	\N
13	22	3	Xenia Dewi	Pengawasan	QA/QC Inspector	xenia.d@bangunbumi.com	089913000013	2025-06-02 01:24:36.306081	3
2	11	1	Lia Kartika	UI/UX	staff UI/UX	lia.k@jagocoding.id	081320000002	2025-06-04 07:57:00.001542	2
7	16	2	Rani Fitriani	Driver	driver mobil	rani.f@siapantar.co.id	085270000007	2025-06-03 23:48:00.001542	\N
12	21	3	Wahyu Saputra	Pengawasan	Site Engineer	wahyu.s@bangunbumi.com	085712000012	2025-05-28 08:35:36.306081	2
1	10	1	Kevin Aditama	Backend Developer	staff backend	kevin.a@jagocoding.id	081210000001	2025-06-06 15:20:00	2
8	17	2	Sandi Maulana	Sortir	sorter	sandi.m@siapantar.co.id	089680000008	2025-06-01 01:15:36.306081	2
17	35	3	Andi Subandi	Operator	Site Engineer	andi.s@bangunbumi.com	081432765976	\N	\N
14	23	3	Yusup Maulana	Perencanaan	Project Planner	yusuf.m@bangunbumi.com	081114000014	2025-06-05 09:54:36.306081	4
\.


--
-- TOC entry 4933 (class 0 OID 17000)
-- Dependencies: 225
-- Data for Name: performance_criteria; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.performance_criteria (criterion_id, name, description, max_score, is_active) FROM stdin;
1	Waktu Respon Tugas	Kecepatan dalam menanggapi dan memulai tugas baru.	10	t
2	Kualitas Hasil Kerja	Akurasi dan kelengkapan output pekerjaan.	10	t
3	Tingkat Kelelahan Mental	Skala kelelahan mental yang dirasakan setelah aktivitas.	5	t
4	Tingkat Kelelahan Fisik	Skala kelelahan fisik yang dirasakan setelah aktivitas.	5	t
5	Efisiensi Penggunaan Sumber Daya	Penggunaan sumber daya (waktu, material) secara optimal.	10	t
\.


--
-- TOC entry 4935 (class 0 OID 17008)
-- Dependencies: 227
-- Data for Name: user_manager; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.user_manager (manager_id, user_id, name, email, mobile_phone, date_created) FROM stdin;
1	25	Rudi Santoso	rudi.santoso@example.com	081233445501	2025-05-10 10:00:00
2	26	Sinta Dewi	sinta.dewi@example.com	081344556602	2025-05-11 11:00:00
3	27	Toni Gunawan	toni.gunawan@example.com	087855667703	2025-05-12 12:00:00
4	28	Fitri Lestari	fitri.lestari@example.com	085666778804	2025-05-13 13:00:00
5	29	Dimas Pratama	dimas.pratama@example.com	089977889905	2025-05-14 14:00:00
\.


--
-- TOC entry 4937 (class 0 OID 17013)
-- Dependencies: 229
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (user_id, username, password, role, created_at) FROM stdin;
1	sys_admin_jago	password123	observer	2025-06-06 11:38:36.306081
2	sys_admin_siap	password123	observer	2025-06-06 11:38:36.306081
3	sys_admin_bangun	password123	observer	2025-06-06 11:38:36.306081
4	obs_jago_01	observer123	observer	2025-06-06 11:38:36.306081
5	obs_jago_02	observer123	observer	2025-06-06 11:38:36.306081
6	obs_siap_01	observer123	observer	2025-06-06 11:38:36.306081
7	obs_siap_02	observer123	observer	2025-06-06 11:38:36.306081
8	obs_bangun_01	observer123	observer	2025-06-06 11:38:36.306081
9	obs_bangun_02	observer123	observer	2025-06-06 11:38:36.306081
10	part_jago_01	participant123	participant	2025-06-06 11:38:36.306081
11	part_jago_02	participant123	participant	2025-06-06 11:38:36.306081
12	part_jago_03	participant123	participant	2025-06-06 11:38:36.306081
13	part_jago_04	participant123	participant	2025-06-06 11:38:36.306081
14	part_jago_05	participant123	participant	2025-06-06 11:38:36.306081
15	part_siap_01	participant123	participant	2025-06-06 11:38:36.306081
16	part_siap_02	participant123	participant	2025-06-06 11:38:36.306081
17	part_siap_03	participant123	participant	2025-06-06 11:38:36.306081
18	part_siap_04	participant123	participant	2025-06-06 11:38:36.306081
19	part_siap_05	participant123	participant	2025-06-06 11:38:36.306081
20	part_bangun_01	participant123	participant	2025-06-06 11:38:36.306081
21	part_bangun_02	participant123	participant	2025-06-06 11:38:36.306081
22	part_bangun_03	participant123	participant	2025-06-06 11:38:36.306081
23	part_bangun_04	participant123	participant	2025-06-06 11:38:36.306081
24	part_bangun_05	participant123	participant	2025-06-06 11:38:36.306081
25	manager_rudi	manager123	user_manager	2025-06-06 12:12:10.153311
26	manager_sinta	manager123	user_manager	2025-06-06 12:12:10.153311
27	manager_toni	manager123	user_manager	2025-06-06 12:12:10.153311
28	manager_fitri	manager123	user_manager	2025-06-06 12:12:10.153311
29	manager_dimas	manager123	user_manager	2025-06-06 12:12:10.153311
30	obs_jago_03	observer123	observer	2025-06-07 11:02:42.831337
31	obs_jago_04	observer123	observer	2025-06-07 11:02:42.831337
32	obs_jago_05	observer123	observer	2025-06-07 11:02:42.831337
33	obs_bangun_03	observer123	observer	2025-06-07 12:35:53.479134
35	part_bangun_06	participant123	participant	2025-06-08 13:39:46.834745
36	obs_sukses_01	observer123	observer	2025-06-08 13:45:05.870703
\.


--
-- TOC entry 4951 (class 0 OID 0)
-- Dependencies: 218
-- Name: companies_company_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.companies_company_id_seq', 5, true);


--
-- TOC entry 4952 (class 0 OID 0)
-- Dependencies: 220
-- Name: measurement_data_measurement_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.measurement_data_measurement_id_seq', 126, true);


--
-- TOC entry 4953 (class 0 OID 0)
-- Dependencies: 222
-- Name: observers_observer_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.observers_observer_id_seq', 11, true);


--
-- TOC entry 4954 (class 0 OID 0)
-- Dependencies: 224
-- Name: participants_participant_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.participants_participant_id_seq', 17, true);


--
-- TOC entry 4955 (class 0 OID 0)
-- Dependencies: 226
-- Name: performance_criteria_criterion_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.performance_criteria_criterion_id_seq', 5, true);


--
-- TOC entry 4956 (class 0 OID 0)
-- Dependencies: 228
-- Name: user_manager_manager_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.user_manager_manager_id_seq', 5, true);


--
-- TOC entry 4957 (class 0 OID 0)
-- Dependencies: 230
-- Name: users_user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.users_user_id_seq', 36, true);


--
-- TOC entry 4746 (class 2606 OID 17027)
-- Name: companies companies_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.companies
    ADD CONSTRAINT companies_name_key UNIQUE (name);


--
-- TOC entry 4748 (class 2606 OID 17029)
-- Name: companies companies_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.companies
    ADD CONSTRAINT companies_pkey PRIMARY KEY (company_id);


--
-- TOC entry 4750 (class 2606 OID 17031)
-- Name: measurement_data measurement_data_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.measurement_data
    ADD CONSTRAINT measurement_data_pkey PRIMARY KEY (measurement_id);


--
-- TOC entry 4752 (class 2606 OID 17033)
-- Name: observers observers_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.observers
    ADD CONSTRAINT observers_pkey PRIMARY KEY (observer_id);


--
-- TOC entry 4754 (class 2606 OID 17035)
-- Name: observers observers_user_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.observers
    ADD CONSTRAINT observers_user_id_key UNIQUE (user_id);


--
-- TOC entry 4756 (class 2606 OID 17037)
-- Name: participants participants_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.participants
    ADD CONSTRAINT participants_pkey PRIMARY KEY (participant_id);


--
-- TOC entry 4758 (class 2606 OID 17039)
-- Name: participants participants_user_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.participants
    ADD CONSTRAINT participants_user_id_key UNIQUE (user_id);


--
-- TOC entry 4760 (class 2606 OID 17041)
-- Name: performance_criteria performance_criteria_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.performance_criteria
    ADD CONSTRAINT performance_criteria_name_key UNIQUE (name);


--
-- TOC entry 4762 (class 2606 OID 17043)
-- Name: performance_criteria performance_criteria_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.performance_criteria
    ADD CONSTRAINT performance_criteria_pkey PRIMARY KEY (criterion_id);


--
-- TOC entry 4764 (class 2606 OID 17045)
-- Name: user_manager user_manager_email_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_manager
    ADD CONSTRAINT user_manager_email_key UNIQUE (email);


--
-- TOC entry 4766 (class 2606 OID 17047)
-- Name: user_manager user_manager_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_manager
    ADD CONSTRAINT user_manager_pkey PRIMARY KEY (manager_id);


--
-- TOC entry 4768 (class 2606 OID 17049)
-- Name: user_manager user_manager_user_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_manager
    ADD CONSTRAINT user_manager_user_id_key UNIQUE (user_id);


--
-- TOC entry 4770 (class 2606 OID 17051)
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (user_id);


--
-- TOC entry 4772 (class 2606 OID 17053)
-- Name: users users_username_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_username_key UNIQUE (username);


--
-- TOC entry 4773 (class 2606 OID 17054)
-- Name: measurement_data measurement_data_criterion_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.measurement_data
    ADD CONSTRAINT measurement_data_criterion_id_fkey FOREIGN KEY (criterion_id) REFERENCES public.performance_criteria(criterion_id) ON DELETE RESTRICT;


--
-- TOC entry 4774 (class 2606 OID 17059)
-- Name: measurement_data measurement_data_participant_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.measurement_data
    ADD CONSTRAINT measurement_data_participant_id_fkey FOREIGN KEY (participant_id) REFERENCES public.participants(participant_id) ON DELETE CASCADE;


--
-- TOC entry 4775 (class 2606 OID 17064)
-- Name: observers observers_company_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.observers
    ADD CONSTRAINT observers_company_id_fkey FOREIGN KEY (company_id) REFERENCES public.companies(company_id) ON DELETE RESTRICT;


--
-- TOC entry 4776 (class 2606 OID 17069)
-- Name: observers observers_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.observers
    ADD CONSTRAINT observers_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(user_id) ON DELETE CASCADE;


--
-- TOC entry 4777 (class 2606 OID 17074)
-- Name: participants participants_company_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.participants
    ADD CONSTRAINT participants_company_id_fkey FOREIGN KEY (company_id) REFERENCES public.companies(company_id) ON DELETE RESTRICT;


--
-- TOC entry 4778 (class 2606 OID 17079)
-- Name: participants participants_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.participants
    ADD CONSTRAINT participants_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(user_id) ON DELETE CASCADE;


--
-- TOC entry 4779 (class 2606 OID 17084)
-- Name: user_manager user_manager_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_manager
    ADD CONSTRAINT user_manager_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(user_id) ON DELETE CASCADE;


-- Completed on 2025-06-08 21:49:59

--
-- PostgreSQL database dump complete
--

