/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.xml.wss.impl.misc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.xml.security.exceptions.Base64DecodingException;


/**
 * Implementation of MIME's Base64 encoding and decoding conversions.
 * Optimized code. (raw version taken from oreilly.jonathan.util,
 * and currently com.sun.org.apache.xerces.internal.ds.util.Base64)
 *
 * @author Raul Benito(Of the xerces copy, and little adaptations).
 * @author Anli Shundi
 * @author Christian Geuer-Pollmann
 * @see <A HREF="ftp://ftp.isi.edu/in-notes/rfc2045.txt">RFC 2045</A>
 * @see org.apache.xml.security.transforms.implementations.TransformBase64Decode
 */
public class Base64 {
    
    
    /** Field BASE64DEFAULTLENGTH */
    public static final int BASE64DEFAULTLENGTH = 76;
    
    /** Field _base64length */
    //static int _base64length = Base64.BASE64DEFAULTLENGTH;
    
    private Base64() {
        // we don't allow instantiation
    }
    
    
    
    
    static private final int  BASELENGTH         = 255;
    static private final int  LOOKUPLENGTH       = 64;
    static private final int  TWENTYFOURBITGROUP = 24;
    static private final int  EIGHTBIT           = 8;
    static private final int  SIXTEENBIT         = 16;
    static private final int  FOURBYTE           = 4;
    static private final int  SIGN               = -128;
    static private final char PAD                = '=';
    static private final boolean fDebug          = false;
    static final private byte [] base64Alphabet        = new byte[BASELENGTH];
    static final private char [] lookUpBase64Alphabet  = new char[LOOKUPLENGTH];
    
    static {
        
        for (int i = 0; i<BASELENGTH; i++) {
            base64Alphabet[i] = -1;
        }
        for (int i = 'Z'; i >= 'A'; i--) {
            base64Alphabet[i] = (byte) (i-'A');
        }
        for (int i = 'z'; i>= 'a'; i--) {
            base64Alphabet[i] = (byte) ( i-'a' + 26);
        }
        
        for (int i = '9'; i >= '0'; i--) {
            base64Alphabet[i] = (byte) (i-'0' + 52);
        }
        
        base64Alphabet['+']  = 62;
        base64Alphabet['/']  = 63;
        
        for (int i = 0; i<=25; i++)
            lookUpBase64Alphabet[i] = (char)('A'+i);
        
        for (int i = 26,  j = 0; i<=51; i++, j++)
            lookUpBase64Alphabet[i] = (char)('a'+ j);
        
        for (int i = 52,  j = 0; i<=61; i++, j++)
            lookUpBase64Alphabet[i] = (char)('0' + j);
        lookUpBase64Alphabet[62] = '+';
        lookUpBase64Alphabet[63] = '/';
        
    }
    
    protected static final boolean isWhiteSpace(byte octect) {
        return (octect == 0x20 || octect == 0xd || octect == 0xa || octect == 0x9);
    }
    
    protected static final boolean isPad(byte octect) {
        return (octect == PAD);
    }
    
    
    /**
     * Encodes hex octects into Base64
     *
     * @param binaryData Array containing binaryData
     * @return Encoded Base64 array
     */
    
    public char [] encodeData(byte[] binaryData){
        return encodeData(binaryData,2046);
                  //BASE64DEFAULTLENGTH);
    }
    /**
     * Encode a byte array in Base64 format and return an optionally
     * wrapped line.
     *
     * @param binaryData <code>byte[]</code> data to be encoded
     * @param length <code>int<code> length of wrapped lines; No wrapping if less than 4.
     * @return a <code>String</code> with encoded data
     */
    public static char[] encodeData(byte[] binaryData,int length) {
        
        if (length<4) {
            length=Integer.MAX_VALUE;
        }
        
        if (binaryData == null)
            return null;
        
        int      lengthDataBits    = binaryData.length*EIGHTBIT;
        if (lengthDataBits == 0) {
            return "".toCharArray();
            
        }
        
        int      fewerThan24bits   = lengthDataBits%TWENTYFOURBITGROUP;
        int      numberTriplets    = lengthDataBits/TWENTYFOURBITGROUP;
        int      numberQuartet     = fewerThan24bits != 0 ? numberTriplets+1 : numberTriplets;
        int      quartesPerLine    = length/4;
        int      numberLines       = (numberQuartet-1)/quartesPerLine;
        char     encodedData[]     = null;
        
        encodedData = new char[numberQuartet*4+numberLines*2];
        
        byte k=0, l=0, b1=0,b2=0,b3=0;
        
        int encodedIndex = 0;
        int dataIndex   = 0;
        int i           = 0;
        if (fDebug) {
            System.out.println("number of triplets = " + numberTriplets );
        }
        
        for (int line = 0; line < numberLines; line++) {
            for (int quartet = 0; quartet < 19; quartet++) {
                b1 = binaryData[dataIndex++];
                b2 = binaryData[dataIndex++];
                b3 = binaryData[dataIndex++];
                
                if (fDebug) {
                    System.out.println( "b1= " + b1 +", b2= " + b2 + ", b3= " + b3 );
                }
                
                l  = (byte)(b2 & 0x0f);
                k  = (byte)(b1 & 0x03);
                
                byte val1 = ((b1 & SIGN)==0)?(byte)(b1>>2):(byte)((b1)>>2^0xc0);
                
                byte val2 = ((b2 & SIGN)==0)?(byte)(b2>>4):(byte)((b2)>>4^0xf0);
                byte val3 = ((b3 & SIGN)==0)?(byte)(b3>>6):(byte)((b3)>>6^0xfc);
                
                if (fDebug) {
                    System.out.println( "val2 = " + val2 );
                    System.out.println( "k4   = " + (k<<4));
                    System.out.println( "vak  = " + (val2 | (k<<4)));
                }
                
                encodedData[encodedIndex++] = lookUpBase64Alphabet[ val1 ];
                encodedData[encodedIndex++] = lookUpBase64Alphabet[ val2 | ( k<<4 )];
                encodedData[encodedIndex++] = lookUpBase64Alphabet[ (l <<2 ) | val3 ];
                encodedData[encodedIndex++] = lookUpBase64Alphabet[ b3 & 0x3f ];
                
                i++;
            }
            encodedData[encodedIndex++] = 0xd;
            encodedData[encodedIndex++] = 0xa;
        }
        
        for (; i<numberTriplets; i++) {
            b1 = binaryData[dataIndex++];
            b2 = binaryData[dataIndex++];
            b3 = binaryData[dataIndex++];
            
            if (fDebug) {
                System.out.println( "b1= " + b1 +", b2= " + b2 + ", b3= " + b3 );
            }
            
            l  = (byte)(b2 & 0x0f);
            k  = (byte)(b1 & 0x03);
            
            byte val1 = ((b1 & SIGN)==0)?(byte)(b1>>2):(byte)((b1)>>2^0xc0);
            
            byte val2 = ((b2 & SIGN)==0)?(byte)(b2>>4):(byte)((b2)>>4^0xf0);
            byte val3 = ((b3 & SIGN)==0)?(byte)(b3>>6):(byte)((b3)>>6^0xfc);
            
            if (fDebug) {
                System.out.println( "val2 = " + val2 );
                System.out.println( "k4   = " + (k<<4));
                System.out.println( "vak  = " + (val2 | (k<<4)));
            }
            
            encodedData[encodedIndex++] = lookUpBase64Alphabet[ val1 ];
            encodedData[encodedIndex++] = lookUpBase64Alphabet[ val2 | ( k<<4 )];
            encodedData[encodedIndex++] = lookUpBase64Alphabet[ (l <<2 ) | val3 ];
            encodedData[encodedIndex++] = lookUpBase64Alphabet[ b3 & 0x3f ];
        }
        
        // form integral number of 6-bit groups
        if (fewerThan24bits == EIGHTBIT) {
            b1 = binaryData[dataIndex];
            k = (byte) ( b1 &0x03 );
            if (fDebug) {
                System.out.println("b1=" + b1);
                System.out.println("b1<<2 = " + (b1>>2) );
            }
            byte val1 = ((b1 & SIGN)==0)?(byte)(b1>>2):(byte)((b1)>>2^0xc0);
            encodedData[encodedIndex++] = lookUpBase64Alphabet[ val1 ];
            encodedData[encodedIndex++] = lookUpBase64Alphabet[ k<<4 ];
            encodedData[encodedIndex++] = PAD;
            encodedData[encodedIndex++] = PAD;
        } else if (fewerThan24bits == SIXTEENBIT) {
            b1 = binaryData[dataIndex];
            b2 = binaryData[dataIndex +1 ];
            l = ( byte ) ( b2 &0x0f );
            k = ( byte ) ( b1 &0x03 );
            
            byte val1 = ((b1 & SIGN)==0)?(byte)(b1>>2):(byte)((b1)>>2^0xc0);
            byte val2 = ((b2 & SIGN)==0)?(byte)(b2>>4):(byte)((b2)>>4^0xf0);
            
            encodedData[encodedIndex++] = lookUpBase64Alphabet[ val1 ];
            encodedData[encodedIndex++] = lookUpBase64Alphabet[ val2 | ( k<<4 )];
            encodedData[encodedIndex++] = lookUpBase64Alphabet[ l<<2 ];
            encodedData[encodedIndex++] = PAD;
        }

        //encodedData[encodedIndex] = 0xd;
        //encodedData[encodedIndex] = 0xa;
        
        return encodedData;
    }
    
    
    public static String encode(byte[] binaryData,int length) {
        
        if (length<4) {
            length=Integer.MAX_VALUE;
        }
        
        if (binaryData == null)
            return null;
        
        int      lengthDataBits    = binaryData.length*EIGHTBIT;
        if (lengthDataBits == 0) {
            return "";
        }
        
        int      fewerThan24bits   = lengthDataBits%TWENTYFOURBITGROUP;
        int      numberTriplets    = lengthDataBits/TWENTYFOURBITGROUP;
        int      numberQuartet     = fewerThan24bits != 0 ? numberTriplets+1 : numberTriplets;
        int      quartesPerLine    = length/4;
        int      numberLines       = (numberQuartet-1)/quartesPerLine;
        char     encodedData[]     = null;
        
        encodedData = new char[numberQuartet*4+numberLines*2];
        
        byte k=0, l=0, b1=0,b2=0,b3=0;
        
        int encodedIndex = 0;
        int dataIndex   = 0;
        int i           = 0;
        if (fDebug) {
            System.out.println("number of triplets = " + numberTriplets );
        }
        
        for (int line = 0; line < numberLines; line++) {
            for (int quartet = 0; quartet < 19; quartet++) {
                b1 = binaryData[dataIndex++];
                b2 = binaryData[dataIndex++];
                b3 = binaryData[dataIndex++];
                
                if (fDebug) {
                    System.out.println( "b1= " + b1 +", b2= " + b2 + ", b3= " + b3 );
                }
                
                l  = (byte)(b2 & 0x0f);
                k  = (byte)(b1 & 0x03);
                
                byte val1 = ((b1 & SIGN)==0)?(byte)(b1>>2):(byte)((b1)>>2^0xc0);
                
                byte val2 = ((b2 & SIGN)==0)?(byte)(b2>>4):(byte)((b2)>>4^0xf0);
                byte val3 = ((b3 & SIGN)==0)?(byte)(b3>>6):(byte)((b3)>>6^0xfc);
                
                if (fDebug) {
                    System.out.println( "val2 = " + val2 );
                    System.out.println( "k4   = " + (k<<4));
                    System.out.println( "vak  = " + (val2 | (k<<4)));
                }
                
                encodedData[encodedIndex++] = lookUpBase64Alphabet[ val1 ];
                encodedData[encodedIndex++] = lookUpBase64Alphabet[ val2 | ( k<<4 )];
                encodedData[encodedIndex++] = lookUpBase64Alphabet[ (l <<2 ) | val3 ];
                encodedData[encodedIndex++] = lookUpBase64Alphabet[ b3 & 0x3f ];
                
                i++;
            }
            encodedData[encodedIndex++] = 0xd;
            encodedData[encodedIndex++] = 0xa;
        }
        
        for (; i<numberTriplets; i++) {
            b1 = binaryData[dataIndex++];
            b2 = binaryData[dataIndex++];
            b3 = binaryData[dataIndex++];
            
            if (fDebug) {
                System.out.println( "b1= " + b1 +", b2= " + b2 + ", b3= " + b3 );
            }
            
            l  = (byte)(b2 & 0x0f);
            k  = (byte)(b1 & 0x03);
            
            byte val1 = ((b1 & SIGN)==0)?(byte)(b1>>2):(byte)((b1)>>2^0xc0);
            
            byte val2 = ((b2 & SIGN)==0)?(byte)(b2>>4):(byte)((b2)>>4^0xf0);
            byte val3 = ((b3 & SIGN)==0)?(byte)(b3>>6):(byte)((b3)>>6^0xfc);
            
            if (fDebug) {
                System.out.println( "val2 = " + val2 );
                System.out.println( "k4   = " + (k<<4));
                System.out.println( "vak  = " + (val2 | (k<<4)));
            }
            
            encodedData[encodedIndex++] = lookUpBase64Alphabet[ val1 ];
            encodedData[encodedIndex++] = lookUpBase64Alphabet[ val2 | ( k<<4 )];
            encodedData[encodedIndex++] = lookUpBase64Alphabet[ (l <<2 ) | val3 ];
            encodedData[encodedIndex++] = lookUpBase64Alphabet[ b3 & 0x3f ];
        }
        
        // form integral number of 6-bit groups
        if (fewerThan24bits == EIGHTBIT) {
            b1 = binaryData[dataIndex];
            k = (byte) ( b1 &0x03 );
            if (fDebug) {
                System.out.println("b1=" + b1);
                System.out.println("b1<<2 = " + (b1>>2) );
            }
            byte val1 = ((b1 & SIGN)==0)?(byte)(b1>>2):(byte)((b1)>>2^0xc0);
            encodedData[encodedIndex++] = lookUpBase64Alphabet[ val1 ];
            encodedData[encodedIndex++] = lookUpBase64Alphabet[ k<<4 ];
            encodedData[encodedIndex++] = PAD;
            encodedData[encodedIndex++] = PAD;
        } else if (fewerThan24bits == SIXTEENBIT) {
            b1 = binaryData[dataIndex];
            b2 = binaryData[dataIndex +1 ];
            l = ( byte ) ( b2 &0x0f );
            k = ( byte ) ( b1 &0x03 );
            
            byte val1 = ((b1 & SIGN)==0)?(byte)(b1>>2):(byte)((b1)>>2^0xc0);
            byte val2 = ((b2 & SIGN)==0)?(byte)(b2>>4):(byte)((b2)>>4^0xf0);
            
            encodedData[encodedIndex++] = lookUpBase64Alphabet[ val1 ];
            encodedData[encodedIndex++] = lookUpBase64Alphabet[ val2 | ( k<<4 )];
            encodedData[encodedIndex++] = lookUpBase64Alphabet[ l<<2 ];
            encodedData[encodedIndex++] = PAD;
        }
        
        //encodedData[encodedIndex] = 0xd;
        //encodedData[encodedIndex] = 0xa;
        
        return new String(encodedData);
    }
    
    public static String encode(byte[] binaryData) {
        //Use 2K as the default length
        return encode(binaryData, 2048);
    }
    
    public static void encodeToStream(byte[] binaryData,int length,OutputStream stream) throws IOException {
        encodeToStream(binaryData,0,length,stream);
    }
    
    public static void encodeToStream(byte[] binaryData,int offset,int length,OutputStream stream) throws IOException {
        
        if (length<4) {
            length=Integer.MAX_VALUE;
        }
        
        if (binaryData == null)
            return ;
        
        int      lengthDataBits    = length*EIGHTBIT;
        if (lengthDataBits == 0) {
            return ;
        }
        
        int      fewerThan24bits   = lengthDataBits%TWENTYFOURBITGROUP;
        int      numberTriplets    = lengthDataBits/TWENTYFOURBITGROUP;
        int      numberQuartet     = fewerThan24bits != 0 ? numberTriplets+1 : numberTriplets;
        int      quartesPerLine    = length/4;
        int      numberLines       = (numberQuartet-1)/quartesPerLine;
        // char     encodedData[]     = null;
        
        //   encodedData = new char[numberQuartet*4+numberLines*2];
        
        byte k=0, l=0, b1=0,b2=0,b3=0;
        
        int encodedIndex = 0;
        int dataIndex   = offset;
        int i           = 0;
        if (fDebug) {
            System.out.println("number of triplets = " + numberTriplets );
        }
        
        for (int line = 0; line < numberLines; line++) {
            for (int quartet = 0; quartet < 19; quartet++) {
                b1 = binaryData[dataIndex++];
                b2 = binaryData[dataIndex++];
                b3 = binaryData[dataIndex++];
                
                if (fDebug) {
                    System.out.println( "b1= " + b1 +", b2= " + b2 + ", b3= " + b3 );
                }
                
                l  = (byte)(b2 & 0x0f);
                k  = (byte)(b1 & 0x03);
                
                byte val1 = ((b1 & SIGN)==0)?(byte)(b1>>2):(byte)((b1)>>2^0xc0);
                
                byte val2 = ((b2 & SIGN)==0)?(byte)(b2>>4):(byte)((b2)>>4^0xf0);
                byte val3 = ((b3 & SIGN)==0)?(byte)(b3>>6):(byte)((b3)>>6^0xfc);
                
                if (fDebug) {
                    System.out.println( "val2 = " + val2 );
                    System.out.println( "k4   = " + (k<<4));
                    System.out.println( "vak  = " + (val2 | (k<<4)));
                }
                
          /*      encodedData[encodedIndex++] = lookUpBase64Alphabet[ val1 ];
                encodedData[encodedIndex++] = lookUpBase64Alphabet[ val2 | ( k<<4 )];
                encodedData[encodedIndex++] = lookUpBase64Alphabet[ (l <<2 ) | val3 ];
                encodedData[encodedIndex++] = lookUpBase64Alphabet[ b3 & 0x3f ];*/
                stream.write(lookUpBase64Alphabet[ val1 ]);
                stream.write(lookUpBase64Alphabet[ val2 | ( k<<4 )]);
                stream.write(lookUpBase64Alphabet[ (l <<2 ) | val3 ]);
                stream.write(lookUpBase64Alphabet[ b3 & 0x3f ]);
                i++;
            }
            //encodedData[encodedIndex++] = 0xa;
            stream.write(0xd);
            stream.write(0xa);
        }
        
        for (; i<numberTriplets; i++) {
            b1 = binaryData[dataIndex++];
            b2 = binaryData[dataIndex++];
            b3 = binaryData[dataIndex++];
            
            if (fDebug) {
                System.out.println( "b1= " + b1 +", b2= " + b2 + ", b3= " + b3 );
            }
            
            l  = (byte)(b2 & 0x0f);
            k  = (byte)(b1 & 0x03);
            
            byte val1 = ((b1 & SIGN)==0)?(byte)(b1>>2):(byte)((b1)>>2^0xc0);
            
            byte val2 = ((b2 & SIGN)==0)?(byte)(b2>>4):(byte)((b2)>>4^0xf0);
            byte val3 = ((b3 & SIGN)==0)?(byte)(b3>>6):(byte)((b3)>>6^0xfc);
            
            if (fDebug) {
                System.out.println( "val2 = " + val2 );
                System.out.println( "k4   = " + (k<<4));
                System.out.println( "vak  = " + (val2 | (k<<4)));
            }
            stream.write(lookUpBase64Alphabet[ val1 ]);
            stream.write(lookUpBase64Alphabet[ val2 | ( k<<4 )]);
            stream.write(lookUpBase64Alphabet[ (l <<2 ) | val3 ]);
            stream.write(lookUpBase64Alphabet[ b3 & 0x3f ]);
           /* encodedData[encodedIndex++] = lookUpBase64Alphabet[ val1 ];
            encodedData[encodedIndex++] = lookUpBase64Alphabet[ val2 | ( k<<4 )];
            encodedData[encodedIndex++] = lookUpBase64Alphabet[ (l <<2 ) | val3 ];
            encodedData[encodedIndex++] = lookUpBase64Alphabet[ b3 & 0x3f ];*/
        }
        
        // form integral number of 6-bit groups
        if (fewerThan24bits == EIGHTBIT) {
            b1 = binaryData[dataIndex];
            k = (byte) ( b1 &0x03 );
            if (fDebug) {
                System.out.println("b1=" + b1);
                System.out.println("b1<<2 = " + (b1>>2) );
            }
            byte val1 = ((b1 & SIGN)==0)?(byte)(b1>>2):(byte)((b1)>>2^0xc0);
       /*     encodedData[encodedIndex++] = lookUpBase64Alphabet[ val1 ];
            encodedData[encodedIndex++] = lookUpBase64Alphabet[ k<<4 ];
            encodedData[encodedIndex++] = PAD;
            encodedData[encodedIndex++] = PAD;*/
            stream.write(lookUpBase64Alphabet[ val1 ]);
            stream.write(lookUpBase64Alphabet[ k<<4 ]);
            stream.write(PAD);
            stream.write(PAD);
        } else if (fewerThan24bits == SIXTEENBIT) {
            b1 = binaryData[dataIndex];
            b2 = binaryData[dataIndex +1 ];
            l = ( byte ) ( b2 &0x0f );
            k = ( byte ) ( b1 &0x03 );
            
            byte val1 = ((b1 & SIGN)==0)?(byte)(b1>>2):(byte)((b1)>>2^0xc0);
            byte val2 = ((b2 & SIGN)==0)?(byte)(b2>>4):(byte)((b2)>>4^0xf0);
            
          /*  encodedData[encodedIndex++] = lookUpBase64Alphabet[ val1 ];
            encodedData[encodedIndex++] = lookUpBase64Alphabet[ val2 | ( k<<4 )];
            encodedData[encodedIndex++] = lookUpBase64Alphabet[ l<<2 ];
            encodedData[encodedIndex++] = PAD;
           */
            stream.write(lookUpBase64Alphabet[ val1 ]);
            stream.write(lookUpBase64Alphabet[ val2 | ( k<<4 )]);
            stream.write(lookUpBase64Alphabet[ l<<2 ]);
            stream.write(PAD);
        }
        
        //encodedData[encodedIndex] = 0xd;
        //encodedData[encodedIndex] = 0xa;
        
        //return new String(encodedData);
    }
    
    public static void encodeToStream(ByteArray binaryData,int length,OutputStream stream) throws IOException {
        
        if (length<4) {
            length=Integer.MAX_VALUE;
        }
        
        if (binaryData == null)
            return ;
        
        int      lengthDataBits    = binaryData.length*EIGHTBIT;
        if (lengthDataBits == 0) {
            return ;
        }
        
        int      fewerThan24bits   = lengthDataBits%TWENTYFOURBITGROUP;
        int      numberTriplets    = lengthDataBits/TWENTYFOURBITGROUP;
        int      numberQuartet     = fewerThan24bits != 0 ? numberTriplets+1 : numberTriplets;
        int      quartesPerLine    = length/4;
        int      numberLines       = (numberQuartet-1)/quartesPerLine;
        // char     encodedData[]     = null;
        
        //   encodedData = new char[numberQuartet*4+numberLines*2];
        
        byte k=0, l=0, b1=0,b2=0,b3=0;
        
        int encodedIndex = 0;
        int dataIndex   = 0;
        int i           = 0;
        if (fDebug) {
            System.out.println("number of triplets = " + numberTriplets );
        }
        
        for (int line = 0; line < numberLines; line++) {
            for (int quartet = 0; quartet < 19; quartet++) {
                b1 = binaryData.byteAt(dataIndex++);
                b2 = binaryData.byteAt(dataIndex++);
                b3 = binaryData.byteAt(dataIndex++);
                
                if (fDebug) {
                    System.out.println( "b1= " + b1 +", b2= " + b2 + ", b3= " + b3 );
                }
                
                l  = (byte)(b2 & 0x0f);
                k  = (byte)(b1 & 0x03);
                
                byte val1 = ((b1 & SIGN)==0)?(byte)(b1>>2):(byte)((b1)>>2^0xc0);
                
                byte val2 = ((b2 & SIGN)==0)?(byte)(b2>>4):(byte)((b2)>>4^0xf0);
                byte val3 = ((b3 & SIGN)==0)?(byte)(b3>>6):(byte)((b3)>>6^0xfc);
                
                if (fDebug) {
                    System.out.println( "val2 = " + val2 );
                    System.out.println( "k4   = " + (k<<4));
                    System.out.println( "vak  = " + (val2 | (k<<4)));
                }
                
          /*      encodedData[encodedIndex++] = lookUpBase64Alphabet[ val1 ];
                encodedData[encodedIndex++] = lookUpBase64Alphabet[ val2 | ( k<<4 )];
                encodedData[encodedIndex++] = lookUpBase64Alphabet[ (l <<2 ) | val3 ];
                encodedData[encodedIndex++] = lookUpBase64Alphabet[ b3 & 0x3f ];*/
                stream.write(lookUpBase64Alphabet[ val1 ]);
                stream.write(lookUpBase64Alphabet[ val2 | ( k<<4 )]);
                stream.write(lookUpBase64Alphabet[ (l <<2 ) | val3 ]);
                stream.write(lookUpBase64Alphabet[ b3 & 0x3f ]);
                i++;
            }
            //encodedData[encodedIndex++] = 0xa;
            stream.write(0xd);
            stream.write(0xa);
        }
        
        for (; i<numberTriplets; i++) {
            b1 = binaryData.byteAt(dataIndex++);
            b2 = binaryData.byteAt(dataIndex++);
            b3 = binaryData.byteAt(dataIndex++);
            
            if (fDebug) {
                System.out.println( "b1= " + b1 +", b2= " + b2 + ", b3= " + b3 );
            }
            
            l  = (byte)(b2 & 0x0f);
            k  = (byte)(b1 & 0x03);
            
            byte val1 = ((b1 & SIGN)==0)?(byte)(b1>>2):(byte)((b1)>>2^0xc0);
            
            byte val2 = ((b2 & SIGN)==0)?(byte)(b2>>4):(byte)((b2)>>4^0xf0);
            byte val3 = ((b3 & SIGN)==0)?(byte)(b3>>6):(byte)((b3)>>6^0xfc);
            
            if (fDebug) {
                System.out.println( "val2 = " + val2 );
                System.out.println( "k4   = " + (k<<4));
                System.out.println( "vak  = " + (val2 | (k<<4)));
            }
            stream.write(lookUpBase64Alphabet[ val1 ]);
            stream.write(lookUpBase64Alphabet[ val2 | ( k<<4 )]);
            stream.write( lookUpBase64Alphabet[ (l <<2 ) | val3 ]);
            stream.write( lookUpBase64Alphabet[ b3 & 0x3f ]);
           /* encodedData[encodedIndex++] = lookUpBase64Alphabet[ val1 ];
            encodedData[encodedIndex++] = lookUpBase64Alphabet[ val2 | ( k<<4 )];
            encodedData[encodedIndex++] = lookUpBase64Alphabet[ (l <<2 ) | val3 ];
            encodedData[encodedIndex++] = lookUpBase64Alphabet[ b3 & 0x3f ];*/
        }
        
        // form integral number of 6-bit groups
        if (fewerThan24bits == EIGHTBIT) {
            b1 = binaryData.byteAt(dataIndex);
            k = (byte) ( b1 &0x03 );
            if (fDebug) {
                System.out.println("b1=" + b1);
                System.out.println("b1<<2 = " + (b1>>2) );
            }
            byte val1 = ((b1 & SIGN)==0)?(byte)(b1>>2):(byte)((b1)>>2^0xc0);
       /*     encodedData[encodedIndex++] = lookUpBase64Alphabet[ val1 ];
            encodedData[encodedIndex++] = lookUpBase64Alphabet[ k<<4 ];
            encodedData[encodedIndex++] = PAD;
            encodedData[encodedIndex++] = PAD;*/
            stream.write(lookUpBase64Alphabet[ val1 ]);
            stream.write(lookUpBase64Alphabet[ k<<4 ]);
            stream.write(PAD);
            stream.write(PAD);
        } else if (fewerThan24bits == SIXTEENBIT) {
            b1 = binaryData.byteAt(dataIndex);
            b2 = binaryData.byteAt(dataIndex +1 );
            l = ( byte ) ( b2 &0x0f );
            k = ( byte ) ( b1 &0x03 );
            
            byte val1 = ((b1 & SIGN)==0)?(byte)(b1>>2):(byte)((b1)>>2^0xc0);
            byte val2 = ((b2 & SIGN)==0)?(byte)(b2>>4):(byte)((b2)>>4^0xf0);
            
          /*  encodedData[encodedIndex++] = lookUpBase64Alphabet[ val1 ];
            encodedData[encodedIndex++] = lookUpBase64Alphabet[ val2 | ( k<<4 )];
            encodedData[encodedIndex++] = lookUpBase64Alphabet[ l<<2 ];
            encodedData[encodedIndex++] = PAD;
           */
            stream.write(lookUpBase64Alphabet[ val1 ]);
            stream.write(lookUpBase64Alphabet[ val2 | ( k<<4 )]);
            stream.write(lookUpBase64Alphabet[ l<<2 ]);
            stream.write(PAD);
        }
        
        //encodedData[encodedIndex] = 0xa;
        
        //return new String(encodedData);
    }
    
    /**
     * Decodes Base64 data into octects
     *
     * @param encoded Byte array containing Base64 data
     * @return Array containind decoded data.
     * @throws Base64DecodingException
     */
    public final static byte[] decode(String encoded) throws Base64DecodingException {
        
        if (encoded == null)
            return null;
        
        return decodeInternal(encoded.getBytes());
    }
    
    public final static byte[] decode(byte[] base64Data) throws Base64DecodingException {
        return  decodeInternal(base64Data);
    }
    
    protected final static byte[] decodeInternal(byte[] base64Data) throws Base64DecodingException {
        // remove white spaces
        int len = removeWhiteSpace(base64Data);
//        int len = base64Data.length;
        if (len%FOURBYTE != 0) {
            throw new Base64DecodingException("decoding.divisible.four");
            //should be divisible by four
        }
        
        int      numberQuadruple    = (len/FOURBYTE );
        
        if (numberQuadruple == 0)
            return new byte[0];
        
        byte     decodedData[]      = null;
        byte     b1=0,b2=0,b3=0, b4=0;
        
        
        int i = 0;
        int encodedIndex = 0;
        int dataIndex    = 0;
        
        //decodedData      = new byte[ (numberQuadruple)*3];
        dataIndex=(numberQuadruple-1)*4;
        encodedIndex=(numberQuadruple-1)*3;
        //first last bits.
        b1 = base64Alphabet[base64Data[dataIndex++]];
        b2 = base64Alphabet[base64Data[dataIndex++]];
        if ((b1==-1) || (b2==-1)) {
            throw new Base64DecodingException("decoding.general");//if found "no data" just return null
        }
        
        
        byte d3,d4;
        b3 = base64Alphabet[d3=base64Data[dataIndex++]];
        b4 = base64Alphabet[d4=base64Data[dataIndex++]];
        if ((b3==-1 ) || (b4==-1) ) {
            //Check if they are PAD characters
            if (isPad( d3 ) && isPad( d4)) {               //Two PAD e.g. 3c[Pad][Pad]
                if ((b2 & 0xf) != 0)//last 4 bits should be zero
                    throw new Base64DecodingException("decoding.general");
                decodedData = new byte[ encodedIndex + 1 ];
                decodedData[encodedIndex]   = (byte)(  b1 <<2 | b2>>4 ) ;
            } else if (!isPad( d3) && isPad(d4)) {               //One PAD  e.g. 3cQ[Pad]
                if ((b3 & 0x3 ) != 0)//last 2 bits should be zero
                    throw new Base64DecodingException("decoding.general");
                decodedData = new byte[ encodedIndex + 2 ];
                decodedData[encodedIndex++] = (byte)(  b1 <<2 | b2>>4 );
                decodedData[encodedIndex]   = (byte)(((b2 & 0xf)<<4 ) |( (b3>>2) & 0xf) );
            } else {
                throw new Base64DecodingException("decoding.general");//an error  like "3c[Pad]r", "3cdX", "3cXd", "3cXX" where X is non data
            }
        } else {
            //No PAD e.g 3cQl
            decodedData = new byte[encodedIndex+3];
            decodedData[encodedIndex++] = (byte)(  b1 <<2 | b2>>4 ) ;
            decodedData[encodedIndex++] = (byte)(((b2 & 0xf)<<4 ) |( (b3>>2) & 0xf) );
            decodedData[encodedIndex++] = (byte)( b3<<6 | b4 );
        }
        encodedIndex=0;
        dataIndex=0;
        //the begin
        for (i=numberQuadruple-1; i>0; i--) {
            b1 = base64Alphabet[base64Data[dataIndex++]];
            b2 = base64Alphabet[base64Data[dataIndex++]];
            b3 = base64Alphabet[base64Data[dataIndex++]];
            b4 = base64Alphabet[base64Data[dataIndex++]];
            
            if ( (b1==-1) ||
                    (b2==-1) ||
                    (b3==-1) ||
                    (b4==-1) ) {
                throw new Base64DecodingException("decoding.general");//if found "no data" just return null
            }
            
            decodedData[encodedIndex++] = (byte)(  b1 <<2 | b2>>4 ) ;
            decodedData[encodedIndex++] = (byte)(((b2 & 0xf)<<4 ) |( (b3>>2) & 0xf) );
            decodedData[encodedIndex++] = (byte)( b3<<6 | b4 );
        }
        return decodedData;
    }
    
    /**
     * Decodes Base64 data into  outputstream
     *
     * @param base64Data Byte array containing Base64 data
     * @param os the outputstream
     * @throws IOException
     * @throws Base64DecodingException
     */
    public final static void decode(byte[] base64Data,
            OutputStream os) throws Base64DecodingException, IOException {
        // remove white spaces
        int len = removeWhiteSpace(base64Data);
//        int len = base64Data.length;
        
        if (len%FOURBYTE != 0) {
            throw new Base64DecodingException("decoding.divisible.four");
            //should be divisible by four
        }
        
        int      numberQuadruple    = (len/FOURBYTE );
        
        if (numberQuadruple == 0)
            return;
        
        //byte     decodedData[]      = null;
        byte     b1=0,b2=0,b3=0, b4=0;
        
        int i = 0;
        
        int dataIndex    = 0;
        
        //the begin
        for (i=numberQuadruple-1; i>0; i--) {
            b1 = base64Alphabet[base64Data[dataIndex++]];
            b2 = base64Alphabet[base64Data[dataIndex++]];
            b3 = base64Alphabet[base64Data[dataIndex++]];
            b4 = base64Alphabet[base64Data[dataIndex++]];
            if ( (b1==-1) ||
                    (b2==-1) ||
                    (b3==-1) ||
                    (b4==-1) )
                throw new Base64DecodingException("decoding.general");//if found "no data" just return null
            
            
            
            os.write((byte)(  b1 <<2 | b2>>4 ) );
            os.write((byte)(((b2 & 0xf)<<4 ) |( (b3>>2) & 0xf) ));
            os.write( (byte)( b3<<6 | b4 ));
        }
        b1 = base64Alphabet[base64Data[dataIndex++]];
        b2 = base64Alphabet[base64Data[dataIndex++]];
        
        //  first last bits.
        if ((b1==-1) ||
                (b2==-1) ){
            throw new Base64DecodingException("decoding.general");//if found "no data" just return null
        }
        
        byte d3,d4;
        b3= base64Alphabet[d3 = base64Data[dataIndex++]];
        b4= base64Alphabet[d4 = base64Data[dataIndex++]];
        if ((b3==-1 ) ||
                (b4==-1) ) {//Check if they are PAD characters
            if (isPad( d3 ) && isPad( d4)) {               //Two PAD e.g. 3c[Pad][Pad]
                if ((b2 & 0xf) != 0)//last 4 bits should be zero
                    throw new Base64DecodingException("decoding.general");
                os.write( (byte)(  b1 <<2 | b2>>4 ) );
            } else if (!isPad( d3) && isPad(d4)) {               //One PAD  e.g. 3cQ[Pad]
                if ((b3 & 0x3 ) != 0)//last 2 bits should be zero
                    throw new Base64DecodingException("decoding.general");
                os.write( (byte)(  b1 <<2 | b2>>4 ));
                os.write( (byte)(((b2 & 0xf)<<4 ) |( (b3>>2) & 0xf) ));
            } else {
                throw new Base64DecodingException("decoding.general");//an error  like "3c[Pad]r", "3cdX", "3cXd", "3cXX" where X is non data
            }
        } else {
            //No PAD e.g 3cQl
            os.write((byte)(  b1 <<2 | b2>>4 ) );
            os.write( (byte)(((b2 & 0xf)<<4 ) |( (b3>>2) & 0xf) ));
            os.write((byte)( b3<<6 | b4 ));
        }
        return ;
    }
    
    /**
     * Decodes Base64 data into  outputstream
     *
     * @param is containing Base64 data
     * @param os the outputstream
     * @throws IOException
     * @throws Base64DecodingException
     */
    public final static void decode(InputStream is,
            OutputStream os) throws Base64DecodingException, IOException {
        //byte     decodedData[]      = null;
        byte     b1=0,b2=0,b3=0, b4=0;
        
        int index=0;
        byte []data=new byte[4];
        int read;
        //the begin
        while ((read=is.read())>0) {
            byte readed=(byte)read;
            if (isWhiteSpace(readed)) {
                continue;
            }
            if (isPad(readed)) {
                data[index++]=readed;
                if (index==3)
                    data[index++]=(byte)is.read();
                break;
            }
            
            
            if ((data[index++]=readed)==-1) {
                throw new Base64DecodingException("decoding.general");//if found "no data" just return null
            }
            
            if (index!=4) {
                continue;
            }
            index=0;
            b1 = base64Alphabet[data[0]];
            b2 = base64Alphabet[data[1]];
            b3 = base64Alphabet[data[2]];
            b4 = base64Alphabet[data[3]];
            
            os.write((byte)(  b1 <<2 | b2>>4 ) );
            os.write((byte)(((b2 & 0xf)<<4 ) |( (b3>>2) & 0xf) ));
            os.write( (byte)( b3<<6 | b4 ));
        }
        
        
        byte     d1=data[0],d2=data[1],d3=data[2], d4=data[3];
        b1 = base64Alphabet[d1];
        b2 = base64Alphabet[d2];
        b3 = base64Alphabet[ d3 ];
        b4 = base64Alphabet[ d4 ];
        if ((b3==-1 ) ||
                (b4==-1) ) {//Check if they are PAD characters
            if (isPad( d3 ) && isPad( d4)) {               //Two PAD e.g. 3c[Pad][Pad]
                if ((b2 & 0xf) != 0)//last 4 bits should be zero
                    throw new Base64DecodingException("decoding.general");
                os.write( (byte)(  b1 <<2 | b2>>4 ) );
            } else if (!isPad( d3) && isPad(d4)) {               //One PAD  e.g. 3cQ[Pad]
                b3 = base64Alphabet[ d3 ];
                if ((b3 & 0x3 ) != 0)//last 2 bits should be zero
                    throw new Base64DecodingException("decoding.general");
                os.write( (byte)(  b1 <<2 | b2>>4 ));
                os.write( (byte)(((b2 & 0xf)<<4 ) |( (b3>>2) & 0xf) ));
            } else {
                throw new Base64DecodingException("decoding.general");//an error  like "3c[Pad]r", "3cdX", "3cXd", "3cXX" where X is non data
            }
        } else {
            //No PAD e.g 3cQl
            
            os.write((byte)(  b1 <<2 | b2>>4 ) );
            os.write( (byte)(((b2 & 0xf)<<4 ) |( (b3>>2) & 0xf) ));
            os.write((byte)( b3<<6 | b4 ));
        }
        
        return ;
    }
    /**
     * remove WhiteSpace from MIME containing encoded Base64 data.
     *
     * @param data  the byte array of base64 data (with WS)
     * @return      the new length
     */
    protected static int removeWhiteSpace(byte[] data) {
        if (data == null)
            return 0;
        
        // count characters that's not whitespace
        int newSize = 0;
        int len = data.length;
        for (int i = 0; i < len; i++) {
            byte dataS=data[i];
            if (!isWhiteSpace(dataS))
                data[newSize++] = dataS;
        }
        return newSize;
    }
}
