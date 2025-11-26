<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html" encoding="UTF-8" indent="yes"/>
    <xsl:template match="/">
        <html>
            <body>
                <h2>Autók, amelyek drágábbak mint 30,000</h2>
                <p>
                    <xsl:value-of select="count(autok/auto[ar > 30000])" /> autó drágább 30,000-nél.
                </p>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>
