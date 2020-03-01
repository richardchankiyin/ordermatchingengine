#!/bin/bash
host=localhost
port=18888

for x in $(grep -v "^#" buysell_market.txt)
do s="$(echo $x | cut -d',' -f1)"; q="$(echo $x | cut -d',' -f2)";  
content=$(echo "{\"side\":\"$s\",\"symbol\":\"0005.HK\",\"ordType\":\"1\",\"quantity\":$q}"); 

echo $content
curl -s http://${host}:${port}/order -H "Content-type:application/json" -d "$content"
done
