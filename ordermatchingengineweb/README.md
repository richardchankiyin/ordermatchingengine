Order Matching Engine Web
-------------------------
This is a web component for:
   - providing rest api for login/logoff/place order/enquire order
   - controlling the core order matching engine lifecycle
   - publishing events via websocket

Build
=====
command: mvn package spring-boot:repackage
run: java -jar target/ordermatchingengineweb-1.0-SNAPSHOT.jar
