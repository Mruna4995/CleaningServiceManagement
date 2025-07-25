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
package org.apache.tomcat.util.http.fileupload.disk;

import java.io.File;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileItemFactory;

/**
 * <p>The default {@link org.apache.tomcat.util.http.fileupload.FileItemFactory}
 * implementation. This implementation creates
 * {@link org.apache.tomcat.util.http.fileupload.FileItem} instances which keep
 * their
 * content either in memory, for smaller items, or in a temporary file on disk,
 * for larger items. The size threshold, above which content will be stored on
 * disk, is configurable, as is the directory in which temporary files will be
 * created.</p>
 *
 * <p>If not otherwise configured, the default configuration values are as
 * follows:</p>
 * <ul>
 *   <li>Size threshold is 10 KiB.</li>
 *   <li>Repository is the system default temp directory, as returned by
 *       {@code System.getProperty("java.io.tmpdir")}.</li>
 * </ul>
 * <p>
 * <strong>NOTE</strong>: Files are created in the system default temp directory with
 * predictable names. This means that a local attacker with write access to that
 * directory can perform a TOUTOC attack to replace any uploaded file with a
 * file of the attackers choice. The implications of this will depend on how the
 * uploaded file is used but could be significant. When using this
 * implementation in an environment with local, untrusted users,
 * {@link #setRepository(File)} MUST be used to configure a repository location
 * that is not publicly writable. In a Servlet container the location identified
 * by the ServletContext attribute {@code javax.servlet.context.tempdir}
 * may be used.
 * </p>
 *
 * <p>Temporary files, which are created for file items, will be deleted when
 * the associated request is recycled.</p>
 *
 * @since FileUpload 1.1
 */
public class DiskFileItemFactory implements FileItemFactory {

    /**
     * The default threshold above which uploads will be stored on disk.
     */
    public static final int DEFAULT_SIZE_THRESHOLD = 10240;

    /**
     * The directory in which uploaded files will be stored, if stored on disk.
     */
    private File repository;

    /**
     * The threshold above which uploads will be stored on disk.
     */
    private int sizeThreshold = DEFAULT_SIZE_THRESHOLD;

    /**
     * Default content charset to be used when no explicit charset
     * parameter is provided by the sender.
     */
    private String defaultCharset = DiskFileItem.DEFAULT_CHARSET;

    /**
     * Constructs an unconfigured instance of this class. The resulting factory
     * may be configured by calling the appropriate setter methods.
     */
    public DiskFileItemFactory() {
        this(DEFAULT_SIZE_THRESHOLD, null);
    }

    /**
     * Constructs a preconfigured instance of this class.
     *
     * @param sizeThreshold The threshold, in bytes, below which items will be
     *                      retained in memory and above which they will be
     *                      stored as a file.
     * @param repository    The data repository, which is the directory in
     *                      which files will be created, should the item size
     *                      exceed the threshold.
     */
    public DiskFileItemFactory(final int sizeThreshold, final File repository) {
        this.sizeThreshold = sizeThreshold;
        this.repository = repository;
    }

    /**
     * Create a new {@link DiskFileItem}
     * instance from the supplied parameters and the local factory
     * configuration.
     *
     * @param fieldName   The name of the form field.
     * @param contentType The content type of the form field.
     * @param isFormField {@code true} if this is a plain form field;
     *                    {@code false} otherwise.
     * @param fileName    The name of the uploaded file, if any, as supplied
     *                    by the browser or other client.
     *
     * @return The newly created file item.
     */
    @Override
    public FileItem createItem(final String fieldName, final String contentType,
            final boolean isFormField, final String fileName) {
        final DiskFileItem result = new DiskFileItem(fieldName, contentType,
                isFormField, fileName, sizeThreshold, repository);
        result.setDefaultCharset(defaultCharset);
        return result;
    }

    /**
     * Gets the default charset for use when no explicit charset
     * parameter is provided by the sender.
     * @return the default charset
     */
    public String getDefaultCharset() {
        return defaultCharset;
    }

    /**
     * Gets the directory used to temporarily store files that are larger
     * than the configured size threshold.
     *
     * @return The directory in which temporary files will be located.
     *
     * @see #setRepository(java.io.File)
     *
     */
    public File getRepository() {
        return repository;
    }

    /**
     * Gets the size threshold beyond which files are written directly to
     * disk. The default value is 10240 bytes.
     *
     * @return The size threshold, in bytes.
     *
     * @see #setSizeThreshold(int)
     */
    public int getSizeThreshold() {
        return sizeThreshold;
    }

    /**
     * Sets the default charset for use when no explicit charset
     * parameter is provided by the sender.
     *
     * @param charset the default charset
     */
    public void setDefaultCharset(final String charset) {
        this.defaultCharset = charset;
    }

    /**
     * Sets the directory used to temporarily store files that are larger
     * than the configured size threshold.
     *
     * @param repository The directory in which temporary files will be located.
     *
     * @see #getRepository()
     *
     */
    public void setRepository(final File repository) {
        this.repository = repository;
    }

    /**
     * Sets the size threshold beyond which files are written directly to disk.
     *
     * @param sizeThreshold The size threshold, in bytes.
     *
     * @see #getSizeThreshold()
     *
     */
    public void setSizeThreshold(final int sizeThreshold) {
        this.sizeThreshold = sizeThreshold;
    }
}
