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
<title><xsl:value-of select=".//*[name(.)='atomTypeList']/@title"/>
[<xsl:value-of select=".//*[name(.)='atomTypeList']/@id"/>]</title>
<informaltable frame='none'>
  <tgroup cols="4">
    <thead>
        <row>
          <entry>ID</entry>
          <entry>Element</entry>
          <entry>Formal Charge</entry>
          <entry>Other</entry>
        </row>
    </thead>
    <tbody>
        <xsl:for-each select=".//*[name(.)='atomType']">
          <xsl:sort select="./@id" order="descending"/>
          <row>
            <entry><xsl:value-of select="./@id"/></entry>
            <entry><xsl:value-of select="./*[name(.)='atom']/@elementType"/></entry>
            <entry>
              <xsl:if test="./*[name(.)='atom']/@formalCharge">
                <xsl:value-of select="./*[name(.)='atom']/@formalCharge"/>
              </xsl:if>
            </entry>
            <entry>
            <xsl:for-each select=".//*[name(.)='scalar']">
              <xsl:sort select="./@dictRef" order="descending"/>
              <para><xsl:value-of select="./@dictRef"/>: <xsl:value-of select="."/></para>
            </xsl:for-each>
            </entry>
          </row>
        </xsl:for-each>
    </tbody>
  </tgroup>
</informaltable>
<para>
  This atom type list [<xsl:value-of select=".//*[name(.)='metadata' and ./@name='cvs:revision']/@content"/>]
  was last modified by 
  [<xsl:value-of select=".//*[name(.)='metadata' and ./@name='cvs:last-change-by']/@content"/>]
  on
  [<xsl:value-of select=".//*[name(.)='metadata' and ./@name='cvs:date']/@content"/>]
</para>
</section>
  </xsl:template>

</xsl:stylesheet>
