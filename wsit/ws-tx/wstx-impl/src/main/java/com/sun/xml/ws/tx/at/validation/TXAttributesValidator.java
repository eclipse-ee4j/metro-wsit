/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.tx.at.validation;

import com.sun.xml.ws.api.tx.at.Transactional;

import jakarta.xml.ws.WebServiceException;
import java.util.HashSet;
import java.util.Set;

public class TXAttributesValidator {

  public static final short TX_NOT_SET = -1;
  public static final short TX_NOT_SUPPORTED = 0;
  public static final short TX_REQUIRED = 1;
  public static final short TX_SUPPORTS = 2;
  public static final short TX_REQUIRES_NEW = 3;
  public static final short TX_MANDATORY = 4;
  public static final short TX_NEVER = 5;

   Set<InvalidCombination> inValidateCombinations = new HashSet<>();
  static Set<Combination> validateCombinations = new HashSet<>();

  static {
    validateCombinations.add(new Combination(TransactionAttributeType.REQUIRED, Transactional.TransactionFlowType.MANDATORY));
    validateCombinations.add(new Combination(TransactionAttributeType.REQUIRED, Transactional.TransactionFlowType.NEVER));
    validateCombinations.add(new Combination(TransactionAttributeType.MANDATORY, Transactional.TransactionFlowType.MANDATORY));
    validateCombinations.add(new Combination(TransactionAttributeType.REQUIRED, Transactional.TransactionFlowType.SUPPORTS));
    validateCombinations.add(new Combination(TransactionAttributeType.SUPPORTS, Transactional.TransactionFlowType.SUPPORTS));
    validateCombinations.add(new Combination(TransactionAttributeType.REQUIRES_NEW, Transactional.TransactionFlowType.NEVER));
    validateCombinations.add(new Combination(TransactionAttributeType.NEVER, Transactional.TransactionFlowType.NEVER));
    validateCombinations.add(new Combination(TransactionAttributeType.NOT_SUPPORTED, Transactional.TransactionFlowType.NEVER));
    //this is not on the FS.
    validateCombinations.add(new Combination(TransactionAttributeType.SUPPORTS, Transactional.TransactionFlowType.NEVER));
    validateCombinations.add(new Combination(TransactionAttributeType.SUPPORTS, Transactional.TransactionFlowType.MANDATORY));
  }

  public void visitOperation(String operationName, short attribute, Transactional.TransactionFlowType wsatType) {
    TransactionAttributeType ejbTx = fromIndex(attribute);
    visitOperation(operationName,ejbTx, wsatType);
  }

  public void validate() throws WebServiceException {
    StringBuilder sb = new StringBuilder();
    for (InvalidCombination combination : inValidateCombinations) {
      sb.append("The effective TransactionAttributeType ").append(combination.ejbTx).append(" and WS-AT Transaction flowType ").append(combination.wsat).append(" on WebService operation ").append(combination.operationName).append(" is not a valid combination! ");
    }
    if (sb.length() > 0)
      throw new WebServiceException(sb.toString());
  }

  public void visitOperation(String operationName, TransactionAttributeType ejbTx, Transactional.TransactionFlowType wsatType) {
    if (wsatType == null) wsatType = Transactional.TransactionFlowType.NEVER;
    Combination combination = new Combination(ejbTx, wsatType);
    if (!validateCombinations.contains(combination)) {
      inValidateCombinations.add(new InvalidCombination(ejbTx, wsatType, operationName));
    }
  }

  public static boolean isValid(TransactionAttributeType ejbTx, Transactional.TransactionFlowType wsatType) {
    return validateCombinations.contains(new Combination(ejbTx, wsatType));
  }

  private static TransactionAttributeType fromIndex(Short index) {
    switch (index) {
      case TX_NOT_SUPPORTED:
        return TransactionAttributeType.NOT_SUPPORTED;
      case TX_REQUIRED:
        return TransactionAttributeType.REQUIRED;
      case TX_SUPPORTS:
        return TransactionAttributeType.SUPPORTS;
      case TX_REQUIRES_NEW:
        return TransactionAttributeType.REQUIRES_NEW;
      case TX_MANDATORY:
        return TransactionAttributeType.MANDATORY;
      case TX_NEVER:
        return TransactionAttributeType.NEVER;
      default:
        return TransactionAttributeType.SUPPORTS;
    }
  }

  static class Combination {
    TransactionAttributeType ejbTx;
    Transactional.TransactionFlowType wsat;

    Combination(TransactionAttributeType ejbTx, Transactional.TransactionFlowType wsat) {
      this.ejbTx = ejbTx;
      this.wsat = wsat;
    }

    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Combination that = (Combination) o;

      if (ejbTx != that.ejbTx) return false;
        return wsat == that.wsat;
    }

    public int hashCode() {
      int result;
      result = ejbTx.hashCode();
      result = 31 * result + wsat.hashCode();
      return result;
    }
  }

  static class InvalidCombination {
    TransactionAttributeType ejbTx;
    Transactional.TransactionFlowType wsat;
    String operationName;

    InvalidCombination(TransactionAttributeType ejbTx, Transactional.TransactionFlowType wsat, String operationName) {
      this.ejbTx = ejbTx;
      this.wsat = wsat;
      this.operationName = operationName;
    }

    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      InvalidCombination that = (InvalidCombination) o;

      if (ejbTx != that.ejbTx) return false;
      if (!operationName.equals(that.operationName)) return false;
        return wsat == that.wsat;
    }

    public int hashCode() {
      int result;
      result = ejbTx.hashCode();
      result = 31 * result + wsat.hashCode();
      result = 31 * result + operationName.hashCode();
      return result;
    }
  }


public enum TransactionAttributeType {

   MANDATORY,
   REQUIRED,
   REQUIRES_NEW,
   SUPPORTS,
   NOT_SUPPORTED,
   NEVER
}

}
