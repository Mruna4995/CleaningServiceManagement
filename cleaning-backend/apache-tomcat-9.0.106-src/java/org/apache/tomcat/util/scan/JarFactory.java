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
package org.apache.tomcat.util.scan;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;

import org.apache.tomcat.Jar;
import org.apache.tomcat.util.buf.UriUtil;

/**
 * Provide a mechanism to obtain objects that implement {@link Jar}.
 */
public class JarFactory {

    private JarFactory() {
        // Factory class. Hide public constructor.
    }


    public static Jar newInstance(URL url) throws IOException {
        String urlString = url.toString();
        if (urlString.startsWith("jar:file:")) {
            if (urlString.endsWith("!/")) {
                return new JarFileUrlJar(url, true);
            } else {
                return new JarFileUrlNestedJar(url);
            }
        } else if (urlString.startsWith("war:file:")) {
            URL jarUrl = UriUtil.warToJar(url);
            return new JarFileUrlNestedJar(jarUrl);
        } else if (urlString.startsWith("file:")) {
            return new JarFileUrlJar(url, false);
        } else {
            return new UrlJar(url);
        }
    }


    public static URL getJarEntryURL(URL baseUrl, String entryName) throws MalformedURLException {

        String baseExternal = baseUrl.toExternalForm();

        if (baseExternal.startsWith("jar")) {
            // Assume this is pointing to a JAR file within a WAR. Java doesn't
            // support jar:jar:file:... so switch to Tomcat's war:file:...
            baseExternal = baseExternal.replaceFirst("^jar:", "war:");
            baseExternal = baseExternal.replaceFirst("!/", Matcher.quoteReplacement(UriUtil.getWarSeparator()));
        }

        return new URL("jar:" + baseExternal + "!/" + entryName);
    }
}
