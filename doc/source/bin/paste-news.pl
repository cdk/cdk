#!/usr/bin/perl
$grphome='/home/groups/c/cd/cdk';
open(TEMPL,$grphome.'/htdocs/index_template.html');
local $/=undef;
$file=<TEMPL>;
close(TEMPL);
open(SNIP,$grphome."/projhtml.cache");
local $/=undef;
$snip=<SNIP>;
close(SNIP);
$smallsnip="<small>".$snip."</small>";
$file=~ s/######/$smallsnip/emg;
$index=$grphome.'/htdocs/index.html';
open(HTML,">$index");
print HTML $file;
close HTML;
