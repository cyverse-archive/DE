#!/bin/bash

#
# tomcat-restart.sh - tomcat restart script for cron
#
echo "`date`------------ Shutting down tomcat---------------"
CATALINA_PATH=/usr/share/apache-tomcat-7.0.11/
CATALINA_SCRIPT=catalina.sh

# Verify that tomcat is not running.  If it is, stop it gracefully
# get the tomcat pid
tomcat_pid=`ps -ef | grep java | grep tomcat | cut -c10-14`
echo "Tomcat PID is: $tomcat_pid"

if [ -n "$tomcat_pid" ]
then
echo "Stopping tomcat ..."
sudo $CATALINA_PATH/bin/$CATALINA_SCRIPT stop
# give tomcat 10 seconds to shutdown gracefully
sleep 10
fi

tomcat_pid=`ps -ef | grep java | grep tomcat | cut -c10-14`
# if tomcat_pid exists, kill the process
if [ -n "$tomcat_pid" ]
then
echo "Noticed that process is still running trying to kill it"
sudo kill $tomcat_pid
sleep 10
fi

tomcat_pid=`ps -ef | grep java | grep tomcat | cut -c10-14`
# if tomcat_pid still exists, really kill the process
if [ -n "$tomcat_pid" ]
then
echo "Forcefully killing the process for tomcat $tomcat_pid..."
sudo kill -n 9 $tomcat_pid
sleep 10
fi

# restart tomcat
echo "`date` Starting tomcat..."
sudo $CATALINA_PATH/bin/$CATALINA_SCRIPT start
echo "`date` Finished starting tomcat"

