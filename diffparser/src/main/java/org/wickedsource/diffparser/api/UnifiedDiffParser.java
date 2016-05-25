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
package org.wickedsource.diffparser.api;

import org.wickedsource.diffparser.api.model.Diff;
import org.wickedsource.diffparser.api.model.Hunk;
import org.wickedsource.diffparser.api.model.Line;
import org.wickedsource.diffparser.api.model.Range;
import org.wickedsource.diffparser.unified.ParserState;
import org.wickedsource.diffparser.unified.ResizingParseWindow;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;
import static org.wickedsource.diffparser.unified.Constants.HUNK_START_PATTERN;
import static org.wickedsource.diffparser.unified.ParserState.*;

@SuppressWarnings("WeakerAccess")
public class UnifiedDiffParser implements DiffParser {
    
    @Override
    public List<Diff> parse(InputStream in) {
        ResizingParseWindow window = new ResizingParseWindow(in);
        ParserState state = ParserState.INITIAL;
        ParserState targetState;
        List<Diff> parsedDiffs = new ArrayList<>();
        Diff currentDiff = new Diff();
        String currentLine;

        while ((currentLine = window.slideForward()) != null) {
            targetState = state.nextState(window);
            
            switch (targetState) {
                case INITIAL:
                    // nothing to do
                    break;
                case HEADER:
                    if (asList(FROM_LINE, TO_LINE, NEUTRAL_LINE).contains(state)) {
                        parsedDiffs.add(currentDiff);
                        currentDiff = new Diff();
                    }
                    
                    parseHeader(currentDiff, currentLine);
                    break;
                case FROM_FILE:
                    if (asList(FROM_LINE, TO_LINE, NEUTRAL_LINE).contains(state)) {
                        parsedDiffs.add(currentDiff);
                        currentDiff = new Diff();
                    }
                    
                    parseFromFile(currentDiff, currentLine);
                    break;
                case TO_FILE:
                    parseToFile(currentDiff, currentLine);
                    break;
                case HUNK_START:
                    parseHunkStart(currentDiff, currentLine);
                    break;
                case FROM_LINE:
                    parseFromLine(currentDiff, currentLine);
                    break;
                case TO_LINE:
                    parseToLine(currentDiff, currentLine);
                    break;
                case NEUTRAL_LINE:
                    parseNeutralLine(currentDiff, currentLine);
                    break;
                case DELIMITER:
                    parsedDiffs.add(currentDiff);
                    currentDiff = new Diff();
                    break;
                default:
                    throw new IllegalStateException(String.format("Illegal parser state '%s", state));
            }
            
            state = targetState;
        }

        return parsedDiffs;
    }

    protected void parseNeutralLine(Diff currentDiff, String currentLine) {
        Line line = new Line(Line.LineType.NEUTRAL, currentLine);
        currentDiff.getLatestHunk().getLines().add(line);
    }

    protected void parseToLine(Diff currentDiff, String currentLine) {
        Line toLine = new Line(Line.LineType.TO, currentLine.substring(1));
        currentDiff.getLatestHunk().getLines().add(toLine);
    }

    protected void parseFromLine(Diff currentDiff, String currentLine) {
        Line fromLine = new Line(Line.LineType.FROM, currentLine.substring(1));
        currentDiff.getLatestHunk().getLines().add(fromLine);
    }

    protected void parseHunkStart(Diff currentDiff, String currentLine) {
        Matcher matcher = HUNK_START_PATTERN.matcher(currentLine);
        
        if (matcher.matches()) {
            String range1Start = matcher.group(1);
            String range1Count = (matcher.group(2) != null) ? matcher.group(2) : "1";
            Range fromRange = new Range(Integer.valueOf(range1Start), Integer.valueOf(range1Count));

            String range2Start = matcher.group(3);
            String range2Count = (matcher.group(4) != null) ? matcher.group(4) : "1";
            Range toRange = new Range(Integer.valueOf(range2Start), Integer.valueOf(range2Count));

            Hunk hunk = new Hunk();
            hunk.setFromFileRange(fromRange);
            hunk.setToFileRange(toRange);
            currentDiff.getHunks().add(hunk);
        } else {
            throw new IllegalStateException(String.format("No line ranges found in the following hunk start line: '%s'. Expected something " +
                    "like '-1,5 +3,5'.", currentLine));
        }
    }

    protected void parseToFile(Diff currentDiff, String currentLine) {
        currentDiff.setToFileName(cutAfterTab(currentLine.substring(4)));
    }

    protected void parseFromFile(Diff currentDiff, String currentLine) {
        currentDiff.setFromFileName(cutAfterTab(currentLine.substring(4)));
    }

    /**
     * Cuts a TAB and all following characters from a String.
     */
    protected String cutAfterTab(String line) {
        Pattern p = Pattern.compile("^(.*)\\t.*$");
        Matcher matcher = p.matcher(line);
        if (matcher.matches()) {
            return matcher.group(1);
        } else {
            return line;
        }
    }

    protected void parseHeader(Diff currentDiff, String currentLine) {
        currentDiff.getHeaderLines().add(currentLine);
    }

    @Override
    public List<Diff> parse(byte[] bytes) {
        return parse(new ByteArrayInputStream(bytes));
    }

    @Override
    public List<Diff> parse(File file) throws IOException {
        return parse(new FileInputStream(file));
    }

}
