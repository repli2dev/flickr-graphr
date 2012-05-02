<!-- flickr-graphr -->
<!-- transforms final vizualisation data from xml to JSON for Google Charts API -->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output 
        method="xml" encoding="UTF-8" indent="yes"/>
        
    <xsl:template match="/">
        <xsl:text>
            {cols: [{id: 'date', label: 'Date', type: 'string'}
        </xsl:text>
        <xsl:apply-templates select="//date" mode="columnHeader"/>
        <xsl:text>,</xsl:text>
    </xsl:template>
        
    <xsl:template match="date" mode="columnHeader">
        <xsl:text>, 
        {id: '', label: '</xsl:text>
        <xsl:value-of select="@value"/>
        <xsl:text>', type: 'number'}</xsl:text>
    </xsl:template>
    
</xsl:stylesheet>