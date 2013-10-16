#!/bin/bash

# This is a work-around for not having a quick means of knowing how to
# build the required jar files for the createArchive.sh script.

if [ $# -ne 1 ] || [ ! -d $1 ]; then
    echo "Usage:"
    echo "    $0 <dir>"
    echo ""
    echo "Where <dir> is the location of the un-tar-ed DPS installer."
    exit 1
fi

export SOURCE_PATH=$(readlink -f ${0%/*})
export DEST_PATH=$(readlink -f $1)/dist/install_files/

cp ${SOURCE_PATH}/launchInstaller.sh ${DEST_PATH}

for SRC_FILE in checkHost.sh installNITools.sh installSGE.sh postInstall.sh checkSGE.sh installBioinformaticsTools.sh installPipeline.sh makefile; do
    cp ${SOURCE_PATH}/${SRC_FILE} ${DEST_PATH}
done
