<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:html='http://www.w3.org/1999/xhtml'
                xmlns:doc="http://nwalsh.com/xsl/documentation/1.0"
                exclude-result-prefixes="doc html"
                version="1.0">

<xsl:output method="html"/>

<!-- ==================================================================== -->
<xsl:param name="header.hr" select="1"/>

<doc:param name="header.hr" xmlns="">
<refpurpose>Toggle &lt;HR> after header</refpurpose>
<refdescription>
<para>If non-zero, an &lt;HR> is generated at the top of each web page,
after the heaader.</para>
</refdescription>
</doc:param>

<!-- ==================================================================== -->
<xsl:param name="footer.hr" select="1"/>

<doc:param name="footer.hr" xmlns="">
<refpurpose>Toggle &lt;HR> before footer</refpurpose>
<refdescription>
<para>If non-zero, an &lt;HR> is generated at the bottom of each web page,
before the footer.</para>
</refdescription>
</doc:param>

<!-- ==================================================================== -->
<xsl:param name="feedback.href"></xsl:param>

<doc:param name="feedback.href" xmlns="">
<refpurpose>HREF for feedback link</refpurpose>
<refdescription>
<para>The <varname>feedback.href</varname> value is used as the value
for the <sgmltag class="attribute">href</sgmltag> attribute on the feedback
link. If <varname>feedback.href</varname>
is empty, no feedback link is generated.</para>
</refdescription>
</doc:param>

<!-- ==================================================================== -->
<xsl:param name="feedback.with.ids" select="0"/>

<doc:param name="feedback.with.ids" xmlns="">
<refpurpose>Toggle use of IDs in feedback</refpurpose>
<refdescription>
<para>If <varname>feedback.with.ids</varname> is non-zero, the ID of the
current page will be added to the feedback link. This can be used, for
example, if the <varname>feedback.href</varname> is a CGI script.</para>
</refdescription>
</doc:param>

<!-- ==================================================================== -->
<xsl:param name="feedback.link.text">Feedback</xsl:param>

<doc:param name="feedback.link.text" xmlns="">
<refpurpose>The text of the feedback link</refpurpose>
<refdescription>
<para>The contents of this variable is used as the text of the feedback
link if <varname>feedback.href</varname> is not empty. If
<varname>feedback.href</varname> is empty, no feedback link is
generated.</para>
</refdescription>
</doc:param>

<!-- ==================================================================== -->
<xsl:param name="filename-prefix" select="''"/>

<doc:param name="filename-prefix" xmlns="">
<refpurpose>Prefix added to all filenames</refpurpose>
<refdescription>
<para>To produce the <quote>text-only</quote> (that is, non-tabular) layout
of a website simultaneously with the tabular layout, the filenames have to
be distinguished. That's accomplished by adding the
<varname>filename-prefix</varname> to the front of each filename.</para>
</refdescription>
</doc:param>

<!-- ==================================================================== -->
<xsl:param name="autolayout-file" select="'autolayout.xml'"/>

<doc:param name="autolayout-file" xmlns="">
<refpurpose>Identifies the autolayout.xml file</refpurpose>
<refdescription>
<para>When the source pages are spread over several directories, this
parameter can be set (for example, from the command line of a batch-mode
XSLT processor) to indicate the location of the autolayout.xml file.</para>
<para>FIXME: for browser-based use, there needs to be a PI for this...
</para>
</refdescription>
</doc:param>

<!-- ==================================================================== -->
<xsl:param name="output-root" select="'.'"/>

<doc:param name="ouput-root" xmlns="">
<refpurpose>Specifies the root directory of the website</refpurpose>
<refdescription>
<para>When using the XSLT processor to manage dependencies and construct
the website, this parameter can be used to indicate the root directory
where the resulting pages are placed.</para>
<para>Only applies when XSLT-based chunking is being used.</para>
</refdescription>
</doc:param>

<!-- ==================================================================== -->
<xsl:param name="dry-run" select="'0'"/>

<doc:param name="dry-run" xmlns="">
<refpurpose>Indicates that no files should be produced</refpurpose>
<refdescription>
<para>When using the XSLT processor to manage dependencies and construct
the website, this parameter can be used to suppress the generation of
new and updated files. Effectively, this allows you to see what the
stylesheet would do, without actually making any changes.</para>
<para>Only applies when XSLT-based chunking is being used.</para>
</refdescription>
</doc:param>

<!-- ==================================================================== -->
<xsl:param name="rebuild-all" select="'0'"/>

<doc:param name="" xmlns="">
<refpurpose>Indicates that all files should be produced</refpurpose>
<refdescription>
<para>When using the XSLT processor to manage dependencies and construct
the website, this parameter can be used to regenerate the whole website,
updating even pages that don't appear to need to be updated.</para>
<para>The dependency extension only looks at the source documents. So
if you change something in the stylesheet, for example, that has a global
effect, you can use this parameter to force the stylesheet to rebuild the
whole website.
</para>
<para>Only applies when XSLT-based chunking is being used.</para>
</refdescription>
</doc:param>

<!-- ==================================================================== -->
<xsl:param name="nav.table.summary">Navigation</xsl:param>

<doc:param name="nav.table.summary" xmlns="">
<refpurpose>HTML Table summary attribute value for navigation tables</refpurpose>
<refdescription>
<para>The value of this parameter is used as the value of the table
summary attribute for the navigation table.</para>
<para>Only applies with the tabular presentation is being used.</para>
</refdescription>
</doc:param>

<!-- ==================================================================== -->
<xsl:param name="navtocwidth">220</xsl:param>

<doc:param name="navtocwidth" xmlns="">
<refpurpose>Specifies the width of the navigation table TOC</refpurpose>
<refdescription>
<para>The width, in pixels, of the navigation column.</para>
<para>Only applies with the tabular presentation is being used.</para>
</refdescription>
</doc:param>

<!-- ==================================================================== -->
<xsl:param name="navbodywidth"></xsl:param>

<doc:param name="navbodywidth" xmlns="">
<refpurpose>Specifies the width of the navigation table body</refpurpose>
<refdescription>
<para>The width of the body column.</para>
<para>Only applies with the tabular presentation is being used.</para>
</refdescription>
</doc:param>

<!-- ==================================================================== -->
<xsl:param name="textbgcolor">white</xsl:param>

<doc:param name="textbgcolor" xmlns="">
<refpurpose>The background color of the table body</refpurpose>
<refdescription>
<para>The background color of the table body.</para>
<para>Only applies with the tabular presentation is being used.</para>
</refdescription>
</doc:param>

<!-- ==================================================================== -->
<xsl:param name="navbgcolor">#FCF0DA</xsl:param>
<!-- was: xsl:param name="navbgcolor">#4080FF</xsl:param -->

<doc:param name="navbgcolor" xmlns="">
<refpurpose>The background color of the navigation TOC</refpurpose>
<refdescription>
<para>The background color of the navigation TOC.</para>
<para>Only applies with the tabular presentation is being used.</para>
</refdescription>
</doc:param>

<!-- ==================================================================== -->
<xsl:param name="toc.spacer.graphic" select="1"/>

<doc:param name="toc.space.graphic" xmlns="">
<refpurpose>Use graphic for TOC spacer?</refpurpose>
<refdescription>
<para>If non-zero, the indentation in the TOC will be accomplished
with the graphic identified by <varname>toc.spacer.image</varname>.
</para>
<para>Only applies with the tabular presentation is being used.</para>
</refdescription>
</doc:param>

<!-- ==================================================================== -->
<xsl:param name="toc.spacer.text">&#160;&#160;&#160;</xsl:param>

<doc:param name="toc.spacer.text" xmlns="">
<refpurpose>The text for spacing the TOC</refpurpose>
<refdescription>
<para>If <varname>toc.spacer.graphic</varname> is zero, this text string
will be used to indent the TOC.</para>
<para>Only applies with the tabular presentation is being used.</para>
</refdescription>
</doc:param>

<!-- ==================================================================== -->
<xsl:param name="toc.spacer.image">graphics/blank.gif</xsl:param>

<doc:param name="toc.spacer.image" xmlns="">
<refpurpose>The image for spacing the TOC</refpurpose>
<refdescription>
<para>If <varname>toc.spacer.graphic</varname> is non-zero, this image
will be used to indent the TOC.</para>
<para>Only applies with the tabular presentation is being used.</para>
</refdescription>
</doc:param>

<!-- ==================================================================== -->
<xsl:param name="toc.pointer.graphic" select="1"/>

<doc:param name="toc.space.graphic" xmlns="">
<refpurpose>Use graphic for TOC pointer?</refpurpose>
<refdescription>
<para>If non-zero, the indentation in the TOC will be accomplished
with the graphic identified by <varname>toc.pointer.image</varname>.
</para>
<para>Only applies with the tabular presentation is being used.</para>
</refdescription>
</doc:param>

<!-- ==================================================================== -->
<xsl:param name="toc.pointer.text">&#160;&#160;&#160;</xsl:param>

<doc:param name="toc.pointer.text" xmlns="">
<refpurpose>The text for spacing the TOC</refpurpose>
<refdescription>
<para>If <varname>toc.pointer.graphic</varname> is zero, this text string
will be used to indent the TOC.</para>
<para>Only applies with the tabular presentation is being used.</para>
</refdescription>
</doc:param>

<!-- ==================================================================== -->
<xsl:param name="toc.pointer.image">graphics/blank.gif</xsl:param>

<doc:param name="toc.pointer.image" xmlns="">
<refpurpose>The image for spacing the TOC</refpurpose>
<refdescription>
<para>If <varname>toc.pointer.graphic</varname> is non-zero, this image
will be used to indent the TOC.</para>
<para>Only applies with the tabular presentation is being used.</para>
</refdescription>
</doc:param>

<!-- ==================================================================== -->
<xsl:param name="toc.blank.graphic" select="1"/>

<doc:param name="toc.space.graphic" xmlns="">
<refpurpose>Use graphic for TOC blank?</refpurpose>
<refdescription>
<para>If non-zero, the indentation in the TOC will be accomplished
with the graphic identified by <varname>toc.blank.image</varname>.
</para>
<para>Only applies with the tabular presentation is being used.</para>
</refdescription>
</doc:param>

<!-- ==================================================================== -->
<xsl:param name="toc.blank.text">&#160;&#160;&#160;</xsl:param>

<doc:param name="toc.blank.text" xmlns="">
<refpurpose>The text for spacing the TOC</refpurpose>
<refdescription>
<para>If <varname>toc.blank.graphic</varname> is zero, this text string
will be used to indent the TOC.</para>
<para>Only applies with the tabular presentation is being used.</para>
</refdescription>
</doc:param>

<!-- ==================================================================== -->
<xsl:param name="toc.blank.image">graphics/blank.gif</xsl:param>

<doc:param name="toc.blank.image" xmlns="">
<refpurpose>The image for spacing the TOC</refpurpose>
<refdescription>
<para>If <varname>toc.blank.graphic</varname> is non-zero, this image
will be used to indent the TOC.</para>
<para>Only applies with the tabular presentation is being used.</para>
</refdescription>
</doc:param>

<!-- ==================================================================== -->
<xsl:param name="suppress.homepage.title" select="'1'"/>

<doc:param name="" xmlns="">
<refpurpose></refpurpose>
<refdescription>
<para></para>
</refdescription>
</doc:param>

<!-- ==================================================================== -->
<xsl:attribute-set name="body.attributes">
  <xsl:attribute name="bgcolor">white</xsl:attribute>
  <xsl:attribute name="text">black</xsl:attribute>
  <xsl:attribute name="link">#0000FF</xsl:attribute>
  <xsl:attribute name="vlink">#840084</xsl:attribute>
  <xsl:attribute name="alink">#0000FF</xsl:attribute>
</xsl:attribute-set>

<doc:attribute-set name="body.attributes" xmlns="">
<refpurpose></refpurpose>
<refdescription>
<para></para>
</refdescription>
</doc:attribute-set>

<!-- ==================================================================== -->
<xsl:param name="sequential.links" select="'0'"/>

<doc:param name="sequential.links" xmlns="">
<refpurpose></refpurpose>
<refdescription>
<para></para>
</refdescription>
</doc:param>

<!-- ==================================================================== -->
<xsl:param name="currentpage.marker" select="'@'"/>

<doc:param name="currentpage.marker" xmlns="">
<refpurpose></refpurpose>
<refdescription>
<para></para>
</refdescription>
</doc:param>

<!-- ==================================================================== -->

<doc:param name="" xmlns="">
<refpurpose></refpurpose>
<refdescription>
<para></para>
</refdescription>
</doc:param>

<!-- ==================================================================== -->

<doc:param name="" xmlns="">
<refpurpose></refpurpose>
<refdescription>
<para></para>
</refdescription>
</doc:param>

<!-- ==================================================================== -->


</xsl:stylesheet>
