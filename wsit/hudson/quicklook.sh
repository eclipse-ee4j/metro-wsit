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

source setenv.sh

USAGE="Usage: `basename $0` -s gfsvnroot [-g glassfish.zip] [-w workingdir] [-m metro.zip] [-p profile]"

while getopts "g:w:s:m:p:" opt; do
    case $opt in
        s)
         GF_SVN_ROOT=$OPTARG
         ;;
        g)
         GF_ZIP=$OPTARG
         ;;
        w)
         WORK_DIR=$OPTARG
         ;;
        m)
         METRO_ZIP=$OPTARG
         ;;
        p)
         QL_TEST_PROFILE=$OPTARG
         ;;
        \?)
         echo $USAGE >&2
         exit 1
         ;;
    esac
done

#fallback to defaults if needed
if [ -z "$GF_ZIP" ]; then
    GF_ZIP=$GF_SVN_ROOT/appserver/distributions/glassfish/target/glassfish.zip
    echo "setting GF_ZIP to $GF_ZIP"
fi

if [ -z "$QL_TEST_PROFILE" ]; then
    QL_TEST_PROFILE="all"
    echo "setting QL_TEST_PROFILE to $QL_TEST_PROFILE"
fi

set_common

print_env
echo "Test settings:"
echo "====================="
print_test_env

#validate input
declare -a errors
if [[ ( -z "$GF_SVN_ROOT" ) || ( ! -d $GF_SVN_ROOT ) ]]; then
    errors+="GF_SVN_ROOT"
fi
if [ ! -f $GF_ZIP ]; then
    errors+="GF_ZIP "
fi
if [ ! -w "$WORK_DIR" ]; then
    errors+="WORK_DIR "
fi
if [[ ( ! -z "$METRO_ZIP" ) && ( ! -f $METRO_ZIP ) ]]; then
    errors+="METRO_ZIP "
fi
if [ ${#errors[@]} -gt 0 ]; then
    echo "${errors[*]} not set correctly"
    exit 1
fi

_unzip $GF_ZIP $GF_WORK_DIR

if [ -d "$GF_WORK_DIR/glassfish4" ]; then
    SERVER_DIR=glassfish4
else
    SERVER_DIR=glassfish3
fi

if [ ! -z "$METRO_ZIP" ]; then
    install_metro $GF_WORK_DIR/$SERVER_DIR/glassfish
fi

echo "Running GlassFish QuickLook (Profile: $QL_TEST_PROFILE) tests..."

pushd $GF_SVN_ROOT/appserver/tests/quicklook
mvn -s $MVN_SETTINGS -P$QL_TEST_PROFILE -Dglassfish.home=$GF_WORK_DIR/$SERVER_DIR/glassfish test | tee $WORK_DIR/test-quicklook-$QL_TEST_PROFILE.log.txt
popd

echo "Done."
