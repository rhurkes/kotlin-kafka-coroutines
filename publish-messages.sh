#!/usr/bin/env bash
ITERATIONS=25

# kcat -C -F ~/local.conf -t test-topic -e -u -o beginning

for i in $(seq 1 $ITERATIONS); do
  ID=$(echo $RANDOM)
  echo "{\"id\": $ID}" | kcat -P -F ~/local.conf -t test-topic -k $ID;
done
