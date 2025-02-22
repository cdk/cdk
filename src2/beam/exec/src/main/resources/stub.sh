#!/bin/bash
MYSELF="${BASH_SOURCE[0]}"
[ $? -gt 0 -a -f "$0" ] && MYSELF="./$0"
java=java
if test -n "$JAVA_HOME"; then
    java="$JAVA_HOME/bin/java"
fi
exec "$java" $JAVA_OPTS -jar $MYSELF "$@"
exit 1