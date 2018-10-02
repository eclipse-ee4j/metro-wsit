/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.server;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author Alexey Stashok
 */
public final class TCPStandaloneContext implements TCPContext {
    
    private final ClassLoader classloader;
    private final Map<String, Object> attributes = new HashMap<String, Object>();
    
    public TCPStandaloneContext(final ClassLoader classloader) {
        this.classloader = classloader;
    }
    
    public InputStream getResourceAsStream(final String resource) throws IOException {
        return classloader.getResourceAsStream(resource);
    }
    
    public Set<String> getResourcePaths(final String path) {
        try {
            return populateResourcePaths(path);
        } catch (Exception ex) {
        }
        
        return Collections.emptySet();
    }
    
    
    public URL getResource(String resource) {
        if (resource.charAt(0) == '/') {
            resource = resource.substring(1, resource.length());
        }
        
        return classloader.getResource(resource);
    }
    
    private Enumeration<URL> getResources(String resource) throws IOException {
        if (resource.charAt(0) == '/') {
            resource = resource.substring(1, resource.length());
        }
        
        return classloader.getResources(resource);
    }
    
    private Set<String> populateResourcePaths(final String path) throws Exception {
        final Set<String> resources = new HashSet<String>();
        
        for(final Enumeration<URL> initResources = getResources(path); initResources.hasMoreElements(); ) {
            final URI resourceURI = initResources.nextElement().toURI();
            if (resourceURI.getScheme().equals("file")) {
                gatherResourcesWithFileMode(path, resourceURI, resources);
            } else if (resourceURI.getScheme().equals("jar")) {
                gatherResourcesWithJarMode(path, resourceURI, resources);
            }
        }
        
        return resources;
    }
    
    private void gatherResourcesWithFileMode(final String path, final URI resourceURI, final Set<String> resources) {
        final File file = new File(resourceURI);
        final String[] list = file.list(new FilenameFilter() {
            public boolean accept(File file, String name) {
                return name.charAt(0) != '.';
            }
        });
        
        for(String filename : list) {
            resources.add(path + filename);
        }
    }
    
    private void gatherResourcesWithJarMode(final String path, final URI resourceURI, final Set<String> resources) {
        final String resourceURIAsString = resourceURI.toASCIIString();
        final int pathDelim = resourceURIAsString.indexOf('!');
        final String zipFile = resourceURIAsString.substring("jar:file:/".length(), (pathDelim != -1) ? pathDelim : resourceURIAsString.length());
        ZipFile file = null;
        
        try {
            file = new ZipFile(zipFile);
            
            String pathToCompare = path;
            if (pathToCompare.charAt(0) == '/') {
                pathToCompare = pathToCompare.substring(1, pathToCompare.length());
            }
            if (!pathToCompare.endsWith("/")) {
                pathToCompare = pathToCompare + "/";
            }
            
            for(final Enumeration<? extends ZipEntry> e = file.entries(); e.hasMoreElements(); ) {
                final ZipEntry entry = e.nextElement();
                if (entry.getName().startsWith(pathToCompare) && !entry.getName().equals(pathToCompare)) {
                    resources.add("/" + entry.getName());
                }
            }
        } catch(IOException e) {
        } finally {
            if (file != null) {
                try {
                    file.close();
                } catch (IOException ex) {
                }
            }
        }
    }
    
    public Object getAttribute(final String name) {
        return attributes.get(name);
    }
    
    public void setAttribute(final String name, final Object value) {
        attributes.put(name, value);
    }
}
