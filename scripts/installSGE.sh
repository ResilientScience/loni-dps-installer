#!/bin/sh

if [ -z "$INSTALL_SGE" ]
then
    echo "Please do not run this script manually. Run launchInstaller.sh instead."
    exit 1
fi

# add /usr/sbin and /sbin to PATH
export PATH=${PATH}:/usr/sbin:/sbin

umask 0002

DL_DIR=$(cd `dirname $0` && pwd)

# store SGE_ROOT for new installation
sgetmp=${SGE_ROOT}

echo "sge-->Checking for previous installations...";
sgedirs=$(${DL_DIR}/checkSGE.sh | grep SGE | grep -v " SGE")
numsgedirs=$(echo $sgedirs | wc -l)
if [ $numsgedirs -eq 1 ]
then
	sgedir=$(echo $sgedirs | cut -d' ' -f2)
fi

if [ ! -z "${sgedir}" ] && [ -d ${sgedir} ]
then
    echo "sge-->Uninstalling previous installation...";
    cdir=$(pwd)
    export SGE_ROOT=$sgedir
    cd $SGE_ROOT
    rm -Rf /etc/init.d/sge*
    ${sgedir}/inst_sge -ux -auto ${sgedir}/util/install_modules/my_configuration.conf
    ${sgedir}/inst_sge -um -auto ${sgedir}/util/install_modules/my_configuration.conf
    rm -Rf ${sgedir}
    cd $cdir
fi

echo "sge-->"

# restore SGE_ROOT for new installation
export SGE_ROOT=$sgetmp

mkdir -p ${SGE_ROOT}

##### SGE CONFIG
if [ -z "`grep SGE_CONFIG /etc/profile`" ]
then
    echo -e "########## SGE_CONFIG\nexport SGE_ROOT=$SGE_ROOT\nsource ${SGE_ROOT}/default/common/settings.sh">> /etc/profile
else
    sgeroot_lineNumber=$(grep -n "export SGE_ROOT" /etc/profile | cut -d':' -f1)
    sed -i ${sgeroot_lineNumber}c"export SGE_ROOT=$SGE_ROOT" /etc/profile
    sourcesettings_lineNumber=$[sgeroot_lineNumber+1]
    sed -i ${sourcesettings_lineNumber}c"source ${SGE_ROOT}/default/common/settings.sh" /etc/profile
fi

if [ -z "`grep SGE_CONFIG /etc/csh.login`" ]
then
    echo -e "########## SGE_CONFIG\nsetenv SGE_ROOT $SGE_ROOT\nsource ${SGE_ROOT}/default/common/settings.csh">> /etc/csh.login
else
    sgeroot_lineNumber=$(grep -n "setenv SGE_ROOT" /etc/csh.login | cut -d':' -f1)
    sed -i ${sgeroot_lineNumber}c"setenv SGE_ROOT $SGE_ROOT" /etc/csh.login
    sourcesettings_lineNumber=$[sgeroot_lineNumber+1]
    sed -i ${sourcesettings_lineNumber}c"source ${SGE_ROOT}/default/common/settings.csh" /etc/csh.login
fi

####################################################################################################
################################################## SGE WITH COMPILATION
####################################################################################################
if [ ! -z "$COMPILE_AND_INSTALL_SGE" ]
then
    echo "sge=>68"

    ##################################################
    #           VARIABLES
    ##################################################

    LIB=/usr/lib64


    PREREQUISITES="jpackage-utils javacc openmotif ant-nodeps gcc ant pam-devel"


    DOWNLOADS="http://www.openssl.org/source/openssl-0.9.8o.tar.gz \
              http://github.com/downloads/KentBeck/junit/junit-4.8.2.jar \
              http://gridengine.sunsource.net/files/documents/7/212/ge-V62u4_TAG-src.tar.gz \
              http://gridengine.sunsource.net/nonav/issues/showattachment.cgi/164/libcore.so.gz"

    TAR_FILES="openssl-0.9.8o.tar.gz \
              ge-V62u4_TAG-src.tar.gz"

    GZ_FILES="libcore.so.gz"

    ##################################################
    #           DOWNLOADING
    ##################################################
    cd $DL_DIR

    echo "sge-->Downloading: "

    for file in $DOWNLOADS
    do
        filename=`basename $file`
        echo "sge->" $filename
        wget -c --progress=dot $file
        if [ $? -ne 0 ]; then
            echo "sge!Failed to download $file"
            exit 1
        fi
    #++++++++++++++ INCREASE PROGRESS
        echo "sge+"
        echo "sge+"
    done;

    ##################################################
    #           INSTALLING PREREQUISITES
    ##################################################
    echo "sge-->Installing Prerequisites: "

    yum clean metadata

    for package in $PREREQUISITES
    do
        echo "sge->$package"
        yum -y install $package
    #++++++++++++++ INCREASE PROGRESS
        echo "sge+"
        echo "sge+"
        echo "sge+"
    done;

    ##################################################
    #           EXTRACTING
    ##################################################
    echo "sge-->Extracting: "

    ################################ TAR FILES

    for tarFile in $TAR_FILES
    do
        echo "sge->" $tarFile
        tar -xmvzf $tarFile
    #++++++++++++++ INCREASE PROGRESS
        echo "sge+"
    done;

    ################################ GZ FILES
    for gzFile in $GZ_FILES
    do
        echo "sge->" $gzFile
        gunzip $gzFile
    #++++++++++++++ INCREASE PROGRESS
        echo "sge+"
    done;


    # as of now num_steps = 32

    ##################################################
    #           CONFIGURING SCRIPTS
    ##################################################

    #++++++++++++++ INCREASE PROGRESS
    echo "sge+"

    # update installation options
    opensslhome_lineNumber=`grep -n "set OPENSSL_HOME" ${DL_DIR}/gridengine/source/aimk.site | cut -d':' -f1`
    sed -i ${opensslhome_lineNumber}c"set OPENSSL_HOME = ${DL_DIR}/openssl-0.9.8o" ${DL_DIR}/gridengine/source/aimk.site

    opensslversion_lineNumber=`grep -n OPENSSL_SOVERSION ${DL_DIR}/gridengine/source/aimk.site | cut -d':' -f1`
    sed -i ${opensslversion_lineNumber}c"set OPENSSL_SOVERSION = 0.9.8o" ${DL_DIR}/gridengine/source/aimk.site

    cp ${DL_DIR}/gridengine/source/build.properties ${DL_DIR}/gridengine/source/build_private.properties

    javacchome=`grep -n javacc.home ${DL_DIR}/gridengine/source/build_private.properties | cut -d':' -f1`
    sed -i ${javacchome}c"javacc.home=/usr/share/java" ${DL_DIR}/gridengine/source/build_private.properties

    junitclasspath=`grep -n libs.junit.classpath ${DL_DIR}/gridengine/source/build_private.properties | cut -d':' -f1`
    sed -i ${junitclasspath}c"libs.junit.classpath=${DL_DIR}/junit-4.8.2.jar" ${DL_DIR}/gridengine/source/build_private.properties

    javacchomejgdi=`grep -n javacchome ${DL_DIR}/gridengine/source/libs/jgdi/cullconv/build.xml | cut -d':' -f1`
    sed -i ${javacchomejgdi}c"<javacc javacchome=\"/usr/share/java\"" ${DL_DIR}/gridengine/source/libs/jgdi/cullconv/build.xml

    #++++++++++++++ INCREASE PROGRESS
    echo "sge+"

    ##################################################
    #           COMPILING
    ##################################################
    echo "sge-->Compiling: "
    echo "sge->Dependency files"

    cd ${DL_DIR}
    mv -f libcore.so ${LIB}/

    cd ${DL_DIR}/gridengine/source

    # generate 'sge_depend' tool used to create header dependencies
    ./aimk -only-depend

    #++++++++++++++ INCREASE PROGRESS
    echo "sge+"

    # create zero length dependency files
    echo "Creating zero length dependency files"
    scripts/zerodepend

    #++++++++++++++ INCREASE PROGRESS
    echo "sge+"

    # create dependencies
    echo "Creating dependencies"
    ./aimk depend

    #++++++++++++++ INCREASE PROGRESS
    echo "sge+"

    # compile grid engine with aimk tool
    echo "sge->Core files"
    ./aimk -spool-classic -no-secure -only-core
    #++++++++++++++ INCREASE PROGRESS
    echo "sge+"
    echo "sge+"
    echo "sge+"
    echo "sge+"
    echo "sge+"
    echo "sge+"
    echo "sge->Java files"
    ./aimk -spool-classic -only-java -no-gui-inst
    #++++++++++++++ INCREASE PROGRESS
    echo "sge+"
    echo "sge+"
    echo "sge+"



    #=======as of now num_steps = 46


    ##################################################
    #           GENERATING
    ##################################################
    echo "sge-->Generating: "
    echo "sge->Manual pages"
    ./aimk -man
    #++++++++++++++ INCREASE PROGRESS
    echo "sge+"
    echo "sge+"


    ##################################################
    #           INSTALLING
    ##################################################
    echo "sge-->Installing: "
    echo "sge->SGE files"

    #++++++++++++++ INCREASE PROGRESS
    echo "sge+"

    SRWEC=${SGE_ROOT//\//\\/}

    #++++++++++++++ INCREASE PROGRESS
    echo "sge+"
    echo Y | ./scripts/distinst -local -noexit -allall lx26-amd64
    #++++++++++++++ INCREASE PROGRESS
    echo "sge+"
    echo "sge+"

    #=======as of now num_steps = 52

    export PATH=${PATH}:${SGE_ROOT}/bin/lx26-amd64

####################################################################################################
################################################## SGE WITH BINARIES
####################################################################################################
else
    echo "sge=>25"

    ##################################################
    #           DOWNLOAD GRIDENGINE BINARIES
    ##################################################
    #++++++++++++++ INCREASE PROGRESS
    echo "sge+"


    echo "sge-->Downloading: "
    echo "sge->Grid Engine"
    arch=`arch`
    if [ "$arch" == "x86_64" ]
    then
        sge_filename="sge-8.0.0d-bin-lx-amd64.tar.gz"
        hwloc_filename="libhwloc.so.4.tar.gz"
        lib="lib64"
    else
        sge_filename="sge-8.0.0d-bin-lx-x86.tar.gz"
        hwloc_filename="libhwloc.so.5.tar.gz"
        lib="lib"
    fi
    wget -c --progress=dot http://users.loni.usc.edu/~pipeline/dps/${sge_filename}
    wget -c --progress=dot http://users.loni.usc.edu/~pipeline/dps/${hwloc_filename}
    wget -c --progress=dot http://users.loni.usc.edu/~pipeline/dps/sge-8.0.0d-common.tar.gz
    if [ $? -ne 0 ]; then
 	echo "sge!Failed to download SGE binaries"
 	exit 1
    fi
    
    #++++++++++++++ INCREASE PROGRESS
    echo "sge+"
    echo "sge+"
    echo "sge+"

    echo "sge-->Extracting: "
    echo "sge->Grid Engine"
    echo "sge+"

    mv ${sge_filename} sge-8.0.0d-common.tar.gz $SGE_ROOT
    mv ${hwloc_filename} /usr/${lib}
    cd $SGE_ROOT
    tar xmvzf ${sge_filename}
    echo "sge+"
    
    tar xmvzf sge-8.0.0d-common.tar.gz
    echo "sge+"

    cd /usr/${lib}
    tar xmvzf ${hwloc_filename}
    rm ${hwloc_filename}
    cd $SGE_ROOT

    arch=`${SGE_ROOT}/util/arch`
    export PATH=${PATH}:${SGE_ROOT}/bin/${arch}

fi


##################################################
#           CONFIGURING
##################################################
echo "sge-->Configuring: "
echo "sge->Configuration file"

firewall_file=/etc/sysconfig/system-config-securitylevel
if [ ! -f $firewall_file ]
then
    firewall_file=/etc/sysconfig/system-config-firewall
fi

if [ -f $firewall_file ]
then
    echo "Configuring firewall to allow for gridengine communication. A backup of the previous configuration is stored in the /etc/syconfig/ directory."
    if [ -z "`grep enabled $firewall_file`" ]
    then
        # Firewall is Off
        lokkit -q --selinux='disabled' --disabled --port=ssh:tcp --port=6444:tcp --port=6445:tcp
    else
        # Firewall is On
        lokkit -q --selinux='disabled' --enabled --port=ssh:tcp --port=6444:tcp --port=6445:tcp
    fi
fi


# create auto configuration file
cp ${SGE_ROOT}/util/install_modules/inst_template.conf ${SGE_ROOT}/util/install_modules/my_configuration.conf

#++++++++++++++ INCREASE PROGRESS
echo "sge+"

adminuser_line=`grep -n ADMIN_USER= ${SGE_ROOT}/util/install_modules/my_configuration.conf | cut -d':' -f1`
sed -i ${adminuser_line}c"ADMIN_USER=\"${SGE_ADMIN_USER}\"" ${SGE_ROOT}/util/install_modules/my_configuration.conf

# set static values in config file
qmasterport_line=`grep -n SGE_QMASTER_PORT= ${SGE_ROOT}/util/install_modules/my_configuration.conf | cut -d':' -f1`
sed -i ${qmasterport_line}c"SGE_QMASTER_PORT=\"6444\"" ${SGE_ROOT}/util/install_modules/my_configuration.conf

execdport_line=`grep -n SGE_EXECD_PORT= ${SGE_ROOT}/util/install_modules/my_configuration.conf | cut -d':' -f1`
sed -i ${execdport_line}c"SGE_EXECD_PORT=\"6445\"" ${SGE_ROOT}/util/install_modules/my_configuration.conf

shadow_line=`grep -n SHADOW_HOST= ${SGE_ROOT}/util/install_modules/my_configuration.conf | cut -d':' -f1`
sed -i ${shadow_line}c"SHADOW_HOST=\"\"" ${SGE_ROOT}/util/install_modules/my_configuration.conf

gidrange_line=`grep -n GID_RANGE= ${SGE_ROOT}/util/install_modules/my_configuration.conf | cut -d':' -f1`
sed -i ${gidrange_line}c"GID_RANGE=\"20000-20100\"" ${SGE_ROOT}/util/install_modules/my_configuration.conf

#++++++++++++++ INCREASE PROGRESS
echo "sge+"

spooling_line=`grep -n SPOOLING_METHOD= ${SGE_ROOT}/util/install_modules/my_configuration.conf | cut -d':' -f1`
sed -i ${spooling_line}c"SPOOLING_METHOD=\"classic\"" ${SGE_ROOT}/util/install_modules/my_configuration.conf

shell_line=`grep -n SHELL_NAME= ${SGE_ROOT}/util/install_modules/my_configuration.conf | cut -d':' -f1`
sed -i ${shell_line}c"SHELL_NAME=\"ssh\"" ${SGE_ROOT}/util/install_modules/my_configuration.conf

addtorc_line=`grep -n ADD_TO_RC= ${SGE_ROOT}/util/install_modules/my_configuration.conf | cut -d':' -f1`
sed -i ${addtorc_line}c"ADD_TO_RC=\"true\"" ${SGE_ROOT}/util/install_modules/my_configuration.conf

#++++++++++++++ INCREASE PROGRESS
echo "sge+"

shadow_line=`grep -n SHADOW_HOST= ${SGE_ROOT}/util/install_modules/my_configuration.conf | cut -d':' -f1`
sed -i ${shadow_line}c"SHADOW_HOST=\"\"" ${SGE_ROOT}/util/install_modules/my_configuration.conf

removerc_line=`grep -n REMOVE_RC= ${SGE_ROOT}/util/install_modules/my_configuration.conf | cut -d':' -f1`
sed -i ${removerc_line}c"REMOVE_RC=\"true\"" ${SGE_ROOT}/util/install_modules/my_configuration.conf

# remove unnecessary lines from config file
begin=`grep -n JMX ${SGE_ROOT}/util/install_modules/my_configuration.conf | head -n 1 | cut -d':' -f1`
end=`grep -n JVM ${SGE_ROOT}/util/install_modules/my_configuration.conf | tail -n 1 | cut -d':' -f1`

#++++++++++++++ INCREASE PROGRESS
echo "sge+"

first=$(expr $begin - 1 )
lines=`wc ${SGE_ROOT}/util/install_modules/my_configuration.conf | cut -d' ' -f3`
last=$(expr $lines - $end )
head -n $first ${SGE_ROOT}/util/install_modules/my_configuration.conf > ${SGE_ROOT}/util/install_modules/tmp.conf
tail -n $last ${SGE_ROOT}/util/install_modules/my_configuration.conf >> ${SGE_ROOT}/util/install_modules/tmp.conf
mv -f ${SGE_ROOT}/util/install_modules/tmp.conf ${SGE_ROOT}/util/install_modules/my_configuration.conf

#++++++++++++++ INCREASE PROGRESS
echo "sge+"

# update auto configuration file
sgeroot_line=`grep -n SGE_ROOT= ${SGE_ROOT}/util/install_modules/my_configuration.conf | cut -d':' -f1`
sed -i ${sgeroot_line}c"SGE_ROOT=\"${SGE_ROOT}\"" ${SGE_ROOT}/util/install_modules/my_configuration.conf


#++++++++++++++ INCREASE PROGRESS
echo "sge+"

qmasterspool_line=`grep -n QMASTER_SPOOL_DIR= ${SGE_ROOT}/util/install_modules/my_configuration.conf | cut -d':' -f1`
sed -i ${qmasterspool_line}c"QMASTER_SPOOL_DIR=\"${SGE_ROOT}/${cellname}/spool\"" ${SGE_ROOT}/util/install_modules/my_configuration.conf

execdspool_line=`grep -n EXECD_SPOOL_DIR= ${SGE_ROOT}/util/install_modules/my_configuration.conf | cut -d':' -f1`
sed -i ${execdspool_line}c"EXECD_SPOOL_DIR=\"${SGE_ROOT}/${cellname}/sge_spool\"" ${SGE_ROOT}/util/install_modules/my_configuration.conf

dbspool_line=`grep -n DB_SPOOLING_DIR= ${SGE_ROOT}/util/install_modules/my_configuration.conf | cut -d':' -f1`
sed -i ${dbspool_line}c"DB_SPOOLING_DIR=\"${SGE_ROOT}/${cellname}/db\"" ${SGE_ROOT}/util/install_modules/my_configuration.conf

#++++++++++++++ INCREASE PROGRESS
echo "sge+"

# update auto configuration file
sgecluster_line=`grep -n SGE_CLUSTER_NAME= ${SGE_ROOT}/util/install_modules/my_configuration.conf | cut -d':' -f1`
sed -i ${sgecluster_line}c"SGE_CLUSTER_NAME=\"${SGE_CLUSTER}\"" ${SGE_ROOT}/util/install_modules/my_configuration.conf

echo "sge+"
# update auto configuration file
submithost_line=`grep -n SUBMIT_HOST_LIST= ${SGE_ROOT}/util/install_modules/my_configuration.conf | cut -d':' -f1`
sed -i ${submithost_line}c"SUBMIT_HOST_LIST=\"${SGE_SUBMITHOSTS}\"" ${SGE_ROOT}/util/install_modules/my_configuration.conf

#++++++++++++++ INCREASE PROGRESS
echo "sge+"

EXECUTIONHOSTS=$(echo "$SGE_EXECHOSTS" | tr ',' ' ')

# update auto configuration file
exechost_line=`grep -n EXEC_HOST_LIST= ${SGE_ROOT}/util/install_modules/my_configuration.conf | cut -d':' -f1`
exechostremove_line=`grep -n EXEC_HOST_LIST_RM= ${SGE_ROOT}/util/install_modules/my_configuration.conf | cut -d':' -f1`
sed -i ${exechost_line}c"EXEC_HOST_LIST=\"${EXECUTIONHOSTS}\"" ${SGE_ROOT}/util/install_modules/my_configuration.conf
sed -i ${exechostremove_line}c"EXEC_HOST_LIST_RM=\"${EXECUTIONHOSTS}\"" ${SGE_ROOT}/util/install_modules/my_configuration.conf


# update auto configuration file
adminhost_line=`grep -n ADMIN_HOST_LIST= ${SGE_ROOT}/util/install_modules/my_configuration.conf | cut -d':' -f1`
sed -i ${adminhost_line}c"ADMIN_HOST_LIST=\"${SGE_ADMINHOSTS}\"" ${SGE_ROOT}/util/install_modules/my_configuration.conf

# update auto configuration file
localspool_line=`grep -n EXECD_SPOOL_DIR_LOCAL= ${SGE_ROOT}/util/install_modules/my_configuration.conf | cut -d':' -f1`
sed -i ${localspool_line}c"EXECD_SPOOL_DIR_LOCAL=\"${SGE_SPOOLDIR}\"" ${SGE_ROOT}/util/install_modules/my_configuration.conf

#++++++++++++++ INCREASE PROGRESS
echo "sge+"
echo "sge+"

# set environment
# SGE services
if [ -z "`grep sge_qmaster /etc/services`" ]
then
    echo "
    sge_qmaster     6444/tcp                        # SGE QMaster
    sge_execd       6445/tcp                        # SGE Execution Daemon" >> /etc/services
fi

# install grid engine
echo "Installing grid engine"
echo "sge-->Installing: "
echo "sge->Grid Engine"

cd ${SGE_ROOT}

./inst_sge -m -x -auto ${SGE_ROOT}/util/install_modules/my_configuration.conf
if [ $? -ne 0 ]; then
    echo "sge!Failed to install SGE"
    exit 1
fi
#++++++++++++++ INCREASE PROGRESS
echo "sge+"
echo "sge+"
echo "sge+"




source $SGE_ROOT/default/common/settings.sh

qconf -ssconf > /tmp/sconf.txt
scheduler_line=`grep -n schedd_job_info /tmp/sconf.txt | cut -d':' -f1`
sed -i ${scheduler_line}c"schedd_job_info true" /tmp/sconf.txt
qconf -Msconf /tmp/sconf.txt
if [ $? -ne 0 ]; then
	echo "sge!Failed to configure SGE scheduler"
	exit 1
fi
rm /tmp/sconf.txt
echo "sge+"


##################################################
#           VALIDATION
##################################################
echo "sge-->Validating..."
echo "sge->"



# installation verification
echo "Verifying grid engine installation by submitting a simple job using qsub"
#qsub ${SGE_ROOT}/examples/jobs/simple.sh

#++++++++++++++ INCREASE PROGRESS
echo "sge+"

if [ $? -eq 0 ]
then
    echo "sge->Success"
else
    echo "sge->Failed"
fi



##################################################
#           QUEUE CONFIGURATION
##################################################

cd $SGE_ROOT

if [ $SGE_CONFIGQUEUE = "true" ]
then
        echo "sge-->Creating queue:"
	
	# we can add more queue configuration questions here


        QUEUESLOTS=${SGE_QUEUESLOTS//","/" "}

	# create a new host list file with specified slots
	qconf -shgrp @allhosts > ${SGE_ROOT}/${SGE_QUEUEHOSTLIST}
	sed -i 1c"group_name ${SGE_QUEUEHOSTLIST}" ${SGE_ROOT}/${SGE_QUEUEHOSTLIST}
	sed -i 2c"hostlist ${QUEUESLOTS}" ${SGE_ROOT}/${SGE_QUEUEHOSTLIST}

	# modify slots string for auto queue config file
	tmp=`echo [${QUEUESLOTS}=1] | tr -s ' '`
	queue_slots=${tmp//[[:space:]]/'=1],['}

	# create a new queue configuration file with specified host group, slots
	qconf -sq all.q > ${SGE_ROOT}/${SGE_QUEUENAME}
	sed -i "s/all.q/${SGE_QUEUENAME}/g" ${SGE_ROOT}/${SGE_QUEUENAME}
	sed -i "s/@allhosts/${SGE_QUEUEHOSTLIST}/g" ${SGE_ROOT}/${SGE_QUEUENAME}
	slotsline=`grep -n slots ${SGE_ROOT}/${SGE_QUEUENAME} | cut -d':' -f1`
	sed -i ${slotsline}c"slots                 1,${queue_slots}" ${SGE_ROOT}/${SGE_QUEUENAME}

	# add host group
	qconf -Ahgrp ${SGE_QUEUEHOSTLIST}
	# add queue
	qconf -Aq ${SGE_QUEUENAME}
fi

#++++++++++++++ INCREASE PROGRESS
echo "sge+"

# show new host group list and queue list
echo "New host group list"
qconf -shgrpl
echo "New queue list"
qconf -sql


#++++++++++++++ INCREASE PROGRESS
echo "sge+"
echo "sge-->Complete"
echo "sge->"
echo "SGE Installation Complete"
