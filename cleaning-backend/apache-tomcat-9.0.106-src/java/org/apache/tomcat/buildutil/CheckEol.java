/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tomcat.buildutil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

/**
 * Ant task that checks that all the files in the given fileset have end-of-line delimiters that are appropriate.
 * <p>
 * The goal is to check whether we have problems with Subversion's svn:eol-style property or Git's autocrlf setting when
 * files are committed on one OS and then checked on another one.
 */
public class CheckEol extends Task {

    /** The files to be checked */
    private final List<FileSet> filesets = new ArrayList<>();

    /** The line ending mode (either LF, CRLF, or null for OS specific) */
    private Mode mode;

    /**
     * Sets the files to be checked
     *
     * @param fs The fileset to be checked.
     */
    public void addFileset(FileSet fs) {
        filesets.add(fs);
    }

    /**
     * Sets the line ending mode.
     *
     * @param mode The line ending mode (either LF or CRLF)
     */
    public void setMode(String mode) {
        this.mode = Mode.valueOf(mode.toUpperCase(Locale.ENGLISH));
    }

    private Mode getMode() {
        if (mode != null) {
            return mode;
        } else {
            if ("\n".equals(System.lineSeparator())) {
                return Mode.LF;
            } else if ("\r\n".equals(System.lineSeparator())) {
                return Mode.CRLF;
            }
        }

        return null;
    }

    /**
     * Perform the check
     *
     * @throws BuildException if an error occurs during execution of this task.
     */
    @Override
    public void execute() throws BuildException {

        Mode mode = getMode();
        if (mode == null) {
            log("Line ends check skipped, because OS line ends setting is neither LF nor CRLF.", Project.MSG_VERBOSE);
            return;
        }

        int count = 0;

        List<CheckFailure> errors = new ArrayList<>();

        // Step through each file and check.
        for (FileSet fs : filesets) {
            DirectoryScanner ds = fs.getDirectoryScanner(getProject());
            File basedir = ds.getBasedir();
            String[] files = ds.getIncludedFiles();
            if (files.length > 0) {
                log("Checking line ends in " + files.length + " file(s)");
                for (String filename : files) {
                    File file = new File(basedir, filename);
                    log("Checking file '" + file + "' for correct line ends", Project.MSG_DEBUG);
                    try {
                        check(file, errors, mode);
                    } catch (IOException e) {
                        throw new BuildException("Could not check file '" + file.getAbsolutePath() + "'", e);
                    }
                    count++;
                }
            }
        }
        if (count > 0) {
            log("Done line ends check in " + count + " file(s), " + errors.size() + " error(s) found.");
        }
        if (!errors.isEmpty()) {
            String message = "The following files have wrong line ends: " + errors;
            // We need to explicitly write the message to the log, because
            // long BuildException messages may be trimmed. E.g. I observed
            // this problem with Eclipse IDE 3.7.
            log(message, Project.MSG_ERR);
            throw new BuildException(message);
        }
    }

    private enum Mode {
        LF,
        CRLF
    }

    private static class CheckFailure {
        private final File file;
        private final int line;
        private final String value;

        CheckFailure(File file, int line, String value) {
            this.file = file;
            this.line = line;
            this.value = value;
        }

        @Override
        public String toString() {
            return System.lineSeparator() + file + ": uses " + value + " on line " + line;
        }
    }

    private void check(File file, List<CheckFailure> errors, Mode mode) throws IOException {
        try (FileInputStream fis = new FileInputStream(file); BufferedInputStream is = new BufferedInputStream(fis)) {
            int line = 1;
            int prev = -1;
            int ch;
            while ((ch = is.read()) != -1) {
                if (ch == '\n') {
                    if (mode == Mode.LF && prev == '\r') {
                        errors.add(new CheckFailure(file, line, "CRLF"));
                        return;
                    } else if (mode == Mode.CRLF && prev != '\r') {
                        errors.add(new CheckFailure(file, line, "LF"));
                        return;
                    }
                    line++;
                } else if (prev == '\r') {
                    errors.add(new CheckFailure(file, line, "CR"));
                    return;
                }
                prev = ch;
            }
        }
    }
}
