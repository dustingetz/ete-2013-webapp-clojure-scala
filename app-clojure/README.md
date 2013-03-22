# app-clojure

A Clojure library designed to ... well, that part is up to you.

## prerequisites

* `lein2`

## installation

install datomic free outside of lein, afaik it is not published anywhere that lein knows about:

    cd ~/opt
    wget http://downloads.datomic.com/0.8.3767/datomic-free-0.8.3848.zip
    unzip datomic-free-0.8.3848.zip
    cd datomic-free-0.8.3848
    mvn install:install-file -DgroupId=com.datomic -DartifactId=datomic -DpomFile=pom.xml -Dfile=datomic-free-0.8.3848.jar



## License
