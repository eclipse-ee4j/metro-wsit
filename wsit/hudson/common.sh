#!/bin/bash -ex
#
# Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Distribution License v. 1.0, which is available at
# http://www.eclipse.org/org/documents/edl-v10.php.
#
# SPDX-License-Identifier: BSD-3-Clause
#

# $1 - URL
function _wget() {
    if [[ "$1" == *.oracle.com* ]]; then
        WGET_PROXY="--no-proxy"
    else
        WGET_PROXY=""
    fi
    wget $WGET_PROXY -N $1
}

function set_proxy() {
    if [ ! -z "$PROXY_HOST" ]; then
        if [ ! -z "$PROXY_PORT" ]; then
            export http_proxy=$PROXY_HOST:$PROXY_PORT
            JAVA_PROXY_PORT="-Dhttp.proxyPort=$PROXY_PORT -Dhttps.proxyPort=$PROXY_PORT"
        else
            export http_proxy=$PROXY_HOST
        fi
        export https_proxy=$http_proxy
        export HTTP_PROXY=$http_proxy
        export HTTPS_PROXY=$https_proxy
        export JAVA_PROXY_PROP="-Dhttp.proxyHost=$PROXY_HOST -Dhttps.proxyHost=$PROXY_HOST $JAVA_PROXY_PORT"
    fi
}
