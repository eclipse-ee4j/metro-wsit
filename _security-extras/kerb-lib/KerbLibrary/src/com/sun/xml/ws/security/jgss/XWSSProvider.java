/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*    */ package com.sun.xml.ws.security.jgss;
/*    */ 
/*    */ import java.security.AccessController;
/*    */ import java.security.PrivilegedAction;
/*    */ import java.security.Provider;
/*    */ 
/*    */ public final class XWSSProvider extends Provider
/*    */ {
/*    */   private static final long serialVersionUID = -238911724858694198L;
/*    */   private static final String INFO = "Sun (Kerberos v5, SPNEGO)";
/* 62 */   public static final XWSSProvider INSTANCE = new XWSSProvider();
/*    */ 
/*    */   public XWSSProvider()
/*    */   {
/* 66 */     super("XWSSJGSS", 1.0D, INFO);
/*    */ 
/* 68 */     AccessController.doPrivileged(new PrivilegedAction() {
/*    */       public Object run() {
/* 70 */         XWSSProvider.this.put("GssApiMechanism.1.2.840.113554.1.2.2", "com.sun.xml.ws.security.kerb.Krb5MechFactory");
/*    */ 
/* 72 */         XWSSProvider.this.put("GssApiMechanism.1.3.6.1.5.5.2", "sun.security.jgss.spnego.SpNegoMechFactory");
/*    */ 
/* 78 */         return null;
/*    */       }
/*    */     });
/*    */   }
/*    */ }
