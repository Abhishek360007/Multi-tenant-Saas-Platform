#!/bin/bash
#chkconfig:345 61 61
#description: Spring Boot App Script
#auther: Wang Chen Chen

#===========================================================================================
# Document And Example
#===========================================================================================
#Usage: ./script.sh {start|stop|restart|status}
#Example:./tenanthub.sh start 					  # Start
#Example:./tenanthub.sh stop 					  # Stop
#Example:./tenanthub.sh restart 					# Restart
#Example:./tenanthub.sh status 					# Status

#===========================================================================================
# Spring Boot Application Start Configuration
#===========================================================================================

# Spring Boot App Name
APP_NAME=/opt/tenanthub-server/tenanthub-service-1.0.1.jar

# spring.profiles.active Active profile
#SPRING_ENV=dev           # Development
SPRING_ENV=pro            # Local production
#SPRING_ENV=prod          # Production
#SPRING_ENV=test          # Testing

# JVM Parameters
JVM_OPTS="-Dname=$APP_NAME -server -Xms4g -Xmx4g -XX:+DisableExplicitGC -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8 -Duser.timezone=UTC -Djava.security.egd=file:/dev/./urandom"

# JDK Path
JAVA_HOME=/opt/jdk-17

# First argument: one of start|stop|restart|status
if [ "$1" = "" ];
then
	echo -e "\033[0;31m No action given \033[0m  \033[0;34m {start|stop|restart|status} \033[0m"
	exit 1
fi


# Start the service
function start()
{
	PID=`ps -ef |grep java|grep $APP_NAME|grep -v grep|wc -l`
	if [ $PID != 0 ];then
		echo "$APP_NAME is running..."
	else
		echo "start $APP_NAME active: $SPRING_ENV success..." &
		nohup $JAVA_HOME/bin/java -jar $JVM_OPTS $APP_NAME --spring.profiles.active=$SPRING_ENV >/dev/null 2>&1 &
	fi
}


# Stop the service
function stop()
{
	PID=""
	query(){
		PID=`ps -ef |grep java|grep $APP_NAME|grep -v grep|awk '{print $2}'`
	}
	query
	if [ x"$PID" != x"" ]; then
		kill -9 $PID
		echo "$APP_NAME (pid:$PID) exiting..."
		while [ x"$PID" != x"" ]
		do
			sleep 1
			query
		done
		echo "$APP_NAME exited ..."
	else
		echo "$APP_NAME already stopped ..."
	fi
}


# Restart the service
function restart()
{
	stop
	sleep 2
	start
}


# Show service status
function status()
{
	PID=`ps -ef |grep java|grep $APP_NAME|grep -v grep|wc -l`
	if [ $PID != 0 ];then
		echo "$APP_NAME is running..."
	else
		echo "$APP_NAME is stop..."
	fi
}


case $1 in
	start)
		start;;
	stop)
		stop;;
	restart)
		restart;;
	status)
		status;;
	*)

		echo -e "\033[0;31m Usage: \033[0m  \033[0;34m sh  $0  {start|stop|restart|status}  {APP_NAME} \033[0m
		\033[0;31m Example: \033[0m
		\033[0;33m sh  $0  start example.jar \033[0m"
esac

