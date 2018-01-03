#!/bin/sh

c_pid=`pgrep -f com.sun.enterprise.glassfish.bootstrap.ASMain`
if [ "$c_pid" = "" ] ; then
  echo "glassfish is stop!"
  exit 3
else
  echo "check glassfish started. running $c_pid"
#  exit 0
fi

/sbin/runuser -s /bin/sh "javaops" -c "/usr/bin/oadmin undeploy  $RPM_PACKAGE_NAME"
sleep 2
/sbin/runuser -s /bin/sh "javaops" -c "/usr/bin/oadmin list-applications"
sleep 3