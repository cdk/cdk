#!/usr/bin/perl
use strict;
use diagnostics;

# prior to BSP: 6872

my $startRevision = 7031;
#my $startRevision = 7000;
my $endRevision = 7037;

my $ant_cmd = "JAVA_HOME=/usr/lib/jvm/java-1.5.0-sun ant";
`$ant_cmd clean`;
for (my $rev = $startRevision; $rev <= $endRevision; $rev += 3) {
    my $logfile = "antlog/ant_r$rev.log";
    if (-e $logfile) {
        print "Already got log for rev $rev\n";
    } else {
        print "Analyzing rev $rev\n";
        `svn update -r $rev`;
        `$ant_cmd -DrunSlowTests=false -logfile $logfile dist-all test-dist-all test-all`
    }
}