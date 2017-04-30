#!/bin/sh

MAIN_CLASS="de.rainu.alexa.Main"
LIB_DIR="/application/lib"

for a in $LIB_DIR/*; do
  CP="$CP":"$a"
done

java -cp $CP $MAIN_CLASS $@