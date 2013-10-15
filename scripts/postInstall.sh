#!/bin/bash

PL_USER=$1
PL_LOCATION=$2
START_SERVER=$3
CONFIGURE=$4
START_CLIENT=$5

. /etc/profile

if [ "$START_SERVER" = "true" ] 
then
	sudo -u ${PL_USER} ${PL_LOCATION}/db/startDB.sh
	sudo -u ${PL_USER} ${PL_LOCATION}/launchServer.sh
fi

if [ "$START_CLIENT" = "true" ]
then

	ARGUMENTS=
	I=1
	for ARG in $*
	do
		if [ "$I" -lt "6" ]; then
			let I+=1
			continue
		fi
		
		ARGUMENTS+=" $ARG"
	done

        xhost +
        sudo su - ${PL_USER} -c "cd ${PL_LOCATION}/dist; java -cp Pipeline.jar ui.gui.Main $ARGUMENTS" &
        sleep 2
fi

if [ "$CONFIGURE" = "true" ]
then
        xhost +
        sudo su - ${PL_USER} -c "cd ${PL_LOCATION}/dist; java -cp Pipeline.jar server.config.Main -preferences ${PL_LOCATION}/preferences.xml"
fi

