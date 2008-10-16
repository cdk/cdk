<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

  <xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

  <xsl:template match="*">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="text()"/>

  <xsl:template match="/">
            <p><div class="titlepage"><div><div>
<h3><a name="N10019"></a>Project Statistics</h3>
</div></div></div></p>
<p>
      This section shows you some statistics on the use of JavaDoc in
      the source code. Note that the number of javadocs should equal the number of
      classes + the number of functions.
    </p>
<p>
      Besides the below statistics, JavaDoc errors are given in more detail on
      a per module basis <a href="http://cheminfo.informatics.indiana.edu/~rguha/code/java/nightly/" target="_top">elsewhere on this site</a>.
    </p>
<p>These statistics were generated on
<xsl:value-of select="javancss/date"/> at
<xsl:value-of select="javancss/time"/>.</p>
<table>
  <tr>
    <th>Package</th>
    <th>Classes</th>
    <th>Functions</th>
    <th>Javadocs</th>
    <th>Javadocs</th>
    <th>Done</th>
  </tr>
  <tr>
    <th></th>
    <th></th>
    <th></th>
    <th>Expected</th>
    <th>Found</th>
    <th></th>
  </tr>
  <xsl:for-each select="javancss/packages/package">
    <xsl:sort select="javadocs" data-type="number" order="descending"/>
    <xsl:apply-templates select="self::node()[not(contains(./name, 'test'))]"/>
  </xsl:for-each>
  <xsl:variable name="classes"   select="sum(javancss/packages/package[not(contains(./name, 'test'))]/classes)"/>
  <xsl:variable name="functions" select="sum(javancss/packages/package[not(contains(./name, 'test'))]/functions)"/>
  <xsl:variable name="javadocs"  select="sum(javancss/packages/package[not(contains(./name, 'test'))]/javadocs)"/>
  <xsl:variable name="done" select="$javadocs div ($classes + $functions)"/>
  <tr>
    <td>Total</td>
    <td><xsl:value-of select="$classes"/></td>
    <td><xsl:value-of select="$functions"/></td>
    <td><xsl:value-of select="$classes + $functions"/></td>
    <td><xsl:value-of select="$javadocs"/></td>
    <td>
      <xsl:choose>
        <xsl:when test="1.0 > $done">
          <b><xsl:value-of select="format-number($done div 0.01,'#00.0')"/> %</b>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="format-number($done div 0.01,'#00.0')"/> %
        </xsl:otherwise>
      </xsl:choose>
    </td>
  </tr>
</table>
<p>And for the test classes:</p>
<table>
  <tr>
    <th>Package</th>
    <th>Classes</th>
    <th>Functions</th>
    <th>Javadocs</th>
    <th>Javadocs</th>
    <th>Done</th>
  </tr>
  <tr>
    <th></th>
    <th></th>
    <th></th>
    <th>Expected</th>
    <th>Found</th>
    <th></th>
  </tr>
  <xsl:for-each select="javancss/packages/package">
    <xsl:sort select="javadocs" data-type="number" order="descending"/>
    <xsl:apply-templates select="self::node()[contains(./name, 'test')]"/>
  </xsl:for-each>
  <xsl:variable name="classes"   select="sum(javancss/packages/package[contains(./name, 'test')]/classes)"/>
  <xsl:variable name="functions" select="sum(javancss/packages/package[contains(./name, 'test')]/functions)"/>
  <xsl:variable name="javadocs"  select="sum(javancss/packages/package[contains(./name, 'test')]/javadocs)"/>
  <xsl:variable name="done" select="$javadocs div ($classes + $functions)"/>
  <tr>
    <td>Total</td>
    <td><xsl:value-of select="$classes"/></td>
    <td><xsl:value-of select="$functions"/></td>
    <td><xsl:value-of select="$classes + $functions"/></td>
    <td><xsl:value-of select="$javadocs"/></td>
    <td>
      <xsl:choose>
        <xsl:when test="1.0 > $done">
          <emphasis role="bold"><xsl:value-of select="format-number($done div 0.01,'#00.0')"/> %</emphasis>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="format-number($done div 0.01,'#00.0')"/> %
        </xsl:otherwise>
      </xsl:choose>
    </td>
  </tr>
</table>
  </xsl:template>

  <xsl:template match="package">
      <xsl:variable name="classes"   select="number(classes)"/>
      <xsl:variable name="functions" select="number(functions)"/>
      <xsl:variable name="javadocs"  select="number(javadocs)"/>
      <xsl:variable name="done" select="$javadocs div ($classes + $functions)"/>
      <tr>
        <td><xsl:value-of select="name"/></td>
        <td><xsl:value-of select="classes"/></td>
        <td><xsl:value-of select="functions"/></td>
        <td><xsl:value-of select="$classes + $functions"/></td>
        <td><xsl:value-of select="javadocs"/></td>
        <td>
          <xsl:choose>
            <xsl:when test="1.0 > $done">
              <emphasis role="bold"><xsl:value-of select="format-number($done div 0.01,'#00.0')"/> %</emphasis>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="format-number($done div 0.01,'#00.0')"/> %
            </xsl:otherwise>
          </xsl:choose>
        </td>
      </tr>
  </xsl:template>

</xsl:stylesheet>
