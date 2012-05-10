<?xml version="1.0"?>

<!-- Graphr -->
<!-- transforms data about best photos for given day -->
<!-- input format: flickr.interestingness.getList API result (flickr_interestingness.xsd) -->
<!-- output format: Graphr DB top_photos (graphr_top_photos.xsd) -->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
    
    <!-- date that is being processed-->
    <!-- required parameter, format YYYY-MM-DD -->
    <xsl:param name="DATE">undefined</xsl:param>
    
    <!-- root element template -->
    <!-- checks if the parameter DATE was specified -->
    <xsl:template match="/">
        <xsl:choose>
            <xsl:when test="$DATE='undefined'">
                <error code="-1" message="Parameter date was not set."/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates select="rsp"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <!-- query unsuccessfull: creates node with error description -->
    <xsl:template match="rsp[@stat='fail']">
        <error>
            <xsl:attribute name="code">
                <xsl:value-of select="err/@code"/>
            </xsl:attribute> 
            <xsl:attribute name="message">
                <xsl:value-of select="err/@msg"/>
            </xsl:attribute> 
        </error>
    </xsl:template>
    
    <!-- successfull query: creates root document structure -->
    <xsl:template match="rsp[@stat='ok']">
        <top-photos>
            <xsl:attribute name="date">
                <xsl:value-of select="$DATE"/>
            </xsl:attribute>
            <xsl:attribute name="count">
                <xsl:value-of select="count(./photos/photo)"/>
            </xsl:attribute>
            <xsl:apply-templates select="./photos/photo"/>
        </top-photos>
    </xsl:template>
    
    <!-- creates the element photo -->
    <xsl:template match="photo">
        <photo> 
            <xsl:attribute name="photo-id"> 
                <xsl:value-of select="./@id"/>
            </xsl:attribute>
            <xsl:attribute name="user-id"> 
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