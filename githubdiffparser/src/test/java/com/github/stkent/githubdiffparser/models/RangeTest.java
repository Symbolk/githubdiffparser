package com.github.stkent.githubdiffparser.models;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RangeTest {
    
    @Test
    public void testContains() {
        final int rangeStart = 3;
        final int rangeCount = 5;
        
        final Range range = new Range(3, 5);

        assertFalse(range.contains(rangeStart - 1));

        for (int lineNumber = rangeStart; lineNumber < rangeStart + rangeCount; lineNumber++) {
            assertTrue(range.contains(lineNumber));
        }

        assertFalse(range.contains(rangeStart + rangeCount));
    }
    
}
