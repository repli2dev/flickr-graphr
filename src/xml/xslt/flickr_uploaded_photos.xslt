<?xml version="1.0"?>

<!-- Graphr -->
<!-- transforms flickr search data to database format -->
<!-- input format: flickr.photos.search API result (flickr_api_uploaded_photos.xsd) -->
<!-- output format: Graphr count of uploaded photos (graphr_uploaded_photos.xsd) -->

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
        <day>
            <xsl:attribute name="date">
                <xsl:value-of select="$DATE"/>
            </xsl:attribute>
            <xsl:attribute name="photosCount">
                <xsl:value-of select="./photos/@total"/>
            </xsl:attribute>
        </day>
    </xsl:template>
</xsl:stylesheet>
