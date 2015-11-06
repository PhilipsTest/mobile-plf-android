#!/usr/bin/env perl
$in_file_name = "../Source/CatalogApp/build.gradle";
$temp_file_name = $in_file_name . ".inserted-maven";
open(INFILE, "<$in_file_name") || die("cannot open in file");
open(OUTFILE, ">$temp_file_name") || die("cannot open temp file");

$count = 0;
$in_all_project_block = 0;

while (<INFILE>) {
  $out_line = $_;
  if ($in_all_project_block == 0) {
    print "not found all project block yet\n";
    if ($_ =~ "allprojects") {
      $in_all_project_block = 1;
      print "found all project block! counting braces\n";
    }
  }

  if ($in_all_project_block == 1) {
    if ($_ =~ "{") {
      $count++;
      print "found open brace, count: " . $count . "\n";
    }
    
    if ($_ =~ "}") {
      $count--;
      print "found close brace, count: " . $count . "\n";
      if ($count == 0) {
        print "close brace of allprojects block found -> inserting maven url block\n";
        $line = ("maven { url 'http://maartens-mini.ddns.htc.nl.philips.com:8081/artifactory/libs-release-local' }\n") ;
        print OUTFILE $line;
      }
    }
  }
  print OUTFILE $out_line;
}

close INFILE;
close OUTFILE;

unlink $in_file_name;
rename $temp_file_name, $in_file_name;
