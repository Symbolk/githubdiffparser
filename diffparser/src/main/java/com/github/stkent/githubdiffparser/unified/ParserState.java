/**
 *    Copyright 2013-2015 Tom Hombergs (tom.hombergs@gmail.com | http://wickedsource.org)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.github.stkent.githubdiffparser.unified;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.stkent.githubdiffparser.unified.Constants.HUNK_START_PATTERN;

/**
 * State machine for a parser parsing a unified diff.
 *
 * @author Tom Hombergs <tom.hombergs@gmail.com>
 */
@SuppressWarnings("Duplicates")
public enum ParserState {

    /**
     * This is the initial state of the parser.
     */
    INITIAL {
        @Override
        public ParserState nextState(ParseWindow window) {
            String line = window.getFocusLine();
            
            if (matchesFromFilePattern(line)) {
                return transition(line, FROM_FILE);
            } else {
                return transition(line, HEADER);
            }
        }
    },

    /**
     * The parser is in this state if it is currently parsing a header line.
     */
    HEADER {
        @Override
        public ParserState nextState(ParseWindow window) {
            String line = window.getFocusLine();
            
            if (matchesFromFilePattern(line)) {
                return transition(line, FROM_FILE);
            } else {
                return transition(line, HEADER);
            }
        }
    },

    /**
     * The parser is in this state if it is currently parsing the line containing the "from" file.
     * <p/>
     * Example line:<br/>
     * {@code --- /path/to/file.txt}
     */
    FROM_FILE {
        @Override
        public ParserState nextState(ParseWindow window) {
            String line = window.getFocusLine();
            
            if (matchesToFilePattern(line)) {
                return transition(line, TO_FILE);
            } else {
                throw new IllegalStateException("A FROM_FILE line ('---') must be directly followed by a TO_FILE line ('+++')!");
            }
        }
    },

    /**
     * The parser is in this state if it is currently parsing the line containing the "to" file.
     * <p/>
     * Example line:<br/>
     * {@code +++ /path/to/file.txt}
     */
    TO_FILE {
        @Override
        public ParserState nextState(ParseWindow window) {
            String line = window.getFocusLine();
            
            if (matchesHunkStartPattern(line)) {
                return transition(line, HUNK_START);
            } else {
                throw new IllegalStateException("A TO_FILE line ('+++') must be directly followed by a HUNK_START line ('@@')!");
            }
        }
    },

    /**
     * The parser is in this state if it is currently parsing a line containing the header of a hunk.
     * <p/>
     * Example line:<br/>
     * {@code @@ -1,5 +2,6 @@}
     */
    HUNK_START {
        @Override
        public ParserState nextState(ParseWindow window) {
            String line = window.getFocusLine();
            
            if (matchesFromLinePattern(line)) {
                return transition(line, FROM_LINE);
            } else if (matchesToLinePattern(line)) {
                return transition(line, TO_LINE);
            } else {
                return transition(line, NEUTRAL_LINE);
            }
        }
    },

    /**
     * The parser is in this state if it is currently parsing a line containing a line that is in the first file,
     * but not the second (a "from" line).
     * <p/>
     * Example line:<br/>
     * {@code - only the dash at the start is important}
     */
    FROM_LINE {
        @Override
        public ParserState nextState(ParseWindow window) {
            String line = window.getFocusLine();

            if (matchesFromLinePattern(line)) {
                return transition(line, FROM_LINE);
            } else if (matchesToLinePattern(line)) {
                return transition(line, TO_LINE);
            } else if (matchesNeutralLinePattern(line) || matchesNoNewlineAtEndOfFileLinePattern(line)) {
                return transition(line, NEUTRAL_LINE);
            } else if (matchesHunkStartPattern(line)) {
                return transition(line, HUNK_START);
            } else if (matchesDelimiterPattern(line)) {
                return transition(line, DELIMITER);
            } else {
                if (matchesFromFilePattern(line)) {
                    return transition(line, FROM_FILE);
                } else {
                    return transition(line, HEADER);
                }
            }
        }
    },

    /**
     * The parser is in this state if it is currently parsing a line containing a line that is in the second file,
     * but not the first (a "to" line).
     * <p/>
     * Example line:<br/>
     * {@code + only the plus at the start is important}
     */
    TO_LINE {
        @Override
        public ParserState nextState(ParseWindow window) {
            String line = window.getFocusLine();

            if (matchesFromLinePattern(line)) {
                return transition(line, FROM_LINE);
            } else if (matchesToLinePattern(line)) {
                return transition(line, TO_LINE);
            } else if (matchesNeutralLinePattern(line) || matchesNoNewlineAtEndOfFileLinePattern(line)) {
                return transition(line, NEUTRAL_LINE);
            } else if (matchesHunkStartPattern(line)) {
                return transition(line, HUNK_START);
            } else if (matchesDelimiterPattern(line)) {
                return transition(line, DELIMITER);
            } else {
                if (matchesFromFilePattern(line)) {
                    return transition(line, FROM_FILE);
                } else {
                    return transition(line, HEADER);
                }
            }
        }
    },

    /**
     * The parser is in this state if it is currently parsing a line that is contained in both files (a "neutral" line). This line can
     * contain any string.
     */
    NEUTRAL_LINE {
        @Override
        public ParserState nextState(ParseWindow window) {
            String line = window.getFocusLine();
            
            if (matchesFromLinePattern(line)) {
                return transition(line, FROM_LINE);
            } else if (matchesToLinePattern(line)) {
                return transition(line, TO_LINE);
            } else if (matchesNeutralLinePattern(line) || matchesNoNewlineAtEndOfFileLinePattern(line)) {
                return transition(line, NEUTRAL_LINE);
            } else if (matchesHunkStartPattern(line)) {
                return transition(line, HUNK_START);
            } else if (matchesDelimiterPattern(line)) {
                return transition(line, DELIMITER);
            } else {
                if (matchesFromFilePattern(line)) {
                    return transition(line, FROM_FILE);
                } else {
                    return transition(line, HEADER);
                }
            }
        }
    },

    /**
     * The parser is in this state if it is currently parsing a line that is the delimiter between two Diffs.
     * Assumption: there is at most one delimiter line between diffs.
     */
    DELIMITER {
        @Override
        public ParserState nextState(ParseWindow window) {
            String line = window.getFocusLine();
            
            return transition(line, INITIAL);
        }
    };

    protected static Logger logger = LoggerFactory.getLogger(ParserState.class);

    /**
     * Returns the next state of the state machine depending on the current state and the content of a window of lines around the line
     * that is currently being parsed.
     *
     * @param window the window around the line currently being parsed.
     * @return the next state of the state machine.
     */
    public abstract ParserState nextState(ParseWindow window);

    protected ParserState transition(final String currentLine, final ParserState toState) {
        logger.debug(String.format("%12s -> %12s: %s", this, toState, currentLine));
        return toState;
    }

    protected boolean matchesFromFilePattern(String line) {
        return line.startsWith("---");
    }

    protected boolean matchesToFilePattern(String line) {
        return line.startsWith("+++");
    }

    protected boolean matchesFromLinePattern(String line) {
        return line.startsWith("-");
    }

    protected boolean matchesToLinePattern(String line) {
        return line.startsWith("+");
    }
    
    protected boolean matchesNeutralLinePattern(String line) {
        return line.startsWith(" ");
    }
    
    protected boolean matchesDelimiterPattern(String line) {
        return line.isEmpty();
    }

    protected boolean matchesNoNewlineAtEndOfFileLinePattern(String line) {
        return line.contains("\\ No newline at end of file");
    }

    protected boolean matchesHunkStartPattern(String line) {
        return HUNK_START_PATTERN.matcher(line).matches();
    }

}
