<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html" encoding="UTF-8" indent="yes"/>
    <xsl:key name="varosok" match="auto" use="tulaj/varos"/>
    <xsl:template match="/">
        <html>
            <body>
                <h2>Autók száma városonként</h2>
                <ul>
                    <xsl:for-each select="autok/auto[generate-id() = generate-id(key('varosok', tulaj/varos)[1])]">
                        <li>
                            <xsl:value-of select="tulaj/varos" />: 
                            <xsl:value-of select="count(key('varosok', tulaj/varos))" /> db
                        </li>
                    </xsl:for-each>
                </ul>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>
