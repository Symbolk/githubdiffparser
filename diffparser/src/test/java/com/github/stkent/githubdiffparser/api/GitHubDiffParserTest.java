package com.github.stkent.githubdiffparser.api;

import com.github.stkent.githubdiffparser.api.model.Diff;
import com.github.stkent.githubdiffparser.api.model.Hunk;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class GitHubDiffParserTest {
    
    @Test
    public void testParse_WhenHunkRangeLineCountNotSpecified_ShouldSetHunkRangeLineCountToOne() throws Exception {
        // given
        DiffParser parser = new GitHubDiffParser();
        String in = ""
                + "--- from	2015-12-21 17:53:29.082877088 -0500\n"
                + "+++ to	2015-12-21 08:41:52.663714666 -0500\n"
                + "@@ -10 +10 @@\n"
                + "-from\n"
                + "+to\n"
                + "\n";

        // when
        List<Diff> diffs = parser.parse(in.getBytes());

        // then
        Assert.assertNotNull(diffs);
        Assert.assertEquals(1, diffs.size());

        Diff diff1 = diffs.get(0);
        Assert.assertEquals(1, diff1.getHunks().size());

        Hunk hunk1 = diff1.getHunks().get(0);
        Assert.assertEquals(1, hunk1.getFromFileRange().getLineCount());
        Assert.assertEquals(1, hunk1.getToFileRange().getLineCount());
    }

    @Test
    public void testParse_WhenInputDoesNotEndWithEmptyLine_ShouldTransitionToEndState() throws Exception {
        // given
        DiffParser parser = new GitHubDiffParser();
        String in = ""
                + "--- from	2015-12-21 17:53:29.082877088 -0500\n"
                + "+++ to	2015-12-21 08:41:52.663714666 -0500\n"
                + "@@ -10,1 +10,1 @@\n"
                + "-from\n"
                + "+to\n";

        // when
        List<Diff> diffs = parser.parse(in.getBytes());

        // then
        Assert.assertNotNull(diffs);
        Assert.assertEquals(1, diffs.size());

        Diff diff1 = diffs.get(0);
        Assert.assertEquals(1, diff1.getHunks().size());
    }
    
}
