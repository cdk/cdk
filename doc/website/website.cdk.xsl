<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version='1.0'>

<xsl:import href="website-2.5.0/xsl/tabular.xsl"/>

<!-- Replace the text in these templates with whatever you want -->
<!-- to appear in the respective location on the home page. -->

<!-- nothing on top -->
<xsl:template name="home.navhead"/>
<xsl:template name="home.navhead.upperright"/>

<!-- put your customizations here -->

<xsl:template match="toc">

  <!-- same as in website-2.5.0/xsl except for added SF button at bottom -->

  <xsl:param name="pageid" select="@id"/>

  <xsl:variable name="relpath">
    <xsl:call-template name="toc-rel-path">
      <xsl:with-param name="pageid" select="$pageid"/>
    </xsl:call-template>
  </xsl:variable>

  <xsl:variable name="textbgcolor"><xsl:text>#FCF0DA</xsl:text></xsl:variable>

  <xsl:variable name="homebanner"
                select="/autolayout/config[@param='homebanner-tabular'][1]"/>

  <xsl:variable name="banner"
                select="/autolayout/config[@param='banner-tabular'][1]"/>

  <xsl:choose>
    <xsl:when test="$pageid = @id">
      <img align="left" border="0">
        <xsl:attribute name="src">
          <xsl:value-of select="$relpath"/>
          <xsl:value-of select="$homebanner/@value"/>
        </xsl:attribute>
        <xsl:attribute name="alt">
          <xsl:value-of select="$homebanner/@altval"/>
        </xsl:attribute>
      </img>
      <br clear="all"/>
      <br/>
    </xsl:when>
    <xsl:otherwise>
      <a href="{$relpath}{@dir}{$filename-prefix}{@filename}">
        <img align="left" border="0">
          <xsl:attribute name="src">
            <xsl:value-of select="$relpath"/>
            <xsl:value-of select="$banner/@value"/>
          </xsl:attribute>
          <xsl:attribute name="alt">
            <xsl:value-of select="$banner/@altval"/>
          </xsl:attribute>
        </img>
      </a>
      <br clear="all"/>
      <br/>
    </xsl:otherwise>
  </xsl:choose>

  <xsl:apply-templates select="tocentry">
    <xsl:with-param name="pageid" select="$pageid"/>
    <xsl:with-param name="relpath" select="$relpath"/>
  </xsl:apply-templates>
  <br/>
  
  <!-- here's the SF button -->
  <center><a href="http://sourceforge.net">
    <img src="http://sourceforge.net/sflogo.php?group_id=20024&amp;type=5" width="88" height="31"
         border="0" alt="SourceForge Logo" />
  </a></center>
  
</xsl:template>

</xsl:stylesheet>

