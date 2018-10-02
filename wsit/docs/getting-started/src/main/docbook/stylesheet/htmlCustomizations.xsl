<?xml version="1.0" encoding="utf-8"?>
<!--

    Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.

    This program and the accompanying materials are made available under the
    terms of the Eclipse Distribution License v. 1.0, which is available at
    http://www.eclipse.org/org/documents/edl-v10.php.

    SPDX-License-Identifier: BSD-3-Clause

-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                xmlns:d="http://docbook.org/ns/docbook"
                exclude-result-prefixes="fo d"
                version="1.0">

    <xsl:import href="common.xsl"/>
    <xsl:import href="htmlProcessing.xsl"/>
    <xsl:import href="urn:docbkx:stylesheet/autotoc.xsl"/>

    <xsl:template match="d:itemizedlist[@role = 'package']" mode="class.value">
        <xsl:value-of select="'itemizedlist package'"/>
    </xsl:template>

    <xsl:template match="d:itemizedlist[@role = 'document']" mode="class.value">
        <xsl:value-of select="'itemizedlist document'"/>
    </xsl:template>

    <xsl:param name="css.decoration" select="0" />

    <!--Override ToC lines-->
    <xsl:template name="toc.line">
        <xsl:param name="toc-context" select="."/>
        <xsl:param name="depth" select="1"/>
        <xsl:param name="depth.from.context" select="8"/>

        <div>
            <xsl:attribute name="class"><xsl:value-of select="local-name(.)"/></xsl:attribute>

            <!-- * if $autotoc.label.in.hyperlink is zero, then output the label -->
            <!-- * before the hyperlinked title (as the DSSSL stylesheet does) -->
            <xsl:if test="$autotoc.label.in.hyperlink = 0">
                <xsl:variable name="label">
                    <xsl:apply-templates select="." mode="label.markup"/>
                </xsl:variable>
                <xsl:copy-of select="$label"/>
                <xsl:if test="$label != ''">
                    <xsl:value-of select="$autotoc.label.separator"/>
                </xsl:if>
            </xsl:if>

            <a>
                <xsl:attribute name="href">
                    <xsl:call-template name="href.target">
                        <xsl:with-param name="context" select="$toc-context"/>
                        <xsl:with-param name="toc-context" select="$toc-context"/>
                    </xsl:call-template>
                </xsl:attribute>

                <xsl:if test="local-name(.) = 'article'">
                    <img src="icons/book.gif" class="article-image" />
                </xsl:if>

                <!-- * if $autotoc.label.in.hyperlink is non-zero, then output the label -->
                <!-- * as part of the hyperlinked title -->
                <xsl:if test="not($autotoc.label.in.hyperlink = 0)">
                    <xsl:variable name="label">
                        <xsl:apply-templates select="." mode="label.markup"/>
                    </xsl:variable>
                    <xsl:copy-of select="$label"/>
                    <xsl:if test="$label != ''">
                        <xsl:value-of select="$autotoc.label.separator"/>
                    </xsl:if>
                </xsl:if>

                <xsl:apply-templates select="." mode="titleabbrev.markup"/>
            </a>

            <xsl:if test="local-name(.) = 'article' and ./d:info/d:abstract/d:para">
                <br /><br />
                <xsl:value-of select="./d:info/d:abstract/d:para" />
            </xsl:if>
        </div>
    </xsl:template>

    <xsl:template name="user.header.content">
        <small class="small">Links: <a href="index.html">Table of Contents</a> | <a href="getting-started.html">Single HTML</a> | <a
                href="getting-started.pdf">Single PDF</a></small>
    </xsl:template>

    <xsl:template name="user.head.content">
<script type="text/javascript">
  var _gaq = _gaq || [];
  _gaq.push(['_setAccount', 'UA-2105126-1']);
  _gaq.push(['_trackPageview']);

  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();
</script>
    </xsl:template>

</xsl:stylesheet>
