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
    public void testGetCorrectHunkLineNumberCase1() {
        final int toFileRangeStart = 3;
        final int toFileRangeCount = 5;

        final Hunk hunk = new Hunk();
        hunk.setToFileRange(new Range(toFileRangeStart, toFileRangeCount));
        hunk.setLines(new ArrayList<Line>() {{
            add(new Line(TO, "line3"));
            add(new Line(TO, "line4"));
            add(new Line(TO, "line5"));
            add(new Line(TO, "line6"));
            add(new Line(TO, "line7"));
        }});

        assert hunk.getLines().size() == hunk.getToFileRange().getLineCount();
        
        final int targetToFileLineNumber = 6;

        //noinspection ConstantConditions
        assert toFileRangeStart <= targetToFileLineNumber;

        //noinspection ConstantConditions
        assert targetToFileLineNumber < toFileRangeStart + toFileRangeCount;

        final int expectedHunkLineNumber = 4;
        final int actualHunkLineNumber = hunk.getHunkLineNumberForToFileLineNumber(targetToFileLineNumber);
        assertEquals(expectedHunkLineNumber, actualHunkLineNumber);
    }

    @Test
    public void testGetCorrectHunkLineNumberCase2() {
        final int toFileRangeStart = 3;
        final int toFileRangeCount = 5;

        final Hunk hunk = new Hunk();
        hunk.setToFileRange(new Range(toFileRangeStart, toFileRangeCount));
        hunk.setLines(new ArrayList<Line>() {{
            add(new Line(FROM, "fromline1"));
            add(new Line(TO, "toline3"));
            add(new Line(TO, "toline4"));
            add(new Line(NEUTRAL, "neutralline9"));
            add(new Line(NEUTRAL, "neutralline10"));
            add(new Line(TO, "toline5"));
            add(new Line(FROM, "fromline2"));
            add(new Line(TO, "toline6"));
            add(new Line(NEUTRAL, "neutralline11"));
            add(new Line(TO, "toline7"));
            add(new Line(NEUTRAL, "neutralline12"));
        }});

        assert hunk.getLines()
                .stream()
                .filter(line -> line.getLineType() == TO)
                .count()
                        == toFileRangeCount;

        final int targetToFileLineNumber = 6;

        //noinspection ConstantConditions
        assert toFileRangeStart <= targetToFileLineNumber;

        //noinspection ConstantConditions
        assert targetToFileLineNumber < toFileRangeStart + toFileRangeCount;

        final int expectedHunkLineNumber = 8;
        final int actualHunkLineNumber = hunk.getHunkLineNumberForToFileLineNumber(targetToFileLineNumber);
        assertEquals(expectedHunkLineNumber, actualHunkLineNumber);
    }
    
}
