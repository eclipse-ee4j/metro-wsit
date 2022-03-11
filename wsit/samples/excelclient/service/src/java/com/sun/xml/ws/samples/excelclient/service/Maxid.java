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
 * Entity class Maxid
 *
 * @author Jakub Podlesak
 * @author Arun Gupta
 */
@Entity
@Table(name = "MAXID")
@NamedQueries( {
        @NamedQuery(name = "Maxid.findById", query = "SELECT m FROM Maxid m WHERE m.maxid = :maxid"),
        @NamedQuery(name = "Maxid.findByMaxval", query = "SELECT m FROM Maxid m WHERE m.maxval = :maxval")
    })
public class Maxid implements Serializable {

    @Id
    @Column(name = "MAXID", nullable = false)
    private String maxid;

    @Column(name = "MAXVAL", nullable = false)
    private int maxval;

    /** Creates a new instance of Maxid */
    public Maxid() {
    }

    /**
     * Creates a new instance of Maxid with the specified values.
     * @param id the id of the Maxid
     */
    public Maxid(String maxid) {
        this.maxid = maxid;
    }

    /**
     * Creates a new instance of Maxid with the specified values.
     * @param id the id of the Maxid
     * @param maxval the maxval of the Maxid
     */
    public Maxid(String id, int maxval) {
        this.maxid = maxid;
        this.maxval = maxval;
    }

    /**
     * Gets the id of this Maxid.
     * @return the id
     */
    public String getId() {
        return this.maxid;
    }

    /**
     * Sets the id of this Maxid to the specified value.
     * @param id the new id
     */
    public void setId(String id) {
        this.maxid = maxid;
    }

    /**
     * Gets the maxval of this Maxid.
     * @return the maxval
     */
    public int getMaxval() {
        return this.maxval;
    }

    /**
     * Sets the maxval of this Maxid to the specified value.
     * @param maxval the new maxval
     */
    public void setMaxval(int maxval) {
        this.maxval = maxval;
    }

    /**
     * Returns a hash code value for the object.  This implementation computes
     * a hash code value based on the id fields in this object.
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.maxid != null ? this.maxid.hashCode() : 0);
        return hash;
    }

    /**
     * Determines whether another object is equal to this Maxid.  The result is
     * <code>true</code> if and only if the argument is not null and is a Maxid object that
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Maxid)) {
            return false;
        }
        Maxid other = (Maxid)object;
        if (this.maxid != other.maxid && (this.maxid == null || !this.maxid.equals(other.maxid))) return false;
        return true;
    }

    /**
     * Returns a string representation of the object.  This implementation constructs
     * that representation based on the id fields.
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return this.getClass().getName() + "[maxid=" + maxid + "]";
    }

}
