-- Sequence and defined type
CREATE SEQUENCE IF NOT EXISTS job_queue_id_seq;

-- Table Definition
CREATE TABLE "job"
  (
     "id"         INT8 NOT NULL DEFAULT NEXTVAL('job_queue_id_seq'::regclass),
     "name"       VARCHAR(255) NOT NULL,
     "type"       VARCHAR(255) NOT NULL,
     "payload"    text NOT NULL,
     "variables"  text NOT NULL,
     "status"     VARCHAR(10) NOT NULL,
     "created_at" TIMESTAMP,
     "updated_at" TIMESTAMP,
     PRIMARY KEY ("id")
  );