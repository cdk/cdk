<?xml version='1.0' encoding='utf-8'?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:cml="http://www.xml-cml.org/schema"

    exclude-result-prefixes="cml">
 
    <xsl:output method="html" encoding="utf-8" indent="yes"/>

    <!-- Change this depending on where we are using the development or the production server -->
    <xsl:variable name="serverRoot" select="'http://cb.openmolecules.net/'"/>

    <xsl:template match="/">
        <html>
            <head>
                <title>
                    <xsl:value-of select="//@rdf:about"/>
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
                  <xsl:apply-templates select="//cml:atomType"/>
               </xsl:element>

            </body>
        </html>
    </xsl:template>

    <xsl:template match="//iupac:inchi">
        <tr>
            <td class="header">
                <xsl:text>InChI</xsl:text>
            </td>
            <td>
                <span property="chem:inchi"><xsl:value-of select="."/></span>
            </td>
        </tr>
    </xsl:template>

</xsl:stylesheet>
