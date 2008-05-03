#!/usr/bin/perl
use strict;
use diagnostics;

if (scalar @ARGV != 1) {
  print "Syntax: checkSVNKeywords.pl <MODULE>\n" ;
  exit(0);
}

my $module = $ARGV[0];

print "Processing module $module...\n";

my $listFilename = "build/$module.javafiles";
open(FILELIST, "<$listFilename") || die "Cannot open the file $listFilename!\n";
open(REPORT, ">reports/$module.svnkeywordscheck");
while (<FILELIST>) {
  my $cdkproject = 0;
  my $lgpl = 0;
  my $javaFile = $_;
  $javaFile =~ s/\n|\r//g;
  if ($module =~ /test/) {
    $javaFile = "src/test/$javaFile";
  } else {
    $javaFile = "src/main/$javaFile";
  }
  my @svnprops = `svn proplist $javaFile`;
  my $keywordsFound = 0;
  foreach my $prop (@svnprops) {
    $prop =~ s/\n|\r//g;
    if ($prop =~ m/svn:keywords/) {
      $keywordsFound = 1;
    }
  }
  if (!($keywordsFound)) {
    print REPORT "ERROR: no keywords set.\n";
    print "Missing svn:keywords on $javaFile... ";
    `svn propset svn:keywords "Author Date Id Revision" $javaFile`;
    print "fixed it.\n";
  }
}
print "\n";
