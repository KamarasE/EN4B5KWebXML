<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html" encoding="UTF-8" indent="yes"/>
    <xsl:key name="tipusok" match="auto" use="tipus"/>
    <xsl:template match="/">
        <html>
            <body>
                <h2>Autótípusok és darabszámuk</h2>
                <ul>
                    <xsl:for-each select="autok/auto[generate-id() = generate-id(key('tipusok', tipus)[1])]">
                        <li>
                            <xsl:value-of select="tipus" />: 
                            <xsl:value-of select="count(key('tipusok', tipus))" /> db
                        </li>
                    </xsl:for-each>
                </ul>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>
