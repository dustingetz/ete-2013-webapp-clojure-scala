#!/bin/bash

fail ()
{
	echo "ERROR: $@"
	exit 1
}
warn ()
{
	echo "WARN: $@"
}
announce ()
{
	echo "--- $@"
}

#-Xnoagent -Djava.compiler=NONE -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005
# ENVIRONMENT

if [ ! -f ./env.sh ]
then
	warn "env.sh not found. A default will be created for you."
	cp env.sh.example env.sh
	exit 1
fi
source ./env.sh
[ -n "$tomcat" ] || fail "Required environment variable tomcat not found."
[ -n "$pgdb" ] || fail "Required environment variable pgdb not found."

# warns when the file or directory you're trying to destroy doesn't exist.
killOrNatter ()
{
	local target="$1"; shift
	if [ -e "$target" ]
	then
		echo "delete: $target"
		rm -rf "$target"
	else
		warn "Target not found: $target"
	fi
}
# completely unpersons a web app from tomcat.
undeploy ()
{
	target="$1" ; shift
	killOrNatter "$tomcat/webapps/$target.war"
	killOrNatter "$tomcat/webapps/$target"
	killOrNatter "$tomcat/work/Catalina/localhost/$target"
}

echo "TMF Console"
if [ -z "$1" ]
then
	cat <<USAGE
usage:
   clean           - removes the tomcat logs
   start           - starts tomcat
   stop            - stops tomcat
   deploy <war>    - deploys the war from the named project to tomcat
   undeploy <name> - cleans out the tomcat instance of artifacts for the basename
USAGE
	exit 1
fi
while [ -n "$1" ]
do
	action="$1" ; shift
	case "$action" in
	"tomcat" )
		[ -d "${tomcat}" ] || fail "Can't find tomcat installation at ${tomcat}."
		sub="$1" ; shift
		case "$sub" in
		"clean" )
	# 		for file in "${tomcat}/logs/"*
	# 		do
	# 			rm -fv "${tomcat}/logs/${file}"
	# 		done
			rm -fv "${tomcat}/logs/"*
			;;
		"start" )
			export JPDA_ADDRESS=8081
			export JPDA_TRANSPORT=dt_socket
			"$tomcat/bin/catalina.sh" jpda start
#			"$tomcat/bin/startup.sh"
			;;
		"stop" )
			"$tomcat/bin/shutdown.sh"
			;;
		"deploy" )
			war="$1" ; shift
			target="$(basename $war)"
			[ -f "$war" ] || fail "War file not found: $war"
			cp -v "$war" "$tomcat/webapps"
			;;
		"undeploy" )
			target="$1" ; shift
			undeploy "$target"
			;;
		"bounce" )
			"$tomcat/bin/shutdown.sh"
			sleep 5
			rm -fv "${tomcat}/logs/"*
			"$tomcat/bin/startup.sh"
			;;
		"pid" )
			ps -s | grep bin/java | awk '{print $1}'
			;;
		* )
			echo "Unknown action: $sub"
			;;
		esac
		;;
	"db" )
		sub="$1" ; shift
		announce "$sub"
		case "$sub" in
		"init" )
			read -p "This will completely hose the DB. Are you sure? " -n 1 -r
			echo "\n"
			if [ $REPLY == y ] || [ $REPLY == Y ] # no regexes in git bash :(
			then
				rm -v "$pgdb/log"
				pg_ctl init -D "$pgdb/db/"
			fi
			;;
		"start" )
			pg_ctl -D "$pgdb/db/" -l "$pgdb/log" start
			;;
		"stop" )
			pg_ctl -D "$pgdb/db/" stop
			;;
		"status" )
			pg_ctl -D "$pgdb/db/" status
			;;
		* )
			echo Unknown action: $sub
			;;
		esac
		;;
	# this is here just so we can embed it in a string of other commands.
	"sleep" )
		w="$1" ; shift
		announce "sleep $w"
		[ -n "$w" ] || fail "parameter required for sleep"
		sleep "$w"
		;;
	"build" )
		ant clean
		ant 2>&1 | tee build.log
		;;
	* )
		fail "Unkown command: $action"
		;;
	esac
done
