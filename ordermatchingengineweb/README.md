Order Matching Engine Web
=========================
This is a web component for:
   - providing rest api for login/logoff/place order/enquire order
   - controlling the core order matching engine lifecycle
   - publishing events via websocket

Tech Requirement
----------------
   - Java 1.8 or above
   - Maven 3
   - Spring boot (download via maven build)

Build
-----
Using maven to build/package. Can be run as basic java (>=1.8)
   - command: mvn package spring-boot:repackage
   - run: java -jar target/ordermatchingengineweb-1.0-SNAPSHOT.jar
