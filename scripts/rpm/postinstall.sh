#!/bin/sh
/sbin/runuser -s /bin/sh "javaops" -c "/usr/bin/oadmin deploy --property implicitCdiEnabled=false  --name $RPM_PACKAGE_NAME --contextroot $RPM_PACKAGE_NAME /tmp/gfproject/wars/$RPM_PACKAGE_NAME-$RPM_PACKAGE_VERSION.war"
sleep 2

/sbin/runuser -s /bin/sh "javaops" -c "/usr/bin/oadmin list-applications"

rm -r /tmp/gfproject/wars/$RPM_PACKAGE_NAME-$RPM_PACKAGE_VERSION.war
