<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:html='http://www.w3.org/1999/xhtml'
                xmlns:doc="http://nwalsh.com/xsl/documentation/1.0"
                exclude-result-prefixes="html doc"
                version="1.0">

<xsl:import href="website-common.xsl"/>
<xsl:include href="toc-tabular.xsl"/>

<xsl:output method="html"
            indent="no"
            doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"
            doctype-system="http://www.w3.org/TR/html4/loose.dtd"
/>

<xsl:param name="autolayout-file" select="'autolayout.xml'"/>
<xsl:param name="autolayout" select="document($autolayout-file, /*)"/>

<!-- ==================================================================== -->

<!-- Netscape gets badly confused if it sees a CSS style... -->
<xsl:param name="admon.style" select="''"/>
<xsl:param name="admon.graphics" select="1"/>
<xsl:param name="admon.graphics.path">graphics/</xsl:param>
<xsl:param name="admon.graphics.extension">.gif</xsl:param>

<xsl:attribute-set name="table.properties">
  <xsl:attribute name="border">0</xsl:attribute>
  <xsl:attribute name="cellpadding">0</xsl:attribute>
  <xsl:attribute name="cellspacing">0</xsl:attribute>
  <xsl:attribute name="width">100%</xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="table.navigation.cell.properties">
  <xsl:attribute name="valign">top</xsl:attribute>
  <xsl:attribute name="align">left</xsl:attribute>
  <!-- width is set with $navotocwidth -->
  <xsl:attribute name="bgcolor">
    <xsl:value-of select="$navbgcolor"/>
  </xsl:attribute>
</xsl:attribute-set>

<xsl:attribute-set name="table.body.cell.properties">
  <xsl:attribute name="valign">top</xsl:attribute>
  <xsl:attribute name="align">left</xsl:attribute>
  <!-- width is set with $navobodywidth -->
  <xsl:attribute name="bgcolor">
    <xsl:value-of select="$textbgcolor"/>
  </xsl:attribute>
</xsl:attribute-set>

<!-- ==================================================================== -->

<xsl:template match="/">
  <xsl:apply-templates/>
</xsl:template>

<xsl:template name="home.navhead">
<xsl:text>Navhead</xsl:text>
</xsl:template>

<xsl:template name="home.navhead.upperright">
<xsl:text>Upper-right</xsl:text>
</xsl:template>

<xsl:template name="home.navhead.separator">
  <hr/>
</xsl:template>

<xsl:template match="webpage">
  <xsl:variable name="id">
    <xsl:call-template name="object.id"/>
  </xsl:variable>

  <xsl:variable name="relpath">
    <xsl:call-template name="root-rel-path">
      <xsl:with-param name="webpage" select="."/>
    </xsl:call-template>
  </xsl:variable>

  <xsl:variable name="filename">
    <xsl:apply-templates select="." mode="filename"/>
  </xsl:variable>

  <xsl:variable name="tocentry" select="$autolayout/autolayout//*[$id=@id]"/>
  <xsl:variable name="toc" select="($tocentry/ancestor-or-self::toc
                                   |$autolayout/autolayout/toc[1])[last()]"/>

  <html>
    <xsl:apply-templates select="head" mode="head.mode"/>
    <xsl:apply-templates select="config" mode="head.mode"/>
    <body xsl:use-attribute-sets="body.attributes">

      <div id="{$id}" class="{name(.)}">
        <a name="{$id}"/>

        <table xsl:use-attribute-sets="table.properties">
          <xsl:if test="$nav.table.summary!=''">
            <xsl:attribute name="summary">
              <xsl:value-of select="$nav.table.summary"/>
            </xsl:attribute>
          </xsl:if>
          <tr>
            <td xsl:use-attribute-sets="table.navigation.cell.properties">
              <xsl:if test="$navtocwidth != ''">
                <xsl:attribute name="width">
                  <xsl:value-of select="$navtocwidth"/>
                </xsl:attribute>
              </xsl:if>
              <xsl:choose>
                <xsl:when test="$toc">
                  <p class="navtoc">
                    <xsl:apply-templates select="$toc">
                      <xsl:with-param name="pageid" select="@id"/>
                    </xsl:apply-templates>
                  </p>
                </xsl:when>
                <xsl:otherwise>&#160;</xsl:otherwise>
              </xsl:choose>
            </td>

            <td xsl:use-attribute-sets="table.body.cell.properties">
              <xsl:if test="$navbodywidth != ''">
                <xsl:attribute name="width">
                  <xsl:value-of select="$navbodywidth"/>
                </xsl:attribute>
              </xsl:if>

              <xsl:if test="$autolayout/autolayout/toc[1]/@id = $id">
                <table border="0" summary="home page extra headers"
                       cellpadding="0" cellspacing="0" width="100%">
                  <tr>
                    <td width="50%" valign="middle" align="left">
                      <xsl:call-template name="home.navhead"/>
                    </td>
                    <td width="50%" valign="middle" align="right">
                      <xsl:call-template name="home.navhead.upperright"/>
                    </td>
                  </tr>
                </table>
                <xsl:call-template name="home.navhead.separator"/>
              </xsl:if>

              <xsl:if test="$autolayout/autolayout/toc[1]/@id != $id
                            or $suppress.homepage.title = 0">
                <xsl:apply-templates select="./head/title" mode="title.mode"/>
              </xsl:if>

              <xsl:apply-templates select="child::*[name(.) != 'webpage']"/>
              <xsl:call-template name="process.footnotes"/>
              <br/>
            </td>
          </tr>
          <xsl:call-template name="webpage.table.footer"/>
        </table>

        <xsl:call-template name="webpage.footer"/>
      </div>

    </body>
  </html>
</xsl:template>

<xsl:template match="config[@param='filename']" mode="head.mode">
</xsl:template>

<xsl:template match="webtoc">
  <!-- nop -->
</xsl:template>

</xsl:stylesheet>
