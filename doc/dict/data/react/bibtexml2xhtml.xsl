<?xml version="1.0"?>

<!-- this XSL is taken from http://www.cs.duke.edu/~sprenkle/bibtex2html/ and
     copyright (c) 2003 licensed with GPL by Sara E. Sprenkle -->

<!--
    File    : $Id: bibtexml2xhtml.xsl 104 2005-02-17 15:34:07Z egonw $
    Abstract: Transform bibteXML file into a publication list HTML page
  -->

<!--
	Modified by SES 2003.01.15
	
	&#160; = &nbsp;

  -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:bibxml="http://bibtexml.sf.net/"
                xmlns="http://www.w3.org/1999/xhtml"
                version="1.0">

<!-- 
   - bibtex entry types 
   - follows bibtex specs mostly 
   -->


<xsl:template match="bibxml:article">
	<xsl:apply-templates select="bibxml:author"/>
	<xsl:apply-templates select="bibxml:title"/>
	<xsl:apply-templates select="bibxml:journal"/>

	<xsl:choose>
	<xsl:when test="bibxml:volume != '' and (not(bibxml:number) or bibxml:number = '')">
		<xsl:apply-templates select="bibxml:volume"/>
	</xsl:when>
	<xsl:when test="bibxml:volume != '' and bibxml:number != '' ">
		<xsl:value-of select="bibxml:volume"/>
		(<xsl:value-of select="bibxml:number"/>).
	</xsl:when>
	<!-- only a number, no volume -->
	<xsl:when test="bibxml:number != ''">
		no. <xsl:apply-templates select="bibxml:number"/>.
	</xsl:when>
	</xsl:choose>
	<xsl:apply-templates select="bibxml:month"/>
	<xsl:apply-templates select="bibxml:year"/>
	<xsl:apply-templates select="bibxml:pages"/>
	<xsl:apply-templates select="bibxml:doi"/>
</xsl:template>

<xsl:template match="bibxml:inproceedings | bibxml:conference">
	<xsl:apply-templates select="bibxml:author"/>
	<xsl:apply-templates select="bibxml:title"/>
	<xsl:apply-templates select="bibxml:booktitle"/>
	<xsl:if test="bibxml:organization != ''">
		<xsl:value-of select="bibxml:organization"/>.
	</xsl:if>
	<xsl:apply-templates select="bibxml:address"/>
	<xsl:apply-templates select="bibxml:month"/>
	<xsl:apply-templates select="bibxml:year"/>
	<xsl:apply-templates select="bibxml:pages"/>
	<xsl:apply-templates select="bibxml:url"/>
</xsl:template>

<xsl:template match="bibxml:techreport">
	<xsl:apply-templates select="bibxml:author"/>
	<xsl:apply-templates select="bibxml:title"/>
	<xsl:apply-templates select="bibxml:booktitle"/>
	<xsl:value-of select="bibxml:institution"/>.
	<xsl:if test="bibxml:number != ''">
		<xsl:value-of select="bibxml:number"/>.
	</xsl:if>
	<xsl:if test="bibxml:type != ''">
		<xsl:value-of select="bibxml:type"/>.
	</xsl:if>
	<xsl:apply-templates select="bibxml:month"/>
	<xsl:apply-templates select="bibxml:year"/>
	<xsl:apply-templates select="bibxml:url"/>
</xsl:template>

<xsl:template match="bibxml:book">
	<xsl:apply-templates select="bibxml:author"/>
	<xsl:apply-templates select="bibxml:title"/>
	<xsl:if test="bibxml:editor != ''">
		<xsl:value-of select="bibxml:editor"/>.
	</xsl:if>
	<xsl:if test="bibxml:publisher != ''">
		<xsl:value-of select="bibxml:publisher"/>.
	</xsl:if>
	<xsl:if test="bibxml:series != ''">
		<xsl:value-of select="bibxml:series"/>.
	</xsl:if>
	<xsl:apply-templates select="bibxml:volume"/>
	<xsl:apply-templates select="bibxml:edition"/>
	<xsl:apply-templates select="bibxml:address"/>
	<xsl:apply-templates select="bibxml:month"/>
	<xsl:apply-templates select="bibxml:year"/>
</xsl:template>

<xsl:template match="bibxml:phdthesis | bibxml:masterthesis">
	<xsl:apply-templates select="bibxml:author"/>
	<xsl:apply-templates select="bibxml:title"/>
	<xsl:if test="bibxml:school != ''">
		<xsl:value-of select="bibxml:school"/>.
	</xsl:if>
	<xsl:if test="bibxml:number != ''">
		<xsl:value-of select="bibxml:number"/>.
	</xsl:if>
	<xsl:apply-templates select="bibxml:year"/>
</xsl:template>

<xsl:template match="bibxml:misc">
	<xsl:apply-templates select="bibxml:author"/>
	<xsl:if test="bibxml:title != ''">
		<xsl:apply-templates select="bibxml:title"/>
	</xsl:if>
	<xsl:if test="bibxml:howpublished != ''">
		<xsl:value-of select="bibxml:howpublished"/>.
	</xsl:if>
	<xsl:if test="bibxml:url != ''">
		<xsl:apply-templates select="bibxml:url"/>.
	</xsl:if>
	<xsl:apply-templates select="bibxml:month"/>
	<xsl:apply-templates select="bibxml:year"/>
</xsl:template>

<xsl:template match="bibxml:unpublished">
	<xsl:apply-templates select="bibxml:author"/>
	<xsl:apply-templates select="bibxml:title"/>
	<xsl:apply-templates select="bibxml:note"/>
</xsl:template>

<xsl:template match="bibxml:manual">
	<xsl:apply-templates select="bibxml:author"/>
	<xsl:apply-templates select="bibxml:title"/>
	<xsl:if test="bibxml:organization != ''">
		<xsl:value-of select="bibxml:organization"/>.
	</xsl:if>
	<xsl:apply-templates select="bibxml:edition"/>
	<xsl:apply-templates select="bibxml:address"/>
	<xsl:apply-templates select="bibxml:month"/>
	<xsl:apply-templates select="bibxml:year"/>
	<xsl:apply-templates select="bibxml:note"/>
</xsl:template>

<xsl:template match="bibxml:proceedings">
	<xsl:apply-templates select="bibxml:title"/>
	<xsl:if test="bibxml:editor != ''">
		<xsl:value-of select="bibxml:editor"/>.
	</xsl:if>
	<xsl:if test="bibxml:publisher != ''">
		<xsl:value-of select="bibxml:publisher"/>.
	</xsl:if>
	<xsl:if test="bibxml:organization != ''">
		<xsl:value-of select="bibxml:organization"/>.
	</xsl:if>
	<xsl:apply-templates select="bibxml:address"/>
	<xsl:apply-templates select="bibxml:month"/>
	<xsl:apply-templates select="bibxml:year"/>
	<xsl:apply-templates select="bibxml:note"/>
</xsl:template>

<xsl:template match="bibxml:booklet">
	<xsl:apply-templates select="bibxml:author"/>
	<xsl:apply-templates select="bibxml:title"/>
	<xsl:if test="bibxml:howpublished != ''">
		<xsl:value-of select="bibxml:howpublished"/>.
	</xsl:if>
	<xsl:apply-templates select="bibxml:address"/>
	<xsl:apply-templates select="bibxml:month"/>
	<xsl:apply-templates select="bibxml:year"/>
	<xsl:apply-templates select="bibxml:note"/>
</xsl:template>


<xsl:template match="bibxml:inbook">
	<xsl:apply-templates select="bibxml:author"/>
	<xsl:apply-templates select="bibxml:title"/>
	<xsl:if test="bibxml:chapter != ''">
		<xsl:value-of select="bibxml:chapter"/>.
	</xsl:if>
	<xsl:if test="bibxml:editor != ''">
		Editted by <xsl:value-of select="bibxml:editor"/>.
	</xsl:if>
	<xsl:if test="bibxml:howpublished != ''">
		<xsl:value-of select="bibxml:howpublished"/>.
	</xsl:if>
	<xsl:apply-templates select="bibxml:address"/>

	<xsl:if test="bibxml:publisher != ''">
		<xsl:value-of select="bibxml:publisher"/>.
	</xsl:if>
	<xsl:if test="bibxml:series != ''">
		<xsl:value-of select="bibxml:series"/>.
	</xsl:if>
	<xsl:apply-templates select="bibxml:volume"/>
	<xsl:apply-templates select="bibxml:edition"/>

	<xsl:apply-templates select="bibxml:month"/>
	<xsl:apply-templates select="bibxml:year"/>
	<xsl:apply-templates select="bibxml:pages"/>
</xsl:template>


<xsl:template match="bibxml:incollection">
	<xsl:apply-templates select="bibxml:author"/>
	<xsl:apply-templates select="bibxml:title"/>
	<xsl:apply-templates select="bibxml:booktitle"/>
	<xsl:if test="bibxml:editor != ''">
		Editted by <xsl:value-of select="bibxml:editor"/>.
	</xsl:if>
	<xsl:apply-templates select="bibxml:address"/>

	<xsl:if test="bibxml:publisher != ''">
		<xsl:value-of select="bibxml:publisher"/>.
	</xsl:if>

	<xsl:apply-templates select="bibxml:month"/>
	<xsl:apply-templates select="bibxml:year"/>
	<xsl:apply-templates select="bibxml:pages"/>
</xsl:template>

<!-- fields -->


<xsl:template match="bibxml:pages">
	<xsl:if test=". != ''">
	     pp. <xsl:value-of select="."/>.
	</xsl:if>
</xsl:template>

<xsl:template match="bibxml:month">
	<xsl:if test=". != ''">
		<xsl:value-of select="."/> </xsl:if>
</xsl:template>

<xsl:template match="bibxml:volume">
	<xsl:if test=". != ''">	
	     vol. <xsl:value-of select="."/>.
	</xsl:if>
</xsl:template>

<xsl:template match="bibxml:edition">
	<xsl:if test=". != ''">	
	     ed. <xsl:value-of select="."/>.
	</xsl:if>
</xsl:template>

<xsl:template match="bibxml:title">
	<xsl:if test=". != ''">	
		 <i><xsl:value-of select="."/></i>,
	</xsl:if>
</xsl:template>

<xsl:template match="bibxml:note">
	<xsl:if test=". != ''">
		<i>Note: </i>
		<xsl:value-of select="."/>.
	</xsl:if>
</xsl:template>

<xsl:template match="bibxml:address">
	<xsl:if test=". != ''">	
		 <xsl:value-of select="."/>.
	</xsl:if>
</xsl:template>


<xsl:template match="bibxml:booktitle | bibxml:journal">
     <xsl:if test=". != ''">
         <i><xsl:value-of select="."/></i>
     </xsl:if>
     <xsl:if test="position() + 1 != last()">. </xsl:if>
</xsl:template>

<xsl:template match="bibxml:year">
	<xsl:if test=". != ''">
	     <b><xsl:value-of select="."/></b>,
	</xsl:if>
</xsl:template>

<xsl:template match="bibxml:author">
	<xsl:value-of select="."/>
	<xsl:choose>
	<xsl:when test="position() = count(../bibxml:author)">. </xsl:when>
	<xsl:when test="count(../bibxml:author) = 2 and position() = 1"> and </xsl:when>
	<xsl:when test="position() + 1 != count(../bibxml:author)">, </xsl:when>
	<xsl:when test="position() + 1 = count(../bibxml:author)">, and </xsl:when>
	</xsl:choose>	
</xsl:template>

<xsl:template match="bibxml:editor |
                     bibxml:number | bibxml:series | bibxml:institution |
                    bibxml:organization |
                     bibxml:publisher | bibxml:school |
                     bibxml:type | bibxml:bookshelf | 
                     bibxml:annotate | bibxml:crossref |
                     bibxml:issn | bibxml:isbn |  bibxml:uri | 
                     bibxml:urn">
     <xsl:value-of select="."/>
     <xsl:if test="position() + 1 != last()">, </xsl:if>
</xsl:template>

<xsl:template match="bibxml:url">
	<xsl:if test=". != ''">	
		<a href="{.}">
		<xsl:value-of select="."/>	
		</a>
	</xsl:if>
</xsl:template>

<xsl:template match="bibxml:doi">
	<xsl:if test=". != ''">	
		<a href="http://dx.doi.org/{.}">
		<xsl:value-of select="."/>	
		</a>
	</xsl:if>
</xsl:template>

<!--
   - Do not print the following entries
   -->
<xsl:template match="bibxml:category | bibxml:key | 
                     bibxml:keywords"/>
</xsl:stylesheet>
