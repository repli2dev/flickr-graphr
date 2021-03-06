<?xml version="1.0"?>

<!-- Graphr -->
<!-- transforms hot list data for given period -->
<!-- input format: flickr.tags.getHotList API result (flickr_hot_list.xsd) -->
<!-- output format: Graphr tags (graphr_tags.xsd) -->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
    
    <!-- date that is being processed-->
    <!-- required parameter, format YYYY-MM-DD -->
    <xsl:param name="DATE">undefined</xsl:param>
    
    <!-- root element template -->
    <xsl:template match="/">
        <xsl:apply-templates select="rsp"/>
    </xsl:template>
    
    <!-- successfull query: creates root document structure -->
    <xsl:template match="rsp[@stat='ok']">
        <tags>
            <xsl:attribute name="date">
                <xsl:value-of select="$DATE"/>
            </xsl:attribute>
	    <xsl:attribute name="period">
                <xsl:value-of select="./hottags/@period"/>
            </xsl:attribute>
            <xsl:attribute name="count">
                <xsl:value-of select="count(./hottags/tag)"/>
            </xsl:attribute>
            <xsl:apply-templates select="./hottags/tag"/>
        </tags>
    </xsl:template>
    
    <!-- creates the element tag -->
    <xsl:template match="tag">
        <tag> 
            <xsl:attribute name="value"> 
                <xsl:value-of select="./text()"/>
            </xsl:attribute>
            <xsl:attribute name="ourScore"> 
                <xsl:value-of select="100 - (100 div count(../tag)) * position()"/>
            </xsl:attribute>
	    <xsl:attribute name="theirScore"> 
                <xsl:value-of select="./@score"/>
            </xsl:attribute>
        </tag>
    </xsl:template>
    
</xsl:stylesheet>
