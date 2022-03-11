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

import jakarta.annotation.Resource;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import jakarta.transaction.UserTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import java.util.List;

/**
 * @author Jakub Podlesak
 * @author Arun Gupta
 */
@WebService()
public class WSITEndpoint {

    @PersistenceUnit
    private EntityManagerFactory emf;

    @Resource
    private UserTransaction utx;

    @WebMethod(action="getPatientId")
    public int getPatientId(
            @WebParam(name = "firstname") String firstname,
    @WebParam(name = "surname") String surname,
    @WebParam(name ="dob") String dob,
    @WebParam(name = "ssn") String ssn) {
        int patientId = 0;
        Patient ourGuy;
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            utx.begin();
            em.joinTransaction();
            Query query = em.createQuery("SELECT p FROM Patient p WHERE p.ssn = :ssn");
            query.setParameter("ssn", ssn);
            try {
                ourGuy = (Patient)query.getSingleResult();
            } catch (NoResultException nre) {
                ourGuy = null;
            }
            if(ourGuy != null) {
                patientId = ourGuy.getPatientid();
            } else {
                ourGuy = new Patient();
                ourGuy.setFirstname(firstname);
                ourGuy.setSurname(surname);
                ourGuy.setSsn(ssn);
                ourGuy.setDob(dob);
                ourGuy.setPatientid(getNewId(em, "PATIENTID"));
                em.persist(ourGuy);
                patientId = ourGuy.getPatientid();
            }
            utx.commit();
        } catch (Exception e) {
            System.out.println("e=" + e.getMessage());
            e.printStackTrace(System.out);
        } finally {
            if (null != em) {
                em.close();
            }
        }

        return patientId;
    }

    @WebMethod(action="getPatientFirstname")
    public String getPatientFirstname(
            @WebParam(name = "patientid") int patientid) {
        Patient ourGuy;
        String result = null;
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            utx.begin();
            em.joinTransaction();
            ourGuy = em.find(Patient.class, patientid);
            if(ourGuy != null) {
                result = ourGuy.getFirstname();
            }
            utx.commit();
        } catch (Exception e) {
            System.out.println("e=" + e.getMessage());
            e.printStackTrace(System.out);
        } finally {
            if (null != em) {
                em.close();
            }
        }

        return result;
    }

    @WebMethod(action="getPatientSurname")
    public String getPatientSurname(
            @WebParam(name = "patientid") int patientid) {
        Patient ourGuy;
        String result = null;
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            utx.begin();
            em.joinTransaction();
            ourGuy = em.find(Patient.class, patientid);
            if(ourGuy != null) {
                result = ourGuy.getSurname();
            }
            utx.commit();
        } catch (Exception e) {
            System.out.println("e=" + e.getMessage());
            e.printStackTrace(System.out);
        } finally {
            if (null != em) {
                em.close();
            }
        }

        return result;
    }

    @WebMethod(action="getPatientDOB")
    public String getPatientDOB(
            @WebParam(name = "patientid") int patientid) {
        Patient ourGuy;
        String result = null;
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            utx.begin();
            em.joinTransaction();
            ourGuy = em.find(Patient.class, patientid);
            if(ourGuy != null) {
                result = ourGuy.getDob();
            }
            utx.commit();
        } catch (Exception e) {
            System.out.println("e=" + e.getMessage());
            e.printStackTrace(System.out);
        } finally {
            if (null != em) {
                em.close();
            }
        }

        return result;
    }

    @WebMethod(action="getPatientSSN")
    public String getPatientSSN(
            @WebParam(name = "patientid") int patientid) {
        Patient ourGuy;
        String result = null;
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            utx.begin();
            em.joinTransaction();
            ourGuy = em.find(Patient.class, patientid);
            if(ourGuy != null) {
                result = ourGuy.getSsn();
            }
            utx.commit();
        } catch (Exception e) {
            System.out.println("e=" + e.getMessage());
            e.printStackTrace(System.out);
        } finally {
            if (null != em) {
                em.close();
            }
        }

        return result;
    }

    @WebMethod(action="getPatientDiagnosis")
    @SuppressWarnings("unchecked")
    public String getPatientDiagnosis(
            @WebParam(name = "patientid") int patientid) {
        String result = "";
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            utx.begin();
            em.joinTransaction();
            Query query = em.createQuery("SELECT d FROM Diagnosis d WHERE d.patientid = :patientid");
            query.setParameter("patientid", patientid);
            List<Diagnosis> diags = query.getResultList();
            if (diags != null) {
                for (Diagnosis diag : diags) {
                    result += (("".equals(result)) ? "" : ", ") + diag.getDiagcode();
                }
            }
            utx.commit();
        } catch (Exception e) {
            System.out.println("e=" + e.getMessage());
            e.printStackTrace(System.out);
        } finally {
            if (null != em) {
                em.close();
            }
        }
        return result;
    }

    private int getNewId(final EntityManager em, final String maxid) {
            Maxid currentMax = em.find(Maxid.class, maxid);
            int result = 1;
            if(currentMax != null) {
                result = currentMax.getMaxval();
                currentMax.setMaxval(result+1);
                em.merge(currentMax);
            } else {
                currentMax = new Maxid();
                currentMax.setId(maxid);
                currentMax.setMaxval(result+1);
                em.persist(currentMax);
            }
            return result;
    }

}
