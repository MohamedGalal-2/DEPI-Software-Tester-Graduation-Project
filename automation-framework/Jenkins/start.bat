@echo off
cd /d "C:\Users\Mohamed-Galal\Downloads"
echo Starting Jenkins on port 9090...
java -jar jenkins.war --enable-future-java --httpPort=9090
pause
