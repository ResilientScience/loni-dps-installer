#!/bin/bash

SERVERNAME_PREV="~pipeline/.servername.txt"

if [ $# -eq 1 ]; then
	if [ -f ${SERVERNAME_PREV} ]; then
		OldServer=`cat ${SERVERNAME_PREV}`;
	fi
	NewServer=$1;
elif [ $# -eq 2 ]; then
	OldServer=$1;
	NewServer=$2;
else 
	echo "Usage: $0 [OldServer] NewServer"
	exit 1;
fi

echo "${NewServer}" > ~pipeline/.servername.txt

echo "Update Pipeline server hostname to ${NewServer}"

WHOAMI=$(whoami)
if [ "$WHOAMI" != "pipeline" ]; then
    PREFIX="sudo sudo -u pipeline"
else
    PREFIX=""
fi

${PREFIX} perl -pi -w -e "s/${OldServer}/${NewServer}/g;" /usr/pipeline/preferences.xml

${PREFIX} perl -pi -w -e "s/${OldServer}/${NewServer}/g;" /usr/pipeline/serverLibrary/*/*/*.pipe
