#!/bin/sh

args=$@

if [ "`whoami`" = "root" ]
then
	if [  ! -f /etc/redhat-release ] || [ -z "`cat /etc/redhat-release | grep CentOS`" ]
	then
		echo ""
		echo "     WARNING: Pipeline Server Installer is designed to run on Linux CentOS 5.4+"
		echo ""
		echo "     Using this installer on this OS may DAMAGE YOUR SYSTEM."
		echo ""
		echo -n "     Are you sure you want to start the installer anyway [y/n] ?"
	        read yesno
		
		if [ "$yesno" = "y" ] || [ "$yesno" = "yes" ]
		then
			echo "Launching the installer..."
		else
			exit 1
		fi
	fi

        if [ $# -ne 0 ]
        then
            printusage=false
            if [ $# -ne 2 ]
            then
                printusage=true
            fi

            if [ $1 != "-auto" ]
            then
                printusage=true
            fi

            if [ "$printusage" == "true" ]
            then
                echo "Usage: ./launchInstaller.sh [-auto installPreferences.xml]"
                echo "Running the script without any arguments will launch the GUI."
                echo "The optional -auto flag, followed by a valid installation preferences"
                echo "file will start the installation in automatic, headless mode."
                exit 1
            fi

            if [ ! -f $2 ]
            then
                echo "The installation preferences file does not exist."
                echo "Please launch the installer again with a valid file."
                exit 1
            else
                # check if the user has specified a relative path for the config file
                firstchar=${2:0:1}
                if [ "$firstchar" != "/" ]
                then
                    args="$1 `pwd`/$2"
                fi
            fi
        fi


	which java > /dev/null 2> /dev/null
	if [ $? != 0 ] 
	then
		echo "Missing JAVA"
		echo "The installer requires Oracle JAVA. Please follow these instructions to install Oracle JAVA, then launch the installer again."
		echo "1. Visit the following URL: http://www.oracle.com/technetwork/java/javase/downloads/index.html"
                echo "2. Click on Download JDK button."
                echo "3. Click on jdk-[ver]-linux-[platform].rpm link to begin download."
                echo "4. Run rpm --prefix=INSTALL_DIR -ivh RPM_FILE_PATH"
                echo "5. Run /usr/sbin/alternatives --install /usr/bin/java java INSTALL_DIR/JAVA_DIR/bin/java 2"
                echo "6. Run /usr/sbin/update-alternatives --set java INSTALL_DIR/JAVA_DIR/bin/java"
                exit 3
	fi

        which make > /dev/null 2> /dev/null
        if [ $? != 0 ]
        then
                echo "Missing make"
                echo "The installer requires GNU make. It will install make before proceeding with the rest of the installation."
                yum -y install make
        fi

        # create backup files
        cp /etc/sudoers /etc/profile /etc/csh.login /etc/services /tmp
        echo yes | cp --backup=numbered /tmp/sudoers /etc
        echo yes | cp --backup=numbered /tmp/profile /etc
        echo yes | cp --backup=numbered /tmp/csh.login /etc
        echo yes | cp --backup=numbered /tmp/services /etc

	cd dist
	java -Xmx512m -jar -Djava.awt.headless=false PipelineServerInstaller.jar $args
	
else
	echo "Permission denied: You have to be root to launch the Pipeline Installer"
	exit 1
fi