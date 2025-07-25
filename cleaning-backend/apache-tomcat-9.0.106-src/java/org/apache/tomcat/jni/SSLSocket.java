/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tomcat.jni;

/**
 * SSL Socket
 *
 * @author Mladen Turk
 *
 * @deprecated The scope of the APR/Native Library will be reduced in Tomcat 9.1.x / Tomcat Native 2.x and has been
 *                 reduced in Tomcat 10.1.x / Tomcat Native 2.x onwards to only include those components required to
 *                 provide OpenSSL integration with the NIO and NIO2 connectors.
 */
@Deprecated
public class SSLSocket {

    /**
     * Attach APR socket on an SSL connection.
     *
     * @param ctx  SSLContext to use.
     * @param sock APR Socket that already did physical connect or accept.
     *
     * @return APR_STATUS code.
     *
     * @throws Exception An error occurred
     */
    public static native int attach(long ctx, long sock) throws Exception;

    /**
     * Do an SSL handshake.
     *
     * @param thesocket The socket to use
     *
     * @return the handshake status
     */
    public static native int handshake(long thesocket);

    /**
     * Do an SSL renegotiation. SSL supports per-directory re-configuration of SSL parameters. This is implemented by
     * performing an SSL renegotiation of the re-configured parameters after the request is read, but before the
     * response is sent. In more detail: the renegotiation happens after the request line and MIME headers were read,
     * but _before_ the attached request body is read. The reason simply is that in the HTTP protocol usually there is
     * no acknowledgment step between the headers and the body (there is the 100-continue feature and the chunking
     * facility only), so Apache has no API hook for this step.
     *
     * @param thesocket The socket to use
     *
     * @return the operation status
     */
    public static native int renegotiate(long thesocket);

    /**
     * Set Type of Client Certificate verification and Maximum depth of CA Certificates in Client Certificate
     * verification. <br>
     * This is used to change the verification level for a connection prior to starting a re-negotiation. <br>
     * The following levels are available for level:
     *
     * <PRE>
     * SSL_CVERIFY_NONE           - No client Certificate is required at all
     * SSL_CVERIFY_OPTIONAL       - The client may present a valid Certificate
     * SSL_CVERIFY_REQUIRE        - The client has to present a valid
     *                              Certificate
     * SSL_CVERIFY_OPTIONAL_NO_CA - The client may present a valid Certificate
     *                              but it need not to be (successfully)
     *                              verifiable
     * </PRE>
     *
     * <br>
     *
     * @param sock  The socket to change.
     * @param level Type of Client Certificate verification.
     * @param depth Maximum number of certificates to permit in chain from client to trusted CA. Use a value of 0 or
     *                  less to leave the current value unchanged
     */
    public static native void setVerify(long sock, int level, int depth);

    /**
     * Return SSL Info parameter as byte array.
     *
     * @param sock The socket to read the data from.
     * @param id   Parameter id.
     *
     * @return Byte array containing info id value.
     *
     * @throws Exception An error occurred
     */
    public static native byte[] getInfoB(long sock, int id) throws Exception;

    /**
     * Return SSL Info parameter as String.
     *
     * @param sock The socket to read the data from.
     * @param id   Parameter id.
     *
     * @return String containing info id value.
     *
     * @throws Exception An error occurred
     */
    public static native String getInfoS(long sock, int id) throws Exception;

    /**
     * Return SSL Info parameter as integer.
     *
     * @param sock The socket to read the data from.
     * @param id   Parameter id.
     *
     * @return Integer containing info id value or -1 on error.
     *
     * @throws Exception An error occurred
     */
    public static native int getInfoI(long sock, int id) throws Exception;


    /**
     * Obtain the name of the protocol negotiated via ALPN. Only valid after the TLS handshake has completed.
     *
     * @param sock               Socket
     * @param negotiatedProtocol Byte array in which to store agreed protocol
     *
     * @return Length of agreed protocol. Zero means no protocol agreed.
     */
    public static native int getALPN(long sock, byte[] negotiatedProtocol);

}
