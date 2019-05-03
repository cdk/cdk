<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://www.w3.org/1999/xhtml"
                version="1.0">

  <!-- $Author$
       $Date$
       $Revision$ -->

  <xsl:output method="xml" indent="yes"
    omit-xml-declaration="no" encoding="utf-8"/>

  <xsl:template match="*">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="text()">
    <xsl:value-of select="."/>
  </xsl:template>

  <xsl:template match="/">
<div><div>
<h3>Blue Obelisk Chemoinformatics Dictionary</h3>
</div></div>
<table>
<xsl:for-each select="//dictRef[./dictionary='blue-obelisk']">
  <xsl:sort select="."/>
  <xsl:apply-templates select="."/>
</xsl:for-each>
</table>

<div><div>
<h3>QSAR.sf.net Descriptors Dictionary</h3>
</div></div>
<table>
<xsl:for-each select="//dictRef[./dictionary='qsar-descriptors']">
  <xsl:sort select="."/>
  <xsl:apply-templates select="."/>
</xsl:for-each>
</table>
  </xsl:template>

  <xsl:template match="dictRef">
<tr>
  <td><xsl:value-of select="./dictionary"/><xsl:text>:</xsl:text><xsl:value-of select="./entry"/></td>
  <td>
   <xsl:element name="a">
     <xsl:attribute name="href">http://cdk.sf.net/<xsl:value-of select="./api"/></xsl:attribute>
     <xsl:value-of select="./class"/>
   </xsl:element>
  </td>
</tr>
  </xsl:template>

</xsl:stylesheet>