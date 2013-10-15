#!/bin/sh

if [ -z "$INSTALL_BI_TOOLS" ]
then
    echo "Please do not run this script manually. Run launchInstaller.sh instead."
    exit 1
fi


DL_DIR=$(cd `dirname $0` && pwd)

if [ "$INSTALLER_MODE_MANUAL_TOOL" = "true" ]
then
    # no manual bioinformatics tools yet
    echo "bitools->No manual bioinformatics tools to install"
else
    AT_LEAST_ONE_TOOL="false"

    if  [ "$INSTALL_BATWING" = "true" ]
    then
        AT_LEAST_ONE_TOOL="true"

        if [ "$INSTALL_BI_EXECUTABLES" = "true" ]
        then
            echo "bitools=>2"
        fi

        if [ "$INSTALL_BI_SERVERLIB" = "true" ]
        then
            echo "bitools=>2"
        fi
    fi

    if  [ "$INSTALL_BAYESASS" = "true" ]
    then
        AT_LEAST_ONE_TOOL="true"

        if [ "$INSTALL_BI_EXECUTABLES" = "true" ]
        then
            echo "bitools=>3"
        fi

        if [ "$INSTALL_BI_SERVERLIB" = "true" ]
        then
            echo "bitools=>2"
        fi
    fi

    if  [ "$INSTALL_BOWTIE" = "true" ]
    then
        AT_LEAST_ONE_TOOL="true"

        if [ "$INSTALL_BI_EXECUTABLES" = "true" ]
        then
            echo "bitools=>2"
        fi
    fi

    if  [ "$INSTALL_EMBOSS" = "true" ]
    then
        AT_LEAST_ONE_TOOL="true"

        if [ "$INSTALL_BI_EXECUTABLES" = "true" ]
        then
            echo "bitools=>7"
        fi

        if [ "$INSTALL_BI_SERVERLIB" = "true" ]
        then
            echo "bitools=>5"
        fi
    fi

    if  [ "$INSTALL_FORMATOMATIC" = "true" ]
    then
        AT_LEAST_ONE_TOOL="true"

        if [ "$INSTALL_BI_EXECUTABLES" = "true" ]
        then
            echo "bitools=>2"
        fi

        if [ "$INSTALL_BI_SERVERLIB" = "true" ]
        then
            echo "bitools=>2"
        fi
    fi

    if  [ "$INSTALL_GENEPOP" = "true" ]
    then
        AT_LEAST_ONE_TOOL="true"

        if [ "$INSTALL_BI_EXECUTABLES" = "true" ]
        then
            echo "bitools=>2"
        fi

        if [ "$INSTALL_BI_SERVERLIB" = "true" ]
        then
            echo "bitools=>2"
        fi
    fi

    if  [ "$INSTALL_GWASS" = "true" ]
    then
        AT_LEAST_ONE_TOOL="true"

        if [ "$INSTALL_BI_EXECUTABLES" = "true" ]
        then
            echo "bitools=>2"
        fi

        if [ "$INSTALL_BI_SERVERLIB" = "true" ]
        then
            echo "bitools=>2"
        fi
    fi

    if  [ "$INSTALL_MAQ" = "true" ]
    then
        AT_LEAST_ONE_TOOL="true"

        if [ "$INSTALL_BI_EXECUTABLES" = "true" ]
        then
            echo "bitools=>2"
        fi
    fi

    if  [ "$INSTALL_MIGRATE" = "true" ]
    then
        AT_LEAST_ONE_TOOL="true"

        if [ "$INSTALL_BI_EXECUTABLES" = "true" ]
        then
            echo "bitools=>2"
        fi

        if [ "$INSTALL_BI_SERVERLIB" = "true" ]
        then
            echo "bitools=>2"
        fi
    fi

    if  [ "$INSTALL_MRFAST" = "true" ]
    then
        AT_LEAST_ONE_TOOL="true"

        if [ "$INSTALL_BI_EXECUTABLES" = "true" ]
        then
            echo "bitools=>2"
        fi

        if [ "$INSTALL_BI_SERVERLIB" = "true" ]
        then
            echo "bitools=>2"
        fi
    fi

    if  [ "$INSTALL_PICARD" = "true" ]
    then
        AT_LEAST_ONE_TOOL="true"

        if [ "$INSTALL_BI_EXECUTABLES" = "true" ]
        then
            echo "bitools=>8"
        fi

        if [ "$INSTALL_BI_SERVERLIB" = "true" ]
        then
            echo "bitools=>5"
        fi
    fi

    if  [ "$INSTALL_MSA" = "true" ]
    then
        AT_LEAST_ONE_TOOL="true"

        if [ "$INSTALL_BI_EXECUTABLES" = "true" ]
        then
            echo "bitools=>2"
        fi

        if [ "$INSTALL_BI_SERVERLIB" = "true" ]
        then
            echo "bitools=>2"
        fi
    fi

    if  [ "$INSTALL_PLINK" = "true" ]
    then
        AT_LEAST_ONE_TOOL="true"

        if [ "$INSTALL_BI_EXECUTABLES" = "true" ]
        then
            echo "bitools=>4"
        fi

        if [ "$INSTALL_BI_SERVERLIB" = "true" ]
        then
            echo "bitools=>3"
        fi
    fi

    if  [ "$INSTALL_SAMTOOLS" = "true" ]
    then
        AT_LEAST_ONE_TOOL="true"

        if [ "$INSTALL_BI_EXECUTABLES" = "true" ]
        then
            echo "bitools=>2"
        fi
    fi

    if  [ "$INSTALL_MIBLAST" = "true" ]
    then
        AT_LEAST_ONE_TOOL="true"

        if [ "$INSTALL_BI_EXECUTABLES" = "true" ]
        then
            echo "bitools=>20"
        fi

        if [ "$INSTALL_BI_SERVERLIB" = "true" ]
        then
            echo "bitools=>4"
        fi
    fi

    if [ "$INSTALL_MAQ" = "true" ] && [ "$INSTALL_BOWTIE" = "true" ] && [ "$INSTALL_SAMTOOLS" = "true" ]
    then
        echo "bitools=>2"
    fi

    if [ $AT_LEAST_ONE_TOOL = "false" ]
    then
        echo "bitools=>0"
    fi

    if [ ! -f "$TOOLS_PATH" ]
    then
        mkdir -p $TOOLS_PATH || exit 1
        chown $PL_USER $TOOLS_PATH
    fi

    cd $DL_DIR

    ################################# BATWING 4 steps
    if  [ "$INSTALL_BATWING" = "true" ]
    then
        if [ "$INSTALL_BI_EXECUTABLES" = "true" ]
        then
            echo "bitools-->Downloading: "
            echo "bitools->BATWING v.${BATWING_VERSION}"
            ######################################
            #######     INSTALLATION OF BATWING
            ######################################

            sudo -u $PL_USER rm -Rf ${TOOLS_PATH}/BATWING-${BATWING_VERSION} || exit 1

            wget -c --progress=dot http://users.loni.usc.edu/~pipeline/dps/batwing-${BATWING_VERSION}.tar.gz
            if [ $? -ne 0 ]; then
 	        echo "bitools!Failed to download BATWING binaries"
 	        exit 1
            fi
            echo "bitools+"
        
            echo "bitools-->Installing: "
            echo "bitools->BATWING v.${BATWING_VERSION}"
        
            sudo -u $PL_USER cp batwing-${BATWING_VERSION}.tar.gz $TOOLS_PATH/ || exit 1
            cd $TOOLS_PATH/
        
            sudo -u $PL_USER tar xmvzf batwing-${BATWING_VERSION}.tar.gz || exit 1
            sudo -u $PL_USER rm -f batwing-${BATWING_VERSION}.tar.gz || exit 1
            echo "bitools+"
        fi

        cd $DL_DIR || exit 1

        if [ "$INSTALL_BI_SERVERLIB" = "true" ]
        then
            if [ ! -d "$PL_SERVERLIB" ]
            then
                sudo -u $PL_USER mkdir -p $PL_SERVERLIB || exit 1
            fi

            wget -c --progress=dot http://users.loni.usc.edu/~pipeline/dps/serverLib_BATWING.tar.gz
            if [ $? -ne 0 ]; then
 	        echo "bitools!Failed to download BATWING server library"
 	        exit 1
            fi

            sudo -u $PL_USER cp serverLib_BATWING.tar.gz ${PL_SERVERLIB}/ || exit 1
            echo "bitools+"
            cd $PL_SERVERLIB || exit 1
            sudo -u $PL_USER tar -xmzvf serverLib_BATWING.tar.gz || exit 1
            sudo -u $PL_USER rm serverLib_BATWING.tar.gz || exit 1
            echo "bitools+"
            sudo -u $PL_USER touch ${PL_SERVERLIB}/.monitorFile || exit 1
            cd $DL_DIR || exit 1
        fi
  
    fi

    ################################# BayesAss 5 steps
    if  [ "$INSTALL_BAYESASS" = "true" ]
    then
        if [ "$INSTALL_BI_EXECUTABLES" = "true" ]
        then
            echo "bitools-->Downloading: "
            echo "bitools->BayesAss v.${BAYESASS_VERSION}"
            ######################################
            #######     INSTALLATION OF BAYESASS
            ######################################

            sudo -u $PL_USER rm -Rf ${TOOLS_PATH}/BayesAss-${BAYESASS_VERSION} || exit 1

            wget -c --progress=dot http://users.loni.usc.edu/~pipeline/dps/bayesass-${BAYESASS_VERSION}.tar.gz
            if [ $? -ne 0 ]; then
 	        echo "bitools!Failed to download BAYESASS binaries"
 	        exit 1
            fi

            echo "bitools+"
        
            echo "bitools-->Installing: "
            echo "bitools->BayesAss v.${BAYESASS_VERSION}"
        
            sudo -u $PL_USER cp bayesass-${BAYESASS_VERSION}.tar.gz $TOOLS_PATH/ || exit 1
            cd $TOOLS_PATH/
            echo "bitools+"
        
            sudo -u $PL_USER tar xmvzf bayesass-${BAYESASS_VERSION}.tar.gz || exit 1
            sudo -u $PL_USER rm -f bayesass-${BAYESASS_VERSION}.tar.gz || exit 1
            echo "bitools+"
        fi

        cd $DL_DIR || exit 1

        if [ "$INSTALL_BI_SERVERLIB" = "true" ]
        then
            if [ ! -d "$PL_SERVERLIB" ]
            then
                sudo -u $PL_USER mkdir -p $PL_SERVERLIB || exit 1
            fi

            wget -c --progress=dot http://users.loni.usc.edu/~pipeline/dps/serverLib_BayesAss.tar.gz
            if [ $? -ne 0 ]; then
 	        echo "bitools!Failed to download BAYESASS server library"
 	        exit 1
            fi

            sudo -u $PL_USER cp serverLib_BayesAss.tar.gz ${PL_SERVERLIB}/ || exit 1
            echo "bitools+"
            cd $PL_SERVERLIB || exit 1
            sudo -u $PL_USER tar -xmzvf serverLib_BayesAss.tar.gz || exit 1
            sudo -u $PL_USER rm serverLib_BayesAss.tar.gz || exit 1
            echo "bitools+"
            sudo -u $PL_USER touch ${PL_SERVERLIB}/.monitorFile || exit 1
            cd $DL_DIR || exit 1
        fi
    fi

    ################################# Bowtie 2 steps
    if  [ "$INSTALL_BOWTIE" = "true" ]
    then
        if [ "$INSTALL_BI_EXECUTABLES" = "true" ]
        then
            echo "bitools-->Downloading: "
            echo "bitools->Bowtie v.${BOWTIE_VERSION}"
            ######################################
            #######     INSTALLATION OF BOWTIE
            ######################################

            sudo -u $PL_USER rm -Rf ${TOOLS_PATH}/Bowtie-${BOWTIE_VERSION} || exit 1

            wget -c --progress=dot http://users.loni.usc.edu/~pipeline/dps/bowtie-${BOWTIE_VERSION}.tar.gz
            if [ $? -ne 0 ]; then
 	        echo "bitools!Failed to download BOWTIE binaries"
 	        exit 1
            fi

            echo "bitools+"
        
            echo "bitools-->Installing: "
            echo "bitools->Bowtie v.${BOWTIE_VERSION}"
        
            sudo -u $PL_USER cp bowtie-${BOWTIE_VERSION}.tar.gz $TOOLS_PATH/ || exit 1
            cd $TOOLS_PATH/
        
            sudo -u $PL_USER tar xmvzf bowtie-${BOWTIE_VERSION}.tar.gz || exit 1
            sudo -u $PL_USER rm -f bowtie-${BOWTIE_VERSION}.tar.gz || exit 1
            echo "bitools+"
        fi

        cd $DL_DIR || exit 1
    fi

    ################################# EMBOSS 12 steps
    if  [ "$INSTALL_EMBOSS" = "true" ]
    then
        if [ "$INSTALL_BI_EXECUTABLES" = "true" ]
        then
            echo "bitools-->Downloading: "
            echo "bitools->EMBOSS v.${EMBOSS_VERSION}"
            ######################################
            #######     INSTALLATION OF EMBOSS
            ######################################

            sudo -u $PL_USER rm -Rf ${TOOLS_PATH}/EMBOSS-${EMBOSS_VERSION} || exit 1
            echo "bitools+"

            wget -c --progress=dot http://users.loni.usc.edu/~pipeline/dps/emboss-${EMBOSS_VERSION}.tar.gz
            if [ $? -ne 0 ]; then
 	        echo "bitools!Failed to download EMBOSS binaries"
 	        exit 1
            fi

            echo "bitools+=3"
        
            echo "bitools-->Installing: "
            echo "bitools->EMBOSS v.${EMBOSS_VERSION}"
        
            sudo -u $PL_USER cp emboss-${EMBOSS_VERSION}.tar.gz $TOOLS_PATH/ || exit 1
            cd $TOOLS_PATH/
            echo "bitools+=2"
        
            sudo -u $PL_USER tar xmvzf emboss-${EMBOSS_VERSION}.tar.gz || exit 1
            sudo -u $PL_USER rm -f emboss-${EMBOSS_VERSION}.tar.gz || exit 1
            echo "bitools+"
        fi

        cd $DL_DIR || exit 1

        if [ "$INSTALL_BI_SERVERLIB" = "true" ]
        then
            if [ ! -d "$PL_SERVERLIB" ]
            then
                sudo -u $PL_USER mkdir -p $PL_SERVERLIB || exit 1
            fi

            wget -c --progress=dot http://users.loni.usc.edu/~pipeline/dps/serverLib_EMBOSS.tar.gz
            if [ $? -ne 0 ]; then
 	        echo "bitools!Failed to download EMBOSS server library"
 	        exit 1
            fi

            sudo -u $PL_USER cp serverLib_EMBOSS.tar.gz ${PL_SERVERLIB}/ || exit 1
            echo "bitools+=2"
            cd $PL_SERVERLIB || exit 1
            sudo -u $PL_USER tar -xmzvf serverLib_EMBOSS.tar.gz || exit 1
            echo "bitools+=2"
            sudo -u $PL_USER rm serverLib_EMBOSS.tar.gz || exit 1
            echo "bitools+"
            sudo -u $PL_USER touch ${PL_SERVERLIB}/.monitorFile || exit 1
            cd $DL_DIR || exit 1
        fi
    fi

    ################################# Formatomatic 4 steps
    if  [ "$INSTALL_FORMATOMATIC" = "true" ]
    then
        if [ "$INSTALL_BI_EXECUTABLES" = "true" ]
        then
            echo "bitools-->Downloading: "
            echo "bitools->Formatomatic v.${FORMATOMATIC_VERSION}"
            ######################################
            #######     INSTALLATION OF FORMATOMATIC
            ######################################

            sudo -u $PL_USER rm -Rf ${TOOLS_PATH}/Formatomatic-${FORMATOMATIC_VERSION} || exit 1

            wget -c --progress=dot http://users.loni.usc.edu/~pipeline/dps/formatomatic-${FORMATOMATIC_VERSION}.tar.gz
            if [ $? -ne 0 ]; then
 	        echo "bitools!Failed to download Formatomatic binaries"
 	        exit 1
            fi

            echo "bitools+"
        
            echo "bitools-->Installing: "
            echo "bitools->Formatomatic v.${FORMATOMATIC_VERSION}"
        
            sudo -u $PL_USER cp formatomatic-${FORMATOMATIC_VERSION}.tar.gz $TOOLS_PATH/ || exit 1
            cd $TOOLS_PATH/
        
            sudo -u $PL_USER tar xmvzf formatomatic-${FORMATOMATIC_VERSION}.tar.gz || exit 1
            sudo -u $PL_USER rm -f formatomatic-${FORMATOMATIC_VERSION}.tar.gz || exit 1
            echo "bitools+"
        fi

        cd $DL_DIR || exit 1

        if [ "$INSTALL_BI_SERVERLIB" = "true" ]
        then
            if [ ! -d "$PL_SERVERLIB" ]
            then
                sudo -u $PL_USER mkdir -p $PL_SERVERLIB || exit 1
            fi

            wget -c --progress=dot http://users.loni.usc.edu/~pipeline/dps/serverLib_Formatomatic.tar.gz
            if [ $? -ne 0 ]; then
 	        echo "bitools!Failed to download Formatomatic server library"
 	        exit 1
            fi

            sudo -u $PL_USER cp serverLib_Formatomatic.tar.gz ${PL_SERVERLIB}/ || exit 1
            echo "bitools+"
            cd $PL_SERVERLIB || exit 1
            sudo -u $PL_USER tar -xmzvf serverLib_Formatomatic.tar.gz || exit 1
            sudo -u $PL_USER rm serverLib_Formatomatic.tar.gz || exit 1
            echo "bitools+"
            sudo -u $PL_USER touch ${PL_SERVERLIB}/.monitorFile || exit 1
            cd $DL_DIR || exit 1
        fi
    fi

    ################################# GENEPOP 4 steps
    if  [ "$INSTALL_GENEPOP" = "true" ]
    then
        if [ "$INSTALL_BI_EXECUTABLES" = "true" ]
        then
            echo "bitools-->Downloading: "
            echo "bitools->GENEPOP v.${GENEPOP_VERSION}"
            ######################################
            #######     INSTALLATION OF GENEPOP
            ######################################

            sudo -u $PL_USER rm -Rf ${TOOLS_PATH}/GENEPOP-${GENEPOP_VERSION} || exit 1

            wget -c --progress=dot http://users.loni.usc.edu/~pipeline/dps/genepop-${GENEPOP_VERSION}.tar.gz
            if [ $? -ne 0 ]; then
 	        echo "bitools!Failed to download GENEPOP binaries"
 	        exit 1
            fi
            echo "bitools+"
        
            echo "bitools-->Installing: "
            echo "bitools->GENEPOP v.${GENEPOP_VERSION}"
        
            sudo -u $PL_USER cp genepop-${GENEPOP_VERSION}.tar.gz $TOOLS_PATH/ || exit 1
            cd $TOOLS_PATH/
        
            sudo -u $PL_USER tar xmvzf genepop-${GENEPOP_VERSION}.tar.gz || exit 1
            sudo -u $PL_USER rm -f genepop-${GENEPOP_VERSION}.tar.gz || exit 1
            echo "bitools+"
        fi

        cd $DL_DIR || exit 1

        if [ "$INSTALL_BI_SERVERLIB" = "true" ]
        then
            if [ ! -d "$PL_SERVERLIB" ]
            then
                sudo -u $PL_USER mkdir -p $PL_SERVERLIB || exit 1
            fi

            wget -c --progress=dot http://users.loni.usc.edu/~pipeline/dps/serverLib_GENEPOP.tar.gz
            if [ $? -ne 0 ]; then
 	        echo "bitools!Failed to download GENEPOP server library"
 	        exit 1
            fi

            sudo -u $PL_USER cp serverLib_GENEPOP.tar.gz ${PL_SERVERLIB}/ || exit 1
            echo "bitools+"
            cd $PL_SERVERLIB || exit 1
            sudo -u $PL_USER tar -xmzvf serverLib_GENEPOP.tar.gz || exit 1
            sudo -u $PL_USER rm serverLib_GENEPOP.tar.gz || exit 1
            echo "bitools+"
            sudo -u $PL_USER touch ${PL_SERVERLIB}/.monitorFile || exit 1
            cd $DL_DIR || exit 1
        fi
    fi

    ################################# GWASS 4 steps
    if  [ "$INSTALL_GWASS" = "true" ]
    then
        if [ "$INSTALL_BI_EXECUTABLES" = "true" ]
        then
            echo "bitools-->Downloading: "
            echo "bitools->GWASS v.${GWASS_VERSION}"
            ######################################
            #######     INSTALLATION OF GWASS
            ######################################

            sudo -u $PL_USER rm -Rf ${TOOLS_PATH}/GWASS-${GWASS_VERSION} || exit 1

            wget -c --progress=dot http://users.loni.usc.edu/~pipeline/dps/gwass-${GWASS_VERSION}.tar.gz
            if [ $? -ne 0 ]; then
 	        echo "bitools!Failed to download GWASS binaries"
 	        exit 1
            fi

            echo "bitools+"
        
            echo "bitools-->Installing: "
            echo "bitools->GWASS v.${GWASS_VERSION}"
        
            sudo -u $PL_USER cp gwass-${GWASS_VERSION}.tar.gz $TOOLS_PATH/ || exit 1
            cd $TOOLS_PATH/
        
            sudo -u $PL_USER tar xmvzf gwass-${GWASS_VERSION}.tar.gz || exit 1
            sudo -u $PL_USER rm -f gwass-${GWASS_VERSION}.tar.gz || exit 1
            echo "bitools+"
        fi

        cd $DL_DIR || exit 1

        if [ "$INSTALL_BI_SERVERLIB" = "true" ]
        then
            if [ ! -d "$PL_SERVERLIB" ]
            then
                sudo -u $PL_USER mkdir -p $PL_SERVERLIB || exit 1
            fi

            wget -c --progress=dot http://users.loni.usc.edu/~pipeline/dps/serverLib_GWASS.tar.gz
            if [ $? -ne 0 ]; then
 	        echo "bitools!Failed to download GWASS server library"
 	        exit 1
            fi

            sudo -u $PL_USER cp serverLib_GWASS.tar.gz ${PL_SERVERLIB}/ || exit 1
            echo "bitools+"
            cd $PL_SERVERLIB || exit 1
            sudo -u $PL_USER tar -xmzvf serverLib_GWASS.tar.gz || exit 1
            sudo -u $PL_USER rm serverLib_GWASS.tar.gz || exit 1
            echo "bitools+"
            sudo -u $PL_USER touch ${PL_SERVERLIB}/.monitorFile || exit 1
            cd $DL_DIR || exit 1
        fi
    fi

    ################################# MAQ 2 steps
    if  [ "$INSTALL_MAQ" = "true" ]
    then
        if [ "$INSTALL_BI_EXECUTABLES" = "true" ]
        then
            echo "bitools-->Downloading: "
            echo "bitools->MAQ v.${MAQ_VERSION}"
            ######################################
            #######     INSTALLATION OF MAQ
            ######################################

            sudo -u $PL_USER rm -Rf ${TOOLS_PATH}/MAQ-${MAQ_VERSION} || exit 1

            wget -c --progress=dot http://users.loni.usc.edu/~pipeline/dps/maq-${MAQ_VERSION}.tar.gz
            if [ $? -ne 0 ]; then
 	        echo "bitools!Failed to download MAQ binaries"
 	        exit 1
            fi

            echo "bitools+"
        
            echo "bitools-->Installing: "
            echo "bitools->MAQ v.${MAQ_VERSION}"
        
            sudo -u $PL_USER cp maq-${MAQ_VERSION}.tar.gz $TOOLS_PATH/ || exit 1
            cd $TOOLS_PATH/
        
            sudo -u $PL_USER tar xmvzf maq-${MAQ_VERSION}.tar.gz || exit 1
            sudo -u $PL_USER rm -f maq-${MAQ_VERSION}.tar.gz || exit 1
            echo "bitools+"
        fi

        cd $DL_DIR || exit 1
    fi

    ################################# Migrate 4 steps
    if  [ "$INSTALL_MIGRATE" = "true" ]
    then
        if [ "$INSTALL_BI_EXECUTABLES" = "true" ]
        then
            echo "bitools-->Downloading: "
            echo "bitools->Migrate v.${MIGRATE_VERSION}"
            ######################################
            #######     INSTALLATION OF MIGRATE
            ######################################

            sudo -u $PL_USER rm -Rf ${TOOLS_PATH}/Migrate-${MIGRATE_VERSION} || exit 1

            wget -c --progress=dot http://users.loni.usc.edu/~pipeline/dps/migrate-${MIGRATE_VERSION}.tar.gz
            if [ $? -ne 0 ]; then
 	        echo "bitools!Failed to download Migrate binaries"
 	        exit 1
            fi
            echo "bitools+"
        
            echo "bitools-->Installing: "
            echo "bitools->Migrate v.${MIGRATE_VERSION}"
        
            sudo -u $PL_USER cp migrate-${MIGRATE_VERSION}.tar.gz $TOOLS_PATH/ || exit 1
            cd $TOOLS_PATH/
        
            sudo -u $PL_USER tar xmvzf migrate-${MIGRATE_VERSION}.tar.gz || exit 1
            sudo -u $PL_USER rm -f migrate-${MIGRATE_VERSION}.tar.gz || exit 1
            echo "bitools+"
        fi

        cd $DL_DIR || exit 1

        if [ "$INSTALL_BI_SERVERLIB" = "true" ]
        then
            if [ ! -d "$PL_SERVERLIB" ]
            then
                sudo -u $PL_USER mkdir -p $PL_SERVERLIB || exit 1
            fi

            wget -c --progress=dot http://users.loni.usc.edu/~pipeline/dps/serverLib_Migrate.tar.gz
            if [ $? -ne 0 ]; then
 	        echo "bitools!Failed to download Migrate server library"
 	        exit 1
            fi

            sudo -u $PL_USER cp serverLib_Migrate.tar.gz ${PL_SERVERLIB}/ || exit 1
            echo "bitools+"
            cd $PL_SERVERLIB || exit 1
            sudo -u $PL_USER tar -xmzvf serverLib_Migrate.tar.gz || exit 1
            sudo -u $PL_USER rm serverLib_Migrate.tar.gz || exit 1
            echo "bitools+"
            sudo -u $PL_USER touch ${PL_SERVERLIB}/.monitorFile || exit 1
            cd $DL_DIR || exit 1
        fi
    fi

    ################################# mrFAST 4 steps
    if  [ "$INSTALL_MRFAST" = "true" ]
    then
        if [ "$INSTALL_BI_EXECUTABLES" = "true" ]
        then
            echo "bitools-->Downloading: "
            echo "bitools->mrFAST v.${MRFAST_VERSION}"
            ######################################
            #######     INSTALLATION OF MRFAST
            ######################################

            sudo -u $PL_USER rm -Rf ${TOOLS_PATH}/mrFAST-${MRFAST_VERSION} || exit 1

            wget -c --progress=dot http://users.loni.usc.edu/~pipeline/dps/mrfast-${MRFAST_VERSION}.tar.gz
            if [ $? -ne 0 ]; then
 	        echo "bitools!Failed to download mrFAST binaries"
 	        exit 1
            fi
            echo "bitools+"
        
            echo "bitools-->Installing: "
            echo "bitools->mrFAST v.${MRFAST_VERSION}"
        
            sudo -u $PL_USER cp mrfast-${MRFAST_VERSION}.tar.gz $TOOLS_PATH/ || exit 1
            cd $TOOLS_PATH/
        
            sudo -u $PL_USER tar xmvzf mrfast-${MRFAST_VERSION}.tar.gz || exit 1
            sudo -u $PL_USER rm -f mrfast-${MRFAST_VERSION}.tar.gz || exit 1
            echo "bitools+"
        fi

        cd $DL_DIR || exit 1

        if [ "$INSTALL_BI_SERVERLIB" = "true" ]
        then
            if [ ! -d "$PL_SERVERLIB" ]
            then
                sudo -u $PL_USER mkdir -p $PL_SERVERLIB || exit 1
            fi

            wget -c --progress=dot http://users.loni.usc.edu/~pipeline/dps/serverLib_mrFAST.tar.gz
            if [ $? -ne 0 ]; then
 	        echo "bitools!Failed to download mrFAST server library"
 	        exit 1
            fi

            sudo -u $PL_USER cp serverLib_mrFAST.tar.gz ${PL_SERVERLIB}/ || exit 1
            echo "bitools+"
            cd $PL_SERVERLIB || exit 1
            sudo -u $PL_USER tar -xmzvf serverLib_mrFAST.tar.gz || exit 1
            sudo -u $PL_USER rm serverLib_mrFAST.tar.gz || exit 1
            echo "bitools+"
            sudo -u $PL_USER touch ${PL_SERVERLIB}/.monitorFile || exit 1
            cd $DL_DIR || exit 1
        fi
    fi

    ################################# Picard 13 steps
    if  [ "$INSTALL_PICARD" = "true" ]
    then
        if [ "$INSTALL_BI_EXECUTABLES" = "true" ]
        then
            echo "bitools-->Downloading: "
            echo "bitools->Picard v.${PICARD_VERSION}"
            ######################################
            #######     INSTALLATION OF PICARD
            ######################################

            sudo -u $PL_USER rm -Rf ${TOOLS_PATH}/Picard-${PICARD_VERSION} || exit 1

            wget -c --progress=dot http://users.loni.usc.edu/~pipeline/dps/picard-${PICARD_VERSION}.tar.gz
            if [ $? -ne 0 ]; then
 	        echo "bitools!Failed to download Picard binaries"
 	        exit 1
            fi
            echo "bitools+=3"
        
            echo "bitools-->Installing: "
            echo "bitools->Picard v.${PICARD_VERSION}"
        
            sudo -u $PL_USER cp picard-${PICARD_VERSION}.tar.gz $TOOLS_PATH/ || exit 1
            cd $TOOLS_PATH/
            echo "bitools+=2"
        
            sudo -u $PL_USER tar xmvzf picard-${PICARD_VERSION}.tar.gz || exit 1
            sudo -u $PL_USER rm -f picard-${PICARD_VERSION}.tar.gz || exit 1
            echo "bitools+=3"
        fi

        cd $DL_DIR || exit 1

        if [ "$INSTALL_BI_SERVERLIB" = "true" ]
        then
            if [ ! -d "$PL_SERVERLIB" ]
            then
                sudo -u $PL_USER mkdir -p $PL_SERVERLIB || exit 1
            fi

            wget -c --progress=dot http://users.loni.usc.edu/~pipeline/dps/serverLib_Picard.tar.gz
            if [ $? -ne 0 ]; then
 	        echo "bitools!Failed to download Picard server library"
 	        exit 1
            fi

            sudo -u $PL_USER cp serverLib_Picard.tar.gz ${PL_SERVERLIB}/ || exit 1
            echo "bitools+=2"
            cd $PL_SERVERLIB || exit 1
            sudo -u $PL_USER tar -xmzvf serverLib_Picard.tar.gz || exit 1
            sudo -u $PL_USER rm serverLib_Picard.tar.gz || exit 1
            echo "bitools+=3"
            sudo -u $PL_USER touch ${PL_SERVERLIB}/.monitorFile || exit 1
            cd $DL_DIR || exit 1
        fi
    fi

    ################################# MSA 4 steps
    if  [ "$INSTALL_MSA" = "true" ]
    then
        if [ "$INSTALL_BI_EXECUTABLES" = "true" ]
        then
            echo "bitools-->Downloading: "
            echo "bitools->MSA v.${MSA_VERSION}"
            ######################################
            #######     INSTALLATION OF MSA
            ######################################

            sudo -u $PL_USER rm -Rf ${TOOLS_PATH}/MSA-${MSA_VERSION} || exit 1

            wget -c --progress=dot http://users.loni.usc.edu/~pipeline/dps/msa-${MSA_VERSION}.tar.gz
            if [ $? -ne 0 ]; then
 	        echo "bitools!Failed to download MSA binaries"
 	        exit 1
            fi
            echo "bitools+"
        
            echo "bitools-->Installing: "
            echo "bitools->MSA v.${MSA_VERSION}"
        
            sudo -u $PL_USER cp msa-${MSA_VERSION}.tar.gz $TOOLS_PATH/ || exit 1
            cd $TOOLS_PATH/
        
            sudo -u $PL_USER tar xmvzf msa-${MSA_VERSION}.tar.gz || exit 1
            sudo -u $PL_USER rm -f msa-${MSA_VERSION}.tar.gz || exit 1
            echo "bitools+"
        fi

        cd $DL_DIR || exit 1

        if [ "$INSTALL_BI_SERVERLIB" = "true" ]
        then
            if [ ! -d "$PL_SERVERLIB" ]
            then
                sudo -u $PL_USER mkdir -p $PL_SERVERLIB || exit 1
            fi

            wget -c --progress=dot http://users.loni.usc.edu/~pipeline/dps/serverLib_MSA.tar.gz
            if [ $? -ne 0 ]; then
 	        echo "bitools!Failed to download MSA server library"
 	        exit 1
            fi

            sudo -u $PL_USER cp serverLib_MSA.tar.gz ${PL_SERVERLIB}/ || exit 1
            echo "bitools+"
            cd $PL_SERVERLIB || exit 1
            sudo -u $PL_USER tar -xmzvf serverLib_MSA.tar.gz || exit 1
            sudo -u $PL_USER rm serverLib_MSA.tar.gz || exit 1
            echo "bitools+"
            sudo -u $PL_USER touch ${PL_SERVERLIB}/.monitorFile || exit 1
            cd $DL_DIR || exit 1
        fi
    fi

    ################################# PLINK 7 steps
    if  [ "$INSTALL_PLINK" = "true" ]
    then
        if [ "$INSTALL_BI_EXECUTABLES" = "true" ]
        then
            echo "bitools-->Downloading: "
            echo "bitools->PLINK v.${PLINK_VERSION}"
            ######################################
            #######     INSTALLATION OF PLINK
            ######################################

            sudo -u $PL_USER rm -Rf ${TOOLS_PATH}/PLINK-${PLINK_VERSION} || exit 1

            wget -c --progress=dot http://users.loni.usc.edu/~pipeline/dps/plink-${PLINK_VERSION}.tar.gz
            if [ $? -ne 0 ]; then
 	        echo "bitools!Failed to download PLINK binaries"
 	        exit 1
            fi
            echo "bitools+=2"
        
            echo "bitools-->Installing: "
            echo "bitools->PLINK v.${PLINK_VERSION}"
        
            sudo -u $PL_USER cp plink-${PLINK_VERSION}.tar.gz $TOOLS_PATH/ || exit 1
            cd $TOOLS_PATH/
            echo "bitools+"
        
            sudo -u $PL_USER tar xmvzf plink-${PLINK_VERSION}.tar.gz || exit 1
            sudo -u $PL_USER rm -f plink-${PLINK_VERSION}.tar.gz || exit 1
            echo "bitools+"
        fi

        cd $DL_DIR || exit 1

        if [ "$INSTALL_BI_SERVERLIB" = "true" ]
        then
            if [ ! -d "$PL_SERVERLIB" ]
            then
                sudo -u $PL_USER mkdir -p $PL_SERVERLIB || exit 1
            fi

            wget -c --progress=dot http://users.loni.usc.edu/~pipeline/dps/serverLib_PLINK.tar.gz
            if [ $? -ne 0 ]; then
 	        echo "bitools!Failed to download PLINK server library"
 	        exit 1
            fi

            sudo -u $PL_USER cp serverLib_PLINK.tar.gz ${PL_SERVERLIB}/ || exit 1
            echo "bitools+=2"
            cd $PL_SERVERLIB || exit 1
            sudo -u $PL_USER tar -xmzvf serverLib_PLINK.tar.gz || exit 1
            sudo -u $PL_USER rm serverLib_PLINK.tar.gz || exit 1
            echo "bitools+"
            sudo -u $PL_USER touch ${PL_SERVERLIB}/.monitorFile || exit 1
            cd $DL_DIR || exit 1
        fi
    fi

    ################################# SamTools 2 steps
    if  [ "$INSTALL_SAMTOOLS" = "true" ]
    then
        if [ "$INSTALL_BI_EXECUTABLES" = "true" ]
        then
            echo "bitools-->Downloading: "
            echo "bitools->SamTools v.${SAMTOOLS_VERSION}"
            ######################################
            #######     INSTALLATION OF SAMTOOLS
            ######################################

            sudo -u $PL_USER rm -Rf ${TOOLS_PATH}/SamTools-${SAMTOOLS_VERSION} || exit 1

            wget -c --progress=dot http://users.loni.usc.edu/~pipeline/dps/samtools-${SAMTOOLS_VERSION}.tar.gz
            if [ $? -ne 0 ]; then
 	        echo "bitools!Failed to download SamTools binaries"
 	        exit 1
            fi
            echo "bitools+"
        
            echo "bitools-->Installing: "
            echo "bitools->SamTools v.${SAMTOOLS_VERSION}"
        
            sudo -u $PL_USER cp samtools-${SAMTOOLS_VERSION}.tar.gz $TOOLS_PATH/ || exit 1
            cd $TOOLS_PATH/
        
            sudo -u $PL_USER tar xmvzf samtools-${SAMTOOLS_VERSION}.tar.gz || exit 1
            sudo -u $PL_USER rm -f samtools-${SAMTOOLS_VERSION}.tar.gz || exit 1
            echo "bitools+"
        fi

        cd $DL_DIR || exit 1

    fi

    ################################# miBLAST 24 steps
    if  [ "$INSTALL_MIBLAST" = "true" ]
    then
        if [ "$INSTALL_BI_EXECUTABLES" = "true" ]
        then
            echo "bitools-->Downloading: "
            echo "bitools->miBLAST v.${MIBLAST_VERSION}"
            ######################################
            #######     INSTALLATION OF MIBLAST
            ######################################

            sudo -u $PL_USER rm -Rf ${TOOLS_PATH}/miBLAST-${MIBLAST_VERSION} || exit 1

            wget -c --progress=dot http://users.loni.usc.edu/~pipeline/dps/miblast-${MIBLAST_VERSION}.tar.gz
            if [ $? -ne 0 ]; then
 	        echo "bitools!Failed to download miBLAST binaries"
 	        exit 1
            fi
            echo "bitools+=10"
        
            echo "bitools-->Installing: "
            echo "bitools->miBLAST v.${MIBLAST_VERSION}"
        
            sudo -u $PL_USER cp miblast-${MIBLAST_VERSION}.tar.gz $TOOLS_PATH/ || exit 1
            cd $TOOLS_PATH/
            echo "bitools+=7"
        
            sudo -u $PL_USER tar xmvzf miblast-${MIBLAST_VERSION}.tar.gz || exit 1
            sudo -u $PL_USER rm -f miblast-${MIBLAST_VERSION}.tar.gz || exit 1
            echo "bitools+=3"
        fi

        cd $DL_DIR || exit 1

        if [ "$INSTALL_BI_SERVERLIB" = "true" ]
        then
            if [ ! -d "$PL_SERVERLIB" ]
            then
                sudo -u $PL_USER mkdir -p $PL_SERVERLIB || exit 1
            fi

            wget -c --progress=dot http://users.loni.usc.edu/~pipeline/dps/serverLib_miBLAST.tar.gz
            if [ $? -ne 0 ]; then
 	        echo "bitools!Failed to download miBLAST server library"
 	        exit 1
            fi

            sudo -u $PL_USER cp serverLib_miBLAST.tar.gz ${PL_SERVERLIB}/ || exit 1
            echo "bitools+=2"
            cd $PL_SERVERLIB || exit 1
            sudo -u $PL_USER tar -xmzvf serverLib_miBLAST.tar.gz || exit 1
            sudo -u $PL_USER rm serverLib_miBLAST.tar.gz || exit 1
            echo "bitools+=2"
            sudo -u $PL_USER touch ${PL_SERVERLIB}/.monitorFile || exit 1
            cd $DL_DIR || exit 1
        fi
    fi

    if [ "$INSTALL_MAQ" = "true" ] && [ "$INSTALL_BOWTIE" = "true" ] && [ "$INSTALL_SAMTOOLS" = "true" ]
    then
        if [ "$INSTALL_BI_SERVERLIB" = "true" ]
        then
            if [ ! -d "$PL_SERVERLIB" ]
            then
                sudo -u $PL_USER mkdir -p $PL_SERVERLIB || exit 1
            fi

            wget -c --progress=dot http://users.loni.usc.edu/~pipeline/dps/serverLib_IntegratedBioinformatics.tar.gz
            if [ $? -ne 0 ]; then
 	        echo "bitools!Failed to download Integrated Bioinformatics server library"
 	        exit 1
            fi
            echo "bitools+"

            sudo -u $PL_USER cp serverLib_IntegratedBioinformatics.tar.gz ${PL_SERVERLIB}/ || exit 1
            cd $PL_SERVERLIB || exit 1
            sudo -u $PL_USER tar -xmzvf serverLib_IntegratedBioinformatics.tar.gz || exit 1
            sudo -u $PL_USER rm serverLib_IntegratedBioinformatics.tar.gz || exit 1
            sudo -u $PL_USER touch ${PL_SERVERLIB}/.monitorFile || exit 1
            echo "bitools+"

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
		            sed -i "s/cranium\.loni\.usc\.edu/$HOSTNAME/g" $x || exit 1
                fi
	done
	sudo -u $PL_USER touch ${PL_SERVERLIB}/.monitorFile || exit 1
fi

