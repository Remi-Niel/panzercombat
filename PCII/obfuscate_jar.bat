pause
rem set PATH=C:\Program Files\Java\jdk1.5.0_11\bin
java -jar proguard\lib\proguard.jar @PCII.pro
move PCII.jar PCII_Full.jar
move PCII_out.jar PCII.jar
pause

