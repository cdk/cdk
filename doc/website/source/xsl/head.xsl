<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

<xsl:template match="head" mode="head.mode">
  <xsl:variable name="nodes" select="*"/>
  <head>
    <meta name="generator" content="Website XSL Stylesheet V{$VERSION}"/>
    <xsl:if test="$html.stylesheet != ''">
      <link rel="stylesheet" href="{$html.stylesheet}" type="text/css">
	<xsl:if test="$html.stylesheet.type != ''">
	  <xsl:attribute name="type">
	    <xsl:value-of select="$html.stylesheet.type"/>
	  </xsl:attribute>
	</xsl:if>
      </link>
    </xsl:if>
    <xsl:apply-templates select="$autolayout/autolayout/style
                                 |$autolayout/autolayout/script"
                         mode="head.mode">
      <xsl:with-param name="webpage" select="ancestor::webpage"/>
    </xsl:apply-templates>
    <xsl:apply-templates mode="head.mode"/>
  </head>
</xsl:template>

<xsl:template match="title" mode="head.mode">
  <title><xsl:value-of select="."/></title>
</xsl:template>

<xsl:template match="titleabbrev" mode="head.mode">
  <!--nop-->
</xsl:template>

<xsl:template match="subtitle" mode="head.mode">
  <!--nop-->
</xsl:template>

<xsl:template match="summary" mode="head.mode">
  <!--nop-->
</xsl:template>

<xsl:template match="base" mode="head.mode">
  <base href="{@href}">
    <xsl:if test="@target">
      <xsl:attribute name="target">
        <xsl:value-of select="@target"/>
      </xsl:attribute>
    </xsl:if>
  </base>
</xsl:template>

<xsl:template match="keywords" mode="head.mode">
  <meta name="keyword" content="{.}"/>
</xsl:template>

<xsl:template match="copyright" mode="head.mode">
  <!--nop-->
</xsl:template>

<xsl:template match="author" mode="head.mode">
  <!--nop-->
</xsl:template>

<xsl:template match="edition" mode="head.mode">
  <!--nop-->
</xsl:template>

<xsl:template match="meta" mode="head.mode">
  <xsl:choose>
    <xsl:when test="@http-equiv">
      <meta http-equiv="{@http-equiv}" content="{@content}"/>
    </xsl:when>
    <xsl:otherwise>
      <meta name="{@name}" content="{@content}"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="script" mode="head.mode">
  <script>
    <xsl:choose>
      <xsl:when test="@language">
	<xsl:attribute name="language">
	  <xsl:value-of select="@language"/>
	</xsl:attribute>
      </xsl:when>
      <xsl:otherwise>
	<xsl:attribute name="language">JavaScript</xsl:attribute>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates/>
  </script>
</xsl:template>

<xsl:template match="script[@src]" mode="head.mode" priority="2">
  <xsl:param name="webpage" select="ancestor::webpage"/>
  <xsl:variable name="relpath">
    <xsl:call-template name="root-rel-path">
      <xsl:with-param name="webpage" select="$webpage"/>
    </xsl:call-template>
  </xsl:variable>

  <xsl:variable name="language">
    <xsl:choose>
      <xsl:when test="@language">
	<xsl:value-of select="@language"/>
      </xsl:when>
      <xsl:otherwise>JavaScript</xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <script src="{$relpath}{@src}" language="{$language}"/>
</xsl:template>

<xsl:template match="style" mode="head.mode">
  <style>
    <xsl:if test="@type">
      <xsl:attribute name="type">
	<xsl:value-of select="@type"/>
      </xsl:attribute>
    </xsl:if>

    <xsl:apply-templates/>

  </style>
</xsl:template>

<xsl:template match="style[@src]" mode="head.mode" priority="2">
  <xsl:param name="webpage" select="ancestor::webpage"/>
  <xsl:variable name="relpath">
    <xsl:call-template name="root-rel-path">
      <xsl:with-param name="webpage" select="$webpage"/>
    </xsl:call-template>
  </xsl:variable>

  <xsl:choose>
    <xsl:when test="starts-with(@src, '/')">
      <link rel="stylesheet" href="{@src}">
        <xsl:if test="@type">
          <xsl:attribute name="type">
            <xsl:value-of select="@type"/>
          </xsl:attribute>
        </xsl:if>
      </link>
    </xsl:when>
    <xsl:otherwise>
      <link rel="stylesheet" href="{$relpath}{@src}">
        <xsl:if test="@type">
          <xsl:attribute name="type">
            <xsl:value-of select="@type"/>
          </xsl:attribute>
        </xsl:if>
      </link>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="abstract" mode="head.mode">
  <!--nop-->
</xsl:template>

<xsl:template match="revhistory" mode="head.mode">
  <!--nop-->
</xsl:template>

<xsl:template match="rddl:*" mode="head.mode"
              xmlns:rddl='http://www.rddl.org/'>
  <!--nop-->
</xsl:template>

</xsl:stylesheet>
