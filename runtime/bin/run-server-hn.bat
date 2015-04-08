@echo off
unzip .\build\distributions\lq-server-grizzly-trunk-SNAPSHOT.zip -d .\build\distributions\
java -jar .\build\distributions\lq-server-grizzly-trunk-SNAPSHOT\lib\lq-server-grizzly.jar notify.runtime_dir .. notify.config_name spring-config-hn.xml
echo LQ-Server has been shut down.