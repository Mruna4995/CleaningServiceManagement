/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.util.codec.binary;

import java.util.Arrays;

import org.apache.tomcat.util.buf.HexUtils;
import org.apache.tomcat.util.res.StringManager;

/**
 * Abstract superclass for Base-N encoders and decoders.
 * <p>
 * This class is thread-safe.
 * </p>
 *
 * @deprecated Unused. This class will be removed in Tomcat 11 onwards.
 */
@Deprecated
public abstract class BaseNCodec {

    protected static final StringManager sm = StringManager.getManager(BaseNCodec.class);

    /**
     * Holds thread context so classes can be thread-safe. This class is not itself thread-safe; each thread must
     * allocate its own copy.
     *
     * @since 1.7
     */
    static class Context {

        /**
         * Placeholder for the bytes we're dealing with for our based logic. Bitwise operations store and extract the
         * encoding or decoding from this variable.
         */
        int ibitWorkArea;

        /**
         * Buffer for streaming.
         */
        byte[] buffer;

        /**
         * Position where next character should be written in the buffer.
         */
        int pos;

        /**
         * Position where next character should be read from the buffer.
         */
        int readPos;

        /**
         * Boolean flag to indicate the EOF has been reached. Once EOF has been reached, this object becomes useless,
         * and must be thrown away.
         */
        boolean eof;

        /**
         * Variable tracks how many characters have been written to the current line. Only used when encoding. We use it
         * to make sure each encoded line never goes beyond lineLength (if lineLength &gt; 0).
         */
        int currentLinePos;

        /**
         * Writes to the buffer only occur after every 3/5 reads when encoding, and every 4/8 reads when decoding. This
         * variable helps track that.
         */
        int modulus;

        /**
         * Returns a String useful for debugging (especially within a debugger.)
         *
         * @return a String useful for debugging.
         */
        @SuppressWarnings("boxing") // OK to ignore boxing here
        @Override
        public String toString() {
            return String.format(
                    "%s[buffer=%s, currentLinePos=%s, eof=%s, ibitWorkArea=%s, " + "modulus=%s, pos=%s, readPos=%s]",
                    this.getClass().getSimpleName(), HexUtils.toHexString(buffer), currentLinePos, eof, ibitWorkArea,
                    modulus, pos, readPos);
        }
    }

    /**
     * EOF
     *
     * @since 1.7
     */
    static final int EOF = -1;

    /**
     * MIME chunk size per RFC 2045 section 6.8.
     * <p>
     * The {@value} character limit does not count the trailing CRLF, but counts all other characters, including any
     * equal signs.
     * </p>
     *
     * @see <a href="http://www.ietf.org/rfc/rfc2045.txt">RFC 2045 section 6.8</a>
     */
    public static final int MIME_CHUNK_SIZE = 76;

    private static final int DEFAULT_BUFFER_RESIZE_FACTOR = 2;

    /**
     * Defines the default buffer size - currently {@value} - must be large enough for at least one encoded
     * block+separator
     */
    private static final int DEFAULT_BUFFER_SIZE = 128;

    /**
     * The maximum size buffer to allocate.
     * <p>
     * This is set to the same size used in the JDK {@link java.util.ArrayList}:
     * </p>
     * <blockquote> Some VMs reserve some header words in an array. Attempts to allocate larger arrays may result in
     * OutOfMemoryError: Requested array size exceeds VM limit. </blockquote>
     */
    private static final int MAX_BUFFER_SIZE = Integer.MAX_VALUE - 8;

    /** Mask used to extract 8 bits, used in decoding bytes */
    protected static final int MASK_8BITS = 0xff;

    /**
     * Byte used to pad output.
     */
    protected static final byte PAD_DEFAULT = '='; // Allow static access to default

    /**
     * Chunk separator per RFC 2045 section 2.1.
     *
     * @see <a href="http://www.ietf.org/rfc/rfc2045.txt">RFC 2045 section 2.1</a>
     */
    static final byte[] CHUNK_SEPARATOR = { '\r', '\n' };

    /**
     * Create a positive capacity at least as large the minimum required capacity. If the minimum capacity is negative
     * then this throws an OutOfMemoryError as no array can be allocated.
     *
     * @param minCapacity the minimum capacity
     *
     * @return the capacity
     *
     * @throws OutOfMemoryError if the {@code minCapacity} is negative
     */
    private static int createPositiveCapacity(final int minCapacity) {
        if (minCapacity < 0) {
            // overflow
            throw new OutOfMemoryError(sm.getString("base64.outOfMemory", Long.valueOf(minCapacity & 0xffffffffL)));
        }
        // This is called when we require buffer expansion to a very big array.
        // Use the conservative maximum buffer size if possible, otherwise the biggest required.
        //
        // Note: In this situation JDK 1.8 java.util.ArrayList returns Integer.MAX_VALUE.
        // This excludes some VMs that can exceed MAX_BUFFER_SIZE but not allocate a full
        // Integer.MAX_VALUE length array.
        // The result is that we may have to allocate an array of this size more than once if
        // the capacity must be expanded again.
        return Math.max(minCapacity, MAX_BUFFER_SIZE);
    }

    /**
     * Increases our buffer by the {@link #DEFAULT_BUFFER_RESIZE_FACTOR}.
     *
     * @param context     the context to be used
     * @param minCapacity the minimum required capacity
     *
     * @return the resized byte[] buffer
     *
     * @throws OutOfMemoryError if the {@code minCapacity} is negative
     */
    private static byte[] resizeBuffer(final Context context, final int minCapacity) {
        // Overflow-conscious code treats the min and new capacity as unsigned.
        final int oldCapacity = context.buffer.length;
        int newCapacity = oldCapacity * DEFAULT_BUFFER_RESIZE_FACTOR;
        if (Integer.compareUnsigned(newCapacity, minCapacity) < 0) {
            newCapacity = minCapacity;
        }
        if (Integer.compareUnsigned(newCapacity, MAX_BUFFER_SIZE) > 0) {
            newCapacity = createPositiveCapacity(minCapacity);
        }

        final byte[] b = Arrays.copyOf(context.buffer, newCapacity);
        context.buffer = b;
        return b;
    }

    /** Pad byte. Instance variable just in case it needs to vary later. */
    protected final byte pad;

    /** Number of bytes in each full block of unencoded data, e.g. 4 for Base64 and 5 for Base32 */
    private final int unencodedBlockSize;

    /** Number of bytes in each full block of encoded data, e.g. 3 for Base64 and 8 for Base32 */
    private final int encodedBlockSize;

    /**
     * Chunksize for encoding. Not used when decoding. A value of zero or less implies no chunking of the encoded data.
     * Rounded down to the nearest multiple of encodedBlockSize.
     */
    protected final int lineLength;

    /**
     * Size of chunk separator. Not used unless {@link #lineLength} &gt; 0.
     */
    private final int chunkSeparatorLength;

    /**
     * Note {@code lineLength} is rounded down to the nearest multiple of the encoded block size. If
     * {@code chunkSeparatorLength} is zero, then chunking is disabled.
     *
     * @param unencodedBlockSize   the size of an unencoded block (e.g. Base64 = 3)
     * @param encodedBlockSize     the size of an encoded block (e.g. Base64 = 4)
     * @param lineLength           if &gt; 0, use chunking with a length {@code lineLength}
     * @param chunkSeparatorLength the chunk separator length, if relevant
     */
    protected BaseNCodec(final int unencodedBlockSize, final int encodedBlockSize, final int lineLength,
            final int chunkSeparatorLength) {
        this(unencodedBlockSize, encodedBlockSize, lineLength, chunkSeparatorLength, PAD_DEFAULT);
    }

    /**
     * Note {@code lineLength} is rounded down to the nearest multiple of the encoded block size. If
     * {@code chunkSeparatorLength} is zero, then chunking is disabled.
     *
     * @param unencodedBlockSize   the size of an unencoded block (e.g. Base64 = 3)
     * @param encodedBlockSize     the size of an encoded block (e.g. Base64 = 4)
     * @param lineLength           if &gt; 0, use chunking with a length {@code lineLength}
     * @param chunkSeparatorLength the chunk separator length, if relevant
     * @param pad                  byte used as padding byte.
     */
    protected BaseNCodec(final int unencodedBlockSize, final int encodedBlockSize, final int lineLength,
            final int chunkSeparatorLength, final byte pad) {
        this.unencodedBlockSize = unencodedBlockSize;
        this.encodedBlockSize = encodedBlockSize;
        final boolean useChunking = lineLength > 0 && chunkSeparatorLength > 0;
        this.lineLength = useChunking ? lineLength / encodedBlockSize * encodedBlockSize : 0;
        this.chunkSeparatorLength = chunkSeparatorLength;
        this.pad = pad;
    }

    /**
     * Returns the amount of buffered data available for reading.
     *
     * @param context the context to be used
     *
     * @return The amount of buffered data available for reading.
     */
    int available(final Context context) { // package protected for access from I/O streams
        return hasData(context) ? context.pos - context.readPos : 0;
    }

    /**
     * Tests a given byte array to see if it contains any characters within the alphabet or PAD. Intended for use in
     * checking line-ending arrays
     *
     * @param arrayOctet byte array to test
     *
     * @return {@code true} if any byte is a valid character in the alphabet or PAD; {@code false} otherwise
     */
    protected boolean containsAlphabetOrPad(final byte[] arrayOctet) {
        if (arrayOctet == null) {
            return false;
        }
        for (final byte element : arrayOctet) {
            if (pad == element || isInAlphabet(element)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Decodes a byte[] containing characters in the Base-N alphabet.
     *
     * @param pArray A byte array containing Base-N character data
     *
     * @return a byte array containing binary data
     */
    public byte[] decode(final byte[] pArray) {
        return decode(pArray, 0, pArray.length);
    }

    public byte[] decode(final byte[] pArray, final int off, final int len) {
        if (pArray == null || len == 0) {
            return new byte[0];
        }
        final Context context = new Context();
        decode(pArray, off, len, context);
        decode(pArray, off, EOF, context); // Notify decoder of EOF.
        final byte[] result = new byte[context.pos];
        readResults(result, 0, result.length, context);
        return result;
    }

    // package protected for access from I/O streams
    abstract void decode(byte[] pArray, int i, int length, Context context);

    /**
     * Decodes a String containing characters in the Base-N alphabet.
     *
     * @param pArray A String containing Base-N character data
     *
     * @return a byte array containing binary data
     */
    public byte[] decode(final String pArray) {
        return decode(StringUtils.getBytesUtf8(pArray));
    }

    /**
     * Encodes a byte[] containing binary data, into a byte[] containing characters in the alphabet.
     *
     * @param pArray a byte array containing binary data
     *
     * @return A byte array containing only the base N alphabetic character data
     */
    public byte[] encode(final byte[] pArray) {
        if (pArray == null || pArray.length == 0) {
            return pArray;
        }
        return encode(pArray, 0, pArray.length);
    }

    /**
     * Encodes a byte[] containing binary data, into a byte[] containing characters in the alphabet.
     *
     * @param pArray a byte array containing binary data
     * @param offset initial offset of the subarray.
     * @param length length of the subarray.
     *
     * @return A byte array containing only the base N alphabetic character data
     *
     * @since 1.11
     */
    public byte[] encode(final byte[] pArray, final int offset, final int length) {
        if (pArray == null || pArray.length == 0) {
            return pArray;
        }
        final Context context = new Context();
        encode(pArray, offset, length, context);
        encode(pArray, offset, EOF, context); // Notify encoder of EOF.
        final byte[] buf = new byte[context.pos - context.readPos];
        readResults(buf, 0, buf.length, context);
        return buf;
    }

    // package protected for access from I/O streams
    abstract void encode(byte[] pArray, int i, int length, Context context);

    /**
     * Encodes a byte[] containing binary data, into a String containing characters in the appropriate alphabet. Uses
     * UTF8 encoding.
     *
     * @param pArray a byte array containing binary data
     *
     * @return String containing only character data in the appropriate alphabet.
     *
     * @since 1.5
     */
    public String encodeAsString(final byte[] pArray) {
        return StringUtils.newStringUtf8(encode(pArray));
    }

    /**
     * Ensure that the buffer has room for {@code size} bytes
     *
     * @param size    minimum spare space required
     * @param context the context to be used
     *
     * @return the buffer
     */
    protected byte[] ensureBufferSize(final int size, final Context context) {
        if (context.buffer == null) {
            context.buffer = new byte[Math.max(size, getDefaultBufferSize())];
            context.pos = 0;
            context.readPos = 0;

            // Overflow-conscious:
            // x + y > z == x + y - z > 0
        } else if (context.pos + size - context.buffer.length > 0) {
            return resizeBuffer(context, context.pos + size);
        }
        return context.buffer;
    }

    /**
     * Gets the default buffer size. Can be overridden.
     *
     * @return the default buffer size.
     */
    protected int getDefaultBufferSize() {
        return DEFAULT_BUFFER_SIZE;
    }

    /**
     * Calculates the amount of space needed to encode the supplied array.
     *
     * @param pArray byte[] array which will later be encoded
     *
     * @return amount of space needed to encode the supplied array. Returns a long since a max-len array will require
     *             &gt; Integer.MAX_VALUE
     */
    public long getEncodedLength(final byte[] pArray) {
        // Calculate non-chunked size - rounded up to allow for padding
        // cast to long is needed to avoid possibility of overflow
        long len = (pArray.length + unencodedBlockSize - 1) / unencodedBlockSize * (long) encodedBlockSize;
        if (lineLength > 0) { // We're using chunking
            // Round up to nearest multiple
            len += (len + lineLength - 1) / lineLength * chunkSeparatorLength;
        }
        return len;
    }

    /**
     * Returns true if this object has buffered data for reading.
     *
     * @param context the context to be used
     *
     * @return true if there is data still available for reading.
     */
    boolean hasData(final Context context) { // package protected for access from I/O streams
        return context.pos > context.readPos;
    }

    /**
     * Returns whether or not the {@code octet} is in the current alphabet. Does not allow whitespace or pad.
     *
     * @param value The value to test
     *
     * @return {@code true} if the value is defined in the current alphabet, {@code false} otherwise.
     */
    protected abstract boolean isInAlphabet(byte value);

    /**
     * Extracts buffered data into the provided byte[] array, starting at position bPos, up to a maximum of bAvail
     * bytes. Returns how many bytes were actually extracted.
     * <p>
     * Package private for access from I/O streams.
     * </p>
     *
     * @param b       byte[] array to extract the buffered data into.
     * @param bPos    position in byte[] array to start extraction at.
     * @param bAvail  amount of bytes we're allowed to extract. We may extract fewer (if fewer are available).
     * @param context the context to be used
     *
     * @return The number of bytes successfully extracted into the provided byte[] array.
     */
    int readResults(final byte[] b, final int bPos, final int bAvail, final Context context) {
        if (hasData(context)) {
            final int len = Math.min(available(context), bAvail);
            System.arraycopy(context.buffer, context.readPos, b, bPos, len);
            context.readPos += len;
            if (!hasData(context)) {
                // All data read.
                // Reset position markers but do not set buffer to null to allow its reuse.
                // hasData(context) will still return false, and this method will return 0 until
                // more data is available, or -1 if EOF.
                context.pos = context.readPos = 0;
            }
            return len;
        }
        return context.eof ? EOF : 0;
    }
}
