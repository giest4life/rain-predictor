#!/bin/bash

WEBAPP_FOLDER=/opt/tomcat/webapps
APP_NAME=rain-predictor
APP_PATH=${WEBAPP_FOLDER}/${APP_NAME}

echo "Deleting $APP_PATH*"
rm -rf ${APP_PATH}*

echo "Copying war file into $WEBAPP_FOLDER"
for f in target/*.war
do
  cp -v "$f" ${APP_PATH}.war 
done
