<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:cl="http://cdk.sf.net/schema/changelog"
                version="1.0">

  <!-- $Author$
       $Date$
       $Revision$ -->

  <xsl:output method = "text"/>

  <xsl:template match="*">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="text()">
    <xsl:value-of select="."/>
  </xsl:template>

  <xsl:template match="cl:changelog">
    <xsl:text>CHANGELOG
</xsl:text>
    <xsl:apply-templates select=".//cl:release"/>
  </xsl:template>

  <xsl:template match="cl:release">
    <xsl:text>
Release </xsl:text><xsl:value-of select="./@id"/><xsl:text>
</xsl:text>
    <xsl:apply-templates select=".//cl:addition"/>
    <xsl:apply-templates select=".//cl:bugfix"/>
    <xsl:apply-templates select=".//cl:apiChange"/>
  </xsl:template>

  <xsl:template match="cl:addition">
      <xsl:text>- </xsl:text><xsl:value-of select="normalize-space(.)"/>
      <xsl:if test="@contributor"><xsl:text> [</xsl:text>
        <xsl:value-of select="@contributor"/>
        <xsl:text>]</xsl:text></xsl:if>
      <xsl:if test="@closes">
        <xsl:text> (closes #</xsl:text>
        <xsl:value-of select="@closes"/>
        <xsl:text>)</xsl:text>
      </xsl:if>
      <xsl:if test="@implements">
        <xsl:text> (closes RFE #</xsl:text>
        <xsl:value-of select="@implements"/>
        <xsl:text>)</xsl:text>
      </xsl:if>
      <xsl:text>
</xsl:text>
  </xsl:template>

  <xsl:template match="cl:bugfix">
      <xsl:text>- </xsl:text><xsl:value-of select="normalize-space(.)"/>
      <xsl:if test="@contributor">
        <xsl:text> [</xsl:text>
        <xsl:value-of select="@contributor"/>
        <xsl:text>]</xsl:text>
      </xsl:if>
      <xsl:if test="@closes">
        <xsl:text> (closes #</xsl:text>
        <xsl:value-of select="@closes"/>
        <xsl:text>)</xsl:text>
      </xsl:if>
      <xsl:if test="@implements">
        <xsl:text> (closes RFE #</xsl:text>
        <xsl:value-of select="@implements"/>
        <xsl:text>)</xsl:text>
      </xsl:if>
      <xsl:text>
</xsl:text>
  </xsl:template>

  <xsl:template match="cl:apiChange">
      <xsl:text>- </xsl:text><xsl:value-of select="normalize-space(.)"/>
      <xsl:if test="@contributor">
        <xsl:text> [</xsl:text>
        <xsl:value-of select="@contributor"/>
        <xsl:text>]</xsl:text>
      </xsl:if>
      <xsl:if test="@closes">
        <xsl:text> (closes #</xsl:text>
        <xsl:value-of select="@closes"/>
        <xsl:text>)</xsl:text>
      </xsl:if>
      <xsl:if test="@implements">
        <xsl:text> (closes RFE #</xsl:text>
        <xsl:value-of select="@implements"/>
        <xsl:text>)</xsl:text>
      </xsl:if>
      <xsl:text>
</xsl:text>
  </xsl:template>

</xsl:stylesheet>