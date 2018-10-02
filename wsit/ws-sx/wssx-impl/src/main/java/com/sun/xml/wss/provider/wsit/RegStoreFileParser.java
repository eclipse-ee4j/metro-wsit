/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.provider.wsit;

import com.sun.xml.wss.provider.wsit.logging.LogDomainConstants;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.message.config.AuthConfigFactory.RegistrationContext;

/**
 * Used by GFServerConfigProvider to parse the configuration file. If
 * a file does not exist originally, the default providers are not used.
 * A file is only created if needed, which happens if providers are
 * registered or unregistered through the store() or delete() methods.
 *
 * @author Bobby Bissett
 */
public final class RegStoreFileParser {

    private static Logger logger =Logger.getLogger(
            LogDomainConstants.WSIT_PVD_DOMAIN,
            LogDomainConstants.WSIT_PVD_DOMAIN_BUNDLE);

    private static final String SEP = ":";
    private static final String CON_ENTRY = "con-entry";
    private static final String REG_ENTRY = "reg-entry";
    private static final String REG_CTX = "reg-ctx";
    private static final String LAYER = "layer";
    private static final String APP_CTX = "app-ctx";
    private static final String DESCRIPTION = "description";
    private static final String [] INDENT = { "", "  ", "    " };
    
    private File confFile;
    private List<EntryInfo> entries;
    
    /*
     * Loads the configuration file from the given filename.
     * If a file is not found, then the default entries
     * stored in GFAuthConfigFactory are used. Otherwise,
     * the file is parsed to load the entries.
     *
     * The boolean argument tells whether to create the config
     * file always (true) or only if it's needed (false).
     */
    RegStoreFileParser(String pathParent, String pathChild, boolean create) {
        try {
            confFile = new File(pathParent, pathChild);
            if (confFile.exists()) {
                loadEntries();
            } else {
                if (create) {
                    synchronized (confFile) {
                        entries = JMACAuthConfigFactory.getDefaultProviders();
                        writeEntries();
                    }
                } else {
                    if (logger.isLoggable(Level.FINER)) {
                        logger.log(Level.FINER, "jmac.factory_file_not_found",
                            pathParent + File.pathSeparator + pathChild);
                    }
                }
            }
        } catch (IOException ioe) {
            logWarningDefault(ioe);
        } catch (IllegalArgumentException iae) {
            logWarningDefault(iae);
        }
        
        // file not parsed
        if (entries == null) {
            entries = JMACAuthConfigFactory.getDefaultProviders();
        }
    }
    
    private void logWarningUpdated(Exception exception) {
        if (logger.isLoggable(Level.WARNING)) {
            logger.log(Level.WARNING,
                "jmac.factory_could_not_persist", exception.toString());
        }
    }
    
    private void logWarningDefault(Exception exception) {
        if (logger.isLoggable(Level.WARNING)) {
            logger.log(Level.WARNING,
                "jmac.factory_could_not_read", exception.toString());
        }
    }
    
    /*
     * Returns the in-memory list of entries.
     */
    List<EntryInfo> getPersistedEntries() {
        return entries;
    }

    /*
     * Adds the provider to the entry list if it is not already
     * present, creates the configuration file if necessary, and
     * writes the entries to the file.
     */
    void store(String className, RegistrationContext ctx, Map<String, String> properties) {
        synchronized (this) {
            if (checkAndAddToList(className, ctx, properties)) {
                try {
                    writeEntries();
                } catch (IOException ioe) {
                    logWarningUpdated(ioe);
                }
            }
        }
    }

    /*
     * Removes the provider from the entry list if it is already
     * present, creates the configuration file if necessary, and
     * writes the entries to the file.
     */
    void delete(RegistrationContext ctx) {
        synchronized (this) {
            if (checkAndRemoveFromList(ctx)) {
                try {
                    writeEntries();
                } catch (IOException ioe) {
                    logWarningUpdated(ioe);
                }
            }
        }
    }

    /*
     * If this entry does not exist, this method stores it in
     * the entries list and returns true to indicate that the
     * configuration file should be written.
     */
    private boolean checkAndAddToList(String className,
        RegistrationContext ctx, Map<String, String> props) {

        // convention is to use null for empty properties
        if (props != null && props.isEmpty()) {
            props = null;
        }
        EntryInfo newEntry = new EntryInfo(className, props, ctx);
        EntryInfo entry = getMatchingRegEntry(newEntry);
        
        // there is no matching entry, so add to list
        if (entry == null) {
            entries.add(newEntry);
            return true;
        }
        
        // if constructor entry, don't need to check reg context
        if (entry.isConstructorEntry()) {
            return false;
        }
        
        // otherwise, check reg contexts to see if there is a match
        if (entry.getRegContexts().contains(ctx)) {
            return false;
        }
        
        // no matching context in existing entry, so add to existing entry
        entry.getRegContexts().add(new RegistrationContextImpl(ctx));
        return true;
    }
    
    /*
     * If this registration context does not exist, this method
     * returns false. Otherwise it removes the entry and returns
     * true to indicate that the configuration file should be written.
     *
     * This only makes sense for registry entries.
     */
    private boolean checkAndRemoveFromList(RegistrationContext target) {
        boolean retValue = false;
        for (EntryInfo info : entries) {
            if (info.isConstructorEntry()) {
                continue;
            }

            Iterator<RegistrationContext> iter = 
                    info.getRegContexts().iterator();
            while (iter.hasNext()) {
                RegistrationContext ctx = iter.next();
                if (ctx.equals(target)) {
                    iter.remove();
                    retValue = true;
                }
            }
        }
        return retValue;
    }
    
    /*
     * Used to find a matching registration entry in the 'entries'
     * list without including registration contexts. If there is not
     * a matching entry, return null.
     */
    private EntryInfo getMatchingRegEntry(EntryInfo target) {
        for (EntryInfo info : entries) {
            if (info.equals(target)) {
                return info;
            }
        }
        return null;
    }
    
    /*
     * This method overwrites the existing file with the
     * current entries.
     */
    private void writeEntries() throws IOException {
        if (!confFile.canWrite() && logger.isLoggable(Level.WARNING)) {
            logger.log(Level.WARNING, "jmac.factory_cannot_write_file",
                confFile.getPath());
        }
        clearExistingFile();
        PrintWriter out = new PrintWriter(confFile);
        int indent = 0;
        for (EntryInfo info : entries) {
            if (info.isConstructorEntry()) {
                writeConEntry(info, out, indent);
            } else {
                writeRegEntry(info, out, indent);
            }
        }
        out.close();
    }

    /*
     * Writes constructor entry output of the form:
     * <pre>
     * con-entry {
     *   className
     *   key:value
     *   key:value
     * }
     * </pre>
     * The first appearance of a colon ":" separates
     * the key and value of the property (so a value may
     * contain a colon as part of the string). For instance:
     * "mydir:c:foo" would have key "mydir" and value "c:foo".
     */
    private void writeConEntry(EntryInfo info, PrintWriter out, int i) {
        out.println(INDENT[i++] + CON_ENTRY + " {");
        out.println(INDENT[i] + info.getClassName());
        Map<String, String> props = info.getProperties();
        if (props != null) {
            for (String key : props.keySet()) {
                out.println(INDENT[i] + key + SEP + props.get(key));
            }
        }
        out.println(INDENT[--i] + "}");
    }
    
    /*
     * Write registration entry output of the form:
     * <pre>
     * reg-entry {
     *   con-entry { see writeConEntry() for detail }
     *   reg-ctx {
     *     layer:HttpServlet
     *     app-ctx:security-jmac-https
     *     description:My provider
     *   }
     * }
     * </pre>
     */
    private void writeRegEntry(EntryInfo info, PrintWriter out, int i) {
        out.println(INDENT[i++] + REG_ENTRY + " {");
        if (info.getClassName() != null) {
            writeConEntry(info, out, i);
        }
        for (RegistrationContext ctx : info.getRegContexts()) {
            out.println(INDENT[i++] + REG_CTX + " {");
            if (ctx.getMessageLayer() != null) {
                out.println(INDENT[i] + LAYER + SEP + ctx.getMessageLayer());
            }
            if (ctx.getAppContext() != null) {
                out.println(INDENT[i] + APP_CTX + SEP + ctx.getAppContext());
            }
            if (ctx.getDescription() != null) {
                out.println(INDENT[i] + DESCRIPTION +
                    SEP + ctx.getDescription());
            }
            out.println(INDENT[--i] + "}");
        }
        out.println(INDENT[--i] + "}");
    }

    private void clearExistingFile() throws IOException {
        if (confFile.exists()) {
            confFile.delete();
        }
        confFile.createNewFile();
    }

    /*
     * Called from the constructor. This is the only time
     * the file is read, though it is written when new
     * entries are stored or deleted.
     */
    private void loadEntries() throws IOException {
        entries = new ArrayList<EntryInfo>();
        BufferedReader reader = new BufferedReader(new FileReader(confFile));
        String line = reader.readLine();
        while (line != null) {
            String trimLine = line.trim(); // can't trim readLine() result
            if (trimLine.startsWith(CON_ENTRY)) {
                entries.add(readConEntry(reader));
            } else if (trimLine.startsWith(REG_ENTRY)) {
                entries.add(readRegEntry(reader));
            }
            line = reader.readLine();
        }
    }

    private EntryInfo readConEntry(BufferedReader reader) throws IOException {
        // entry must contain class name as next line
        String line = reader.readLine();
        String className = (line != null) ? line.trim() : null;
        Map<String, String> properties = readProperties(reader);
        return new EntryInfo(className, properties);
    }
    
    /*
     * Properties must be of the form "key:value." While the key
     * String cannot contain a ":" character, the value can. The
     * line will be broken into key and value based on the first
     * appearance of the ":" character.
     */
    private Map<String, String> readProperties(BufferedReader reader)
        throws IOException {
        String nextLine = reader.readLine();
        String line = (nextLine != null)? nextLine.trim():null;
        if (line != null && line.equals("}")) {
            return null;
        }
        Map<String, String> properties = new HashMap<String, String>();
        while (line != null && !line.equals("}")) {
            properties.put(line.substring(0, line.indexOf(SEP)),
                line.substring(line.indexOf(SEP) + 1, line.length()));
            line = reader.readLine().trim();
        }
        return properties;
    }

    private EntryInfo readRegEntry(BufferedReader reader) throws IOException {
        String className = null;
        Map<String, String> properties = null;
        List<RegistrationContext> ctxs =
            new ArrayList<RegistrationContext>();
        String nextLine = reader.readLine();
        String line = (nextLine != null)? nextLine.trim():null;
        while (line != null && !line.equals("}")) {
            if (line.startsWith(CON_ENTRY)) {
                EntryInfo conEntry = readConEntry(reader);
                className = conEntry.getClassName();
                properties = conEntry.getProperties();
            } else if (line.startsWith(REG_CTX)) {
                ctxs.add(readRegContext(reader));
            }
            line = reader.readLine().trim();
        }
        return new EntryInfo(className, properties, ctxs);
    }
    
    private RegistrationContext readRegContext(BufferedReader reader)
        throws IOException {
        
        String layer = null;
        String appCtx = null;
        String description = null;
        String nextLine = reader.readLine();
        String line = (nextLine != null)? nextLine.trim():null;
        while (line != null && !line.equals("}")) {
            String value = line.substring(line.indexOf(SEP) + 1,
                line.length());
            if (line.startsWith(LAYER)) {
                layer = value;
            } else if (line.startsWith(APP_CTX)) {
                appCtx = value;
            } else if (line.startsWith(DESCRIPTION)) {
                description = value;
            }
            line = reader.readLine().trim();
        }
        return new RegistrationContextImpl(layer, appCtx, description, true);
    }
    
}
