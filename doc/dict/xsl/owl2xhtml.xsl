<?xml version="1.0"?>
<xsl:stylesheet xmlns="http://www.w3.org/1999/xhtml"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
  xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
  xmlns:owl="http://www.w3.org/2002/07/owl#"
  xmlns:dc="http://dublincore.org/"
  xmlns:cvs="https://www.cvshome.org/"
  xmlns:bibtex="http://bibtexml.sf.net/"
  version="1.0">

  <!-- $Author: egonw $
       $Date: 2005-11-17 22:46:40 +0100 (Do, 17 Nov 2005) $
       $Revision: 173 $ -->

  <xsl:output method="xml" indent="yes"
    omit-xml-declaration="no" encoding="utf-8"
    doctype-public="-//W3C//DTD XHTML 1.1 plus MathML 2.0 plus SVG 1.1//EN" 
    doctype-system="http://www.w3.org/2002/04/xhtml-math-svg/xhtml-math-svg.dtd"/>

  <xsl:key name="entryKey" match="*" use="@rdf:ID"/>

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

  <xsl:template match="rdf:RDF">
<html xml:lang="en">
<head>
<title><xsl:value-of select=".//owl:Ontology/rdfs:label"/></title>
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
<h1><xsl:value-of select=".//owl:Ontology/rdfs:label"/></h1>
<p>
  <xsl:for-each select="*[name(.)='Contributor']">
    <xsl:if test="position()=last() and last()!=1"><xsl:text> and </xsl:text></xsl:if>
    <i><xsl:value-of select="rdfs:label"/></i>
    <xsl:if test="position() &lt; (last()-1)"><xsl:text>, </xsl:text></xsl:if>
  </xsl:for-each>
</p>
<p>
  <xsl:apply-templates select=".//owl:Ontology/rdfs:comment"/>
</p>
<p>
  <!-- table of content -->
  [<a href="#Categories">Categories</a>]
  [<a href="#Entries">Algorithms</a>]
  [<a href="#Bibliography">Bibliography</a>]
  [<a href="#FinalNotes">Final Notes</a>]
</p>
<h2><a name="TOC">Table of Contents</a></h2>
<p>
  <!-- entries index -->
  <xsl:for-each select="*[name(.)='Category']">
    <xsl:sort select="@rdf:ID"/>
    <xsl:variable name="className" select="@rdf:ID"/>
    <xsl:if test="count(//*[name(.)='isClassifiedAs' and @rdf:resource=concat('#', $className)]) > 0">
      <b><xsl:value-of select="./rdfs:label"/></b>
      <xsl:for-each select="//*[name(.)='isClassifiedAs' and @rdf:resource=concat('#', $className)]">
        <div class="toc">
        <xsl:element name="a">
          <xsl:attribute name="href">#<xsl:value-of select="../@rdf:ID"/></xsl:attribute>
          <xsl:value-of select="../rdfs:label"/>
        </xsl:element>
        </div>
      </xsl:for-each>
    </xsl:if>
  </xsl:for-each>
</p>

<h2><a name="Categories">Categories</a></h2>
<xsl:for-each select=".//*[name(.)='Category']">
  <xsl:sort select="rdfs:label" order="ascending"/>
  <xsl:apply-templates select="."/>
</xsl:for-each>

<h2><a name="Entries">Descriptors</a></h2>
<xsl:for-each select=".//*[name(.)='Descriptor' or name(.)='MolecularDescriptor' or name(.)='AtomicDescriptor']">
  <xsl:sort select="rdfs:label" order="ascending"/>
  <xsl:apply-templates select="."/>
</xsl:for-each>

<h2><a name="Bibliography">Bibliography</a></h2>
<p>
<xsl:for-each select=".//*[name(.)='Reference']">
  <xsl:sort select="@rdf:ID" order="ascending"/>
  <xsl:apply-templates select="."/>
</xsl:for-each>
</p>
<h2><a name="FinalNotes">Final Notes</a></h2>
<p>
  This dictionary 
  [<xsl:value-of select="/rdf:RDF/owl:Ontology/cvs:revision"/>]
  was last modified by 
  [<xsl:value-of select="/rdf:RDF/owl:Ontology/cvs:last-changed-by"/>]
  on
  [<xsl:value-of select="/rdf:RDF/owl:Ontology/dc:date"/>].
  The OWL source can be found in
  <xsl:element name="a">
    <xsl:attribute name="href">http://cvs.sourceforge.net/viewcvs.py/qsar/qsar-dicts/descriptor-algorithms.owl?view=markup</xsl:attribute>
    CVS
  </xsl:element>
</p>
<p>
  Additions to this dictionary can be sent to the 
  <a href="http://sourceforge.net/mail/?group_id=107219">QSAR project developers list (qsar-devel)</a>.
  If you find an error on this page, please post a bug report then on
  <a href="http://sourceforge.net/tracker/?group_id=107219&amp;atid=647049">this page</a>.
  In <i>Category</i> you can select the dictionary which contains the error.
</p>
<p>
  <a href="http://validator.w3.org/check?uri=referer"><img
      src="http://www.w3.org/Icons/valid-xhtml10"
      alt="Valid XHTML 1.0!" height="31" width="88" /></a>
  <a href="http://sourceforge.net"><img 
      src="http://sourceforge.net/sflogo.php?group_id=107219&amp;type=1" 
      width="88" height="31" border="0" alt="SourceForge.net Logo" /></a>
</p>
</body>
</html>
  </xsl:template>

  <xsl:template match="*[name(.)='Category']">
<h3><xsl:element name="a">
      <xsl:attribute name="name"><xsl:value-of select="@rdf:ID"/></xsl:attribute>
      <xsl:value-of select=".//*[name(.)='rdfs:label']"/>
    </xsl:element>
</h3>
<ul>
<li><p>
  <xsl:if test="./*[name(.)='definition']">
    <b>Definition</b><br/>
    <xsl:apply-templates select="./*[name(.)='definition']"/>
  </xsl:if>
</p>
</li>
</ul>
  </xsl:template>

  <xsl:template match="*[name(.)='Descriptor' or name(.)='MolecularDescriptor' or name(.)='AtomicDescriptor']">
<h3><xsl:element name="a">
      <xsl:attribute name="name"><xsl:value-of select="@rdf:ID"/></xsl:attribute>
      <xsl:value-of select=".//*[name(.)='rdfs:label']"/>
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
    <xsl:apply-templates select="./*[name(.)='description']"/>
  </xsl:if>
</p>
<p>
  <b>Ontological Relations</b><br />
  <xsl:if test="./*[name(.)='isClassifiedAs']">
    <xsl:for-each select="./*[name(.)='isClassifiedAs']">
      <xsl:variable name="citeID" select="substring(@rdf:resource,2)"/>
      <xsl:value-of select="//owl:ObjectProperty[@rdf:ID='isClassifiedAs']/rdfs:label"/><xsl:text>: </xsl:text>
      <xsl:element name="a">
      <xsl:attribute name="href">#<xsl:value-of select="$citeID"/></xsl:attribute>
      <xsl:value-of select="//*[@rdf:ID=$citeID]/rdfs:label"/>
      </xsl:element>
      <xsl:if test="position()!=last()"><xsl:text>, </xsl:text></xsl:if>
    </xsl:for-each><xsl:text>.</xsl:text>
    <br />
  </xsl:if>
  <xsl:if test="./*[name(.)='isA']">
    <xsl:for-each select="./*[name(.)='isA']">
      <xsl:variable name="citeID" select="substring(@rdf:resource,2)"/>
      <xsl:value-of select="//owl:ObjectProperty[@rdf:ID='isA']/rdfs:label"/><xsl:text>: </xsl:text>
      <xsl:element name="a">
      <xsl:attribute name="href">#<xsl:value-of select="$citeID"/></xsl:attribute>
      <xsl:value-of select="//*[@rdf:ID=$citeID]/rdfs:label"/>
      </xsl:element>
      <xsl:if test="position()!=last()"><xsl:text>, </xsl:text></xsl:if>
    </xsl:for-each><xsl:text>.</xsl:text>
    <br />
  </xsl:if>

  <!-- the Google.com search link -->
  <b>Implementations</b><br />
  <xsl:text>Search implementations on </xsl:text>
  <xsl:element name="a">
    <!-- <xsl:attribute name="href">http://www.google.com/search?q=http%3A//qsar.sf.net/dicts/blue-obelisk/%23<xsl:value-of select="./@id"/>&amp;ie=UTF-8&amp;oe=UTF-8</xsl:attribute> -->
    <xsl:attribute name="href">http://www.google.com/search?q=descriptor-algorithms:<xsl:value-of select="@rdf:ID"/>&amp;ie=UTF-8&amp;oe=UTF-8</xsl:attribute>
    Google.com
  </xsl:element>
</p>
</li>
</ul>
<p class="small"><i>
Contributed by:
<xsl:for-each select="./dc:contributor">
  <xsl:variable name="contributorID" select="substring(@rdf:resource,2)"/>
  <xsl:value-of select="key('entryKey',$contributorID)/."/>
  <xsl:if test="position()!=last()"><xsl:text>, </xsl:text></xsl:if>
</xsl:for-each><xsl:text>.</xsl:text>
Created on: <xsl:value-of select="./dc:date"/>.
</i></p>
  </xsl:template>

  <xsl:template match="*[name(.)='metadataList']">
    <xsl:apply-templates select="./*[name(.)='metadata']"/>
  </xsl:template>
  
  <xsl:template match="*[name(.)='metadata']">
    <xsl:if test="./@dictRef='blue-obelisk-metadata:category'">
      <xsl:variable name="metaEntryID" select="substring-after(./@content, ':')"/>
      <xsl:text>This algorithm is classified as: </xsl:text>
      <xsl:element name="a">
        <xsl:attribute name="href">http://qsar.sourceforge.net/dicts/blue-obelisk-metadata/index.xhtml#<xsl:value-of select="$metaEntryID"/></xsl:attribute>
        <xsl:for-each select="$metadataDoc">
          <xsl:value-of select="key('entryKey',$metaEntryID)/@term"/>
        </xsl:for-each>
      </xsl:element><br />
    </xsl:if>
  </xsl:template>
  
  <xsl:template match="*[name(.)='relatedEntry']">
    <xsl:variable name="relationType" select="substring-after(./@type, ':')"/>
    <xsl:variable name="entryID" select="./@href"/>
    <xsl:element name="a">
      <xsl:attribute name="href">http://qsar.sourceforge.net/dicts/blue-obelisk-metadata/index.xhtml#<xsl:value-of select="$relationType"/></xsl:attribute>
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

  <xsl:template match="*[name(.)='Reference']">
    <xsl:element name="a">
      <xsl:attribute name="name">bibtex:<xsl:value-of select="@rdf:ID"/></xsl:attribute>
      [<xsl:value-of select="@rdf:ID"/>]
    </xsl:element>
    <xsl:apply-templates select="./*"/>
    <br/>
  </xsl:template>

</xsl:stylesheet>
