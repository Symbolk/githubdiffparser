package com.github.stkent.githubdiffparser.models;

import org.junit.Test;

import java.util.ArrayList;

import static com.github.stkent.githubdiffparser.models.Line.LineType.FROM;
import static com.github.stkent.githubdiffparser.models.Line.LineType.NEUTRAL;
import static com.github.stkent.githubdiffparser.models.Line.LineType.TO;
import static org.junit.Assert.*;

public class HunkTest {
    
    @Test
    public void testContains() {
        final int toFileRangeStart = 3;
        final int toFileRangeCount = 5;

        final Hunk hunk = new Hunk();
        hunk.setToFileRange(new Range(toFileRangeStart, toFileRangeCount));
        
        assertFalse(hunk.containsToFileLineNumber(toFileRangeStart - 1));

        for (int lineNumber = toFileRangeStart; lineNumber < toFileRangeStart + toFileRangeCount; lineNumber++) {
            assertTrue(hunk.containsToFileLineNumber(lineNumber));
        }

        assertFalse(hunk.containsToFileLineNumber(toFileRangeStart + toFileRangeCount));
    }

    @Test
    public void testGetCorrectHunkLineNumberCase2() {
        final int toFileRangeStart = 27;
        final int toFileRangeCount = 8;

        final Hunk hunk = new Hunk();
        hunk.setToFileRange(new Range(toFileRangeStart, toFileRangeCount));
        hunk.setLines(new ArrayList<Line>() {{
            add(new Line(TO,       "1"));
            add(new Line(NEUTRAL,  "2"));
            add(new Line(FROM,     "3"));
            add(new Line(FROM,     "4"));
            add(new Line(NEUTRAL,  "5"));
            add(new Line(NEUTRAL,  "6"));
            add(new Line(FROM,     "7"));
            add(new Line(TO,       "8"));
            add(new Line(NEUTRAL,  "9"));
            add(new Line(NEUTRAL, "10"));
            add(new Line(NEUTRAL, "11"));
        }});

        assert hunk.getLines()
                .stream()
                .filter(line -> line.getLineType() != FROM)
                .count()
                        == toFileRangeCount;

        final int targetToFileLineNumber = 32;

        //noinspection ConstantConditions
        assert toFileRangeStart <= targetToFileLineNumber;

        //noinspection ConstantConditions
        assert targetToFileLineNumber < toFileRangeStart + toFileRangeCount;

        final int expectedHunkLineNumber = 9;
        final int actualHunkLineNumber = hunk.getHunkLineNumberForToFileLineNumber(targetToFileLineNumber);
        assertEquals(expectedHunkLineNumber, actualHunkLineNumber);
    }
    
}
