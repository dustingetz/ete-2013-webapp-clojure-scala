## play app

* install play framework 2.10, add `play` to path
* from the scala app dir, `play` to launch play console
* `play help`
* `idea` - generate intellij project files
* if your syntax highlighting is borked (but `play compile` works), intellij didn't discover your jdk. Do a intellij rebuild and it should prompt you and take you to the right place to fix it.
* output path sharing for external mode (for intellij leda)

## clojure app

* install lein2, add `lein` to path
* in March 2013, the best clojure setup for emacs is 'nrepl.el'

## project structure

* a clojure backend, a scala (play2) backend - these expose identical apis.
* a browser client - which is compatible with both apis - whichever one is running

## environment configuration

* need a postgres instance
* the following steps are via the postgres command line tools

    $ initdb ~/var/postgresql
    $ pg_ctl -D ~/var/postgresql/ -l ~/tmp/postgres-log start
    $ pg_ctl -D ~/var/postgresql status

    $ psql postgres
    > create database artscentre;
    > create user artscentre_owner with superuser login;
    > alter user artscentre_owner password 'artscentre_owner123';
    > \q
