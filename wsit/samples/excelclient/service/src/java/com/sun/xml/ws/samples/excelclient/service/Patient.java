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
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Entity class Patient
 *
 * @author Jakub Podlesak
 * @author Arun Gupta
 */
@Entity
@Table(name = "PATIENT")
@NamedQueries( {
        @NamedQuery(name = "Patient.findByPatientid", query = "SELECT p FROM Patient p WHERE p.patientid = :patientid"),
        @NamedQuery(name = "Patient.findByFirstname", query = "SELECT p FROM Patient p WHERE p.firstname = :firstname"),
        @NamedQuery(name = "Patient.findBySurname", query = "SELECT p FROM Patient p WHERE p.surname = :surname"),
        @NamedQuery(name = "Patient.findByDob", query = "SELECT p FROM Patient p WHERE p.dob = :dob"),
        @NamedQuery(name = "Patient.findBySsn", query = "SELECT p FROM Patient p WHERE p.ssn = :ssn")
    })
public class Patient implements Serializable {

    @Id
    @Column(name = "PATIENTID", nullable = false)
    private Integer patientid;

    @Column(name = "FIRSTNAME", nullable = false)
    private String firstname;

    @Column(name = "SURNAME", nullable = false)
    private String surname;

    @Column(name = "DOB", nullable = false)
    private String dob;

    @Column(name = "SSN", nullable = false)
    private String ssn;

    /** Creates a new instance of Patient */
    public Patient() {
    }

    /**
     * Creates a new instance of Patient with the specified values.
     * @param patientid the patientid of the Patient
     */
    public Patient(Integer patientid) {
        this.patientid = patientid;
    }

    /**
     * Creates a new instance of Patient with the specified values.
     * @param patientid the patientid of the Patient
     * @param firstname the firstname of the Patient
     * @param surname the surname of the Patient
     * @param dob the dob of the Patient
     * @param ssn the ssn of the Patient
     */
    public Patient(Integer patientid, String firstname, String surname, String dob, String ssn) {
        this.patientid = patientid;
        this.firstname = firstname;
        this.surname = surname;
        this.dob = dob;
        this.ssn = ssn;
    }

    /**
     * Gets the patientid of this Patient.
     * @return the patientid
     */
    public Integer getPatientid() {
        return this.patientid;
    }

    /**
     * Sets the patientid of this Patient to the specified value.
     * @param patientid the new patientid
     */
    public void setPatientid(Integer patientid) {
        this.patientid = patientid;
    }

    /**
     * Gets the firstname of this Patient.
     * @return the firstname
     */
    public String getFirstname() {
        return this.firstname;
    }

    /**
     * Sets the firstname of this Patient to the specified value.
     * @param firstname the new firstname
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /**
     * Gets the surname of this Patient.
     * @return the surname
     */
    public String getSurname() {
        return this.surname;
    }

    /**
     * Sets the surname of this Patient to the specified value.
     * @param surname the new surname
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /**
     * Gets the dob of this Patient.
     * @return the dob
     */
    public String getDob() {
        return this.dob;
    }

    /**
     * Sets the dob of this Patient to the specified value.
     * @param dob the new dob
     */
    public void setDob(String dob) {
        this.dob = dob;
    }

    /**
     * Gets the ssn of this Patient.
     * @return the ssn
     */
    public String getSsn() {
        return this.ssn;
    }

    /**
     * Sets the ssn of this Patient to the specified value.
     * @param ssn the new ssn
     */
    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    /**
     * Returns a hash code value for the object.  This implementation computes
     * a hash code value based on the id fields in this object.
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.patientid != null ? this.patientid.hashCode() : 0);
        return hash;
    }

    /**
     * Determines whether another object is equal to this Patient.  The result is
     * <code>true</code> if and only if the argument is not null and is a Patient object that
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Patient)) {
            return false;
        }
        Patient other = (Patient)object;
        if (this.patientid != other.patientid && (this.patientid == null || !this.patientid.equals(other.patientid))) return false;
        return true;
    }

    /**
     * Returns a string representation of the object.  This implementation constructs
     * that representation based on the id fields.
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return this.getClass().getName() + "[patientid=" + patientid + "]";
    }

}
