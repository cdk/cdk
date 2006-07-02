#!/usr/bin/perl
use diagnostics;
use strict;

my $offline = 1;

############################# DOWNLOADING SOURCEFORGE BUGS ################################

my %allSFBugs = ();
my %openSFBugs = ();

# OPEN bugs
my $pageCount = 0;
my $hasNext = 1;
while ($hasNext == 1) {
    # get the HTML from SourceForge
    $pageCount = $pageCount + 1;
    my $offset = ($pageCount-1)*50;
    print "Downloading bugs $offset to " . ($offset + 49) . "...\n";
    `wget -o openPage$pageCount.log -O openPage$pageCount.html "http://sourceforge.net/tracker/index.php?func=browse&group_id=20024&atid=120024&s
et=custom&_assigned_to=0&_status=1&_category=100&_group=100&order=artifact_id&sort=DESC&offset=$offset"` if ($offline == 0);

    # process output
    open(INPUT, "<openPage$pageCount.html");
    $hasNext = 0;
    while (<INPUT>) {
        # links like: /tracker/index.php?func=detail&amp;aid=1506033&amp;group_id=20024&amp;atid=120024
        if (/func=detail/) {
            if (/aid=(\d*)/) {
                my $bug = $1;
                # print "Found a bug: $bug\n";
                $openSFBugs{$bug} = "open";
            }
        }
        $hasNext = 1 if (/Next\s50/);
    }
}

# ALL bugs
$pageCount = 0;
$hasNext = 1;
while ($hasNext == 1) {
    # get the HTML from SourceForge
    $pageCount = $pageCount + 1;
    my $offset = ($pageCount-1)*50;
    print "Downloading bugs $offset to " . ($offset + 49) . "...\n";
    `wget -o allPage$pageCount.log -O allPage$pageCount.html "http://sourceforge.net/tracker/index.php?func=browse&group_id=20024&atid=120024&set
=custom&_assigned_to=0&_status=100&_category=100&_group=100&order=artifact_id&sort=DESC&offset=$offset"` if ($offline == 0);

    # process output
    open(INPUT, "<allPage$pageCount.html");
    $hasNext = 0;
    while (<INPUT>) {
        # links like: /tracker/index.php?func=detail&amp;aid=1506033&amp;group_id=20024&amp;atid=120024
        if (/func=detail/) {
            if (/aid=(\d*)/) {
                my $bug = $1;
                # print "Found a bug: $bug\n";
                $allSFBugs{$bug} = "all";
            }
        }
        $hasNext = 1 if (/Next\s50/);
    }
}

############################# THE ANALYSIS ################################

my %testedBugs = ();
my %markedBugs = ();

my @files = `find src/org -name "*.java" | sort`;

foreach my $file (@files) {
    $file =~ s/[\n|\r]//g;
    # print "Processing file: $file\n";
    open(INPUT, "<$file");
    while (<INPUT>) {
        my $bug = "";
        if (/cdk\.bug/) {
            if (/cdk\.bug\s+(\d+)/) {
                $bug = $1;
                if ($file =~ /cdk\/test/) {
                    $testedBugs{$bug} = "tested";
                } else {
                    $markedBugs{$bug} = $file;
                }
            }
        }
    }
}

close(INPUT);

############################# THE ANALYSIS ################################

open(RESULTS, ">bugAnalysis.html");

print RESULTS "<html>\n";
print RESULTS "<body>\n";

# summarize
print "Found bugs on SF: " . keys(%allSFBugs) . "\n";
print " of which are open: " . keys(%openSFBugs) . "\nList: ";
foreach my $bug (keys %openSFBugs) {
    print "$bug ";
}
print "\n";
print "Found marked bugs: " . keys(%markedBugs) . "\n";
print "Found tested bugs: " . keys(%testedBugs) . "\n";

print RESULTS "<h1>Mark up of open bugs in JavaDoc</h1>\n";
print RESULTS "<p>\n";
print RESULTS "Every open bug is supposed to be marked with \@cdk.bug in the JavaDoc\n";
print RESULTS "off the buggy class and/or method.\n";
print RESULTS "</p>\n";
print RESULTS "<ul>\n";

# check if open bugs are reported in the source code
my $count = 0;
print RESULTS "<h3>Open bugs not marked in source</h3>\n";
print RESULTS "<ul>\n";
foreach my $openbug (sort keys %openSFBugs) {
    if ($markedBugs{$openbug}) {
        # all is fine
    } else {
        print RESULTS "<li>Open bug <a href=\"http://sourceforge.net/tracker/index.php?func=detail&aid=$openbug&group_id=20024&atid=120024\">#$openbug</a> is *not* marked in the source code with \@cdk.bug!</li>\n";
        $count = $count + 1;
    }
}
print RESULTS "</ul>\n";
print "Open bugs that are not marked with \@cdk.bug: $count\n";

# check if marked bugs are no longer open
$count = 0;
print RESULTS "<h3>Marked bugs that are now fixed</h3>\n";
print RESULTS "<ul>\n";
foreach my $markedbug (sort keys %markedBugs) {
    if ($openSFBugs{$markedbug}) {
        # all is fine
    } elsif ($allSFBugs{$markedbug}) {
        # print "Marked bug is fixed!\n";
        print RESULTS "<li>Marked bug <a href=\"http://sourceforge.net/tracker/index.php?func=detail&aid=$markedbug&group_id=20024&atid=120024\">#$markedbug</a> in " .$markedBugs{$markedbug} . " is fixed!</li>\n";
        $count = $count + 1;
    } else {
        print RESULTS "<li>Marked bug #$markedbug is not reported within the CDK project.</li>\n";
    }
}
print "Marked bugs that are now fixed: $count\n";
print RESULTS "</ul>\n";

print RESULTS "</ul>\n";

print RESULTS "<h1>JUnit tests for reported bugs</h1>\n";
print RESULTS "<p>\n";
print RESULTS "Every reported bug should be tested by a JUnit test.\n";
print RESULTS "</p>\n";
print RESULTS "<ul>\n";

# check if marked bugs are no longer open
$count = 0;
print RESULTS "<h3>Reported bugs without JUnit tests</h3>\n";
print RESULTS "<ul>\n";
foreach my $bug (sort keys %allSFBugs) {
    if ($testedBugs{$bug}) {
        # all is fine
    } else {
        # print "Marked bug is fixed!\n";
        print RESULTS "<li>Bug <a href=\"http://sourceforge.net/tracker/index.php?func=detail&aid=$bug&group_id=20024&atid=120024\">#$bug</a> is not JUnit tested!</li>\n";
        $count = $count + 1;
    }
}
print "Bugs that are not JUnit tested: $count\n";
print RESULTS "</ul>\n";

print RESULTS "</ul>\n";

close(RESULTS);
