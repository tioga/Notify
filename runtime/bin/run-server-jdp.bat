@echo off
unzip .\build\distributions\lq-server-grizzly-trunk-SNAPSHOT.zip -d .\build\distributions\
java -jar .\build\distributions\lq-server-grizzly-trunk-SNAPSHOT\lib\lq-server-grizzly.jar serverName localhost port 8080 shutdown 8081 context lq-server open true springFile config\spring-lq-server-core-jdp.xml
echo LQ-Server has been shut down.