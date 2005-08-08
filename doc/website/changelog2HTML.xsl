<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:cl="http://cdk.sf.net/schema/changelog"
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

  <xsl:template match="cl:changelog">
    <h3>ChangeLog</h3>
    <xsl:apply-templates select=".//cl:release"/>
  </xsl:template>

  <xsl:template match="cl:release">
    <h3>Release <xsl:value-of select="./@id"/></h3>
    <!-- p>
      To this release contributed (at least):
      <xsl:for-each select=".//*/@contributor">
        <xsl:sort select="."/>
        <xsl:variable name="contributor" select="."/>
        <xsl:variable name="remainder" select="following::item[.=$contributor]"/>
        <xsl:value-of select="$remainder"/>
        <xsl:if test="not($remainder)">
           <xsl:value-of select="$contributor"/>
        </xsl:if>
        
      </xsl:for-each>
    </p -->
    <h4>Additions</h4>
    <ul>
      <xsl:apply-templates select=".//cl:addition"/>
    </ul>
    <h4>Bug fixes</h4>
    <ul>
      <xsl:apply-templates select=".//cl:bugfix"/>
    </ul>
    <h4>API changes</h4>
    <ul>
      <xsl:apply-templates select=".//cl:apiChange"/>
    </ul>
  </xsl:template>

  <xsl:template match="cl:addition">
    <li>
      <xsl:value-of select="."/>
      <xsl:if test="@contributor">
        <xsl:text>[</xsl:text>
        <xsl:value-of select="@contributor"/>
        <xsl:text>]</xsl:text>
      </xsl:if>
      <xsl:if test="@closes">
        <xsl:text>(closes </xsl:text>
        <xsl:call-template name="recursiveLink">
          <xsl:with-param name="string" select="@closes"/>
          <xsl:with-param name="atid" select="120024"/>
        </xsl:call-template>
        <xsl:text>)</xsl:text>
      </xsl:if>
      <xsl:if test="@implements">
        <xsl:text>(closes RFE </xsl:text>
        <xsl:call-template name="recursiveLink">
          <xsl:with-param name="string" select="@implements"/>
          <xsl:with-param name="atid" select="370024"/>
        </xsl:call-template>
        <xsl:text>)</xsl:text>
      </xsl:if>
    </li>
  </xsl:template>

  <xsl:template match="cl:bugfix">
    <li>
      <xsl:value-of select="."/>
      <xsl:if test="@contributor">
        <xsl:text>[</xsl:text>
        <xsl:value-of select="@contributor"/>
        <xsl:text>]</xsl:text>
      </xsl:if>
      <xsl:if test="@closes">
        <xsl:text>(closes </xsl:text>
        <xsl:call-template name="recursiveLink">
          <xsl:with-param name="string" select="@closes"/>
          <xsl:with-param name="atid" select="120024"/>
        </xsl:call-template>
        <xsl:text>)</xsl:text>
      </xsl:if>
      <xsl:if test="@implements">
        <xsl:text>(closes RFE </xsl:text>
        <xsl:call-template name="recursiveLink">
          <xsl:with-param name="string" select="@implements"/>
          <xsl:with-param name="atid" select="370024"/>
        </xsl:call-template>
        <xsl:text>)</xsl:text>
      </xsl:if>
    </li>
  </xsl:template>

  <xsl:template match="cl:apiChange">
    <li>
      <xsl:value-of select="."/>
      <xsl:if test="@contributor">
        <xsl:text>[</xsl:text>
        <xsl:value-of select="@contributor"/>
        <xsl:text>]</xsl:text>
      </xsl:if>
      <xsl:if test="@closes">
        <xsl:text>(closes </xsl:text>
        <xsl:call-template name="recursiveLink">
          <xsl:with-param name="string" select="@closes"/>
          <xsl:with-param name="atid" select="120024"/>
        </xsl:call-template>
        <xsl:text>)</xsl:text>
      </xsl:if>
      <xsl:if test="@implements">
        <xsl:text>(closes RFE </xsl:text>
        <xsl:call-template name="recursiveLink">
          <xsl:with-param name="string" select="@implements"/>
          <xsl:with-param name="atid" select="370024"/>
        </xsl:call-template>
        <xsl:text>)</xsl:text>
      </xsl:if>
    </li>
  </xsl:template>

  <xsl:template name="recursiveLink">
    <xsl:param name="string" />
    <xsl:param name="atid" />
    <xsl:choose>
      <xsl:when test="contains($string, ' ')">
        <xsl:if test="not(starts-with($string, ' '))">
          <xsl:element name="a">
            <xsl:attribute name="href">http://sourceforge.net/tracker/index.php?func=detail&amp;aid=<xsl:value-of select="substring-before($string, ' ')"/>&amp;group_id=20024&amp;atid=<xsl:value-of select="$atid"/></xsl:attribute>
            <xsl:text>#</xsl:text><xsl:value-of select="substring-before($string, ' ')" />
          </xsl:element>
        </xsl:if>
        <xsl:text> </xsl:text>
        <xsl:call-template name="recursiveLink">
          <xsl:with-param name="string" select="substring-after($string, ' ')" />
          <xsl:with-param name="atid" select="$atid"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
          <xsl:element name="a">
            <xsl:attribute name="href">http://sourceforge.net/tracker/index.php?func=detail&amp;aid=<xsl:value-of select="$string"/>&amp;group_id=20024&amp;atid=<xsl:value-of select="$atid"/></xsl:attribute>
            <xsl:text>#</xsl:text><xsl:value-of select="$string" />
          </xsl:element>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
</xsl:stylesheet>