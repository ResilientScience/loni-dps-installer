#!/bin/bash

SERVERNAME_PATH="$(readlink -f ~pipeline)/.servername.txt"
EC2_MAGIC_IP="169.254.169.254"
EC2_HOSTNAME_URL="http://${EC2_MAGIC_IP}/latest/meta-data/public-hostname"

# ______________________________________________________________________
# First, figure out what the old server name is...

if [ $# -eq 2 ]; then
    OldServer=$1
elif [ -f ${SERVERNAME_PATH} ]; then
    OldServer=$(cat ${SERVERNAME_PATH})
else
    echo "Unable to determine old server name."
    exit 1
fi

# ______________________________________________________________________
# Then, figure out what the new server name is...

if [ $# -eq 0 ]; then
    NewServer=$(GET ${EC2_HOSTNAME_URL}) || {
        echo "Unable to determine EC2 server name (not on an EC2 instance?).";
        exit 1;
    }
elif [ $# -eq 1 ]; then
	NewServer=$1;
elif [ $# -eq 2 ]; then
	NewServer=$2;
else 
	echo "Usage: $0 [[OldServer] NewServer]"
	exit 1;
fi

# ______________________________________________________________________
# Finally, go to town...

echo "${NewServer}" > ${SERVERNAME_PATH}

echo "Update Pipeline server hostname to ${NewServer}"

WHOAMI=$(whoami)
if [ "$WHOAMI" != "pipeline" ]; then
    PREFIX="sudo sudo -u pipeline"
else
    PREFIX=""
fi

${PREFIX} perl -pi -w -e "s/${OldServer}/${NewServer}/g;" /usr/pipeline/preferences.xml

${PREFIX} perl -pi -w -e "s/${OldServer}/${NewServer}/g;" /usr/pipeline/serverLibrary/*/*/*.pipe
