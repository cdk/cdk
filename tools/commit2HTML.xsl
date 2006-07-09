<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://www.w3.org/1999/xhtml"
                version="1.0">

  <!-- $Author: egonw $
       $Date: 2006-06-08 19:19:18 +0200 (Thu, 08 Jun 2006) $
       $Revision: 6381 $ -->

  <xsl:key name='au' match='author' use='.'/>

  <xsl:output method="xml" indent="yes"
    omit-xml-declaration="no" encoding="utf-8"/>

  <xsl:template match="*">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="text()">
    <xsl:value-of select="."/>
  </xsl:template>

  <xsl:template match="/">
<html>
<body>
<h1>Changelog</h1>
<h3>Statistics</h3>
<xsl:variable name="totalCount" select="count(//logentry)" />
<ul>
  <li>Number of commits: <xsl:value-of select="$totalCount"/></li>
</ul>
<table>
  <tr>
    <td><b>Author</b></td>
    <td><b>Commits</b></td>
    <td><b>Percentage</b></td>
  </tr>
  <xsl:for-each select="//logentry/author[generate-id()=generate-id(key('au',.)[1])]">
    <xsl:variable name="author" select="." />
    <xsl:variable name="commitCount" select="count(//logentry[./author = $author])" />
    <tr>
      <td><xsl:value-of select="$author"/></td>
      <td><xsl:value-of select="$commitCount"/></td>
      <td align="right"><xsl:value-of select="round(10000*$commitCount div $totalCount) div 100"/>%</td>
    </tr>
  </xsl:for-each>
</table>
<h3>All commits</h3>
<table>
  <tr><td><b>Revision</b></td><td><b>Author</b></td><td><b>Message</b></td></tr>
  <xsl:for-each select="//logentry">
  <tr>
    <td><xsl:value-of select="./@revision"/></td>
    <td><xsl:value-of select="./author"/></td>
    <td><xsl:value-of select="./msg"/></td>
  </tr>
  </xsl:for-each>
</table>
</body>
</html>
  </xsl:template>

</xsl:stylesheet>