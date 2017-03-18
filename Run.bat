chcp 1251
echo off
cls
type Start2.txt
java -cp .;"%CD%"/WEB-INF/lib;"%CD%"/WEB-INF/classes com.kadylo.kmdb.Authorization
java -cp .;"%CD%"/WEB-INF/lib;"%CD%"/WEB-INF/classes com.kadylo.kmdb.DataBase
java -cp .;"%CD%"/WEB-INF/lib;"%CD%"/WEB-INF/classes com.kadylo.kmdb.Card
java -cp .;"%CD%"/WEB-INF/lib;"%CD%"/WEB-INF/classes com.kadylo.kmdb.Commander
java -cp .;"%CD%"/WEB-INF/lib;"%CD%"/WEB-INF/classes com.kadylo.kmdb.Document
java -cp .;"%CD%"/WEB-INF/lib;"%CD%"/WEB-INF/classes com.kadylo.kmdb.Signature
java -cp .;"%CD%"/WEB-INF/lib;"%CD%"/WEB-INF/classes com.kadylo.kmdb.Soldier
pause
echo on
cls