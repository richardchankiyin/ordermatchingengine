#!/bin/bash
host=localhost
port=18888

for x in $(grep -v "^#" buysell.txt)
do s="$(echo $x | cut -d',' -f1)"; p="$(echo $x | cut -d',' -f2)"; q="$(echo $x | cut -d',' -f3)"; 
content=$(echo "{\"side\":\"$s\",\"symbol\":\"0005.HK\",\"ordType\":\"2\",\"price\":$p,\"quantity\":$q}"); 

echo $content
curl -s http://${host}:${port}/order -H "Content-type:application/json" -d "$content"
done
