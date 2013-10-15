#!/bin/sh

if [ -z "$INSTALL_NI_TOOLS" ]
then
    echo "Please do not run this script manually. Run launchInstaller.sh instead."
    exit 1
fi


DL_DIR=$(cd `dirname $0` && pwd)

if [ "$INSTALLER_MODE_MANUAL_TOOL" = "true" ]
then
    rm -Rf ${TOOLS_PATH}/${MANUALTOOL_NAME}-${MANUALTOOL_VERSION} || exit 1

    if [ "$MANUALTOOL_NAME" = "FSL" ] || [ "$MANUALTOOL_NAME" = "FreeSurfer" ] || [ "$MANUALTOOL_NAME" = "DTK" ] || [ "$MANUALTOOL_NAME" = "BrainSuite" ] 
    then
        if [ "$MANUALTOOL_NAME" != "$MANUALTOOL_ARCHIVE_PATH" ] && [ "$INSTALL_NI_EXECUTABLES" = "true" ]
        then
            echo "nitools-->Installing: "
            echo "nitools->${MANUALTOOL_NAME} v.${MANUALTOOL_VERSION}"
            sudo -u $PL_USER tar xmvzf $MANUALTOOL_ARCHIVE_PATH

            echo "nitools+=7"

            toolname_lowercase=`echo $MANUALTOOL_NAME | awk '{print tolower($0)}'`
            if [ "$MANUALTOOL_NAME" = "BrainSuite" ]
            then
                toolname_lowercase=${toolname_lowercase}${MANUALTOOL_VERSION}
            fi

            sudo -u $PL_USER mv $toolname_lowercase $TOOLS_PATH/ || exit 1
            sudo -u $PL_USER mv $TOOLS_PATH/$toolname_lowercase $TOOLS_PATH/${MANUALTOOL_NAME}-${MANUALTOOL_VERSION} || exit 1
            sudo -u $PL_USER ln -s $TOOLS_PATH/${MANUALTOOL_NAME}-${MANUALTOOL_VERSION} $TOOLS_PATH/${MANUALTOOL_NAME} || exit 1

            # download and install glue
            cd $TOOLS_PATH/${MANUALTOOL_NAME}-${MANUALTOOL_VERSION}
            wget -c --progress=dot http://users.loni.ucla.edu/~pipeline/dps/${toolname_lowercase}.tar.gz
            if [ $? -ne 0 ]; then
 	        echo "nitools!Failed to download ${toolname_lowercase} binaries"
 	        exit 1
            fi
            sudo -u $PL_USER tar xmvzf ${toolname_lowercase}.tar.gz
            sudo -u $PL_USER rm ${toolname_lowercase}.tar.gz
            echo "nitools+=2"

            if [ ! -z "$MANUALTOOL_LICENSE_PATH" ]
            then
                cp $MANUALTOOL_LICENSE_PATH ${TOOLS_PATH}/${MANUALTOOL_NAME}-${MANUALTOOL_VERSION} || exit 1
            fi
        fi
    fi
else
    AT_LEAST_ONE_TOOL="false"

    if  [ "$INSTALL_AIR" = "true" ]
    then
	  AT_LEAST_ONE_TOOL="true"

        if [ "$INSTALL_NI_EXECUTABLES" = "true" ]
        then
            echo "nitools=>13"
	  fi

        if [ "$INSTALL_NI_SERVERLIB" = "true" ]
        then
            echo "nitools=>7"
        fi
    fi

    if  [ "$INSTALL_BRAINSUITE" = "true" ]
    then
        AT_LEAST_ONE_TOOL="true"

        if [ "$INSTALL_NI_EXECUTABLES" = "true" ]
        then
            echo "nitools=>9"
        fi

        if [ "$INSTALL_NI_SERVERLIB" = "true" ]
        then
            echo "nitools=>7"
        fi
    fi

    if  [ "$INSTALL_AFNI" = "true" ]
    then
        AT_LEAST_ONE_TOOL="true"

        if [ "$INSTALL_NI_EXECUTABLES" = "true" ]
        then
            echo "nitools=>10"
        fi

        if [ "$INSTALL_NI_SERVERLIB" = "true" ]
        then
            echo "nitools=>7"
        fi
    fi

    if  [ "$INSTALL_FSL" = "true" ]
    then
        AT_LEAST_ONE_TOOL="true"

        if [ "$INSTALL_NI_EXECUTABLES" = "true" ]
        then
            echo "nitools=>9"
        fi

        if [ "$INSTALL_NI_SERVERLIB" = "true" ]
        then
            echo "nitools=>7"
        fi
    fi

    if  [ "$INSTALL_FREESURFER" = "true" ]
    then
        AT_LEAST_ONE_TOOL="true"

        if [ "$INSTALL_NI_EXECUTABLES" = "true" ]
        then
            echo "nitools=>9"
        fi

        if [ "$INSTALL_NI_SERVERLIB" = "true" ]
        then
            echo "nitools=>7"
        fi
    fi

    if  [ "$INSTALL_LONITOOLS" = "true" ]
    then
        AT_LEAST_ONE_TOOL="true"

        if [ "$INSTALL_NI_EXECUTABLES" = "true" ]
        then
            echo "nitools=>130"
        fi

        if [ "$INSTALL_NI_SERVERLIB" = "true" ]
        then
            echo "nitools=>7"
        fi
    fi

    if  [ "$INSTALL_MINC" = "true" ]
    then
        AT_LEAST_ONE_TOOL="true"

        if [ "$INSTALL_NI_EXECUTABLES" = "true" ]
        then
            echo "nitools=>8"
        fi

        if [ "$INSTALL_NI_SERVERLIB" = "true" ]
        then
            echo "nitools=>7"
        fi
    fi

    if  [ "$INSTALL_ITK" = "true" ]
    then
        AT_LEAST_ONE_TOOL="true"

        if [ "$INSTALL_NI_EXECUTABLES" = "true" ]
        then
            echo "nitools=>20"
        fi

        if [ "$INSTALL_NI_SERVERLIB" = "true" ]
        then
            echo "nitools=>7"
        fi
    fi

    if  [ "$INSTALL_GAMMA" = "true" ]
    then
        AT_LEAST_ONE_TOOL="true"

        if [ "$INSTALL_NI_EXECUTABLES" = "true" ]
        then
            echo "nitools=>10"
        fi

        if [ "$INSTALL_NI_SERVERLIB" = "true" ]
        then
            echo "nitools=>7"
        fi
    fi

    if  [ "$INSTALL_DTK" = "true" ]
    then
        AT_LEAST_ONE_TOOL="true"

        if [ "$INSTALL_NI_EXECUTABLES" = "true" ]
        then
            echo "nitools=>9"
        fi

        if [ "$INSTALL_NI_SERVERLIB" = "true" ]
        then
            echo "nitools=>7"
        fi
    fi

    if [ $AT_LEAST_ONE_TOOL = "false" ]
    then
        echo "nitools=>0"
    fi

    if [ ! -f "$TOOLS_PATH" ]
    then
        mkdir -p $TOOLS_PATH || exit 1
        chown $PL_USER $TOOLS_PATH
    fi

    cd $DL_DIR

    ################################# FSL 7 steps
    if  [ "$INSTALL_FSL" = "true" ]
    then
        if [ "$INSTALL_NI_EXECUTABLES" = "true" ]
        then
            ######################################
            #######     INSTALLATION OF FSL
            ######################################
            if [ ! -z "$FSL_ARCHIVE_LOCATION" ]
            then
                sudo -u $PL_USER rm -Rf ${TOOLS_PATH}/FSL-${FSL_VERSION} || exit 1
                sudo -u $PL_USER tar xmvzf $FSL_ARCHIVE_LOCATION
                sudo -u $PL_USER mv fsl $TOOLS_PATH/FSL-${FSL_VERSION} || exit 1
                sudo -u $PL_USER ln -s $TOOLS_PATH/FSL-${FSL_VERSION} $TOOLS_PATH/FSL || exit 1

                # download and install glue
                cd $TOOLS_PATH/FSL-${FSL_VERSION}
                wget -c --progress=dot http://users.loni.ucla.edu/~pipeline/dps/fsl.tar.gz
                if [ $? -ne 0 ]; then
                    echo "nitools!Failed to download fsl glue"
                    exit 1
                fi
                sudo -u $PL_USER tar xmvzf fsl.tar.gz
                sudo -u $PL_USER rm fsl.tar.gz
            else
                echo 'nitools@FSL@http://www.fmrib.ox.ac.uk/fsl/fsl/downloading.html@1. Click on Download FSL link<br>2. Click "I agree to the below terms and conditions governing the use of the Software" link<br>3. Fill your personal information<br>4. Select the "Linux CentOS5 64-bit (CentOS5-64, Fedora6->Fedora10, WindowsVM-64bit)" <br>5. Press Download button<br>'
            fi
        fi

        if [ "$INSTALL_NI_SERVERLIB" = "true" ]
        then
            if [ ! -d "$PL_SERVERLIB" ]
            then
                sudo -u $PL_USER mkdir -p $PL_SERVERLIB || exit 1
            fi

            wget -c --progress=dot http://users.loni.ucla.edu/~pipeline/dps/serverLib_FSL.tar.gz
            if [ $? -ne 0 ]; then
 	        echo "nitools!Failed to download FSL server library"
 	        exit 1
            fi

            echo "nitools+=3"
            sudo -u $PL_USER cp serverLib_FSL.tar.gz ${PL_SERVERLIB}/ || exit 1
            echo "nitools+"
            cd ${PL_SERVERLIB}
            sudo -u $PL_USER tar -xmzvf serverLib_FSL.tar.gz || exit 1
            if [ ! -z "${FSL_VERSION}" ]
            then
                sudo -u $PL_USER sed -i "s/FSLVERSION/${FSL_VERSION}/g" ${PL_SERVERLIB}/*/*/*pipe
            fi
            echo "nitools+=2"
            sudo -u $PL_USER rm serverLib_FSL.tar.gz || exit 1
            echo "nitools+"
            sudo -u $PL_USER touch ${PL_SERVERLIB}/.monitorFile || exit 1
        fi
        cd $DL_DIR || exit 1
    fi

    ################################# FreeSurfer 7 steps
    if  [ "$INSTALL_FREESURFER" = "true" ]
    then
        if [ "$INSTALL_NI_EXECUTABLES" = "true" ]
        then
            ######################################
            #######     INSTALLATION OF FREESURFER
            ######################################
            if [ ! -z "$FREESURFER_ARCHIVE_LOCATION" ]
            then
                sudo -u $PL_USER rm -Rf ${TOOLS_PATH}/FREESURFER-${FREESURFER_VERSION} || exit 1
                sudo -u $PL_USER tar xmvzf $FREESURFER_ARCHIVE_LOCATION
                sudo -u $PL_USER mv freesurfer $TOOLS_PATH/FREESURFER-${FREESURFER_VERSION} || exit 1
                sudo -u $PL_USER ln -s $TOOLS_PATH/FREESURFER-${FREESURFER_VERSION} $TOOLS_PATH/FREESURFER || exit 1

                # download and install glue
                cd $TOOLS_PATH/FREESURFER-${FREESURFER_VERSION}
                wget -c --progress=dot http://users.loni.ucla.edu/~pipeline/dps/freesurfer.tar.gz
                if [ $? -ne 0 ]; then
                    echo "nitools!Failed to download freesurfer glue"
                    exit 1
                fi
                sudo -u $PL_USER tar xmvzf freesurfer.tar.gz
                sudo -u $PL_USER rm freesurfer.tar.gz

                if [ ! -z "$FREESURFER_LICENSE_LOCATION" ]
                then
                    cp $FREESURFER_LICENSE_LOCATION ${TOOLS_PATH}/FREESURFER-${FREESURFER_VERSION} || exit 1
                fi
            else
                echo 'nitools@FreeSurfer@http://surfer.nmr.mgh.harvard.edu/fswiki/Download@1. Click on freesurfer-Linux-centos4_x86_64-stable-pub-v5.0.0.tar.gz link<br>2. Note that a license key file is necessary to run the FreeSurfer binaries;<br>in the next step, you will be prompted to enter the location of the license file<br>@https://surfer.nmr.mgh.harvard.edu/registration.html@3. Fill out the form on the FreeSurfer registration page<br>(make sure to select the proper Operating System/Platform)<br>4. Press the "I AGREE" button at the bottom of the page<br>5. You will receive an email with the license information;<br>please follow the instructions in the email to create the .license file<br>'
            fi
        fi

        if [ "$INSTALL_NI_SERVERLIB" = "true" ]
        then
            if [ ! -d "$PL_SERVERLIB" ]
            then
                sudo -u $PL_USER mkdir -p $PL_SERVERLIB || exit 1
            fi

            wget -c --progress=dot http://users.loni.ucla.edu/~pipeline/dps/serverLib_FreeSurfer.tar.gz
            if [ $? -ne 0 ]; then
 	        echo "nitools!Failed to download FreeSurfer server library"
 	        exit 1
            fi

            echo "nitools+=3"
            sudo -u $PL_USER cp serverLib_FreeSurfer.tar.gz ${PL_SERVERLIB}/ || exit 1
            echo "nitools+"
            cd ${PL_SERVERLIB}
            sudo -u $PL_USER tar -xmzvf serverLib_FreeSurfer.tar.gz || exit 1
            if [ ! -z "${FREESURFER_VERSION}" ]
            then
                sudo -u $PL_USER sed -i "s/FREESURFERVERSION/${FREESURFER_VERSION}/g" ${PL_SERVERLIB}/*/*/*pipe
            fi
            echo "nitools+=2"
            sudo -u $PL_USER rm serverLib_FreeSurfer.tar.gz || exit 1
            echo "nitools+"
            sudo -u $PL_USER touch ${PL_SERVERLIB}/.monitorFile || exit 1
        fi
        cd $DL_DIR || exit 1
    fi

    ################################# DTK 7 steps
    if  [ "$INSTALL_DTK" = "true" ]
    then
        if [ "$INSTALL_NI_EXECUTABLES" = "true" ]
        then
            #############################################
            #######     INSTALLATION OF DIFFUSION TOOLKIT
            #############################################
            if [ ! -z "$DTK_ARCHIVE_LOCATION" ]
            then
                sudo -u $PL_USER rm -Rf ${TOOLS_PATH}/DTK-${DTK_VERSION} || exit 1
                sudo -u $PL_USER tar xmvzf $DTK_ARCHIVE_LOCATION
                sudo -u $PL_USER mv dtk $TOOLS_PATH/DTK-${DTK_VERSION} || exit 1
                sudo -u $PL_USER ln -s $TOOLS_PATH/DTK-${DTK_VERSION} $TOOLS_PATH/DTK || exit 1

                # download and install glue
                cd $TOOLS_PATH/DTK-${DTK_VERSION}
                wget -c --progress=dot http://users.loni.ucla.edu/~pipeline/dps/dtk.tar.gz
                if [ $? -ne 0 ]; then
                    echo "nitools!Failed to download diffusion toolkit glue"
                    exit 1
                fi
                sudo -u $PL_USER tar xmvzf dtk.tar.gz
                sudo -u $PL_USER rm dtk.tar.gz
            else
                echo 'nitools@DTK@http://trackvis.org/download/@1. Sign up or, if you have already registered, log in.<br>2. Select "Linux (64-bit)" as the platform from the drop down menu.<br>3. Press Download button.<br>'
            fi
        fi

        if [ "$INSTALL_NI_SERVERLIB" = "true" ]
        then
            if [ ! -d "$PL_SERVERLIB" ]
            then
                sudo -u $PL_USER mkdir -p $PL_SERVERLIB || exit 1
            fi

            wget -c --progress=dot http://users.loni.ucla.edu/~pipeline/dps/serverLib_DTK.tar.gz
            if [ $? -ne 0 ]; then
 	        echo "nitools!Failed to download DTK server library"
 	        exit 1
            fi

            echo "nitools+=3"
            sudo -u $PL_USER cp serverLib_DTK.tar.gz ${PL_SERVERLIB}/ || exit 1
            echo "nitools+"
            cd ${PL_SERVERLIB}
            sudo -u $PL_USER tar xmzvf serverLib_DTK.tar.gz || exit 1
            if [ ! -z "${DTK_VERSION}" ]
            then
                sudo -u $PL_USER sed -i "s/DTKVERSION/${DTK_VERSION}/g" ${PL_SERVERLIB}/*/*/*pipe
            fi
            echo "nitools+=2"
            sudo -u $PL_USER rm serverLib_DTK.tar.gz || exit 1
            echo "nitools+"
            sudo -u $PL_USER touch ${PL_SERVERLIB}/.monitorFile || exit 1
        fi
        cd $DL_DIR || exit 1
    fi


    ################################# BrainSuite 7 steps
    if  [ "$INSTALL_BRAINSUITE" = "true" ]
    then
        if [ "$INSTALL_NI_EXECUTABLES" = "true" ]
        then
            ######################################
            #######     INSTALLATION OF BRAINSUITE
            ######################################
            if [ ! -z "$BRAINSUITE_ARCHIVE_LOCATION" ]
            then
                sudo -u $PL_USER rm -Rf ${TOOLS_PATH}/BRAINSUITE-${BRAINSUITE_VERSION} || exit 1
                sudo -u $PL_USER tar xmvzf $BRAINSUITE_ARCHIVE_LOCATION
                sudo -u $PL_USER mv brainsuite11a $TOOLS_PATH/BRAINSUITE-${BRAINSUITE_VERSION} || exit 1
                sudo -u $PL_USER ln -s $TOOLS_PATH/BRAINSUITE-${BRAINSUITE_VERSION} $TOOLS_PATH/BRAINSUITE || exit 1

                # download and install glue
                cd $TOOLS_PATH/BRAINSUITE-${BRAINSUITE_VERSION}
                wget -c --progress=dot http://users.loni.ucla.edu/~pipeline/dps/brainsuite11a.tar.gz
                if [ $? -ne 0 ]; then
                    echo "nitools!Failed to download brainsuite glue"
                    exit 1
                fi
                sudo -u $PL_USER tar xmvzf brainsuite11a.tar.gz
                sudo -u $PL_USER rm brainsuite11a.tar.gz
            else
                echo 'nitools@BrainSuite@http://www.loni.ucla.edu/Software/BrainSuite@1. Click gray Download button.<br>2. Sign in, or if you are a new user, create an account.<br>3. Read the software license agreement, select "I Agree", and press submit button.<br>4. Select "11a (64 bit) - Linux, released: 2011-05-01" as the platform (if your server is 32-bit,<br>select the appropriate version) and press CONTINUE to download the archive.<br>'
            fi
        fi

        cd $DL_DIR || exit 1

        if [ "$INSTALL_NI_SERVERLIB" = "true" ]
        then
            if [ ! -d "$PL_SERVERLIB" ]
            then
                sudo -u $PL_USER mkdir -p $PL_SERVERLIB || exit 1
            fi

            wget -c --progress=dot http://users.loni.ucla.edu/~pipeline/dps/serverLib_BrainSuite.tar.gz
            if [ $? -ne 0 ]; then
 	        echo "nitools!Failed to download brainsuite server library"
 	        exit 1
            fi

            echo "nitools+=3"
            sudo -u $PL_USER cp serverLib_BrainSuite.tar.gz ${PL_SERVERLIB}/ || exit 1
            echo "nitools+"
            cd ${PL_SERVERLIB} || exit 1
            sudo -u $PL_USER tar -xmzvf serverLib_BrainSuite.tar.gz || exit 1
            echo "nitools+=2"
            sudo -u $PL_USER rm serverLib_BrainSuite.tar.gz || exit 1
            echo "nitools+"
            sudo -u $PL_USER touch ${PL_SERVERLIB}/.monitorFile || exit 1

            cd $DL_DIR || exit 1
        fi
    fi

    ################################# AIR 13 steps
    if  [ "$INSTALL_AIR" = "true" ]
    then
        if [ "$INSTALL_NI_EXECUTABLES" = "true" ]
        then
            echo "nitools-->Downloading: "
            echo "nitools->AIR v.${AIR_VERSION}"
            ######################################
            #######     INSTALLATION OF AIR
            ######################################

            rm -Rf ${TOOLS_PATH}/AIR-${AIR_VERSION} || exit 1

            sudo -u $PL_USER mkdir -p $TOOLS_PATH/AIR-${AIR_VERSION}/ || exit 1
            sudo -u $PL_USER mkdir -p $TOOLS_PATH/AIR-${AIR_VERSION}/8bit || exit 1
            sudo -u $PL_USER mkdir -p $TOOLS_PATH/AIR-${AIR_VERSION}/16bit || exit 1
            echo "nitools+"

            wget -c --progress=dot http://users.loni.ucla.edu/~pipeline/dps/air-${AIR_VERSION}_64_8.tar.gz
            if [ $? -ne 0 ]; then
 	        echo "nitools!Failed to download AIR binaries"
 	        exit 1
            fi

            echo "nitools+=4"

            wget -c --progress=dot http://users.loni.ucla.edu/~pipeline/dps/air-${AIR_VERSION}_64_16.tar.gz
            if [ $? -ne 0 ]; then
 	        echo "nitools!Failed to download AIR binaries"
 	        exit 1
            fi

            echo "nitools+=4"
        
            echo "nitools-->Installing: "
            echo "nitools->AIR v.${AIR_VERSION}"
        
            cp air-${AIR_VERSION}_64_8.tar.gz $TOOLS_PATH/AIR-${AIR_VERSION}/8bit/ || exit 1
            cp air-${AIR_VERSION}_64_16.tar.gz $TOOLS_PATH/AIR-${AIR_VERSION}/16bit/ || exit 1
        
            cd $TOOLS_PATH/AIR-${AIR_VERSION}/8bit || exit 1

            tar xmvzf air-${AIR_VERSION}_64_8.tar.gz
            rm -f air-${AIR_VERSION}_64_8.tar.gz || exit 1
            echo "nitools+=2"

            cd $TOOLS_PATH/AIR-${AIR_VERSION}/16bit || exit 1
            tar xmvzf air-${AIR_VERSION}_64_16.tar.gz
            rm -f air-${AIR_VERSION}_64_16.tar.gz || exit 1
            echo "nitools+=2"
        fi

        cd $DL_DIR || exit 1

        if [ "$INSTALL_NI_SERVERLIB" = "true" ]
        then
            if [ ! -d "$PL_SERVERLIB" ]
            then
                sudo -u $PL_USER mkdir -p $PL_SERVERLIB || exit 1
            fi

            wget -c --progress=dot http://users.loni.ucla.edu/~pipeline/dps/serverLib_AIR.tar.gz
            if [ $? -ne 0 ]; then
 	        echo "nitools!Failed to download AIR server library"
 	        exit 1
            fi

            echo "nitools+=3"
            sudo -u $PL_USER cp serverLib_AIR.tar.gz ${PL_SERVERLIB}/ || exit 1
            echo "nitools+"
            cd $PL_SERVERLIB || exit 1
            sudo -u $PL_USER tar -xmzvf serverLib_AIR.tar.gz || exit 1
            echo "nitools+=2"
            sudo -u $PL_USER rm serverLib_AIR.tar.gz || exit 1
            echo "nitools+"
            sudo -u $PL_USER touch ${PL_SERVERLIB}/.monitorFile || exit 1
            cd $DL_DIR || exit 1
        fi
  
    fi

    ################################# AFNI 10 steps
    if  [ "$INSTALL_AFNI" = "true" ]
    then
        if [ "$INSTALL_NI_EXECUTABLES" = "true" ]
        then
            echo "nitools-->Installing: "
            echo "nitools->libXp for AFNI"

            yum -y install libXp
            echo "nitools+=2"

            echo "nitools-->Downloading: "
            echo "nitools->AFNI v.${AFNI_VERSION}"

            wget -c --progress=dot http://users.loni.ucla.edu/~pipeline/dps/afni-${AFNI_VERSION}_64bit.tar.gz
            if [ $? -ne 0 ]; then
 	        echo "nitools!Failed to download AFNI binaries"
 	        exit 1
            fi
        
            echo "nitools+=4"


            echo "nitools-->Installing: "
            echo "nitools->AFNI v.${AFNI_VERSION}"

            sudo -u $PL_USER mkdir -p $TOOLS_PATH/AFNI-$AFNI_VERSION || exit 1

            cp afni-${AFNI_VERSION}_64bit.tar.gz $TOOLS_PATH/AFNI-${AFNI_VERSION} || exit 1
            echo "nitools+"
            cd $TOOLS_PATH/AFNI-${AFNI_VERSION} || exit 1

            tar xmvzf afni-${AFNI_VERSION}_64bit.tar.gz
            echo "nitools+=2"
            rm -f afni-${AFNI_VERSION}_64bit.tar.gz || exit 1
            echo "nitools+"
        fi

        cd $DL_DIR || exit 1

        if [ "$INSTALL_NI_SERVERLIB" = "true" ]
        then
            if [ ! -d "$PL_SERVERLIB" ]
            then
                sudo -u $PL_USER mkdir -p $PL_SERVERLIB || exit 1
            fi

            wget -c --progress=dot http://users.loni.ucla.edu/~pipeline/dps/serverLib_AFNI.tar.gz
            if [ $? -ne 0 ]; then
 	        echo "nitools!Failed to download AFNI server library"
 	        exit 1
            fi

            echo "nitools+=3"
            sudo -u $PL_USER cp serverLib_AFNI.tar.gz ${PL_SERVERLIB}/ || exit 1
            echo "nitools+"
            cd ${PL_SERVERLIB} || exit 1
            sudo -u $PL_USER tar -xmzvf serverLib_AFNI.tar.gz || exit 1
            echo "nitools+=2"
            sudo -u $PL_USER rm serverLib_AFNI.tar.gz || exit 1
            echo "nitools+"
            sudo -u $PL_USER touch ${PL_SERVERLIB}/.monitorFile || exit 1

            cd $DL_DIR || exit 1
        fi
    fi

    ################################# MINC 6 steps
    if  [ "$INSTALL_MINC" = "true" ]
    then
        if [ "$INSTALL_NI_EXECUTABLES" = "true" ]
        then
            echo "nitools-->Downloading: "
            echo "nitools->MINC v.${MINC_VERSION}"

            wget -c --progress=dot http://users.loni.ucla.edu/~pipeline/dps/minc-${MINC_VERSION}_64bit.tar.gz
            if [ $? -ne 0 ]; then
 	        echo "nitools!Failed to download MINC binaries"
 	        exit 1
            fi

            echo "nitools+=4"

            echo "nitools-->Installing: "
            echo "nitools->MINC v.${MINC_VERSION}"

            sudo -u $PL_USER mkdir -p $TOOLS_PATH/MINC-$MINC_VERSION || exit 1

            cp minc-${MINC_VERSION}_64bit.tar.gz $TOOLS_PATH/MINC-${MINC_VERSION} || exit 1
            echo "nitools+"
            cd $TOOLS_PATH/MINC-${MINC_VERSION} || exit 1

            tar xmvzf minc-${MINC_VERSION}_64bit.tar.gz
            echo "nitools+=2"
            rm -f minc-${MINC_VERSION}_64bit.tar.gz || exit 1
            echo "nitools+"
        fi

        cd $DL_DIR || exit 1

        if [ "$INSTALL_NI_SERVERLIB" = "true" ]
        then
            if [ ! -d "$PL_SERVERLIB" ]
            then
                sudo -u $PL_USER mkdir -p $PL_SERVERLIB || exit 1
            fi

            wget -c --progress=dot http://users.loni.ucla.edu/~pipeline/dps/serverLib_MINC.tar.gz
            if [ $? -ne 0 ]; then
 	        echo "nitools!Failed to download MINC server library"
 	        exit 1
            fi

            echo "nitools+=3"
            sudo -u $PL_USER cp serverLib_MINC.tar.gz ${PL_SERVERLIB}/ || exit 1
            echo "nitools+"
            cd ${PL_SERVERLIB} || exit 1
            sudo -u $PL_USER tar -xmzvf serverLib_MINC.tar.gz || exit 1
            echo "nitools+=2"
            sudo -u $PL_USER rm serverLib_MINC.tar.gz || exit 1
            echo "nitools+"
            sudo -u $PL_USER touch ${PL_SERVERLIB}/.monitorFile || exit 1

            cd $DL_DIR || exit 1
        fi
    fi

    ################################# ITK 20 steps
    if  [ "$INSTALL_ITK" = "true" ]
    then
        if [ "$INSTALL_NI_EXECUTABLES" = "true" ]
        then
            echo "nitools-->Downloading: "
            echo "nitools->ITK v.${ITK_VERSION}"

            wget -c --progress=dot http://users.loni.ucla.edu/~pipeline/dps/itk-${ITK_VERSION}.tar.gz
            if [ $? -ne 0 ]; then
 	        echo "nitools!Failed to download ITK binaries"
 	        exit 1
            fi

            echo "nitools+=10"

            echo "nitools-->Installing: "
            echo "nitools->ITK v.${ITK_VERSION}"

            cp itk-${ITK_VERSION}.tar.gz $TOOLS_PATH || exit 1
            echo "nitools+=4"
            cd $TOOLS_PATH || exit 1

            tar xmvzf itk-${ITK_VERSION}.tar.gz
            echo "nitools+=4"
            rm -f itk-${ITK_VERSION}.tar.gz || exit 1
            echo "nitools+=2"
        fi

        cd $DL_DIR || exit 1

        if [ "$INSTALL_NI_SERVERLIB" = "true" ]
        then
            if [ ! -d "$PL_SERVERLIB" ]
            then
                sudo -u $PL_USER mkdir -p $PL_SERVERLIB || exit 1
            fi

            wget -c --progress=dot http://users.loni.ucla.edu/~pipeline/dps/serverLib_ITK.tar.gz
            if [ $? -ne 0 ]; then
 	        echo "nitools!Failed to download ITK server library"
 	        exit 1
            fi

            echo "nitools+=3"
            sudo -u $PL_USER cp serverLib_ITK.tar.gz ${PL_SERVERLIB}/ || exit 1
            echo "nitools+"
            cd ${PL_SERVERLIB} || exit 1
            sudo -u $PL_USER tar -xmzvf serverLib_ITK.tar.gz || exit 1
            echo "nitools+=2"
            sudo -u $PL_USER rm serverLib_ITK.tar.gz || exit 1
            echo "nitools+"
            sudo -u $PL_USER touch ${PL_SERVERLIB}/.monitorFile || exit 1

            cd $DL_DIR || exit 1
        fi
    fi

    ################################# GAMMA 10 steps
    if  [ "$INSTALL_GAMMA" = "true" ]
    then
        if [ "$INSTALL_NI_EXECUTABLES" = "true" ]
        then
            echo "nitools-->Downloading: "
            echo "nitools->GAMMA v.${GAMMA_VERSION}"

            wget -c --progress=dot http://users.loni.ucla.edu/~pipeline/dps/gamma-${GAMMA_VERSION}_64bit.tar.gz
            if [ $? -ne 0 ]; then
 	        echo "nitools!Failed to download GAMMA binaries"
 	        exit 1
            fi

            echo "nitools+=5"

            echo "nitools-->Installing: "
            echo "nitools->GAMMA v.${GAMMA_VERSION}"

            cp gamma-${GAMMA_VERSION}_64bit.tar.gz $TOOLS_PATH || exit 1
            echo "nitools+=3"
            cd $TOOLS_PATH || exit 1

            tar xmvzf gamma-${GAMMA_VERSION}_64bit.tar.gz
            echo "nitools+"
            rm -f gamma-${GAMMA_VERSION}_64bit.tar.gz || exit 1
            echo "nitools+"
        fi

        cd $DL_DIR || exit 1

        if [ "$INSTALL_NI_SERVERLIB" = "true" ]
        then
            if [ ! -d "$PL_SERVERLIB" ]
            then
                sudo -u $PL_USER mkdir -p $PL_SERVERLIB || exit 1
            fi

            wget -c --progress=dot http://users.loni.ucla.edu/~pipeline/dps/serverLib_GAMMA.tar.gz
            if [ $? -ne 0 ]; then
 	        echo "nitools!Failed to download GAMMA server library"
 	        exit 1
            fi

            echo "nitools+=3"
            sudo -u $PL_USER cp serverLib_GAMMA.tar.gz ${PL_SERVERLIB}/ || exit 1
            echo "nitools+"
            cd ${PL_SERVERLIB} || exit 1
            sudo -u $PL_USER tar -xmzvf serverLib_GAMMA.tar.gz || exit 1
            echo "nitools+=2"
            sudo -u $PL_USER rm serverLib_GAMMA.tar.gz || exit 1
            echo "nitools+"
            sudo -u $PL_USER touch ${PL_SERVERLIB}/.monitorFile || exit 1

            cd $DL_DIR || exit 1
        fi
    fi

    ################################# LONI TOOLS 130 steps
    if  [ "$INSTALL_LONITOOLS" = "true" ]
    then
        if [ "$INSTALL_NI_EXECUTABLES" = "true" ]
        then
            echo "nitools-->Cleaning previous installations of LONI Tools..."
            echo "nitools->"
            ######################################
            #######     INSTALLATION OF LONI TOOLS
            ######################################
            rm -Rf ${TOOLS_PATH}/LONI || exit 1

            sudo -u $PL_USER mkdir -p $TOOLS_PATH/LONI || exit 1
            sudo -u $PL_USER mkdir -p $TOOLS_PATH/LONI/data || exit 1
            sudo -u $PL_USER mkdir -p $TOOLS_PATH/LONI/scripts || exit 1
            sudo -u $PL_USER mkdir -p $TOOLS_PATH/LONI/apps || exit 1
            sudo -u $PL_USER mkdir -p $TOOLS_PATH/LONI/workflows || exit 1
            sudo -u $PL_USER mkdir -p $TOOLS_PATH/LONI/jars || exit 1
        
            echo "nitools+"
            echo "nitools-->Downloading: "
            echo "nitools->Jar files of LONI Tools [ 1/5 ]"

            wget -c --progress=dot http://users.loni.ucla.edu/~pipeline/dps/loniJars.tar.gz
            if [ $? -ne 0 ]; then
 	        echo "nitools!Failed to download LONI jars"
 	        exit 1
            fi

            echo "nitools+=3"
            echo "nitools-->Downloading: "
            echo "nitools->Scripts of LONI Tools [ 2/5 ]"

            wget -c --progress=dot http://users.loni.ucla.edu/~pipeline/dps/loniScripts.tar.gz
            if [ $? -ne 0 ]; then
 	        echo "nitools!Failed to download LONI scripts"
 	        exit 1
            fi
        
            echo "nitools+"
            echo "nitools-->Downloading: "
            echo "nitools->Apps of LONI Tools [ 3/5 ]"

            wget -c --progress=dot http://users.loni.ucla.edu/~pipeline/dps/loniApps.tar.gz
            if [ $? -ne 0 ]; then
 	        echo "nitools!Failed to download LONI apps"
 	        exit 1
            fi
        
            echo "nitools+=10"
            echo "nitools-->Downloading: "
            echo "nitools->Workflows of LONI Tools [ 4/5 ]"

            wget -c --progress=dot http://users.loni.ucla.edu/~pipeline/dps/loniWorkflows.tar.gz
            if [ $? -ne 0 ]; then
 	        echo "nitools!Failed to download brainsuite LONI workflows"
 	        exit 1
            fi
        
            echo "nitools+=15"
            echo "nitools-->Downloading: "
            echo "nitools->Data files of LONI Tools [ 5/5 ]"

            wget -c --progress=dot http://users.loni.ucla.edu/~pipeline/dps/loniData.tar.gz
            if [ $? -ne 0 ]; then
 	        echo "nitools!Failed to download LONI data"
 	        exit 1
            fi

            echo "nitools+=40"

            cd $TOOLS_PATH/LONI || exit 1

            echo "nitools-->Installing: "
            echo "nitools->[1/5] JAR files of LONI Tools"

            cd jars || exit 1
            cp $DL_DIR/loniJars.tar.gz ./loniJars.tar.gz || exit 1
            echo "nitools+=2"
            tar xmvzf loniJars.tar.gz
            rm -f loniJars.tar.gz || exit 1
            echo "nitools+=3"

            echo "nitools-->Installing: "
            echo "nitools->[2/5] Scripts of LONI Tools"
            cd ../scripts || exit 1
            cp $DL_DIR/loniScripts.tar.gz ./loniScripts.tar.gz || exit 1
            echo "nitools+=2"
            tar xmvzf loniScripts.tar.gz
            rm -f loniScripts.tar.gz || exit 1
            echo "nitools+"

            echo "nitools-->Installing: "
            echo "nitools->[3/5] Apps of LONI Tools"
            cd ../apps || exit 1
            cp $DL_DIR/loniApps.tar.gz ./loniApps.tar.gz || exit 1
            echo "nitools+=2"
            tar xmvzf loniApps.tar.gz
            rm -f loniApps.tar.gz || exit 1
            echo "nitools+=6"

            echo "nitools-->Installing: "
            echo "nitools->[4/5] Workflows of LONI Tools"
            cd ../workflows || exit 1
            cp $DL_DIR/loniWorkflows.tar.gz ./loniWorkflows.tar.gz || exit 1
            echo "nitools+=2"
            tar xmvzf loniWorkflows.tar.gz
            rm -f loniWorkflows.tar.gz || exit 1
            echo "nitools+=5"

            echo "nitools-->Installing: "
            echo "nitools->[5/5] Data files of LONI Tools"
            cd ../data || exit 1
            cp $DL_DIR/loniData.tar.gz ./loniData.tar.gz || exit 1
            echo "nitools+=2"
            tar xmvzf loniData.tar.gz
            rm -f loniData.tar.gz || exit 1
            echo "nitools+=35"
        fi

        cd $DL_DIR || exit 1

        if [ "$INSTALL_NI_SERVERLIB" = "true" ]
        then
            if [ ! -d "$PL_SERVERLIB" ]
            then
                sudo -u $PL_USER mkdir -p $PL_SERVERLIB || exit 1
            fi
            
            wget -c --progress=dot http://users.loni.ucla.edu/~pipeline/dps/serverLib_LONI.tar.gz
            if [ $? -ne 0 ]; then
 	        echo "nitools!Failed to download LONI server library"
 	        exit 1
            fi

            echo "nitools+=3"
            sudo -u $PL_USER cp serverLib_LONI.tar.gz ${PL_SERVERLIB}/ || exit 1
            echo "nitools+"
            cd ${PL_SERVERLIB} || exit 1
            sudo -u $PL_USER tar -xmzvf serverLib_LONI.tar.gz || exit 1
            echo "nitools+=2"
            sudo -u $PL_USER rm serverLib_LONI.tar.gz || exit 1
            echo "nitools+"
            sudo -u $PL_USER touch ${PL_SERVERLIB}/.monitorFile || exit 1

            cd $DL_DIR || exit 1
        fi
    fi

fi

# update hostname specified in module definitions
HOSTNAME=${PL_HOSTNAME}
if [ ${PL_PORT} != 8001 ]; then
	HOSTNAME=${PL_HOSTNAME}:${PL_PORT}
fi

if [ -d ${PL_SERVERLIB} ]; then
	for x in `ls ${PL_SERVERLIB}/*/*/*pipe 2> /dev/null`; do
                if [ -f $x ]
                then
		            sed -i "s/cranium\.loni\.ucla\.edu/$HOSTNAME/g" $x || exit 1
                fi
	done
	sudo -u $PL_USER touch ${PL_SERVERLIB}/.monitorFile || exit 1
fi

