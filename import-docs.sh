#!/bin/bash
#
# Copyright (c) 2020 Oracle and/or its affiliates.
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Distribution License v. 1.0, which is available at
# http://www.eclipse.org/org/documents/edl-v10.php.
#
# SPDX-License-Identifier: BSD-3-Clause
#

# utility to help importing release documentation

VERSION=3.0.0
ARTIFACT=https://jakarta.oss.sonatype.org/content/groups/staging/org/glassfish/metro/guide/$VERSION/guide-$VERSION-docbook.zip

wget -O guide.zip $ARTIFACT

ARTIFACT=https://jakarta.oss.sonatype.org/content/groups/staging/org/glassfish/metro/getting-started/$VERSION/getting-started-$VERSION-docbook.zip

wget -O getting-started.zip $ARTIFACT

mkdir -p $VERSION/guide $VERSION/getting-started

unzip -d $VERSION/guide guide.zip
unzip -d $VERSION/getting-started getting-started.zip

find $VERSION -name "*.html" -exec sed -i '' $'1s/^/---\\\nlayout: content\\\n---\\\n/' {} \;

rm guide.zip getting-started.zip
