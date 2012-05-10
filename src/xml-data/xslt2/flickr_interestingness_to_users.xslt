<?xml version="1.0"?>

<!-- Graphr -->
<!-- extracts uder information from today's top photos -->
<!-- input format: flickr.interestingness.getList API result (flickr_interestingness.xsd) -->
<!-- output format: Graphr DB users (graphr_users.xsd) -->

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
    
    <!-- key for determining unique users -->
    <xsl:key name="user-key" match="/rsp/photos/photo/@owner" use="." />
    
    <!-- successfull query: creates root document structure -->
    <xsl:template match="rsp[@stat='ok']">
        <users>
            <xsl:attribute name="date">
                <xsl:value-of select="$DATE"/>
            </xsl:attribute>
            <xsl:apply-templates select="./photos/photo"/>
            <!-- xsl:apply-templates select="./photos/photo[generate-id(@owner)=generate-id(key('user-key',@owner)[1])]"/ -->
        </users>
    </xsl:template>
    
    <!-- creates the user element -->
    <xsl:template match="photo">
        <user> 
            <xsl:attribute name="user-id"> 
                <xsl:value-of select="./@owner"/>
            </xsl:attribute>
            <xsl:attribute name="display-name"> 
                <xsl:value-of select="./@ownername"/>
            </xsl:attribute>
        </user>
    </xsl:template>
    
</xsl:stylesheet>