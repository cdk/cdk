#!/bin/sh
#
#       Sample shell script to fetch your project HTML
#
#       Creates a file called projhtml.cache in your home directory
#
GRPHOME=/home/groups/c/cd/cdk
/usr/bin/wget -q -O $GRPHOME/projhtml.tmp 'http://sourceforge.net//export/projnews.php?group_id=20024&limit=6&flat=1&show_summaries=1'  > /dev/null
/bin/mv -f $GRPHOME//projhtml.tmp $GRPHOME/projhtml.cache


