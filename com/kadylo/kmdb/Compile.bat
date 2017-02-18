echo off
cls
type Start.txt
javac -Xlint:all -cp .;$HOME DataBase.java
javac -Xlint:all -cp .;$HOME Application.java
javac -Xlint:all -cp .;$HOME Commander.java
javac -Xlint:all -cp .;$HOME Document.java
javac -Xlint:all -cp .;$HOME MasterCard.java
javac -Xlint:all -cp .;$HOME MasterDocument.java
javac -Xlint:all -cp .;$HOME MasterSignature.java
javac -Xlint:all -cp .;$HOME Signature.java
javac -Xlint:all -cp .;$HOME Soldier.java
javac -Xlint:all -cp .;$HOME Card.java
javac -Xlint:all -cp .;$HOME Commanders.java
javac -Xlint:all -cp .;$HOME Soldiers.java
cd ..
cd ..
cd ..
pause
echo on
cls