#!/bin/sh -ex
#
# Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Distribution License v. 1.0, which is available at
# http://www.eclipse.org/org/documents/edl-v10.php.
#
# SPDX-License-Identifier: BSD-3-Clause
#

# sed/changeword

old=$1
new=$2
file=$3

for FIL in `find . -name $file -exec grep -l $old {} \;`
   do
       echo 'Replacing in '$FIL
       perl -i -pe "s|$old|$new|g" $FIL
   done 
