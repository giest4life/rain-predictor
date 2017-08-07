#!/bin/bash

for f in target/*.war
do
  cp -v "$f" /opt/tomcat/webapps/rain-predictor.war
done
