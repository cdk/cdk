<!-- ====================================================================== -->
<!-- Website DTD RDDL Module V2.0b1
     Part of the Website distribution
     http://sourceforge.net/projects/docbook/

     Please direct all questions and comments about this DTD to
     Norman Walsh, <ndw@nwalsh.com>.
                                                                            -->
<!-- ====================================================================== -->

<!ENTITY % rddl-prefix "rddl:">
<!ENTITY % rddl-suffix ":rddl">

<!ENTITY % rddl-namespace "'http://www.rddl.org/'">
<!ENTITY % rddl-xmlns  "xmlns%rddl-suffix;">

<!ENTITY % rddl-resource "%rddl-prefix;resource">

<!ELEMENT %rddl-resource; (%para.char.mix;)*>

<!ATTLIST %rddl-resource;
	id		ID	#IMPLIED
	xml:lang	NMTOKEN	#IMPLIED
	xml:base	CDATA	#IMPLIED
	%rddl-xmlns;	CDATA	#FIXED %rddl-namespace;
	%xlink-xmlns;	CDATA	#FIXED %xlink-namespace;
	%xlink-type;	(simple) #FIXED "simple"
	%xlink-arcrole;	CDATA	#IMPLIED
	%xlink-role;	CDATA	"http://www.rddl.org/#resource"
	%xlink-href;	CDATA	#IMPLIED
	%xlink-title;	CDATA	#IMPLIED
>
<!--
	%xlink-embed;	CDATA	#FIXED   "none"
	%xlink-actuate;	CDATA	#FIXED   "none"
>
-->

<!-- End of rddl.mod V2.0b1 ............................................... -->
<!-- ...................................................................... -->
