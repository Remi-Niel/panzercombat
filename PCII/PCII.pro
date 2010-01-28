-injars       PCII.jar
-outjars      PCII_out.jar
-libraryjars  <java.home>/lib/rt.jar; freetts/freetts.jar
-printmapping proguard.map
-overloadaggressively
-defaultpackage ''
-allowaccessmodification

-keep public class com.gampire.pc.Main {
    public static void main(java.lang.String[]);
}
-keep public class com.gampire.pc.model.**Bean {
    void set*(***);
    boolean is*(); 
    *** get*();
}
