<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://www.w3.org/1999/xhtml"
                version="1.0">

  <!-- $Author: mrc $
       $Date: 2006/05/08 17:00:44 $
       $Revision: 0.1 $ -->

  <xsl:output method="xml" indent="yes"
    omit-xml-declaration="no" encoding="utf-8"
    doctype-public="-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN" 
    doctype-system="http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd"/>

  <xsl:variable name="metadataDoc" select="document('reaction-types-metadata.xml')"/>

  <xsl:key name="entryKey" match="*" use="@id"/>

  <xsl:include href="bibtexml2xhtml.xsl"/>
  
  <xsl:template match="*">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="*[namespace-uri(.)='http://www.w3.org/1998/Math/MathML']">
    <xsl:element name="{name(.)}">
      <xsl:apply-templates select="text()|*"/>
    </xsl:element>
  </xsl:template>
  
  <xsl:template match="*[name(.)='mrow']">
    <xsl:element name="math">
      <xsl:element name="mrow">
        <xsl:apply-templates select="*"/>
      </xsl:element>
    </xsl:element>
  </xsl:template>
  
  <xsl:template match="text()">
    <xsl:value-of select="."/>
  </xsl:template>

  <xsl:template match="/">
<html xml:lang="en">
<head>
<title><xsl:value-of select=".//*[name(.)='dictionary']/@title"/>
[<xsl:value-of select=".//*[name(.)='dictionary']/@id"/>]</title>
<style type="text/css">
p.small {font-size: 70%}
a:hover {background-color: #000000; color: white; text-decoration: none}
div.toc { margin-left: 2em; }
h1 {background-color: #CCCCCC}
h2 {background-color: #DDDDDD}
h3 {background-color: #EEEEEE}
</style>
</head>
<body>
<h1><xsl:value-of select=".//*[name(.)='dictionary']/@title"/></h1>
<p>
  <xsl:for-each select=".//*[name(.)='contributor']">
    <xsl:if test="position()=last() and last()!=1"><xsl:text> and </xsl:text></xsl:if>
    <i><xsl:value-of select="."/></i>
    <xsl:if test="position() &lt; (last()-1)"><xsl:text>, </xsl:text></xsl:if>
  </xsl:for-each>
</p>
<p>
  <xsl:apply-templates select=".//*[name(.)='dictionary']/*[name(.)='description']"/>
</p>
<p>
  <!-- table of content -->
  [<a href="#Entries">Entries</a>]
  [<a href="#Bibliography">Bibliography</a>]
  [<a href="#FinalNotes">Final Notes</a>]
</p>
<xsl:if test="/*[name(.)='dictionary']/@id='reaction-types'">
<h2><a name="TOC">Table of Contents</a></h2>
<p>
  <!-- entries index -->
  <xsl:variable name="self" select="/"/>
  <xsl:for-each select="$metadataDoc//*[name(.)='entry']//*[name(.)='relatedEntry' and @type='reaction-types-metadata:instanceOf' and (@href='reactionType' or @href='reactionClass')]">
    <xsl:sort select="./@href"/>
    <xsl:variable name="className" select="../@id"/>
    <b><xsl:value-of select="../@term"/>s</b>
    <xsl:for-each select="$self//*[name(.)='metadata' and @content=concat('reaction-types-metadata:', $className)]">
      <div class="toc">
      <xsl:element name="a">
        <xsl:attribute name="href">#<xsl:value-of select="../../@id"/></xsl:attribute>
	<xsl:value-of select="../../@id"/>
      </xsl:element>
      </div>
    </xsl:for-each><br/>
  </xsl:for-each>
</p>
</xsl:if>
<h2><a name="Entries">Entries</a></h2>
<xsl:for-each select=".//*[name(.)='entry']">
  <xsl:sort select="./@term" order="ascending"/>
  <xsl:apply-templates select="."/>
</xsl:for-each>
<h2><a name="Bibliography">Bibliography</a></h2>
<p>
<xsl:for-each select=".//*[name(.)='bibtex:entry']">
  <xsl:sort select="./@id" order="ascending"/>
  <xsl:apply-templates select="."/>
</xsl:for-each>
</p>
<h2><a name="FinalNotes">Final Notes</a></h2>
<p>
  This dictionary 
  [<xsl:value-of select=".//*[name(.)='metadata' and ./@name='cvs:revision']/@content"/>]
  was last modified by 
  [<xsl:value-of select=".//*[name(.)='metadata' and ./@name='cvs:last-change-by']/@content"/>]
  on
  [<xsl:value-of select=".//*[name(.)='metadata' and ./@name='cvs:date']/@content"/>].
</p>
<p>
  <a href="http://validator.w3.org/check?uri=referer"><img
      src="http://www.w3.org/Icons/valid-xhtml10"
      alt="Valid XHTML 1.0!" height="31" width="88" /></a>
</p>
</body>
</html>
  </xsl:template>

  <xsl:template match="*[name(.)='entry']">
<h3><xsl:element name="a">
      <xsl:attribute name="id"><xsl:value-of select="./@id"/></xsl:attribute>
      <xsl:value-of select="./@term"/> (<xsl:value-of select="./@id"/>)
    </xsl:element>
</h3>
<ul>
<li><p>
  <xsl:if test="./*[name(.)='definition']">
    <b>Definition</b><br/>
    <xsl:apply-templates select="./*[name(.)='definition']"/>
  </xsl:if>
</p>
<p>
  <xsl:if test="./*[name(.)='description']">
    <b>Description</b><br/>
    <xsl:apply-templates select="./*[name(.)='description']"/> Examples 
	with JChemPaint Viewer <xsl:element name="a"> 
    	<xsl:attribute name="href">http://almost.cubic.uni-koeln.de/jrg/Members/mrc/reactionDict/examples/"./@id"/>&amp;ie=UTF-8&amp;oe=UTF-8</xsl:attribute>
 		here
    </xsl:element>

  </xsl:if>
</p>
<p>
  <xsl:if test="./*[name(.)='relatedEntry']">
    <b>Relations</b><br />
    <xsl:for-each select="./*[name(.)='relatedEntry']">
      <xsl:apply-templates select="."/><br />
    </xsl:for-each>
  </xsl:if>
  <xsl:if test="./*[name(.)='metadataList']">
    <b>Classification</b><br />
    <xsl:apply-templates select="./*[name(.)='metadataList']"/>
  </xsl:if> 
</p>

<!--<p>
  <xsl:if test="./*[name(.)='exemplesReaction']">
    <b>Exemple</b>
	<xsl:apply-templates select="./*[name(.)='exemplesReaction']"/>
    with JChemPaint Viewer <xsl:element name="a"> 
    	<xsl:attribute name="href">http://almost.cubic.uni-koeln.de/jrg/Members/mrc/reactionDict/examples/"./@id"/>&amp;ie=UTF-8&amp;oe=UTF-8</xsl:attribute>
 		here
    </xsl:element>
  </xsl:if>
</p>-->
	
	<!-- the Google.com search link -->
<p>
  <b>Implementations</b><br />
  <xsl:text>Search implementations on </xsl:text>
  <xsl:element name="a">
    <!-- <xsl:attribute name="href">http://www.google.com/search?q=http%3A//qsar.sf.net/dicts/qsar-descriptors/%23<xsl:value-of select="./@id"/>&amp;ie=UTF-8&amp;oe=UTF-8</xsl:attribute> -->
    <xsl:attribute name="href">http://www.google.com/search?<xsl:value-of select="./@term"/>&amp;ie=UTF-8&amp;oe=UTF-8</xsl:attribute>
    Google.com
  </xsl:element>
</p>

</li>
</ul>
<p class="small"><i>
Contributed by:
<xsl:for-each select="./*[name(.)='annotation']/*[name(.)='documentation']/*[name(.)='metadata' and @name='dc:contributor']">
  <xsl:variable name="contributorID" select="./@content"/>
  <xsl:value-of select="key('entryKey',$contributorID)/."/>
  <xsl:if test="position()!=last()"><xsl:text>, </xsl:text></xsl:if>
</xsl:for-each><xsl:text>.</xsl:text>
Created on: <xsl:value-of select="./*[name(.)='annotation']/*[name(.)='documentation']/*[name(.)='metadata' and @name='dc:date']/@content"/>.
</i></p>
  </xsl:template>

  <xsl:template match="*[name(.)='metadataList']">
    <xsl:apply-templates select="./*[name(.)='metadata']"/>
  </xsl:template>
  
  <xsl:template match="*[name(.)='metadata']">
    <xsl:if test="./@dictRef='reaction-types-metadata:reactionType'">
      <xsl:variable name="metaEntryID" select="substring-after(./@content, ':')"/>
      <xsl:text>This reaction is a </xsl:text>
      <xsl:element name="a">
        <xsl:attribute name="href">http://almost.cubic.uni-koeln.de/jrg/Members/mrc/reactionDict/reactionMetadata#<xsl:value-of select="$metaEntryID"/></xsl:attribute>
        <xsl:for-each select="$metadataDoc">
          <xsl:value-of select="key('entryKey',$metaEntryID)/@term"/>
        </xsl:for-each>
      </xsl:element><br />
    </xsl:if>
    <xsl:if test="./@dictRef='reaction-types-metadata:reactionClass'">
      <xsl:variable name="metaEntryID" select="substring-after(./@content, ':')"/>
      <xsl:text>This reaction is a </xsl:text>
      <xsl:element name="a">
        <xsl:attribute name="href">http://almost.cubic.uni-koeln.de/jrg/Members/mrc/reactionDict/reactionMetadata#<xsl:value-of select="$metaEntryID"/></xsl:attribute>
        <xsl:for-each select="$metadataDoc">
          <xsl:value-of select="key('entryKey',$metaEntryID)/@term"/>
        </xsl:for-each>
      </xsl:element><br />
    </xsl:if>
  </xsl:template>

  <xsl:template match="*[name(.)='exemplesReaction']">
    <center><xsl:apply-templates select="./*[name(.)='imagedata']"/></center>
  </xsl:template>
  
  <xsl:template match="*[name(.)='imagedata']">
    <xsl:if test="./@dictRef='reactions-metadata:CML'">
      <xsl:variable name="ReactionCML" select="./@content"/>
      <applet code="org.openscience.cdk.applications.jchempaint.applet.JChemPaintViewerOnlyApplet"
              name="JCPApplet" archive="jar/jchempaint-applet-core.jar"
	        width="500" height="150">
	<param name="load" value="{$ReactionCML}"/>
	<param name="compact" value="true"/>
      </applet>
    </xsl:if>
  </xsl:template>

  
  <xsl:template match="*[name(.)='relatedEntry']">
    <xsl:variable name="relationType" select="substring-after(./@type, ':')"/>
    <xsl:variable name="entryID" select="./@href"/>
    <xsl:element name="a">
      <xsl:attribute name="href">http://qsar.sourceforge.net/dicts/reaction-types-metadata/index.xhtml#<xsl:value-of select="$relationType"/></xsl:attribute>
      <xsl:for-each select="$metadataDoc">
        <xsl:value-of select="key('entryKey',$relationType)/@term"/>
      </xsl:for-each>
    </xsl:element>
    <xsl:text>: </xsl:text>
    <xsl:element name="a">
      <xsl:attribute name="href">#<xsl:value-of select="./@href"/></xsl:attribute>
      <xsl:value-of select="key('entryKey',$entryID)/@term"/>
    </xsl:element>
  </xsl:template>

  <xsl:template match="*[name(.)='bibtex:cite']">
    [<xsl:element name="a">
      <xsl:attribute name="href">#bibtex:<xsl:value-of select="./@ref"/></xsl:attribute>
      <xsl:value-of select="./@ref"/>
    </xsl:element>]
  </xsl:template>

  <xsl:template match="*[name(.)='bibtex:entry']">
    <xsl:element name="a">
      <xsl:attribute name="id">bibtex:<xsl:value-of select="./@id"/></xsl:attribute>
      [<xsl:value-of select="./@id"/>]
    </xsl:element>
    <xsl:apply-templates select="./*"/>
    <br/>
  </xsl:template>

</xsl:stylesheet>