<?xml version='1.0' encoding='utf-8'?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:cml="http://www.xml-cml.org/schema"
    xmlns:cdk="http://cdk.sf.net/dict/cdk/"

    exclude-result-prefixes="cml">
 
    <xsl:output method="html" encoding="utf-8" indent="yes"/>

    <!-- Change this depending on where we are using the development or the production server -->
    <xsl:variable name="serverRoot" select="'http://cb.openmolecules.net/'"/>

    <xsl:template match="/">
        <html>
            <head>
                <title>
                    <xsl:value-of select="/cml:atomTypeList/@title"/>
                    <style type="text/css"> 
                      body { font-family: Verdana,Arial,Helvetica, sans-serif; font-size: 11px; }
                      td { font-family: Verdana,Arial,Helvetica, sans-serif; font-size: 11px; }
                      .header { text-align:right; font-weight:bold; color:rgb(0,0,0); }
                    </style>                
                </title>
            </head>
            <body>
               <h2>Atom Types</h2>
               <xsl:element name="div">
                  <table>
                    <td><b>identifier</b></td>
                    <td><b>element</b></td>
                    <td><b>formal charge</b></td>
                    <td><b>hybridization</b></td>
                    <xsl:apply-templates select="//cml:atomType"/>
                  </table>
               </xsl:element>

            </body>
        </html>
    </xsl:template>

    <xsl:template match="//cml:atomType">
        <tr>
            <td><xsl:value-of select="./@id"/></td>
            <td><xsl:value-of select="./cml:atom/@elementType"/></td>
            <td><xsl:value-of select="./cml:atom/@formalCharge"/></td>
            <td><xsl:value-of select="./cml:scalar[@dictRef='cdk:hybridization']"/></td>
        </tr>
    </xsl:template>

</xsl:stylesheet>
