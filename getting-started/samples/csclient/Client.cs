/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

using System;

class Client {
	static void Main(String[] args) {
	AddNumbersImplClient port = null;
	try {
	          port = new AddNumbersImplClient("AddNumbersImplPort");
                int number1 = 10;
                int number2 = 20;

		    Console.Write("Adding {0} and {1}.  ", number1, number2);
                int result = port.addNumbers (number1, number2);
                Console.WriteLine("Result is {0}.\n\n",result);
            
                number1 = -10;
		    Console.Write("Adding {0} and {1}.  ", number1, number2);
                result = port.addNumbers (number1, number2);
		    Console.WriteLine("Result is {0}.\n\n",result);
                port.Close();	} catch (System.ServiceModel.FaultException e) 
      {
		Console.WriteLine("Exception: " + e.Message);
		if (port != null) port.Close();
	}
   }
}
