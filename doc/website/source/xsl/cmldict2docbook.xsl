<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

  <xsl:output method="xml" indent="yes" omit-xml-declaration="yes"/>

  <xsl:template match="*">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="text()"/>

  <xsl:template match="/">
<section>
<title><xsl:value-of select=".//*[name(.)='dictionary']/@title"/></title>
<para>
  <xsl:value-of select=".//*[name(.)='description']"/>
</para>
<informaltable frame='none'>
  <tgroup cols="2">
    <thead>
        <row>
          <entry>Entry</entry>
          <entry>Definition</entry>
        </row>
    </thead>
    <tbody>
        <xsl:for-each select=".//*[name(.)='entry']">
          <xsl:apply-templates select="self::node()"/>
        </xsl:for-each>
    </tbody>
  </tgroup>
</informaltable>
</section>
  </xsl:template>

  <xsl:template match="//*[name(.)='entry']">
      <row>
        <entry><xsl:value-of select="./@term"/></entry>
        <entry><xsl:value-of select="./*[name(.)='definition']"/></entry>
      </row>
  </xsl:template>
  
</xsl:stylesheet>
