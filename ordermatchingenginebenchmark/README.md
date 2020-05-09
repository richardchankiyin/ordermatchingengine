OrderMatchingEngine Benchmark
=============================
This is a maven project performing JMH (Java Microbenchmark Harness) for ordermatchingengine and its web. 

Build
-----
command: mvn clean compile assembly:single
output: there will be jar file created at target/ordermatchingenginebenchmark-1.0-SNAPSHOT-jar-with-dependencies.jar

Run
---
java -cp target/ordermatchingenginebenchmark-1.0-SNAPSHOT-jar-with-dependencies.jar com.richardchankiyin.ordermatchingengine.BenchmarkApp
