/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.as.cli.parsing.test;

import java.io.File;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.jboss.as.cli.CommandContext;
import org.jboss.as.cli.CommandContextFactory;
import org.jboss.as.cli.Util;

import org.jboss.as.cli.impl.ValueTypeCompleter;
import org.jboss.dmr.ModelNode;
import org.junit.Test;

/**
 *
 * @author Alexey Loubyansky
 */
public class ValueTypeCompletionTestCase {

    private static final String loginModulesDescr = "{" +
            "\"type\" => LIST," +
            "\"description\" => \"List of authentication modules\"," +
            "\"expressions-allowed\" => false," +
            "\"required\" => true," +
            "\"nillable\" => false," +
            "\"value-type\" => {" +
            "     \"code\" => {" +
            "        \"description\" => \"Class name of the module to be instantiated.\"," +
            "        \"type\" => BOOLEAN," +
            "        \"nillable\" => false" +
            "     }," +
            "    \"flag\" => {" +
            "        \"description\" => \"The flag controls how the module participates in the overall procedure.\"," +
            "        \"type\" => STRING," +
            "        \"nillable\" => false," +
            "        \"allowed\" => [" +
            "            \"required\"," +
            "            \"requisite\"," +
            "            \"sufficient\"," +
            "            \"optional\"" +
            "        ]" +
            "    }," +
            "    \"module\" => {" +
            "        \"type\" => STRING," +
            "        \"nillable\" => true," +
            "        \"description\" => \"Name of JBoss Module where the login module code is located.\"" +
            "    }," +
            "    \"module-options\" => {" +
            "        \"description\" => \"List of module options containing a name/value pair.\"," +
            "        \"type\" => OBJECT," +
            "        \"value-type\" => STRING," +
            "        \"nillable\" => true" +
            "    }," +
            "    \"aa\" => {" +
            "        \"description\" => \"smth\"," +
            "        \"type\" => OBJECT," +
            "        \"value-type\" => {" +
            "            \"ab1\" => {" +
            "                \"description\" => \"smth\"," +
            "                \"type\" => STRING," +
            "            }," +
            "            \"ab2\" => {" +
            "                \"description\" => \"smth\"," +
            "                \"type\" => STRING," +
            "            }," +
            "            \"ac1\" => {" +
            "                \"description\" => \"smth\"," +
            "                \"type\" => BOOLEAN," +
            "            }" +
            "        }" +
            "    }," +
            "    \"bb\" => {" +
            "        \"description\" => \"smth\"," +
            "        \"type\" => LIST," +
            "        \"value-type\" => {" +
            "            \"bb1\" => {" +
            "                \"description\" => \"smth\"," +
            "                \"type\" => STRING," +
            "            }," +
            "            \"bb2\" => {" +
            "                \"description\" => \"smth\"," +
            "                \"type\" => STRING," +
            "            }," +
            "            \"bc1\" => {" +
            "                \"description\" => \"smth\"," +
            "                \"type\" => STRING," +
            "            }" +
            "        }" +
            "    }" +
            "}" +
 "}";

    private static final String FILTER_DESCRIPTION = "{\n"
            + "                \"type\" => OBJECT,\n"
            + "                \"description\" => \"Defines a simple filter type.\",\n"
            + "                \"expressions-allowed\" => false,\n"
            + "                \"required\" => false,\n"
            + "                \"nillable\" => true,\n"
            + "                \"alternatives\" => [\"filter-spec\"],\n"
            + "                \"deprecated\" => {\n"
            + "                    \"since\" => \"1.2.0\",\n"
            + "                    \"reason\" => \"Use filter-spec.\"\n"
            + "                },\n"
            + "                \"value-type\" => {\n"
            + "                    \"all\" => {\n"
            + "                        \"type\" => OBJECT,\n"
            + "                        \"description\" => \"A filter consisting of several filters in a chain.  If any filter finds the log message to be unloggable,the message will not be logged and subsequent filters will not be checked.\",\n"
            + "                        \"expressions-allowed\" => false,\n"
            + "                        \"nillable\" => true,\n"
            + "                        \"value-type\" => {\n"
            + "                            \"accept\" => {\n"
            + "                                \"type\" => BOOLEAN,\n"
            + "                                \"description\" => \"Accepts all log messages.\",\n"
            + "                                \"expressions-allowed\" => false,\n"
            + "                                \"nillable\" => true,\n"
            + "                                \"default\" => true\n"
            + "                            },\n"
            + "                            \"change-level\" => {\n"
            + "                                \"type\" => STRING,\n"
            + "                                \"description\" => \"A filter which modifies the log record with a new level if the nested filter evaluates true for that record.\",\n"
            + "                                \"expressions-allowed\" => false,\n"
            + "                                \"nillable\" => true,\n"
            + "                                \"allowed\" => [\n"
            + "                                    \"ALL\",\n"
            + "                                    \"FINEST\",\n"
            + "                                    \"FINER\",\n"
            + "                                    \"TRACE\",\n"
            + "                                    \"DEBUG\",\n"
            + "                                    \"FINE\",\n"
            + "                                    \"CONFIG\",\n"
            + "                                    \"INFO\",\n"
            + "                                    \"WARN\",\n"
            + "                                    \"WARNING\",\n"
            + "                                    \"ERROR\",\n"
            + "                                    \"SEVERE\",\n"
            + "                                    \"FATAL\",\n"
            + "                                    \"OFF\"\n"
            + "                                ]\n"
            + "                            },\n"
            + "                            \"deny\" => {\n"
            + "                                \"type\" => BOOLEAN,\n"
            + "                                \"description\" => \"Denys all log messages.\",\n"
            + "                                \"expressions-allowed\" => false,\n"
            + "                                \"nillable\" => true,\n"
            + "                                \"default\" => true\n"
            + "                            },\n"
            + "                            \"level\" => {\n"
            + "                                \"type\" => STRING,\n"
            + "                                \"description\" => \"A filter which excludes a message with the specified level.\",\n"
            + "                                \"expressions-allowed\" => true,\n"
            + "                                \"nillable\" => true,\n"
            + "                                \"default\" => \"ALL\",\n"
            + "                                \"allowed\" => [\n"
            + "                                    \"ALL\",\n"
            + "                                    \"FINEST\",\n"
            + "                                    \"FINER\",\n"
            + "                                    \"TRACE\",\n"
            + "                                    \"DEBUG\",\n"
            + "                                    \"FINE\",\n"
            + "                                    \"CONFIG\",\n"
            + "                                    \"INFO\",\n"
            + "                                    \"WARN\",\n"
            + "                                    \"WARNING\",\n"
            + "                                    \"ERROR\",\n"
            + "                                    \"SEVERE\",\n"
            + "                                    \"FATAL\",\n"
            + "                                    \"OFF\"\n"
            + "                                ]\n"
            + "                            },\n"
            + "                            \"level-range\" => {\n"
            + "                                \"type\" => OBJECT,\n"
            + "                                \"description\" => \"A filter which logs only messages that fall within a level range.\",\n"
            + "                                \"expressions-allowed\" => false,\n"
            + "                                \"nillable\" => true,\n"
            + "                                \"value-type\" => {\n"
            + "                                    \"min-level\" => {\n"
            + "                                        \"type\" => STRING,\n"
            + "                                        \"description\" => \"The minimum (least severe) level, inclusive.\",\n"
            + "                                        \"expressions-allowed\" => false,\n"
            + "                                        \"nillable\" => false,\n"
            + "                                        \"allowed\" => [\n"
            + "                                            \"ALL\",\n"
            + "                                            \"FINEST\",\n"
            + "                                            \"FINER\",\n"
            + "                                            \"TRACE\",\n"
            + "                                            \"DEBUG\",\n"
            + "                                            \"FINE\",\n"
            + "                                            \"CONFIG\",\n"
            + "                                            \"INFO\",\n"
            + "                                            \"WARN\",\n"
            + "                                            \"WARNING\",\n"
            + "                                            \"ERROR\",\n"
            + "                                            \"SEVERE\",\n"
            + "                                            \"FATAL\",\n"
            + "                                            \"OFF\"\n"
            + "                                        ]\n"
            + "                                    },\n"
            + "                                    \"min-inclusive\" => {\n"
            + "                                        \"type\" => BOOLEAN,\n"
            + "                                        \"description\" => \"True if the min-level value is inclusive, false if it is exclusive.\",\n"
            + "                                        \"expressions-allowed\" => false,\n"
            + "                                        \"nillable\" => true,\n"
            + "                                        \"default\" => true\n"
            + "                                    },\n"
            + "                                    \"max-level\" => {\n"
            + "                                        \"type\" => STRING,\n"
            + "                                        \"description\" => \"The maximum (most severe) level, inclusive.\",\n"
            + "                                        \"expressions-allowed\" => false,\n"
            + "                                        \"nillable\" => false,\n"
            + "                                        \"allowed\" => [\n"
            + "                                            \"ALL\",\n"
            + "                                            \"FINEST\",\n"
            + "                                            \"FINER\",\n"
            + "                                            \"TRACE\",\n"
            + "                                            \"DEBUG\",\n"
            + "                                            \"FINE\",\n"
            + "                                            \"CONFIG\",\n"
            + "                                            \"INFO\",\n"
            + "                                            \"WARN\",\n"
            + "                                            \"WARNING\",\n"
            + "                                            \"ERROR\",\n"
            + "                                            \"SEVERE\",\n"
            + "                                            \"FATAL\",\n"
            + "                                            \"OFF\"\n"
            + "                                        ]\n"
            + "                                    },\n"
            + "                                    \"max-inclusive\" => {\n"
            + "                                        \"type\" => BOOLEAN,\n"
            + "                                        \"description\" => \"True if the max-level value is inclusive, false if it is exclusive.\",\n"
            + "                                        \"expressions-allowed\" => false,\n"
            + "                                        \"nillable\" => true,\n"
            + "                                        \"default\" => true\n"
            + "                                    }\n"
            + "                                }\n"
            + "                            }"
            + "                        }"
            + "                    },"
            + "                    \"match\" => {\n"
            + "                         \"type\" => STRING,\n"
            + "                         \"description\" => \"A regular-expression-based filter. Used to exclude log records which match or don't match the expression. The regular expression is checked against the raw (unformatted) message.\",\n"
            + "                         \"expressions-allowed\" => false,\n"
            + "                         \"nillable\" => true,\n"
            + "                         \"min-length\" => 1L,\n"
            + "                         \"max-length\" => 2147483647L\n"
            + "                    },"
            + "                }"
            + "            }";

    private static final String VALUETYPE_WITH_FILES
            = "{ \"value-type\": \n"
            + "           {\n"
            + "                \"p1_a\": {\n"
            + "                    \"type\": \"INT\",\n"
            + "                    \"" + Util.FILESYSTEM_PATH + "\": true\n"
            + "                },\n"
            + "                \"p2_a\": {\n"
            + "                    \"type\": \"INT\",\n"
            + "                    \"" + Util.FILESYSTEM_PATH + "\": true\n"
            + "                },\n"
            + "                \"p3\": {\n"
            + "                    \"type\": \"LIST\",\n"
            + "                    \"description\": \"\",\n"
            + "                    \"value-type\": {\n"
            + "                        \"oo_file_a\": {\n"
            + "                            \"type\": \"INT\",\n"
            + "                            \"" + Util.FILESYSTEM_PATH + "\": true\n"
            + "                        },\n"
            + "                        \"ii_file\": {\n"
            + "                            \"type\": \"STRING\"\n"
            + "                        }\n"
            + "                    }\n"
            + "                },\n"
            + "                \"p4\": {\n"
            + "                    \"type\": \"OBJECT\",\n"
            + "                    \"description\": \"\",\n"
            + "                    \"value-type\": {\n"
            + "                        \"oo_file_a\": {\n"
            + "                            \"type\": \"INT\",\n"
            + "                            \"" + Util.FILESYSTEM_PATH + "\": true\n"
            + "                        },\n"
            + "                        \"ii_file\": {\n"
            + "                            \"type\": \"STRING\"\n"
            + "                        }\n"
            + "                    }\n"
            + "                }\n"
            + "             }\n"
            + "          }\n";

    @Test
    public void testFilter() throws Exception {
        final ModelNode propDescr = ModelNode.fromString(FILTER_DESCRIPTION);
        assertTrue(propDescr.isDefined());

        final List<String> candidates = new ArrayList<>();

        int i;
        i = new ValueTypeCompleter(propDescr).complete(null, "{", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"all", "match"}), candidates);
        assertEquals(1, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "{all={", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"accept", "change-level", "deny",
            "level", "level-range"}), candidates);
        assertEquals(6, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "{all={change-level=", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"ALL", "CONFIG", "DEBUG",
            "ERROR", "FATAL", "FINE", "FINER", "FINEST", "INFO", "OFF",
            "SEVERE", "TRACE", "WARN", "WARNING"}), candidates);
        assertEquals(19, i);

        candidates.clear();
    }

    @Test
    public void testLoginModules() throws Exception {
        final ModelNode propDescr = ModelNode.fromString(loginModulesDescr);
        assertTrue(propDescr.isDefined());

        final List<String> candidates = new ArrayList<String>();

        int i;
        i = new ValueTypeCompleter(propDescr).complete(null, "", 0, candidates);
        assertEquals(Collections.singletonList("["), candidates);
        assertEquals(0, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"{"}), candidates);
        assertEquals(1, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"aa", "bb", "code", "flag", "module", "module-options"}), candidates);
        assertEquals(2, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{f", 0, candidates);
        assertEquals(Collections.singletonList("flag"), candidates);
        assertEquals(2, i);


        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{m", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"module", "module-options"}), candidates);
        assertEquals(2, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{module", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"module", "module-options"}), candidates);
        assertEquals(2, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{module=", 0, candidates);
        assertEquals(Collections.emptyList(), candidates);
        assertEquals(-1 /*7*/, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{module=m", 0, candidates);
        assertEquals(Collections.emptyList(), candidates);
        assertEquals(-1 /*7*/, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{flag = ", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"optional", "required", "requisite", "sufficient"}), candidates);
        assertEquals(8, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{flag= s", 0, candidates);
        assertEquals(Collections.singletonList("sufficient"), candidates);
        assertEquals(8, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{flag=requi", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"required", "requisite"}), candidates);
        assertEquals(7, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"false", "true"}), candidates);
        assertEquals(/*-1*/7, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=t", 0, candidates);
        assertEquals(Collections.singletonList("true"), candidates);
        assertEquals(/*-1*/7, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=Main", 0, candidates);
        assertEquals(Collections.emptyList(), candidates);
        assertEquals(-1 /*5*/, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=Main,", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"aa", "bb", "flag", "module", "module-options"}), candidates);
        assertEquals(12, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=Main,w", 0, candidates);
        assertEquals(Collections.emptyList(), candidates);
        assertEquals(-1 /*10*/, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=Main,module", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"module", "module-options"}), candidates);
        assertEquals(12, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=Main,fl", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"flag"}), candidates);
        assertEquals(12, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=Main,flag = ", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"optional", "required", "requisite", "sufficient"}), candidates);
        assertEquals(18, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=Main,flag = requi", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"required", "requisite"}), candidates);
        assertEquals(19, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=Main,flag = required", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"required"}), candidates);
        assertEquals(19, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=Main,flag = required,", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"aa", "bb", "module", "module-options"}), candidates);
        assertEquals(28, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=Main,flag = required,aa=", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"{"}), candidates);
        assertEquals(31, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=Main,flag = required,aa={", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"ab1", "ab2", "ac1"}), candidates);
        assertEquals(32, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=Main,flag = required,aa={ab", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"ab1", "ab2"}), candidates);
        assertEquals(32, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=Main,flag = required,aa={ab1=1,", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"ab2", "ac1"}), candidates);
        assertEquals(38, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=Main,flag = required,aa={ab1=1,a", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"ab2", "ac1"}), candidates);
        assertEquals(38, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=Main,flag = required,aa={ab1=1,ac", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"ac1"}), candidates);
        assertEquals(38, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=Main,flag = required,aa={ab1=1,ac1=", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"false", "true"}), candidates);
        assertEquals(/*36*/42, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=Main,flag = required,aa={ab1=1,ac1=s", 0, candidates);
        assertEquals(Collections.emptyList(), candidates);
        assertEquals(-1, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=Main,flag = required,aa={ab1=1,ac1=f", 0, candidates);
        assertEquals(Collections.singletonList("false"), candidates);
        assertEquals(42, i);

        //assertEquals(Arrays.asList(new String[]{","}), valueTypeHandler.getCandidates(valueType, "code=Main,flag = required,aa={ab1=1,ac1=2}"));
        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=Main,flag = required,aa={ab1=1,ac1=false", 0, candidates);
        assertEquals(Collections.singletonList("false"), candidates);
        assertEquals(42, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=Main,flag = required,aa={ab1=1,ac1=2,", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"ab2"}), candidates);
        assertEquals(44, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=Main,flag = required,aa={ab1=1,ac1=2},", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"bb", "module", "module-options"}), candidates);
        assertEquals(45, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=Main,flag = required,aa={ab1=1,ac1=2},bb=", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"["}), candidates);
        assertEquals(48, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=Main,flag = required,aa={ab1=1,ac1=2},bb=[{", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"bb1", "bb2", "bc1"}), candidates);
        assertEquals(50, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=\"UsersRoles\",flag=required,module-options=[(", 0, candidates);
        assertEquals(Collections.emptyList(), candidates);
        assertEquals(-1, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{}", 0, candidates);
        assertEquals(Arrays.asList(new String[]{",", "]"}), candidates);
        assertEquals(3, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=toto,flag=required}", 0, candidates);
        assertEquals(Arrays.asList(new String[]{",", "]"}), candidates);
        assertEquals(17, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=toto,flag=required},{", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"aa", "bb", "code", "flag", "module", "module-options"}), candidates);
        assertEquals(28, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=toto,flag=required},{f", 0, candidates);
        assertEquals(Collections.singletonList("flag"), candidates);
        assertEquals(28, i);


        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=toto,flag=required},{m", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"module", "module-options"}), candidates);
        assertEquals(28, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=toto,flag=required},{module", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"module", "module-options"}), candidates);
        assertEquals(28, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=toto,flag=required},{module=", 0, candidates);
        assertEquals(Collections.emptyList(), candidates);
        assertEquals(-1 /*7*/, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=toto,flag=required},{module=m", 0, candidates);
        assertEquals(Collections.emptyList(), candidates);
        assertEquals(-1 /*7*/, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=toto,flag=required},{flag = ", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"optional", "required", "requisite", "sufficient"}), candidates);
        assertEquals(34, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=toto,flag=required},{flag= s", 0, candidates);
        assertEquals(Collections.singletonList("sufficient"), candidates);
        assertEquals(34, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=toto,flag=required},{flag=requi", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"required", "requisite"}), candidates);
        assertEquals(33, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=toto,flag=required},{code=", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"false", "true"}), candidates);
        assertEquals(/*-1*/33, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=toto,flag=required},{code=t", 0, candidates);
        assertEquals(Collections.singletonList("true"), candidates);
        assertEquals(/*-1*/33, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=toto,flag=required},{code=Main", 0, candidates);
        assertEquals(Collections.emptyList(), candidates);
        assertEquals(-1 /*5*/, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=toto,flag=required},{code=Main,", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"aa", "bb", "flag", "module", "module-options"}), candidates);
        assertEquals(38, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=toto,flag=required},{code=Main,w", 0, candidates);
        assertEquals(Collections.emptyList(), candidates);
        assertEquals(-1 /*10*/, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=toto,flag=required},{code=Main,module", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"module", "module-options"}), candidates);
        assertEquals(38, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=toto,flag=required},{code=Main,fl", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"flag"}), candidates);
        assertEquals(38, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=toto,flag=required},{code=Main,flag = ", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"optional", "required", "requisite", "sufficient"}), candidates);
        assertEquals(44, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=toto,flag=required},{code=Main,flag = requi", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"required", "requisite"}), candidates);
        assertEquals(45, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=toto,flag=required},{code=Main,flag = required", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"required"}), candidates);
        assertEquals(45, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=toto,flag=required},{code=Main,flag = required,", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"aa", "bb", "module", "module-options"}), candidates);
        assertEquals(54, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=toto,flag=required},{code=Main,flag = required,aa=", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"{"}), candidates);
        assertEquals(57, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=toto,flag=required},{code=Main,flag = required,aa={", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"ab1", "ab2", "ac1"}), candidates);
        assertEquals(58, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=toto,flag=required},{code=Main,flag = required,aa={ab", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"ab1", "ab2"}), candidates);
        assertEquals(58, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=toto,flag=required},{code=Main,flag = required,aa={ab1=1,", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"ab2", "ac1"}), candidates);
        assertEquals(64, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=toto,flag=required},{code=Main,flag = required,aa={ab1=1,a", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"ab2", "ac1"}), candidates);
        assertEquals(64, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=toto,flag=required},{code=Main,flag = required,aa={ab1=1,ac", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"ac1"}), candidates);
        assertEquals(64, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=toto,flag=required},{code=Main,flag = required,aa={ab1=1,ac1=", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"false", "true"}), candidates);
        assertEquals(/*36*/68, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=toto,flag=required},{code=Main,flag = required,aa={ab1=1,ac1=s", 0, candidates);
        assertEquals(Collections.emptyList(), candidates);
        assertEquals(-1, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=toto,flag=required},{code=Main,flag = required,aa={ab1=1,ac1=f", 0, candidates);
        assertEquals(Collections.singletonList("false"), candidates);
        assertEquals(68, i);

        //assertEquals(Arrays.asList(new String[]{","}), valueTypeHandler.getCandidates(valueType, "code=Main,flag = required,aa={ab1=1,ac1=2}"));
        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=toto,flag=required},{code=Main,flag = required,aa={ab1=1,ac1=false", 0, candidates);
        assertEquals(Collections.singletonList("false"), candidates);
        assertEquals(68, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=toto,flag=required},{code=Main,flag = required,aa={ab1=1,ac1=2,", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"ab2"}), candidates);
        assertEquals(70, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=toto,flag=required},{code=Main,flag = required,aa={ab1=1,ac1=2},", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"bb", "module", "module-options"}), candidates);
        assertEquals(71, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=toto,flag=required},{code=Main,flag = required,aa={ab1=1,ac1=2},bb=", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"["}), candidates);
        assertEquals(74, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=toto,flag=required},{code=Main,flag = required,aa={ab1=1,ac1=2},bb=[{", 0, candidates);
        assertEquals(Arrays.asList(new String[]{"bb1", "bb2", "bc1"}), candidates);
        assertEquals(76, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=toto,flag=required},{code=\"UsersRoles\",flag=required,module-options=[(", 0, candidates);
        assertEquals(Collections.emptyList(), candidates);
        assertEquals(-1, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=toto,flag=required},{code=\"UsersRoles\",flag=required,module-options=[{", 0, candidates);
        assertEquals(Collections.emptyList(), candidates);
        assertEquals(-1, i);

        candidates.clear();
        i = new ValueTypeCompleter(propDescr).complete(null, "[{code=toto,flag=required},{code=\"UsersRoles\",flag=required}]", 0, candidates);
        assertEquals(Collections.emptyList(), candidates);
        assertEquals(-1, i);
    }

    @Test
    public void testFileSystem() throws Exception {
        String radical = "valuetype-" + System.currentTimeMillis() + "-test";
        File f = new File(radical + ".txt");
        f.createNewFile();
        try {
            CommandContext ctx = CommandContextFactory.getInstance().newCommandContext();
            ModelNode valueType = ModelNode.fromJSONString(VALUETYPE_WITH_FILES);
            ValueTypeCompleter completer = new ValueTypeCompleter(valueType);
            {
                List<String> candidates = new ArrayList<>();
                String content = "{p1_a=" + radical;
                new ValueTypeCompleter(valueType).complete(ctx, content, content.length() - 1, candidates);
                assertTrue(candidates.size() == 1);
                assertTrue(candidates.get(0).equals(f.getName()));
            }

            {
                List<String> candidates = new ArrayList<>();
                String content = "{p1_a=toto, p2_a=" + radical;
                new ValueTypeCompleter(valueType).complete(ctx, content, content.length() - 1, candidates);
                assertTrue(candidates.size() == 1);
                assertTrue(candidates.get(0).equals(f.getName()));
            }

            {
                List<String> candidates = new ArrayList<>();
                String content = "{p3=[ { oo_file_a=" + radical;
                new ValueTypeCompleter(valueType).complete(ctx, content, content.length() - 1, candidates);
                assertTrue(candidates.size() == 1);
                assertTrue(candidates.get(0).equals(f.getName()));
            }

            {
                List<String> candidates = new ArrayList<>();
                String content = "{p3=[ { ii_file=" + radical;
                new ValueTypeCompleter(valueType).complete(ctx, content, content.length() - 1, candidates);
                assertTrue(candidates.isEmpty());
            }

            {
                List<String> candidates = new ArrayList<>();
                String content = "{p3=[ { oo_file_a=toto, ii_file=titi }, { oo_file_a=" + radical;
                new ValueTypeCompleter(valueType).complete(ctx, content, content.length() - 1, candidates);
                assertTrue(candidates.size() == 1);
                assertTrue(candidates.get(0).equals(f.getName()));
            }

            {
                List<String> candidates = new ArrayList<>();
                String content = "{p4= { ii_file=" + radical;
                new ValueTypeCompleter(valueType).complete(ctx, content, content.length() - 1, candidates);
                assertTrue(candidates.isEmpty());
            }
        } finally {
            f.delete();
        }
    }
}
