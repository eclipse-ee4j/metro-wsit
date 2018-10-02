<?xml version='1.0'?>
<!--

    Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.

    This program and the accompanying materials are made available under the
    terms of the Eclipse Distribution License v. 1.0, which is available at
    http://www.eclipse.org/org/documents/edl-v10.php.

    SPDX-License-Identifier: BSD-3-Clause

-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xslthl="http://xslthl.sf.net"
                exclude-result-prefixes="xslthl"
                version='1.0'>

  <xsl:template match='xslthl:keyword'>
    <span class="ReservedWord"><xsl:value-of select='.'/></span>
  </xsl:template>

  <xsl:template match='xslthl:comment'>
    <span class="Comment"><xsl:value-of select='.'/></span>
  </xsl:template>

  <xsl:template match='xslthl:oneline-comment'>
    <span class="Comment"><xsl:value-of select='.'/></span>
  </xsl:template>

  <xsl:template match='xslthl:multiline-comment'>
    <span class="DocComment"><xsl:value-of select='.'/></span>
  </xsl:template>

  <xsl:template match='xslthl:tag'>
    <span class="ReservedWord"><xsl:value-of select='.'/></span>
  </xsl:template>

  <xsl:template match='xslthl:attribute'>
    <span class="Identifier"><xsl:value-of select='.'/></span>
  </xsl:template>

  <xsl:template match='xslthl:value'>
    <span class="String"><xsl:value-of select='.'/></span>
  </xsl:template>
  
  <xsl:template match='xslthl:string'>
    <span class="String"><xsl:value-of select='.'/></span>
  </xsl:template>

</xsl:stylesheet>
