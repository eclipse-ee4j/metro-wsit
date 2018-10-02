/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.installer;

/*
 * updateSharedLoaderProp.java
 *
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Replace;

public class UpdateSharedLoaderProp extends Task {
    
    String tomcatLib;
    public void settomcatLib(String tomcatLib) {
        this.tomcatLib = tomcatLib;
    }
    
    String catalinaProps;
    public void setCatalinaProps(String catalinaProps) {
        this.catalinaProps = catalinaProps;
    }
    
    @Override
    public void execute() {
        if (tomcatLib == null) {
            // Default to shared/lib
            tomcatLib = new String("${catalina.home}/shared/lib");
        }
        if (catalinaProps == null) {
            throw new BuildException("No catalinaProps set!");
        }
        //log("tomcatLib = " + tomcatLib + " catalinaProps = " + catalinaProps, Project.MSG_WARN);

        //
        final String jarWildcard = new String("/*.jar");
        final String metroJars = new String(tomcatLib + jarWildcard);
        // Read properties file.
        FileInputStream propsFileStream = null;
        Properties properties = new Properties();
        try {
            propsFileStream = new FileInputStream(catalinaProps);
            if (propsFileStream != null) {
                properties.load(propsFileStream);
                propsFileStream.close();
            }
        } catch (IOException e) {
            throw new BuildException("Missing or inaccessible " + catalinaProps + " file");
        }
        
        String sharedLoader = properties.getProperty("shared.loader");
        String newSharedLoader = null;
        if (sharedLoader == null || sharedLoader.length() == 0) {
            newSharedLoader = metroJars;
        }
        else if (sharedLoader.contains(metroJars)) {
            // already has what is needed
            return;
        }
        else {
            // has values but not shared/lib/*.jars
            newSharedLoader = new String(metroJars + "," + sharedLoader);
        }

        Replace replace = new Replace();
        File propsFile = new File(catalinaProps);
        replace.setProject(this.getProject());
        replace.setFile(propsFile);
        replace.setToken("shared.loader=" + sharedLoader);
        replace.setValue("shared.loader=" + newSharedLoader);
        try {
            replace.execute();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

}
