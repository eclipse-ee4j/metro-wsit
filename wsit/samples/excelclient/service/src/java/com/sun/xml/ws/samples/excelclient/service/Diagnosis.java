/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.samples.excelclient.service;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Entity class Diagnosis
 *
 * @author Jakub Podlesak
 * @author Arun Gupta
 */
@Entity
@Table(name = "DIAGNOSIS")
@NamedQueries( {
        @NamedQuery(name = "Diagnosis.findByDiagid", query = "SELECT d FROM Diagnosis d WHERE d.diagid = :diagid"),
        @NamedQuery(name = "Diagnosis.findByPatientid", query = "SELECT d FROM Diagnosis d WHERE d.patientid = :patientid"),
        @NamedQuery(name = "Diagnosis.findByDiagcode", query = "SELECT d FROM Diagnosis d WHERE d.diagcode = :diagcode")
    })
public class Diagnosis implements Serializable {

    @Id
    @Column(name = "DIAGID", nullable = false)
    private Integer diagid;

    @Column(name = "PATIENTID", nullable = false)
    private int patientid;

    @Column(name = "DIAGCODE", nullable = false)
    private String diagcode;

    /** Creates a new instance of Diagnosis */
    public Diagnosis() {
    }

    /**
     * Creates a new instance of Diagnosis with the specified values.
     * @param diagid the diagid of the Diagnosis
     */
    public Diagnosis(Integer diagid) {
        this.diagid = diagid;
    }

    /**
     * Creates a new instance of Diagnosis with the specified values.
     * @param diagid the diagid of the Diagnosis
     * @param patientid the patientid of the Diagnosis
     * @param diagcode the diagcode of the Diagnosis
     */
    public Diagnosis(Integer diagid, int patientid, String diagcode) {
        this.diagid = diagid;
        this.patientid = patientid;
        this.diagcode = diagcode;
    }

    /**
     * Gets the diagid of this Diagnosis.
     * @return the diagid
     */
    public Integer getDiagid() {
        return this.diagid;
    }

    /**
     * Sets the diagid of this Diagnosis to the specified value.
     * @param diagid the new diagid
     */
    public void setDiagid(Integer diagid) {
        this.diagid = diagid;
    }

    /**
     * Gets the patientid of this Diagnosis.
     * @return the patientid
     */
    public int getPatientid() {
        return this.patientid;
    }

    /**
     * Sets the patientid of this Diagnosis to the specified value.
     * @param patientid the new patientid
     */
    public void setPatientid(int patientid) {
        this.patientid = patientid;
    }

    /**
     * Gets the diagcode of this Diagnosis.
     * @return the diagcode
     */
    public String getDiagcode() {
        return this.diagcode;
    }

    /**
     * Sets the diagcode of this Diagnosis to the specified value.
     * @param diagcode the new diagcode
     */
    public void setDiagcode(String diagcode) {
        this.diagcode = diagcode;
    }

    /**
     * Returns a hash code value for the object.  This implementation computes
     * a hash code value based on the id fields in this object.
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.diagid != null ? this.diagid.hashCode() : 0);
        return hash;
    }

    /**
     * Determines whether another object is equal to this Diagnosis.  The result is
     * <code>true</code> if and only if the argument is not null and is a Diagnosis object that
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Diagnosis)) {
            return false;
        }
        Diagnosis other = (Diagnosis)object;
        if (this.diagid != other.diagid && (this.diagid == null || !this.diagid.equals(other.diagid))) return false;
        return true;
    }

    /**
     * Returns a string representation of the object.  This implementation constructs
     * that representation based on the id fields.
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return this.getClass().getName() + "[diagid=" + diagid + "]";
    }

}
