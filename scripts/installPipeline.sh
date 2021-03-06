#!/bin/bash

[ -x "$0" ] || {
    echo "Unable to validate script file path.  Exiting...";
    exit 1;
}
SOURCE_PATH=$(readlink -f ${0%/*})

if [ -z "$INSTALL_PIPELINE" ]
then
    echo "Please do not run this script manually. Run launchInstaller.sh instead."
    exit 1
fi

if which alternatives; then
    FIND_ALTS="alternatives --display"
else
    FIND_ALTS="update-alternatives --display"
fi

# add /usr/sbin and /sbin to PATH
export PATH=${PATH}:/usr/sbin:/sbin

NUM_STEPS=`cat $0 | grep pl+ | wc -l`
NUM_STEPS=$(($NUM_STEPS-1))

DL_DIR=$(cd `dirname $0` && pwd)

echo "pl=>$NUM_STEPS"

##################################################
#           CLEANING PREVIOUS INSTALLATIONS
##################################################
echo "=======| CLEANING PREVIOUS INSTALLATIONS |======="
echo "pl-->Cleaning previous installations..."
echo "pl->"

if [ -f /etc/init.d/pipeline ]
then
    /etc/init.d/pipeline stop
fi

# before removing the Pipeline directory, move the server library files so that
# these files are not lost; we will copy them back after the installation
if [ -d ${PL_LOCATION}/serverLibrary ]
then
    sudo -u $PL_USER mkdir -p /tmp/serverLibrary
    sudo -u $PL_USER mv ${PL_LOCATION}/serverLibrary/* /tmp/serverLibrary/
fi

if [ -d ${PL_LOCATION} ]
then
    rm -rf $(ls -d $PL_LOCATION/* | grep -v ${PL_LOCATION}/serverLibrary) $PLDB_LOCATION
fi

echo "pl+"


##################################################
#           CREATING INSTALLATION DIRECTORIES
##################################################
echo "=======| CLEANING INSTALLATION DIRECTORIES |======="
mkdir -p $PL_LOCATION/dist
chown $PL_USER $PL_LOCATION
chown $PL_USER $PL_LOCATION/dist

mkdir -p $PLDB_LOCATION
chown $PL_USER $PLDB_LOCATION

mkdir -p $PL_TEMPDIR
chown $PL_USER $PL_TEMPDIR

mkdir -p $PL_SCRATCHDIR
chown $PL_USER $PL_SCRATCHDIR

if [ -d /tmp/serverLibrary ]
then
    sudo -u $PL_USER cp -ru /tmp/serverLibrary ${PL_LOCATION}
    sudo -u $PL_USER rm -r /tmp/serverLibrary
fi

##################################################
#           JDK INSTALLATION
##################################################
# echo "=======| CHECKING FOR ORACLE JDK |======="
# echo "pl-->Checking for Oracle JDK..."
# echo "pl->"
if [ `java -version 2>&1 | grep 'Java(TM)' | wc -l` -ne 1 ]
then
    if [ ! -z "$JDK_BINARY_LOCATION" ]
    then
        INSTALLER_MODE_MANUAL_TOOL=true
        MANUALTOOL_ARCHIVE_PATH=$JDK_BINARY_LOCATION
    fi
    if [ "$INSTALLER_MODE_MANUAL_TOOL" = "true" ]
    then
        echo "The provided JDK archive path is: $MANUALTOOL_ARCHIVE_PATH"
        echo "pl-->Installing: "
        echo "pl->Oracle JDK"
        cp "$MANUALTOOL_ARCHIVE_PATH" $SHARED_FILESYSTEM_PATH

        echo MANUALTOOL_ARCHIVE_PATH=$MANUALTOOL_ARCHIVE_PATH
        echo SHARED_FILESYSTEM_PATH=$SHARED_FILESYSTEM_PATH
        FILENAME=${MANUALTOOL_ARCHIVE_PATH##*/}

        # install jdk rpm
        echo FILENAME=$FILENAME
        cd $SHARED_FILESYSTEM_PATH
        rpm --prefix=$SHARED_FILESYSTEM_PATH -ivh $FILENAME
        rm -f $FILENAME

        # determine exact path where jdk was installed
        javapath=`ls -d $SHARED_FILESYSTEM_PATH/jdk1*`
        if [ `echo $javapath | wc -w` -ne 1 ]
        then
            javapath=`echo $javapath | cut -d' ' -f1`
        fi

        # verify that javapath is pointing to the correct location
        ${javapath}/bin/java -version > /dev/null 2> /dev/null
	if [ $? == 0 ]; then
            /usr/sbin/alternatives --install /usr/bin/java java ${javapath}/bin/java 2
            /usr/sbin/update-alternatives --set java ${javapath}/bin/java
            JAVA_HOME=${javapath}

            export JAVA_HOME

            echo JAVA_HOME=$JAVA_HOME

            # configure JAVA_HOME in /etc/profile
            if [ -z "`grep JAVA_CONFIG /etc/profile`" ]
            then
                echo -e "########## JAVA_CONFIG\nexport JAVA_HOME=$JAVA_HOME\n">> /etc/profile
            else
                javahome_lineNumber=$(grep -n "export JAVA_HOME" /etc/profile | cut -d':' -f1)
                sed -i ${javahome_lineNumber}c"export JAVA_HOME=$JAVA_HOME" /etc/profile
            fi

            if [ -z "`grep JAVA_CONFIG /etc/csh.login`" ]
            then
                echo -e "########## JAVA_CONFIG\nsetenv JAVA_HOME $JAVA_HOME\n">> /etc/csh.login
            else
                javahome_lineNumber=$(grep -n "setenv JAVA_HOME" /etc/csh.login | cut -d':' -f1)
                sed -i ${javahome_lineNumber}c"setenv JAVA_HOME $JAVA_HOME" /etc/csh.login
            fi

            echo "JDK Installed"
        fi
    else
        echo 'pl@@Oracle JDK@http://www.oracle.com/technetwork/java/javase/downloads/index.html@1. Click on Download JDK button.<br>2. Select Platform: Linux x64 or x86 and click Continue ( username, password is optional )<br>3. Click on jdk-[ver]-linux-[platform].rpm link to begin download.'
        exit 0;
    fi
fi

# Source /etc/profile
# . /etc/profile

# check if JAVA_HOME is set
if [ -z "$JAVA_HOME" ]
then
    JAVA_BINARY="/$(${FIND_ALTS} java | grep link | cut -d'/' -f2-)"
    JAVA_BIN=`dirname $JAVA_BINARY`
    JAVA_HOME=`dirname $JAVA_BIN`
    export JAVA_HOME
fi

##################################################
#           DOWNLOADING
##################################################
echo "=======| DOWNLOADING FILES |======="
if [ -d ${DL_DIR}/pipeline ]
then
    cd ${DL_DIR}/pipeline
else
    mkdir -p ${DL_DIR}/pipeline
    cd ${DL_DIR}/pipeline
fi

echo "pl-->Downloading: "
echo "pl->Pipeline package"
wget -c --progress=dot http://users.loni.usc.edu/~pipeline/dps/Pipeline.tar.bz2
wget -c --progress=dot http://users.loni.usc.edu/~pipeline/dps/drmaa_plugin.tar.gz
wget -c --progress=dot http://users.loni.usc.edu/~pipeline/dps/jgdi_plugin.tar.gz
wget -c --progress=dot http://users.loni.usc.edu/~pipeline/dps/plgridplugin.tar.gz
wget -c --progress=dot http://users.loni.usc.edu/~pipeline/dps/plgridservice.tar.gz

echo "pl+"
echo "pl+"
echo "pl+"


echo "pl-->Downloading: "
echo "pl->Authentication module library"
wget -c --progress=dot http://users.loni.usc.edu/~pipeline/dps/libjaaspam.so

echo "pl+"
echo "pl+"

echo "pl-->Downloading: "
echo "pl->Authentication module"

if [ "$PL_USER_AUTH" = "SSH" ]
then
    wget -c --progress=dot http://users.loni.usc.edu/~pipeline/dps/SSHLocalLoginModule.tar.gz
    tar xmvzf SSHLocalLoginModule.tar.gz
    LOGIN_MODULE_CLASS="edu.ucla.loni.pipeline.security.SSHLocalLoginModule"
    LOGIN_MODULE=':$BASEPATH/dist/lib/SSHLocalLoginModule.jar'
    LOGIN_MODULE_FILE="SSHLocalLoginModule.jar"
    service sshd start
elif [ "$PL_USER_AUTH" = "PAM" ]
then
    wget -c --progress=dot http://users.loni.usc.edu/~pipeline/dps/PAMLoginModule.jar
    LOGIN_MODULE_CLASS="edu.ucla.loni.pipeline.security.LONILoginModule"
    LOGIN_MODULE=':$BASEPATH/dist/lib/PAMLoginModule.jar'
    LOGIN_MODULE_FILE="PAMLoginModule.jar"
else
    echo "No authentication module to download. User selected no authentication. "
    LOGIN_MODULE_CLASS="com.sun.security.auth.module.UnixLoginModule"
    LOGIN_MODULE=""
    LOGIN_MODULE_FILE=""
fi

echo "pl+"
echo "pl+"

##################################################
#           EXTRACTING
##################################################
echo "=======| EXTRACTING FILES |======="
echo "pl-->Extracting: "
echo "pl->Pipeline package"
tar xmvjf Pipeline.tar.bz2
echo "pl+"





##################################################
#           INSTALLING PERSISTENCE DB
##################################################
echo "=======| INSTALLING PERSISTENCE DB |======="
echo "pl-->Installing: "
echo "pl->Persistence Database"

sudo -u $PL_USER cp lib/hsqldb.jar $PLDB_LOCATION/

###########SYSTEM ARCHITECTURE
# check if we are running on a 64-bit architecture
d64=
if java -d64 -version >/dev/null 2>&1; then
    d64=-d64
fi

###########MEMORY ALLOCATION
# check if the user has specified JVM memory allocation
memory=
if [ ! -z "$PL_MEMORY_ALLOC" ]
then
    memory=-Xmx${PL_MEMORY_ALLOC}M
fi

###########CREATING START DB SCRIPT
echo '
#! /bin/bash

DB_PATH="'$PLDB_LOCATION'"
SERVER_BASEPATH="'$PL_LOCATION'"

if [ -f ${DB_PATH}/running.pid ]
then
    echo "Failed to start database, seems there is already running database"
    exit 1;
fi

WHOAMI=`whoami`
if [ $WHOAMI = "root" ]
then
    echo "You are root. Please run this script only with user:'$PL_USER'"
    exit 1
fi

'$JAVA_HOME'/bin/java '$d64' -server '${memory}' -cp $DB_PATH/hsqldb.jar org.hsqldb.Server -database.1 file:$DB_PATH/persistenceDB'${PL_HOSTNAME}${PL_PORT}' -dbname.1 xdb1 -port 9002 > $DB_PATH/outputStream.log 2> $DB_PATH/errorStream.log&

if [ $? -eq 0 ]
then
	echo $! > ${DB_PATH}/running.pid
	echo "Database successfully started."
	exit 0
else
	echo "Failed to start database"
	exit 1
fi' >>$PLDB_LOCATION/startDB.sh


echo "pl+"

###########CREATING STOP DB SCRIPT
echo '
#! /bin/bash
DB_PATH="'$PLDB_LOCATION'"
PID_FILE=${DB_PATH}/running.pid

if [ -f ${PID_FILE} ]
then
	echo -n "Killing database..."
	PID=`cat ${PID_FILE}`

	kill $PID 2>/dev/null

	while ps -p ${PID} > /dev/null; do sleep 1; done

	if [ $? -eq 0 ]
	then
    		rm ${PID_FILE}
    		echo "DONE"
    		exit 0
	else
    		echo "FAILED"
    		exit 1
	fi
else
	echo "No database running."
fi'>>$PLDB_LOCATION/stopDB.sh


###### Changing start & stop script executable permissions

chown $PL_USER $PLDB_LOCATION/startDB.sh
chown $PL_USER $PLDB_LOCATION/stopDB.sh

chmod +x $PLDB_LOCATION/startDB.sh
chmod +x $PLDB_LOCATION/stopDB.sh

##################################################
#           INSTALLING THE SERVER
##################################################
echo "=======| INSTALLING PIPELINE SERVER |======="
echo "pl-->Installing: "
echo "pl->Pipeline Server"

firewall_file=/etc/sysconfig/system-config-securitylevel
if [ ! -f $firewall_file ]
then
    firewall_file=/etc/sysconfig/system-config-firewall
fi

if [ -f $firewall_file ]
then
    echo "Configuring firewall to allow for Pipeline communication. A backup of the previous configuration is stored in the /etc/syconfig/ directory."
    if [ -z "`grep enabled $firewall_file`" ]
    then
        # Firewall is Off,
        lokkit -q --selinux='disabled' --disabled --port=ssh:tcp --port=$PL_PORT:tcp
    else
        # Firewall is On,
        lokkit -q --selinux='disabled' --enabled --port=ssh:tcp --port=$PL_PORT:tcp
    fi
fi

if [ ! -z "$SGE_ROOT" ]
then
    export SGE_ROOT=$SGE_ROOT
fi

SGE_PORT=$(grep sge-qmaster /etc/services \
    | sed -e "s/^[ \t]*sge-qmaster[ \t]*\([0-9]\+\)\/.*/\1/" \
    | uniq)
if [ ! -z "$SGE_PORT" ]
then
    export SGE_PORT=$SGE_PORT
else
    export SGE_PORT=6444
fi

echo "=======| Copying files"
mv lib $PL_LOCATION/dist/lib
mv Pipeline.jar $PL_LOCATION/dist/Pipeline.jar
mv libjaaspam.so $PL_LOCATION/dist/
mkdir -p $PL_LOCATION/dist/gridplugins
tar xmvzf drmaa_plugin.tar.gz
tar xmvzf jgdi_plugin.tar.gz
tar xmvzf plgridplugin.tar.gz
tar xmvzf plgridservice.tar.gz
mv DRMAAPlugin.jar $PL_LOCATION/dist/gridplugins/
mv JGDIPlugin.jar $PL_LOCATION/dist/gridplugins/
mv PipelineGridService.jar $PL_LOCATION/dist/gridplugins/
mv PipelineGridPlugin.jar $PL_LOCATION/dist/lib/
if [ "$LOGIN_MODULE_FILE" != "" ]
then
    cp $LOGIN_MODULE_FILE $PL_LOCATION/dist/lib/
fi

echo "=======| Changing ownership of files"
chown $PL_USER $PL_LOCATION/*
chown $PL_USER $PL_LOCATION/dist/*
chown $PL_USER $PL_LOCATION/dist/lib/*
chown $PL_USER $PL_LOCATION/dist/gridplugins/*

echo "=======| Checking selected grid plugin"
echo Selected plugin is
if [ "$PL_PLUGIN" = "DRMAA" ]
then
    echo DRMAA
    GRIDPLUGIN_USEARRAYJOBS=false
    GRIDPLUGIN_JARFILES=$PL_LOCATION/dist/gridplugins/DRMAAPlugin.jar
    GRIDPLUGIN_CLASS=drmaaplugin.DRMAAPlugin
elif [ "$PL_PLUGIN" = "JGDI" ]
then
    echo JGDI
    GRIDPLUGIN_USEARRAYJOBS=true
    GRIDPLUGIN_JARFILES="$PL_LOCATION/dist/gridplugins/JGDIPlugin.jar, $SGE_ROOT/lib/jgdi.jar, $PL_LOCATION/dist/lib/mysql-connector-java-5.1.8-bin.jar, $PL_LOCATION/dist/lib/hsqldb.jar"
    GRIDPLUGIN_CLASS=jgdiplugin.JGDIPlugin
else
    echo "[NONE]"
fi

echo "=======| Creating preferences file"

if [ ! -z "$GRIDPLUGIN_JARFILES" ]
then
    QUEUE_PREFS="
    <GridPluginClass>$GRIDPLUGIN_CLASS</GridPluginClass>
    <GridTotalSlotsCmd>qstat -g c | grep _pqueue | awk ""'"'{ print $6-$7-$8 }'"'""</GridTotalSlotsCmd>
    <GridEngineNativeSpecification>-shell y -S /bin/sh -w n -q _pqueue -b y _pmpi _pmem _pstack _pcomplex -N _pjob</GridEngineNativeSpecification>
    <GridPluginJARFiles>$GRIDPLUGIN_JARFILES</GridPluginJARFiles>
    <GridUseArrayJobs>$GRIDPLUGIN_USEARRAYJOBS</GridUseArrayJobs>
    <GridArrayJobsDynamicIncrease>$GRIDPLUGIN_USEARRAYJOBS</GridArrayJobsDynamicIncrease>
    <GridSubmissionQueue>$PL_QUEUE</GridSubmissionQueue>
    <GridPluginUseRestartableService>true</GridPluginUseRestartableService>"
fi

###########CREATING PREFERENCES FILE
echo "<?xml version="'"'1.0'"'" encoding="'"'UTF-8'"'"?>
<preferences>
    <ServerPort>$PL_PORT</ServerPort>
    <Hostname>$PL_HOSTNAME</Hostname>
    <SecureTempFileLocation>$PL_TEMPDIR</SecureTempFileLocation>
    <UsePrivilegeEscalation>$PL_USEPRIVESC</UsePrivilegeEscalation>$QUEUE_PREFS
    <PersistenceURL>jdbc:hsqldb:hsql://localhost:9002/xdb1</PersistenceURL>
    <LogFileLocation>$PL_LOCATION/events.log</LogFileLocation>
    <ServerAdmins>$PL_USER</ServerAdmins>
    <ServerLibraryLocation>$PL_SERVERLIB</ServerLibraryLocation>
    <ServerLibrarySameDirMonitor>false</ServerLibrarySameDirMonitor>
    <ServerLibraryMonitorFile>$PL_SERVERLIB/.monitorFile</ServerLibraryMonitorFile>
</preferences>
">> $PL_LOCATION/preferences.xml
chown $PL_USER $PL_LOCATION/preferences.xml

###########CREATING jaas config FILE
echo "=======| Creating jaas config file"
echo "/** Login Configuration for the Pipeline **/

PipelineLogin {
   $LOGIN_MODULE_CLASS required debug=false;
};
">> $PL_LOCATION/dist/pipeline_jaas.config
chown $PL_USER $PL_LOCATION/dist/pipeline_jaas.config


###########CREATING LAUNCH SERVER SCRIPT(S)
echo "=======| Creating launch Server Script"
echo '
BASEPATH="'$PL_LOCATION'"
PREFS_PATH=$BASEPATH

if [ -f ${BASEPATH}/running.pid ]
then
   echo "Server already running."
   exit 1;
fi


WHOAMI=`whoami`
if [ $WHOAMI = "root" ]
then
    echo "You are root. Please run this script only with user:'$PL_USER'"
    exit 1;
fi

# if the SGE_CELL environmental variable is not set, use the default value
if [ -z $SGE_CELL ]; then
   export SGE_CELL=default
fi

cd ${BASEPATH}

#set core dump file limit to unlimited
ulimit -c unlimited

# so the cache directories that get created by the server have 'rwx' for all
umask 000

if [ ! -z "`grep GridPluginJARFiles $PREFS_PATH/preferences.xml`" ]
then
    if [ -d $SGE_ROOT ] && [ ! -z "$SGE_ROOT" ]
    then
        # setting LD_LIBRARY_PATH in case somebody runs the script using 'su' or 'sudo'
        arch=`${SGE_ROOT}/util/arch`
        export LD_LIBRARY_PATH=$SGE_ROOT/lib/${arch}:$BASEPATH/dist/:$LD_LIBRARY_PATH

        # reading in the SGE libraries
        source $SGE_ROOT/$SGE_CELL/common/settings.sh
    else
        echo "SGE_ROOT is not defined or set to an invalid directory."
        echo "Server not started."
        exit 2;
    fi
else
    export LD_LIBRARY_PATH=$BASEPATH/dist/:$LD_LIBRARY_PATH
fi

if [ -x "${BASEPATH}/updateServer.sh" ]; then
    ${BASEPATH}/updateServer.sh
fi

'$JAVA_HOME'/bin/java '$d64' -server '${memory}' -Djava.awt.headless=true -Djava.security.auth.login.config=$BASEPATH/dist/pipeline_jaas.config -cp .:$SGE_ROOT/lib/drmaa.jar'$LOGIN_MODULE':$BASEPATH/dist/Pipeline.jar server.Main -preferences $PREFS_PATH/preferences.xml > $BASEPATH/outputStream.log 2> $BASEPATH/errorStream.log&

if [ $? -eq 0 ]
then
	echo $! > ${BASEPATH}/running.pid
	echo "Server successfully started."
	exit 0
else
	echo "Failed to start server"
	exit 1
fi'>>$PL_LOCATION/launchServer.sh

chmod +x $PL_LOCATION/launchServer.sh
chown $PL_USER $PL_LOCATION/launchServer.sh

# ______________________________________________________________________

echo_update_server() {
    echo '#!/bin/bash

SERVERNAME_PATH="$(readlink -f ~'${PL_USER}')/.servername.txt"
EC2_MAGIC_IP="169.254.169.254"
EC2_HOSTNAME_URL="http://${EC2_MAGIC_IP}/latest/meta-data/public-hostname"

# First, figure out what the old server name is...

if [ $# -eq 2 ]; then
    OldServer=$1
elif [ -f ${SERVERNAME_PATH} ]; then
    OldServer=$(cat ${SERVERNAME_PATH})
else
    echo "Unable to determine old server name."
    exit 1
fi

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

# Finally, go to town...

[ "${NewServer}" = "${OldServer}" ] && {
    echo "No change to server name detected.";
    exit 0;
}

echo "${NewServer}" > ${SERVERNAME_PATH}

echo "Update Pipeline server hostname to ${NewServer}"

WHOAMI=$(whoami)
if [ "$WHOAMI" != "'${PL_USER}'" ]; then
    PREFIX="sudo sudo -u '${PL_USER}'"
else
    PREFIX=""
fi

${PREFIX} perl -pi -w -e "s/${OldServer}/${NewServer}/g;" /usr/pipeline/preferences.xml

${PREFIX} perl -pi -w -e "s/${OldServer}/${NewServer}/g;" /usr/pipeline/serverLibrary/*/*/*.pipe
'
}

# ____________________________________________________________

echo_update_server > ${PL_LOCATION}/updateServer.sh
chmod +x ${PL_LOCATION}/updateServer.sh
chown ${PL_USER}:${PL_USER} ${PL_LOCATION}/updateServer.sh

# XXX: The following assumes that $PL_LOCATION is the same as
# $PL_USER's home directory.  This will break the automated update
# added to the init script if not true.
echo "${PL_HOSTNAME}" > ${PL_LOCATION}/.servername.txt
chown ${PL_USER}:${PL_USER} ${PL_LOCATION}/.servername.txt

# ______________________________________________________________________
echo "=======| Creating kill Server Script"

###########CREATING KILL SERVER SCRIPT
echo '
#! /bin/bash
BASEPATH="'$PL_LOCATION'"

PID_FILE=$BASEPATH/running.pid

if [ -f ${PID_FILE} ]
then

echo -n "Killing server..."

PID=`cat ${PID_FILE}`

kill $PID 2>/dev/null

while ps -p $PID > /dev/null; do sleep 1; done

if [ $? -eq 0 ]
then
    rm ${BASEPATH}/running.pid
    echo "DONE"
    exit 0
else
    echo "FAILED"
    exit 1
fi
else
echo "No pipeline server running."
fi '>>$PL_LOCATION/killServer.sh

chmod +x $PL_LOCATION/killServer.sh
chown $PL_USER $PL_LOCATION/killServer.sh

install_superuser() {
    ##### PIPELINE CONFIG
    if [ -z "`grep PIPELINE_CONFIG /etc/sudoers`" ]
    then
        echo "=======| Modifying Sudoers file"
        requiretty=`grep -n requiretty /etc/sudoers | grep Defaults | cut -d':' -f1`
        if [ ! -z "${requiretty}" ]; then
            sed -i ${requiretty}c"# Defaults requiretty" /etc/sudoers
        fi

        mainpart=$[`grep -n "main part" /etc/sudoers | cut -d':' -f1`-1]
        if [ ${mainpart} -ge 0 ]; then
            head -n $mainpart /etc/sudoers > /tmp/sudoers
        else
            cp /etc/sudoers /tmp/sudoers
        fi
        echo -e "########## PIPELINE_CONFIG\nDefaults env_keep += \"PERL5OPT PERL5LIB PERLLIB JAVA_HOME SGE_ROOT SGE_CELL SGE_PORT SGE_CLUSTER_NAME\"
Defaults always_set_home" >> /tmp/sudoers
        if [ ${mainpart} -ge 0 ]; then
            lastlines=$[`wc /etc/sudoers | tr -s ' ' |  cut -d' ' -f2`-mainpart]
            tail -n $lastlines /etc/sudoers >> /tmp/sudoers
        fi
    else
        cp /etc/sudoers /tmp/sudoers
    fi


    echo "=======| Making Pipeline power user"
    ##### LONIPipeline_${PL_USER}

    if [ ! -z "`grep LONIPipeline_ /tmp/sudoers`" ]
    then
        LN=`grep -n "LONIPipeline_" /tmp/sudoers | cut -f1 -d:`
        echo LN=$LN
        END_LN=$(( $LN + 6 ))
        echo END_LN=$END_LN
        sed -i ${LN},${END_LN}d /tmp/sudoers
    fi


    SUPERUSERS='!root'

    if [ ! -z "$SUPERUSER_LIST" ]
    then
        SUPERUSER_LIST_0=$(echo "${SUPERUSER_LIST}" | sed -e 's/,/,!/g')
        SUPERUSERS+=',!'${SUPERUSER_LIST_0}
    fi

    echo "Modifying sudoers list for $PL_USER"
    echo -e "########## LONIPipeline_${PL_USER}\nHost_Alias CLUSTER = ${PL_HOSTNAME}\nUser_Alias PIPELINE1 = ${PL_USER}\nUser_Alias PIPELINE2 = ALL,$SUPERUSERS\nRunas_Alias PIPELINE2_RUN = ALL,$SUPERUSERS\nPIPELINE1 CLUSTER = (PIPELINE2_RUN) NOPASSWD: ALL\nDefaults:PIPELINE1 !secure_path\n" >> /tmp/sudoers

    visudo -c -f /tmp/sudoers || {
        echo -e "Failed sanity check of /tmp/sudoers.\nNo changes made.";
        return 1;
    }
    mv -f /tmp/sudoers /etc/sudoers
    chmod 440 /etc/sudoers
    return 0
}

if [ "$INSTALL_SUPERUSER" = "true" ]
then
    install_superuser
fi


if [ $PL_START_ON_STARTUP = "true" ]
then
    echo "=======| Creating Pipeline startup script"
    if [ -f /etc/init.d/pipeline ]
    then
        rm -f /etc/init.d/pipeline
    fi
    ###########CREATING START SERVER ON STARTUP SCRIPT
    echo '#!/bin/sh
    ### BEGIN INIT INFO
    # Provides:          pipeline
    # Required-Start:    $network
    # Required-Stop:
    # Default-Start:     3 5
    # Default-Stop:      0 1 2 6
    # Short-Description: controls Pipeline server on boot+shutdown
    # Description:       Brings up the Pipeline server on boot,
    #       takes it offline on shutdown/restart.
    ### END INIT INFO


    SERVER_PATH="'$PL_LOCATION'"
    DB_PATH="'$PLDB_LOCATION'"
    PIPELINE_USER="'$PL_USER'"

    case "$1" in
      start)
        WHOAMI=`whoami`
        if [ $WHOAMI = "root" ]
        then
          if [ -f ${DB_PATH}/running.pid ]; then
             echo "Deleting defunct ${DB_PATH}/running.pid file..."
             /bin/mv ${DB_PATH}/running.pid /tmp/running.db.pid
          fi
          if [ -f ${SERVER_PATH}/running.pid ]; then
             echo "Deleting defunct ${SERVER_PATH}/running.pid file..."
             /bin/mv ${SERVER_PATH}/running.pid /tmp
          fi
          sudo -u ${PIPELINE_USER} ${DB_PATH}/startDB.sh
          sudo -u ${PIPELINE_USER} ${SERVER_PATH}/launchServer.sh

          exit $?
        else
          echo "Insufficient privileges."
          exit 1
        fi
        ;;
      stop)
        WHOAMI=`whoami`
        if [ $WHOAMI = "root" ]
        then
          if [ -f ${SERVER_PATH}/running.pid ]; then
            echo "Stopping the Pipeline Database..."
            sudo -u ${PIPELINE_USER} ${DB_PATH}/stopDB.sh
            echo "Stopping the Pipeline Server..."
            sudo -u ${PIPELINE_USER} ${SERVER_PATH}/killServer.sh
          else
            echo "Pipeline Server not running."
            exit 1
          fi
        else
          echo "Insufficient privileges."
          exit 1
        fi
        ## If somehow these file were not deleted, delete them
        if [ -f ${SERVER_PATH}/running.pid ]; then
          echo "Deleting ${SERVER_PATH}/running.pid..."
          rm -f ${SERVER_PATH}/running.pid
        fi
        exit 0
        ;;
      *)
        ## If no parameters are given, print which are avaiable.
        echo "Usage: $0 {start|stop}"
        exit 1
        ;;
    esac
    '>>/etc/init.d/pipeline

    chmod +x /etc/init.d/pipeline

    /sbin/chkconfig --add pipeline
    /sbin/chkconfig pipeline on
fi

##################################################
#           INSTALLING TEST IMAGE FILE
##################################################
echo "=======| Downloading test image files"
echo "pl-->Downloading: "
echo "pl->Test input files"

# download test input files
wget -c --progress=dot http://users.loni.usc.edu/~pipeline/dps/test_inputs.tar.gz

echo "pl+";

# decompress test input files
tar xmvzf test_inputs.tar.gz
echo "pl+";
##################################################
#           INSTALLING UTILITIES
##################################################
echo "=======| Installing utilities"
echo "pl-->Installing: "
echo "pl->Utilities"

# download smartline archive
wget -c --progress=dot http://users.loni.usc.edu/~pipeline/dps/smartline.tar.gz
wget -c --progress=dot http://users.loni.usc.edu/~pipeline/dps/idaget.tar.gz
wget -c --progress=dot http://users.loni.usc.edu/~pipeline/dps/xnat.tar.gz
wget -c --progress=dot http://users.loni.usc.edu/~pipeline/dps/thumbgen.tar.gz

echo "pl+";

mkdir -p $TOOLS_PATH/PipelineUtilities
chown $PL_USER $TOOLS_PATH/PipelineUtilities

cp smartline.tar.gz idaget.tar.gz xnat.tar.gz thumbgen.tar.gz $TOOLS_PATH/PipelineUtilities
cd $TOOLS_PATH/PipelineUtilities

# decompress utility archives
tar xmvzf smartline.tar.gz
tar xmvzf idaget.tar.gz
tar xmvzf xnat.tar.gz
tar xmvzf thumbgen.tar.gz

# clean archives
rm smartline.tar.gz idaget.tar.gz xnat.tar.gz thumbgen.tar.gz

echo "pl+";

##################################################
#           CONFIGURE SGE
##################################################

if [ -z "`grep SGE_CONFIG /etc/profile`" ]
then
    echo -e "########## SGE_CONFIG\nexport SGE_ROOT=$SGE_ROOT\n">> /etc/profile
fi

if [ -z "`grep SGE_CONFIG /etc/csh.login`" ]
then
    echo -e "########## SGE_CONFIG\nsetenv SGE_ROOT $SGE_ROOT\n">> /etc/csh.login
fi

echo "=======| PIPELINE INSTALLATION COMPLETE |======="
echo "pl+"
echo "pl-->Complete"
echo "pl->"
