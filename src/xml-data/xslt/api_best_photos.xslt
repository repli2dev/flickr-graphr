<?xml version="1.0"?>

<!-- flickr-graphr -->
<!-- transforms best_photos Flickr API result to database form -->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output 
        method="xml" encoding="UTF-8" indent="yes"/>
    
    <!-- parameter DATE in format YYYY-MM-DD, required -->
    <!-- takes the date which is being processed as parameter -->
    <xsl:param name="DATE">undefined</xsl:param>
    
    <!-- creates root document structure -->
    <!-- checks if the parameter DATE was specified -->
    <xsl:template match="/">
        <xsl:choose>
            <xsl:when test="$DATE='undefined'">
                <error code="1" msg="Date was not specified."/>
            </xsl:when>
            <xsl:otherwise>
                <best-photos>
                    <xsl:apply-templates select="//photo"/>
                </best-photos>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="photo">
        <photo> 
            <xsl:attribute name="photoid"> 
                <xsl:value-of select="./@id"/>
            </xsl:attribute>
            <xsl:attribute name="date"> 
                <xsl:value-of select="$DATE"/>
            </xsl:attribute>
            <xsl:attribute name="ownerid"> 
                <xsl:value-of select="./@owner"/>
            </xsl:attribute>
            <xsl:attribute name="score"> 
                <xsl:value-of select="501 - position()"/>
            </xsl:attribute>
            <xsl:attribute name="views"> 
                <xsl:value-of select="./@views"/>
            </xsl:attribute>
        </photo>
    </xsl:template>
    
</xsl:stylesheet>