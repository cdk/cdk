<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:saxon="http://icl.com/saxon"
                xmlns:lxslt="http://xml.apache.org/xslt"
                xmlns:xalanredirect="org.apache.xalan.xslt.extensions.Redirect"
                xmlns:doc="http://nwalsh.com/xsl/documentation/1.0"
		version="1.0"
                exclude-result-prefixes="doc"
                extension-element-prefixes="saxon xalanredirect lxslt">

<!-- This stylesheet does not work with XT. Use xtchunk.xsl instead. -->

<xsl:import href="docbookimport.xsl"/>

<xsl:template match="homepage">
  <xsl:variable name="id">
    <xsl:call-template name="object.id"/>
  </xsl:variable>
  
  <xsl:variable name="filename">
    <xsl:apply-templates select="." mode="filename" />
  </xsl:variable>

  <!-- Note that we can't call apply-imports in here because we must -->
  <!-- process webpage children *outside* of xt:document or interior -->
  <!-- webpages inherit the directory specified on their parent as   -->
  <!-- their default base directory. Which is not the intended       -->
  <!-- semantic.                                                     -->

  <xsl:variable name="page-content">
    <html>
      <xsl:apply-templates select="head" mode="head.mode"/>
      <xsl:apply-templates select="config" mode="head.mode"/>
      <body>

	<div id="{$id}" class="{name(.)}">
	  <a name="{$id}"/>


	  
	<table border="0" cellpadding="10" cellspacing="0" width="100%">
	    <xsl:if test="$nav.table.summary!=''">
	      <xsl:attribute name="summary">
		<xsl:value-of select="$nav.table.summary"/>
	      </xsl:attribute>
	    </xsl:if>
	    <tr>
	      <td width="{$navtocwidth}" valign="top" align="left" bgcolor="{$navbgcolor}">
		<p class="navtoc">
		  <xsl:call-template name="nav.toc">
		    <xsl:with-param name="pages" select="/website/webpage"/>
		  </xsl:call-template>
		</p>
	      </td>
	      <td bgcolor="{$textbgcolor}">&#160;</td>
	      <td align="left" valign="top" bgcolor="{$textbgcolor}">
		<xsl:apply-templates select="./head/title" mode="title.mode"/>
		<xsl:apply-templates select="child::*[name(.) != 'webpage']" />
		<xsl:call-template name="process.footnotes"/>
		<br/>
	      </td>
	    </tr>
	  </table>
	  
	  
	  
	  
	  <xsl:if test="$footer.spans.page != '0'">
	    <xsl:call-template name="webpage.footer"/>
	  </xsl:if>
	</div>
      </body>
    </html>
  </xsl:variable>

  <xsl:choose>
    <xsl:when test="$using.xt != 0">
      <!-- Sheet does not work anyway xt:document method="html" href="{$filename}">
	<xsl:copy-of select="$page-content"/>
      </xt:document -->
    </xsl:when>
    <xsl:otherwise>
      <xalanredirect:write file="{$filename}">
        <xsl:copy-of select="$page-content"/>
        <xsl:fallback>
          <saxon:output method="html" file="{$filename}">
            <xsl:copy-of select="$page-content"/>
            <xsl:fallback>
              <xsl:copy-of select="$page-content"/>
            </xsl:fallback>
          </saxon:output>
	</xsl:fallback>
      </xalanredirect:write>
    </xsl:otherwise>
  </xsl:choose>

  <xsl:apply-templates select="webpage"/>

</xsl:template>


<xsl:template match="webpage">
  <xsl:variable name="id">
    <xsl:call-template name="object.id"/>
  </xsl:variable>

  <!--xsl:value-of select="document('../news.xml')" disable-output-escaping="yes"/-->
  
  <xsl:variable name="relpath">
    <xsl:call-template name="root-rel-path">
      <xsl:with-param name="webpage" select="."/>
    </xsl:call-template>
  </xsl:variable>

  <xsl:variable name="filename">
    <xsl:apply-templates select="." mode="filename"/>
  </xsl:variable>

  <!-- Note that we can't call apply-imports in here because we must -->
  <!-- process webpage children *outside* of xt:document or interior -->
  <!-- webpages inherit the directory specified on their parent as   -->
  <!-- their default base directory. Which is not the intended       -->
  <!-- semantic.                                                     -->

  <xsl:variable name="page-content">
    <html>
      <xsl:apply-templates select="head" mode="head.mode"/>
      <xsl:apply-templates select="config" mode="head.mode"/>
      <body>

	<div id="{$id}" class="{name(.)}">
	  <a name="{$id}"/>

	  <table border="0" cellpadding="10" cellspacing="0" width="100%">
	    <xsl:if test="$nav.table.summary!=''">
	      <xsl:attribute name="summary">
		<xsl:value-of select="$nav.table.summary"/>
	      </xsl:attribute>
	    </xsl:if>
	    <tr>
	      <td width="{$navtocwidth}" valign="top" align="left" bgcolor="{$navbgcolor}">
		<p class="navtoc">
		  <xsl:call-template name="nav.toc">
		    <xsl:with-param name="pages" select="/website/webpage"/>
		  </xsl:call-template>
		</p>
	      </td>
	      <td bgcolor="{$textbgcolor}">&#160;</td>
	      <td align="left" valign="top" bgcolor="{$textbgcolor}">
		<xsl:apply-templates select="./head/title" mode="title.mode"/>
		<xsl:apply-templates select="child::*[name(.) != 'webpage']"/>
		<xsl:call-template name="process.footnotes"/>
		<br/>
	      </td>
	    </tr>
	  </table>

	  <xsl:call-template name="webpage.footer"/>
	</div>

      </body>
    </html>
  </xsl:variable>

  <xsl:choose>
    <xsl:when test="$using.xt != 0">
      <!-- Sheet does not work with XT anyway xt:document method="html" href="{$filename}">
	<xsl:copy-of select="$page-content"/>
      </xt:document -->
    </xsl:when>
    <xsl:otherwise>
      <xalanredirect:write file="{$filename}">
        <xsl:copy-of select="$page-content"/>
	<xsl:fallback>
	  <saxon:output method="html" file="{$filename}">
	    <xsl:copy-of select="$page-content"/>
	    <xsl:fallback>
	      <xsl:copy-of select="$page-content"/>
	    </xsl:fallback>
	  </saxon:output>
	</xsl:fallback>
      </xalanredirect:write>
    </xsl:otherwise>
  </xsl:choose>

  <xsl:apply-templates select="webpage"/>

</xsl:template>


<xsl:template name="home.navhead">
       <xsl:text>' '</xsl:text>
</xsl:template>

<xsl:template name="home.navhead.upperright">
	<xsl:text>' '</xsl:text>
</xsl:template><xsl:param name="header.hr">0</xsl:param>

<xsl:param name="footer.hr">0</xsl:param>
<xsl:param name="textbgcolor">white</xsl:param>
<xsl:param name="navbgcolor">#FCF0DA</xsl:param>
<xsl:param name="navtocwidth">150</xsl:param>



</xsl:stylesheet> 
