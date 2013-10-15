#!/bin/sh

# this script checks if SGE has been installed
# using a series of heuristics, it compiles a list of all sge installations

# the $sgedirs variable will store a space-separated list of potential sge directories
sgedirs=""

# first, check if the SGE_ROOT variable has been set
if [ ! -z "$SGE_ROOT" ]
then
	sge_root=${SGE_ROOT#*=}
	echo LOG SGE_ROOT environment variable has been set to $sge_root
	if [ -d $sge_root ]
	then
		echo LOG The directory $sge_root exists
		sgedirs=$sgedirs" "$sge_root
	else
		echo "LOG The directory $sge_root does not exist"
	fi
fi

# check if sge is in one of the usual places
for usual in "/usr" "/usr/local"
do
	list=`ls -d ${usual}/*sge* 2> /dev/null`
	if [ ! -z "$list" ]
	then
		echo "LOG Found the following sge directory(ies): $list"
		sgedirs=$sgedirs" "$list
	else
		echo LOG Could not find SGE directory in $usual
	fi
done

# checks if the sge startup scripts are in the usual place
startups=`ls -d /etc/init.d/* 2> /dev/null | grep sge`
for startup in $startups
do
	echo "LOG Found SGE startup script $startup. Looking inside to find SGE_ROOT."
	sge_root=`grep "export SGE_ROOT" $startup | cut -d'=' -f2 | cut -d';' -f1`
	echo LOG Found directory $sge_root inside $startup
	if [ -d $sge_root ]
	then
		echo LOG The directory $sge_root exists
		sgedirs=$sgedirs" "$sge_root
	else
		echo LOG The directory $sge_root does not exist
	fi
done

# check if the sge_qmaster process is running
for process in sge_qmaster sge_execd
do
	if [ ! -z "`ps -e | grep $process`" ]
	then
		sgeps=`ps -ef | grep $process | tr -s ' ' | cut -d' ' -f8 | grep sge`
		echo LOG Found running SGE process
		echo LOG The path to the executable is $sgeps
		sge_root=${sgeps%bin*}
		sgedirs=$sgedirs" "${sge_root%/*}
	fi
done

if [ -z "$sge_root" ]
then
	echo "LOG The SGE_ROOT directory could not be determined"
else
	# loop through the sge directories found thus far and write
	# unique entries to standard output, along with the corresponding sge version
	inda=1;
	$uniqsgedirs
	for inst in $sgedirs
	do
	indb=1;
		uniq=1;
		for check in $uniqsgedirs
		do
			if [ $inda -ne $indb ] && [ "$inst" == "$check" ]
			then
				uniq=0
				break
			fi
			indb=$[$indb+1]
		done
		if [ $uniq -eq 1 ]
		then
			uniqsgedirs=$uniqsgedirs" "$inst
			if [ -f ${inst}/util/arch ]
			then	
				arch=`${inst}/util/arch`
				if [ -f ${inst}/bin/${arch}/qstat ] && [ -f ${inst}/default/common/bootstrap ]
				then
                              export SGE_ROOT=$inst
					version=`${inst}/bin/${arch}/qstat -help | head -n 1 | cut -d' ' -f2`
					if [ ! -z "$version" ]
					then
						echo SGE $inst $version
					fi
				else
					echo "LOG ${inst}/bin/${arch}/qstat and/or ${inst}/default/common/bootstrap do(es) not exist"
				fi
			else
				echo "LOG ${inst}/util/arch does not exist"
			fi
		fi
		inda=$[$inda+1]
	done
fi

exit $?
