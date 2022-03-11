/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.ws.transport.tcp.io;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * @author Alexey Stashok
 */
public final class DataInOutUtils {
    public static int readInt4(final InputStream is) throws IOException {
        int value = 0;

        for(int shVal = 0, neeble = 8; (neeble & 8) != 0; shVal += 6) {
            final int octet = is.read();
            if (octet == -1) {
                throw new EOFException();
            }
            neeble = octet >> 4;

            value |= ((neeble & 7) << shVal);
            if ((neeble & 8) != 0) {
                neeble = octet & 0xF;
                value |= ((neeble & 7) << (shVal + 3));
            }
        }

        return value;
    }

    public static void readInts4(final InputStream is, final int[] array,
            final int count) throws IOException {
        readInts4(is, array, count, 0);
    }

    public static int readInts4(final InputStream is, final int[] array,
            final int count, final int lowValue) throws IOException {
        int value = 0;
        int octet = 0;
        int readInts = 0;
        int shVal = 0;
        int neeble = 0;
        int neebleNum = 0;

        if (lowValue > 0) {
            octet = lowValue & 0xF;
            neebleNum = 1;
        }

        for(; readInts < count; neebleNum++) {
            if (neebleNum % 2 == 0) {
                octet = is.read();
                if (octet == -1) {
                    throw new EOFException();
                }
                neeble = octet >> 4;
            } else {
                neeble = octet & 0xF;
            }

            value |= ((neeble & 7) << shVal);
            if ((neeble & 8) == 0) {
                array[readInts++] = value;
                shVal = 0;
                value = 0;
            } else {
                shVal += 3;
            }
        }

        if (neebleNum % 2 != 0) {
            return 0x80 | (octet & 0xF);
        }

        return 0;
    }

    public static void readInts4(final ByteBuffer buffer, final int[] array, final int count) throws IOException {
        readInts4(buffer, array, count, 0);
    }

    public static int readInts4(final ByteBuffer buffer, final int[] array,
            final int count, final int lowValue) throws IOException {
        int value = 0;
        int octet = 0;
        int readInts = 0;
        int shVal = 0;
        int neeble = 0;
        int neebleNum = 0;

        if (lowValue > 0) {
            octet = lowValue & 0xF;
            neebleNum = 1;
        }

        for(; readInts < count; neebleNum++) {
            if (neebleNum % 2 == 0) {
                if (!buffer.hasRemaining()) {
                    throw new EOFException();
                }
                octet = buffer.get();

                neeble = octet >> 4;
            } else {
                neeble = octet & 0xF;
            }

            value |= ((neeble & 7) << shVal);
            if ((neeble & 8) == 0) {
                array[readInts++] = value;
                shVal = 0;
                value = 0;
            } else {
                shVal += 3;
            }
        }

        if (neebleNum % 2 != 0) {
            return 0x80 | (octet & 0xF);
        }

        return 0;
    }

    public static void writeInt4(final OutputStream os, int value) throws IOException {
        int nibbleL;
        int nibbleH;
        do {
            nibbleH = value & 7;
            value >>>= 3;

            if (value != 0) {
                nibbleH |= 8;
                nibbleL = value & 7;
                value >>>= 3;
                if (value != 0) {
                    nibbleL |= 8;
                }
            } else {
                nibbleL = 0;
            }

            os.write(nibbleL | (nibbleH << 4));
        } while(value != 0);
    }

    public static int readInt8(final InputStream is) throws IOException {
        int value = 0;
        for(int shVal = 0, octet = 0x80; (octet & 0x80) != 0; shVal += 7) {
            octet = is.read();
            if (octet == -1) {
                throw new EOFException();
            }

            value |= ((octet & 0x7F) << shVal);
        }

        return value;
    }

    public static void writeInt8(final OutputStream os, int value) throws IOException {
        int octet;
        do {
            octet = value & 0x7F;
            value >>>= 7;

            if (value != 0) {
                octet |= 0x80;
            }

            os.write(octet);
        } while(value != 0);
    }

    public static void writeInt8(final ByteBuffer bb, int value) {
        int octet;
        do {
            octet = value & 0x7F;
            value >>>= 7;

            if (value != 0) {
                octet |= 0x80;
            }

            bb.put((byte) octet);
        } while(value != 0);
    }

    public static int readInt4(final ByteBuffer buffer) throws IOException {
        int value = 0;

        for(int shVal = 0, neeble = 8; (neeble & 8) != 0; shVal += 6) {
            if (!buffer.hasRemaining()) {
                throw new EOFException();
            }

            final int octet = buffer.get();
            neeble = octet >> 4;

            value |= ((neeble & 7) << shVal);
            if ((neeble & 8) != 0) {
                neeble = octet & 0xF;
                value |= ((neeble & 7) << (shVal + 3));
            }
        }

        return value;
    }

    public static void writeInts4(final ByteBuffer bb, final int ... values) throws IOException {
        writeInts4(bb, values, 0, values.length);
    }

    public static void writeInts4(final ByteBuffer bb, final int[] array, final int offset, final int count) throws IOException {
        int shiftValue = 0;
        for(int i=0; i<count - 1; i++) {
            final int value = array[offset + i];
            shiftValue = writeInt4(bb, value, shiftValue, false);
        }

        if (count > 0) {
            writeInt4(bb, array[offset + count - 1], shiftValue, true);
        }
    }


    public static void writeInts4(final OutputStream out, final int ... values) throws IOException {
        writeInts4(out, values, 0, values.length);
    }

    public static void writeInts4(final OutputStream out, final int[] array,
            final int offset, final int count) throws IOException {
        int shiftValue = 0;
        for(int i=0; i<count - 1; i++) {
            final int value = array[offset + i];
            shiftValue = writeInt4(out, value, shiftValue, false);
        }

        if (count > 0) {
            writeInt4(out, array[offset + count - 1], shiftValue, true);
        }
    }

    public static int writeInt4(final OutputStream out, int value, int highValue, final boolean flush) throws IOException {
        int nibbleL;
        int nibbleH;

        if (highValue > 0) {
            highValue &= 0x70; // clear highest bit
            nibbleL = value & 7;
            value >>>= 3;
            if (value != 0) {
                nibbleL |= 8;
            }

            out.write(highValue | nibbleL);

            if (value == 0) {
                return 0;
            }
        }

        do {
            // shift nibbleH to high byte's bits
            nibbleH = (value & 7) << 4;
            value >>>= 3;

            if (value != 0) {
                nibbleH |= 0x80;
                nibbleL = value & 7;
                value >>>= 3;
                if (value != 0) {
                    nibbleL |= 8;
                }
            } else {
                if (!flush) {
                    return nibbleH | 0x80;
                }

                nibbleL = 0;
            }

            out.write(nibbleH | nibbleL);
        } while(value != 0);

        return 0;
    }

    public static int writeInt4(final ByteBuffer bb, int value, int highValue, final boolean flush) {
        int nibbleL;
        int nibbleH;

        if (highValue > 0) {
            highValue &= 0x70; // clear highest bit
            nibbleL = value & 7;
            value >>>= 3;
            if (value != 0) {
                nibbleL |= 8;
            }

            bb.put((byte) (highValue | nibbleL));

            if (value == 0) {
                return 0;
            }
        }

        do {
            // shift nibbleH to high byte's bits
            nibbleH = (value & 7) << 4;
            value >>>= 3;

            if (value != 0) {
                nibbleH |= 0x80;
                nibbleL = value & 7;
                value >>>= 3;
                if (value != 0) {
                    nibbleL |= 8;
                }
            } else {
                if (!flush) {
                    return nibbleH | 0x80;
                }

                nibbleL = 0;
            }

            bb.put((byte) (nibbleH | nibbleL));
        } while(value != 0);

        return 0;
    }

    public static int readInt8(final ByteBuffer buffer) throws IOException {
        int value = 0;
        for(int shVal = 0, octet = 0x80; (octet & 0x80) != 0; shVal += 7) {
            if (!buffer.hasRemaining()) {
                throw new EOFException();
            }

            octet = buffer.get();

            value |= ((octet & 0x7F) << shVal);
        }

        return value;
    }

    public static void readFully(final InputStream inputStream, final byte[] buffer) throws IOException {
        readFully(inputStream, buffer, 0, buffer.length);
    }

    public static void readFully(final InputStream inputStream, final byte[] buffer,
            final int offset, final int length) throws IOException {
        int bytesRead = 0;
        while(bytesRead < length) {
            final int count = inputStream.read(buffer, offset + bytesRead, length - bytesRead);
            if (count < 0) {
                throw new EOFException();
            }
            bytesRead += count;
        }
    }
}
