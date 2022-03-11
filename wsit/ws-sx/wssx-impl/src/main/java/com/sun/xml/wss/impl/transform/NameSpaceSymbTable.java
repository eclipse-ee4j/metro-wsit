/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 * Copyright 1995-2005 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sun.xml.wss.impl.transform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;



import org.w3c.dom.Attr;
import org.w3c.dom.Node;


/**
 * A stack based Symble Table.
 *<br>For speed reasons all the symbols are introduced in the same map,
 * and at the same time in a list so it can be removed when the frame is pop back.
 * @author Raul Benito
 **/
public class NameSpaceSymbTable {

    /**The map betwen prefix-> entry table. */
    HashMap<String,NameSpaceSymbEntry>  symb = new HashMap<>();
    /**The level of nameSpaces (for Inclusive visibility).*/
    int nameSpaces=0;
    /**The stacks for removing the definitions when doing pop.*/
    List level = new ArrayList();
    boolean cloned=true;
    static final String XMLNS="xmlns";
    /**
     * Default constractor
     **/
    public NameSpaceSymbTable() {
        //Insert the default binding for xmlns.
        NameSpaceSymbEntry ne=new NameSpaceSymbEntry("",null,true);
        ne.lastrendered="";
        symb.put(XMLNS,ne);
    }

    /**
     * Get all the unrendered nodes in the name space.
     * For Inclusive rendering
     * @param result the list where to fill the unrendered xmlns definitions.
     **/
       @SuppressWarnings("unchecked")
    public  void getUnrenderedNodes(Collection result) {
       //List result=new ArrayList();
       Iterator it=symb.entrySet().iterator();
       while (it.hasNext()) {
               NameSpaceSymbEntry n=(NameSpaceSymbEntry)((Map.Entry)it.next()).getValue();
               //put them rendered?
               if ((!n.rendered) && (n.n!=null)) {
                   result.add(n.n);
                   n.rendered=true;
               }
       }
    }

    /**
     * Push a frame for visible namespace.
     * For Inclusive rendering.
     **/
    public void outputNodePush() {
        nameSpaces++;
        push();
    }

    /**
     * Pop a frame for visible namespace.
     **/
    public void outputNodePop() {
        nameSpaces--;
        pop();
    }

    /**
     * Push a frame for a node.
     * Inclusive or Exclusive.
     **/
        @SuppressWarnings("unchecked")
    public void push() {
        //Put the number of namespace definitions in the stack.
        /*if (cloned) {
            Object ob[]= {symb,cloned ? symb : null};
            level.add(ob);
        } **/
        level.add(null);
        cloned=false;
    }

    /**
     * Pop a frame.
     * Inclusive or Exclusive.
     **/
        @SuppressWarnings("unchecked")
    public void pop() {
        int size=level.size()-1;
        Object ob= level.remove(size);
        if (ob!=null) {
            symb=(HashMap)ob;
            if (size==0) {
               cloned=false;
            } else
            cloned=(level.get(size-1)!=symb);
        } else {
            cloned=false;
        }


    }
    @SuppressWarnings("unchecked")
    final void needsClone() {
        if (!cloned) {
            level.remove(level.size()-1);
            level.add(symb);
            symb=(HashMap<String,NameSpaceSymbEntry>) symb.clone();
            cloned=true;
        }
    }


    /**
     * Gets the attribute node that defines the binding for the prefix.
     * @param prefix the prefix to obtain the attribute.
     * @return null if there is no need to render the prefix. Otherwise the node of
     * definition.
     **/
    public Attr getMapping(String prefix) {
        NameSpaceSymbEntry entry= symb.get(prefix);
        if (entry==null) {
            //There is no definition for the prefix(a bug?).
            return null;
        }
        if (entry.rendered) {
            //No need to render an entry already rendered.
            return null;
        }
        // Mark this entry as render.
        entry=(NameSpaceSymbEntry) entry.clone();
        needsClone();
        symb.put(prefix,entry);
        entry.rendered=true;
        entry.level=nameSpaces;
        entry.lastrendered=entry.uri;
        // Return the node for outputing.
        return entry.n;
    }

    /**
     * Gets a definition without mark it as render.
     * For render in exclusive c14n the namespaces in the include prefixes.
     * @param prefix The prefix whose definition is neaded.
     * @return the attr to render, null if there is no need to render
     **/
    public Attr getMappingWithoutRendered(String prefix) {
        NameSpaceSymbEntry entry= symb.get(prefix);
        if (entry==null) {
            return null;
        }
        if (entry.rendered) {
            return null;
        }
        return entry.n;
    }

    /**
     * Adds the mapping for a prefix.
     * @param prefix the prefix of definition
     * @param uri the Uri of the definition
     * @param n the attribute that have the definition
     * @return true if there is already defined.
     **/
    public boolean addMapping(String prefix, String uri,Attr n) {
        NameSpaceSymbEntry ob = symb.get(prefix);
        if ((ob!=null) && uri.equals(ob.uri)) {
            //If we have it previously defined. Don't keep working.
            return false;
        }
        //Creates and entry in the table for this new definition.
        NameSpaceSymbEntry ne=new NameSpaceSymbEntry(uri,n,false);
        needsClone();
        symb.put(prefix, ne);
        if (ob != null) {
            //We have a previous definition store it for the pop.
            //Check if a previous definition(not the inmidiatly one) has been rendered.
            ne.lastrendered=ob.lastrendered;
            if ((ob.lastrendered!=null)&& (ob.lastrendered.equals(uri))) {
                //Yes it is. Mark as rendered.
                ne.rendered=true;
            }
        }
        return true;
    }

    /**
     * Adds a definition and mark it as render.
     * For inclusive c14n.
     * @param prefix the prefix of definition
     * @param uri the Uri of the definition
     * @param n the attribute that have the definition
     * @return the attr to render, null if there is no need to render
     **/
    public Node addMappingAndRender(String prefix, String uri,Attr n) {
        NameSpaceSymbEntry ob = symb.get(prefix);

        if ((ob!=null) && uri.equals(ob.uri)) {
            if (!ob.rendered) {
                ob=(NameSpaceSymbEntry) ob.clone();
                needsClone();
                symb.put(prefix,ob);
                ob.lastrendered=uri;
                ob.rendered=true;
                return ob.n;
            }
            return null;
        }

        NameSpaceSymbEntry ne=new NameSpaceSymbEntry(uri,n,true);
        ne.lastrendered=uri;
        needsClone();
        symb.put(prefix, ne);
        if (ob != null) {

            if ((ob.lastrendered!=null)&& (ob.lastrendered.equals(uri))) {
                ne.rendered=true;
                return null;
            }
        }
        return ne.n;
    }
    /**
     * Adds and gets(if needed) the attribute node that defines the binding for the prefix.
     * Take on account if the rules of rendering in the inclusive c14n.
     * For inclusive c14n.
     * @param prefix the prefix to obtain the attribute.
     * @param outputNode the container element is an output element.
     * @param uri the Uri of the definition
     * @param n the attribute that have the definition
     * @return null if there is no need to render the prefix. Otherwise the node of
     * definition.
     **/
    public Node addMappingAndRenderXNodeSet(String prefix, String uri,Attr n,boolean outputNode) {
        NameSpaceSymbEntry ob = symb.get(prefix);
        int visibleNameSpaces=nameSpaces;
        if ((ob!=null) && uri.equals(ob.uri)) {
            if (!ob.rendered) {
                ob=(NameSpaceSymbEntry)ob.clone();
                needsClone();
                symb.put(prefix,ob);
                ob.rendered=true;
                ob.level=visibleNameSpaces;
                return ob.n;
            }
            ob=(NameSpaceSymbEntry)ob.clone();
            needsClone();
            symb.put(prefix,ob);
            if (outputNode && (((visibleNameSpaces-ob.level)<2) || XMLNS.equals(prefix)) ) {
                ob.level=visibleNameSpaces;
                return null; //Already rendered, just return nulll
            }
            ob.level=visibleNameSpaces;
            return ob.n;
        }

        NameSpaceSymbEntry ne=new NameSpaceSymbEntry(uri,n,true);
        ne.level=nameSpaces;
        ne.rendered=true;
        needsClone();
        symb.put(prefix, ne);
        if (ob != null) {
            ne.lastrendered=ob.lastrendered;

            if ((ob.lastrendered!=null)&& (ob.lastrendered.equals(uri))) {
                ne.rendered=true;
            }
        }
        return ne.n;
    }
}

/**
 * The internal structure of NameSpaceSymbTable.
 **/
class NameSpaceSymbEntry implements Cloneable {
    NameSpaceSymbEntry(String name,Attr n,boolean rendered) {
        this.uri=name;
        this.rendered=rendered;
        this.n=n;
    }
    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
    /** The level where the definition was rendered(Only for inclusive) */
    int level=0;
    /**The URI that the prefix defines */
    String uri;
    /**The last output in the URI for this prefix (This for speed reason).*/
    String lastrendered=null;
    /**This prefix-URI has been already render or not.*/
    boolean rendered=false;
    /**The attribute to include.*/
    Attr n;
}
