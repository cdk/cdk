<?xml version='1.0' encoding='utf-8'?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:cml="http://www.xml-cml.org/schema"
    exclude-result-prefixes="cml">

    <xsl:output method="html" encoding="utf-8" indent="yes"/>

    <xsl:template match="/">
        <html>
            <head>
                <title>
                    <xsl:value-of select="cml:atomTypeList/@title"/>
                </title>
                <style type="text/css">
                      body { font-family: Verdana,Arial,Helvetica, sans-serif; font-size: 11px; }
                      th { font-weight: bold; text-align: left; }
                      td { font-family: Verdana,Arial,Helvetica, sans-serif; font-size: 11px; }
                      .header { text-align:right; font-weight:bold; color:rgb(0,0,0); }
                 </style>
            </head>
            <body>
               <h2>Atom Types</h2>
               <table>
                  <thead>
                     <tr>
                        <th>identifier</th>
                        <th>element</th>
                        <th>formal charge</th>
                        <th>hybridization</th>
                        <th>neighbours</th>
                        <th>pi bonds</th>
                        <th>lone pairs</th>
                        <th>unpaired electron</th>
                     </tr>
                  </thead>
                  <tbody>
                     <xsl:apply-templates select="cml:atomTypeList/cml:atomType">
                        <xsl:sort select="cml:atom/@elementType"/>
                     </xsl:apply-templates>
                  </tbody>
               </table>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="cml:atomType">
        <tr>
            <td><xsl:value-of select="@id"/></td>
            <td><xsl:value-of select="cml:atom/@elementType"/></td>
            <td><xsl:value-of select="cml:atom/@formalCharge"/></td>
            <td><xsl:value-of select="cml:scalar[@dictRef='cdk:hybridization']"/></td>
            <td><xsl:value-of select="cml:atom/cml:scalar[@dictRef='cdk:formalNeighbourCount']"/></td>
            <td><xsl:value-of select="cml:atom/cml:scalar[@dictRef='cdk:piBondCount']"/></td>
            <td><xsl:value-of select="cml:atom/cml:scalar[@dictRef='cdk:lonePairCount']"/></td>
            <td><xsl:if test="cml:atom/cml:scalar[@dictRef='cdk:radicalElectronCount']"><xsl:text>yes</xsl:text></xsl:if></td>
        </tr>
    </xsl:template>

</xsl:stylesheet>
