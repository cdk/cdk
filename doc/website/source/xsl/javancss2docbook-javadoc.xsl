<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

  <xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

  <xsl:template match="*">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="text()"/>

  <xsl:template match="/">
<para>These statistics were generated on
<xsl:value-of select="javancss/date"/> at
<xsl:value-of select="javancss/time"/>.</para>
<informaltable frame='none'>
  <tgroup cols="3">
    <thead>
        <row>
          <entry>Package</entry>
          <entry>Classes</entry>
          <entry>Functions</entry>
          <entry>Javadocs</entry>
          <entry>Javadocs</entry>
          <entry>Done</entry>
        </row>
        <row>
          <entry></entry>
          <entry></entry>
          <entry></entry>
          <entry>Expected</entry>
          <entry>Found</entry>
          <entry></entry>
        </row>
    </thead>
    <tbody>
        <xsl:for-each select="javancss/packages/package">
          <xsl:sort select="javadocs" data-type="number" order="descending"/>
          <xsl:apply-templates select="."/>
        </xsl:for-each>
    </tbody>
  </tgroup>
</informaltable>
  </xsl:template>

  <xsl:template match="package">
      <xsl:variable name="classes" select="number(classes)"/>
      <xsl:variable name="functions" select="number(functions)"/>
      <xsl:variable name="javadocs" select="number(javadocs)"/>
      <xsl:variable name="done" select="$javadocs div ($classes + $functions)"/>
      <row>
        <entry><xsl:value-of select="name"/></entry>
        <entry><xsl:value-of select="classes"/></entry>
        <entry><xsl:value-of select="functions"/></entry>
        <entry><xsl:value-of select="$classes + $functions"/></entry>
        <entry><xsl:value-of select="javadocs"/></entry>
        <entry>
          <xsl:choose>
            <xsl:when test="1.0 > $done">
              <emphasis role="bold"><xsl:value-of select="format-number($done div 0.01,'#00.0')"/> %</emphasis>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="format-number($done div 0.01,'#00.0')"/> %
            </xsl:otherwise>
          </xsl:choose>
        </entry>
      </row>
  </xsl:template>

</xsl:stylesheet>
