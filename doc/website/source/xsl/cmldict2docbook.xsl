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
<title><xsl:value-of select=".//*[name(.)='dictionary']/@title"/>
[<xsl:value-of select=".//*[name(.)='dictionary']/@id"/>]</title>
<para>
  <xsl:value-of select=".//*[name(.)='dictionary']/*[name(.)='description']"/>
</para>
<informaltable frame='none'>
  <tgroup cols="2">
    <thead>
        <row>
          <entry>ID</entry>
          <entry>Entry</entry>
          <entry>Definition</entry>
        </row>
    </thead>
    <tbody>
        <xsl:for-each select=".//*[name(.)='entry']">
          <xsl:sort select="./@id" data-type="number" order="descending"/>
          <row>
            <entry><xsl:value-of select="./@id"/></entry>
            <entry><xsl:value-of select="./@term"/></entry>
            <entry><xsl:value-of select="./*[name(.)='definition']"/></entry>
          </row>
        </xsl:for-each>
    </tbody>
  </tgroup>
</informaltable>
<para>
  This dictionary was last modified by 
  [<xsl:value-of select=".//*[name(.)='metadata' and ./@name='cvs:last-change-by']/@content"/>]
  on
  [<xsl:value-of select=".//*[name(.)='metadata' and ./@name='cvs:date']/@content"/>]
</para>
</section>
  </xsl:template>

</xsl:stylesheet>
