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
                exclude-result-prefixes="d fo"
                xmlns:d="http://docbook.org/ns/docbook"
                version="1.0">

    <xsl:import href="urn:docbkx:stylesheet"/>
    <xsl:import href="highlight.xsl"/>

    <xsl:param name="abstract.notitle.enabled" select="1"/>

    <xsl:param name="toc.max.depth" select="0" />

    <xsl:param name="generate.toc">
        appendix  toc,title
        article/appendix  nop
        article   toc,title
        book      toc,title,figure,table,equation
        chapter   toc,title
        part      toc,title
        preface   toc,title
        qandadiv  toc
        qandaset  toc
        reference toc,title
        sect1     toc
        sect2     toc
        sect3     toc
        sect4     toc
        sect5     toc
        section   toc
        set       toc,title
    </xsl:param>

</xsl:stylesheet>
