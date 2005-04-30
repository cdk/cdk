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

  <xsl:template match="pmd">
    <xsl:param name="date" select="./@timestamp" />
    <p>
      This report was generated with PMD (v.<xsl:value-of select="./@version"/>)
      on <xsl:value-of select="substring($date, 1, 10)"/> at
      <xsl:value-of select="substring($date, 12, 8)"/>.
    </p>
    <xsl:for-each select="//file">
      <xsl:sort select="./name"/>
      <xsl:apply-templates select="."/>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="file">
  <b><xsl:value-of select="@name"/></b><br/>
  <ul>
    <xsl:for-each select=".//violation">
      <xsl:sort select="./@line"/>
      <xsl:apply-templates select="."/>
    </xsl:for-each>
  </ul>
  </xsl:template>
  
  <xsl:template match="violation">
    <xsl:value-of select="./@line"/><xsl:text> </xsl:text><xsl:value-of select="."/>
    <xsl:text>[</xsl:text><xsl:value-of select="./@rule"/><xsl:text>]</xsl:text><br/>
  </xsl:template>
  
</xsl:stylesheet>