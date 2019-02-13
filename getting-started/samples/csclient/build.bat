REM
REM  Copyright (c) 1997-2017 Oracle and/or its affiliates. All rights reserved.
REM 
REM  This program and the accompanying materials are made available under the
REM  terms of the Eclipse Distribution License v. 1.0, which is available at
REM  http://www.eclipse.org/org/documents/edl-v10.php.
REM
REM  SPDX-License-Identifier: BSD-3-Clause
REM

svcutil /config:Client.exe.config http://localhost:8080/wsit-enabled-fromjava/addnumbers?wsdl
C:\WINNT\Microsoft.NET\Framework\v2.0.50727\csc.exe /r:"C:\WINNT\Microsoft.NET\Framework\v3.0\Windows Communication Foundation\System.ServiceModel.dll" /r:"C:\WINNT\Microsoft.NET\Framework\v3.0\Windows Communication Foundation\System.Runtime.Serialization.dll" Client.cs AddNumbersImplService.cs
