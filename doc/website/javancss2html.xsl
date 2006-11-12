<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

  <xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

  <xsl:template match="*">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="text()"/>

  <xsl:template match="/">
            <div class="titlepage"><div><div>
<h3><a name="N10019"></a>Project Statistics</h3>
</div></div></div>
<p>This section shows you some statistics on the Java source code.
    The NCSS number is the number of Non-Commenting Source Statements.</p>
<p>These statistics were generated on
<xsl:value-of select="javancss/date"/> at
<xsl:value-of select="javancss/time"/>.</p>
<div class="informaltable">
<table>
  <tr>
    <th>Package</th>
    <th>Classes</th>
    <th>Functions</th>
    <th>NCSS</th>
    <th>JavaDoc</th>
    <th>JavaDoc Lines</th>
  </tr>
  <xsl:for-each select="javancss/packages/package">
    <xsl:sort select="ncss" data-type="number" order="descending"/>
    <xsl:apply-templates select="self::node()[not(contains(./name, 'net.sf')) and not(contains(./name, 'test'))]"/>
  </xsl:for-each>
  <tr>
    <td>Total</td>
    <td><xsl:value-of select="sum(javancss/packages/package[not(contains(./name, 'net.sf')) and not(contains(./name, 'test'))]/classes)"/></td>
    <td><xsl:value-of select="sum(javancss/packages/package[not(contains(./name, 'net.sf')) and not(contains(./name, 'test'))]/functions)"/></td>
    <td><xsl:value-of select="sum(javancss/packages/package[not(contains(./name, 'net.sf')) and not(contains(./name, 'test'))]/ncss)"/></td>
    <td><xsl:value-of select="sum(javancss/packages/package[not(contains(./name, 'net.sf')) and not(contains(./name, 'test'))]/javadocs)"/></td>
    <td><xsl:value-of select="sum(javancss/packages/package[not(contains(./name, 'net.sf')) and not(contains(./name, 'test'))]/javadoc_lines)"/></td>
  </tr>
</table>
</div>
<p>And for the test classes:</p>
<div class="informaltable">
<table>
  <tr>
    <th>Package</th>
    <th>Classes</th>
    <th>Functions</th>
    <th>NCSS</th>
    <th>JavaDoc</th>
    <th>JavaDoc Lines</th>
  </tr>
  <xsl:for-each select="javancss/packages/package">
    <xsl:sort select="ncss" data-type="number" order="descending"/>
    <xsl:apply-templates select="self::node()[contains(./name, 'test')]"/>
  </xsl:for-each>
  <tr>
    <td>Total</td>
    <td><xsl:value-of select="sum(javancss/packages/package[contains(./name, 'test')]/classes)"/></td>
    <td><xsl:value-of select="sum(javancss/packages/package[contains(./name, 'test')]/functions)"/></td>
    <td><xsl:value-of select="sum(javancss/packages/package[contains(./name, 'test')]/ncss)"/></td>
    <td><xsl:value-of select="sum(javancss/packages/package[contains(./name, 'test')]/javadocs)"/></td>
    <td><xsl:value-of select="sum(javancss/packages/package[contains(./name, 'test')]/javadoc_lines)"/></td>
  </tr>
</table>
</div>
  </xsl:template>

  <xsl:template match="package">
      <tr>
        <td><xsl:value-of select="name"/></td>
        <td><xsl:value-of select="classes"/></td>
        <td><xsl:value-of select="functions"/></td>
        <td><xsl:value-of select="ncss"/></td>
        <td><xsl:value-of select="javadocs"/></td>
        <td><xsl:value-of select="javadoc_lines"/></td>
      </tr>
  </xsl:template>

</xsl:stylesheet>
