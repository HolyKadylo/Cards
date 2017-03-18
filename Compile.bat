chcp 1251
echo off
cls
type Start.txt
javac -Xlint:all -cp .;"%CD%"/WEB-INF/lib;"%CD%"/WEB-INF/classes/ "%CD%"/WEB-INF/classes/com/kadylo/kmdb/DataBase.java
javac -Xlint:all -cp .;"%CD%"/WEB-INF/lib;"%CD%"/WEB-INF/classes/ "%CD%"/WEB-INF/classes/com/kadylo/kmdb/Server.java
javac -Xlint:all -cp .;"%CD%"/WEB-INF/lib;"%CD%"/WEB-INF/classes/ "%CD%"/WEB-INF/classes/com/kadylo/kmdb/Application.java
javac -Xlint:all -cp .;"%CD%"/WEB-INF/lib;"%CD%"/WEB-INF/classes/ "%CD%"/WEB-INF/classes/com/kadylo/kmdb/Commander.java
javac -Xlint:all -cp .;"%CD%"/WEB-INF/lib;"%CD%"/WEB-INF/classes/ "%CD%"/WEB-INF/classes/com/kadylo/kmdb/Document.java
javac -Xlint:all -cp .;"%CD%"/WEB-INF/lib;"%CD%"/WEB-INF/classes/ "%CD%"/WEB-INF/classes/com/kadylo/kmdb/MasterCard.java
javac -Xlint:all -cp .;"%CD%"/WEB-INF/lib;"%CD%"/WEB-INF/classes/ "%CD%"/WEB-INF/classes/com/kadylo/kmdb/MasterDocument.java
javac -Xlint:all -cp .;"%CD%"/WEB-INF/lib;"%CD%"/WEB-INF/classes/ "%CD%"/WEB-INF/classes/com/kadylo/kmdb/MasterSignature.java
javac -Xlint:all -cp .;"%CD%"/WEB-INF/lib;"%CD%"/WEB-INF/classes/ "%CD%"/WEB-INF/classes/com/kadylo/kmdb/Signature.java
javac -Xlint:all -cp .;"%CD%"/WEB-INF/lib;"%CD%"/WEB-INF/classes/ "%CD%"/WEB-INF/classes/com/kadylo/kmdb/Soldier.java
javac -Xlint:all -cp .;"%CD%"/WEB-INF/lib;"%CD%"/WEB-INF/classes/ "%CD%"/WEB-INF/classes/com/kadylo/kmdb/Card.java
javac -Xlint:all -cp .;"%CD%"/WEB-INF/lib;"%CD%"/WEB-INF/classes/ "%CD%"/WEB-INF/classes/com/kadylo/kmdb/Commanders.java
javac -Xlint:all -cp .;"%CD%"/WEB-INF/lib;"%CD%"/WEB-INF/classes/ "%CD%"/WEB-INF/classes/com/kadylo/kmdb/Soldiers.java
javac -Xlint:all -cp .;"%CD%"/WEB-INF/lib;"%CD%"/WEB-INF/classes/ "%CD%"/WEB-INF/classes/com/kadylo/kmdb/EncodingFilter.java
ECHO 
pause
echo on
cls