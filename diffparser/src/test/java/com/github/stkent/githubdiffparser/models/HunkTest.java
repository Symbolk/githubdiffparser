package com.github.stkent.githubdiffparser.models;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
    
}
