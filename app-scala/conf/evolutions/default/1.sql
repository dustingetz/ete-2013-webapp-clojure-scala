# --- !Ups

CREATE TABLE users
(
  id serial,
  username character varying(64) not null unique,
  password character varying(64) not null,
  email character varying(128) not null,
  firstname character varying(64) not null,
  lastname character varying(64) not null,
  created timestamp not null DEFAULT now()
);

ALTER TABLE users ADD CONSTRAINT users_pk0 PRIMARY KEY (id);


CREATE TABLE skills
(
  id serial,
  name character varying(128) not null
);

ALTER TABLE skills ADD CONSTRAINT skills_pk0 PRIMARY KEY (id);
ALTER TABLE skills ADD CONSTRAINT skills_unique0 UNIQUE (name);


CREATE TABLE skillsets
(
  user_id int not null,
  skill_id int not null
);

ALTER TABLE skillsets ADD CONSTRAINT skillsets_fk0 FOREIGN KEY (user_id) REFERENCES users(id);
ALTER TABLE skillsets ADD CONSTRAINT skillsets_fk1 FOREIGN KEY (skill_id) REFERENCES skills(id);
ALTER TABLE skillsets ADD CONSTRAINT skillsets_unique0 UNIQUE (user_id, skill_id);


CREATE TABLE projects
(
  "id" serial,
  "owner" int not null,
  "name" character varying(64) not null,
  "created" timestamp not null DEFAULT now()
);

ALTER TABLE projects ADD CONSTRAINT projects_pk0 PRIMARY KEY (id);
ALTER TABLE projects ADD CONSTRAINT projects_fk0 FOREIGN KEY (owner) REFERENCES users(id);


CREATE TABLE project_members
(
  "project_id" int not null,
  "user_id" int not null,
  "created" timestamp not null DEFAULT now()
);

ALTER TABLE project_members ADD CONSTRAINT project_members_fk0 FOREIGN KEY (project_id) REFERENCES projects(id);
ALTER TABLE project_members ADD CONSTRAINT project_members_fk1 FOREIGN KEY (user_id) REFERENCES users(id);
ALTER TABLE project_members ADD CONSTRAINT project_members_unique0 UNIQUE (project_id, user_id);


CREATE TABLE project_skills
(
  "project_id" int not null,
  "skill_id" int not null
);

ALTER TABLE project_skills ADD CONSTRAINT project_skills_fk0 FOREIGN KEY (project_id) REFERENCES projects(id);
ALTER TABLE project_skills ADD CONSTRAINT project_skills_fk1 FOREIGN KEY (skill_id) REFERENCES skills(id);
ALTER TABLE project_skills ADD CONSTRAINT project_skills_unique0 UNIQUE (project_id, skill_id);



# --- !Downs

DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS skills;
DROP TABLE IF EXISTS skillsets;
DROP TABLE IF EXISTS projects;
DROP TABLE IF EXISTS project_members;
DROP TABLE IF EXISTS project_skills;
