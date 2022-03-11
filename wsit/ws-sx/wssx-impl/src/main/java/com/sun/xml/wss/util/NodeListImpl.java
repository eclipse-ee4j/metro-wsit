/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.wss.util;

import org.w3c.dom.Node;
import java.util.List;
import java.util.ArrayList;
import org.w3c.dom.NodeList;

/**
 *
 * @author ashutosh.shahi@sun.com
 */
public class NodeListImpl implements NodeList{
    
    private List<Node> nodes;
    
    /**
     * Creates a new instance of NodeListImpl
     */
    public NodeListImpl() {
        nodes = new ArrayList<>();
    }
    
    /**
     * get the size of the nodeList
     */
    @Override
    public int getLength(){
        return nodes.size();
    }
    
    /**
     * get the ith item from NodeList
     */
    @Override
    public Node item(int i){
        return nodes.get(i);
    }
    
    /**
     * add node to the end of NodeList
     */
    public void add(Node node){
        nodes.add(node);
    }
    
    public void merge(NodeList nodeList){
        for(int i = 0; i < nodeList.getLength(); i++){
            nodes.add(nodeList.item(i));
        }
    }
    
}
