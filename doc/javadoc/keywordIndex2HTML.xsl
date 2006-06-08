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
<xsl:for-each select="//indexentry">
  <xsl:sort select='translate(.,"ABCDEFGHIJKLMNOPQRSTUVWXYZ","abcdefghijklmnopqrstuvwxyz")'/>
  <xsl:apply-templates select="."/>
</xsl:for-each>
  </xsl:template>

  <xsl:template match="indexentry">
    <xsl:choose>
      <xsl:when test="count(./secondaryie) > 0">
        <xsl:apply-templates select="./primaryie"/>
        <ul>
        <xsl:for-each select="./secondaryie">
          <li><xsl:apply-templates select="."/></li>
        </xsl:for-each>
        </ul>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select="./primaryie"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="primaryie">
    <b><xsl:value-of select="./keyword"/></b>
    <xsl:text> </xsl:text>
    <xsl:if test="./ulink">
    <ul>
    <xsl:for-each select="./ulink">
      <li><xsl:apply-templates select="."/></li>
    </xsl:for-each>
    </ul>
    </xsl:if>
  </xsl:template>

  <xsl:template match="secondaryie">
    <b><xsl:value-of select="./keyword"/></b>
    <xsl:text> </xsl:text>
    <ul>
    <xsl:for-each select="./ulink">
      <li><xsl:apply-templates select="."/></li>
    </xsl:for-each>
    </ul>
  </xsl:template>
  
  <xsl:template match="ulink">
    <xsl:element name="a">
      <xsl:attribute name="href">http://cdk.sf.net<xsl:value-of select="@url"/></xsl:attribute>
      <xsl:value-of select="."/>
    </xsl:element>
  </xsl:template>

</xsl:stylesheet>