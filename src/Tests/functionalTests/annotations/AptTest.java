/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2008 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version
 * 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 */
package functionalTests.annotations;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.Ignore;
import org.objectweb.proactive.extensions.annotation.common.ProActiveAnnotationProcessorFactory;

import functionalTests.FunctionalTest;


/**
 * Root class for tests for annotation implemented using apt + the Mirror API
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.10
 */
@Ignore
public abstract class AptTest extends AnnotationTest {

    private String[] _aptCommand;
    private String _classpath;

    /* (non-Javadoc)
     * @see functionalTests.annotations.AnnotationTest#testInit()
     */
    @Override
    protected void testInit() throws NoCompilerDetectedException {
        _aptCommand = new String[7];
        _aptCommand[0] = getAptCommand();
        _aptCommand[1] = "-factorypath";
        _aptCommand[2] = PROC_PATH;
        _aptCommand[3] = "-factory";
        _aptCommand[4] = ProActiveAnnotationProcessorFactory.class.getName();
        _aptCommand[5] = "-nocompile";

        _classpath = PROC_PATH;
    }

    @Override
    public void testCleanup() {
        // nothing for now...
    }

    public String getAptCommand() {
        return FunctionalTest.getJavaBinDir() + "apt";
    }

    @Override
    protected Result checkFile(String fileName) throws CompilationExecutionException {

        _aptCommand[6] = INPUT_FILES_PATH + File.separator + fileName + ".java";// the input file
        return runApt();
    }

    @Override
    protected Result checkFiles(String... fileNames) throws CompilationExecutionException {
        _aptCommand[6] = "";
        for (String fileName : fileNames) {
            _aptCommand[6] = _aptCommand[6] + " " + INPUT_FILES_PATH + File.separator + fileName + ".java";// the input file
        }
        return runApt();
    }

    private Result runApt() throws CompilationExecutionException {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(Arrays.asList(_aptCommand));
            Map<String, String> env = processBuilder.environment();
            env.put("CLASSPATH", _classpath);

            Process aptProcess = processBuilder.start();

            BufferedReader stderr = new BufferedReader(new InputStreamReader(aptProcess.getErrorStream()));
            //flushOutput(stderr);

            return getResults(stderr);
        } catch (IOException ioExcp) {
            String msg = "Cannot execute the command " + compressCommand(_aptCommand) + ".reason:" +
                ioExcp.getMessage();
            logger.error(msg, ioExcp);
            throw new CompilationExecutionException(msg, ioExcp);
        } catch (SecurityException secExcp) {
            String msg = "Cannot execute the command " + compressCommand(_aptCommand) +
                "; security access violation.";
            logger.error(msg, secExcp);
            throw new CompilationExecutionException(msg, secExcp);
        }
    }

    private Result getResults(BufferedReader stderr) {

        Result ret = new Result();

        try {
            if (stderr.read() == -1) {
                // apt finished succesfully
                return ret;
            }

            for (String line = stderr.readLine(); line != null; line = stderr.readLine()) {
                if (Pattern.matches("\\d* error(s?)", line)) {
                    ret.errors = extractNumber(line);
                }
                if (Pattern.matches("\\d* warning(s?)", line)) {
                    ret.warnings = extractNumber(line);
                }
            }

            return ret;
        } catch (IOException e) {
            // return what we have up until now
            return ret;
        }

    }

    private int extractNumber(String line) {
        return Integer.parseInt(line.split(" ")[0]);
    }

    private String compressCommand(String[] command) {

        String ret = "";
        for (String token : command) {
            ret = ret + " " + token;
        }

        return ret;
    }

    private void flushOutput(BufferedReader br) {

        try {
            if (br.read() == -1) {
                logger.debug("No output\n");
                return;
            }
            logger.debug("\n");

            for (String line = br.readLine(); line != null; line = br.readLine()) {
                logger.debug("\t|" + line + "|");
            }

            logger.debug("\n");
            logger.debug("---------------------------------------------------------------\n");
            logger.debug("\n");
        } catch (IOException e) {
            // nothing!
        }
    }

}
