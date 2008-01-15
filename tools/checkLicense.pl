#!/usr/bin/perl
use strict;
use diagnostics;

if (scalar @ARGV != 1) {
  print "Syntax: checkLicense.pl <MODULE>\n" ;
  exit(0);
}

my $module = $ARGV[0];

print "Processing module $module...\n";

my $listFilename = "build/$module.javafiles";
open(FILELIST, "<$listFilename") || die "Cannot open the file $listFilename!\n";
open(REPORT, ">reports/$module.licensecheck");
while (<FILELIST>) {
  my $copyright = 0;
  my $cdkproject = 0;
  my $copyrightLine = "";
  my $lgpl = 0;
  my $javaFile = $_;
  open(JAVAFILE, "<src/$javaFile") || die "Cannot open the file $javaFile!\n";
  my $line = "";
  print REPORT "File: $javaFile";
  while (<JAVAFILE>) {
    $line = $_;
    if ($line =~ /copyright\s*\(C\)/i) {
      $copyright = 1;
      if ($line =~ /CDK.*Project/i) {
        $cdkproject = 1;
      }
      $copyrightLine = $line;
    }
    if ($line =~ /GNU\sLesser\sGeneral\sPublic/) {
      $lgpl = 1;
    }
  }
  if ($copyright == 1) {
    if ($lgpl != 1) {
      print REPORT "ERROR: incorrect license. Must be LGPL.\n";
      print REPORT $copyrightLine;
      print "l";
    } elsif ($cdkproject == 1) {
      print REPORT "ERROR: incorrect copyright holder. Cannot be the CDK project.\n";
      print REPORT $copyrightLine;
      print "c";
    } else {
      print ".";
    }
  } else {
      print REPORT "ERROR: missing copyright header.\n";
      print "m";
  }
}
print "\n";