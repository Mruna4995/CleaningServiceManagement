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
 * Time
 *
 * @author Mladen Turk
 *
 * @deprecated The scope of the APR/Native Library will be reduced in Tomcat 9.1.x / Tomcat Native 2.x and has been
 *                 reduced in Tomcat 10.1.x / Tomcat Native 2.x onwards to only include those components required to
 *                 provide OpenSSL integration with the NIO and NIO2 connectors.
 */
@Deprecated
public class Time {

    /** number of microseconds per second */
    public static final long APR_USEC_PER_SEC = 1000000L;
    /** number of milliseconds per microsecond */
    public static final long APR_MSEC_PER_USEC = 1000L;

    /**
     * @param t The time
     *
     * @return apr_time_t as a second
     */
    public static long sec(long t) {
        return t / APR_USEC_PER_SEC;
    }

    /**
     * @param t The time
     *
     * @return apr_time_t as a msec
     */
    public static long msec(long t) {
        return t / APR_MSEC_PER_USEC;
    }

    /**
     * number of microseconds since 00:00:00 January 1, 1970 UTC
     *
     * @return the current time
     */
    public static native long now();

    /**
     * Formats dates in the RFC822 format in an efficient manner.
     *
     * @param t the time to convert
     *
     * @return the formatted date
     */
    public static native String rfc822(long t);

    /**
     * Formats dates in the ctime() format in an efficient manner. Unlike ANSI/ISO C ctime(), apr_ctime() does not
     * include a \n at the end of the string.
     *
     * @param t the time to convert
     *
     * @return the formatted date
     */
    public static native String ctime(long t);

    /**
     * Sleep for the specified number of micro-seconds. <br>
     * <b>Warning :</b> May sleep for longer than the specified time.
     *
     * @param t desired amount of time to sleep.
     */
    public static native void sleep(long t);

}
