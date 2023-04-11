-- Sequence and defined type
CREATE SEQUENCE IF NOT EXISTS job_history_id_seq;

-- Table Definition
CREATE TABLE "job_history"
  (
     "id"           INT8 NOT NULL DEFAULT NEXTVAL('job_history_id_seq'::regclass),
     "job_id"       INT8 NOT NULL,
     "name"         VARCHAR(255) NOT NULL,
     "type"         VARCHAR(255) NOT NULL,
     "status"       VARCHAR(10) NOT NULL,
     "service_name" VARCHAR(10) NOT NULL,
     "created_at"   TIMESTAMP,
     "updated_at"   TIMESTAMP,
     PRIMARY KEY ("id")
  );