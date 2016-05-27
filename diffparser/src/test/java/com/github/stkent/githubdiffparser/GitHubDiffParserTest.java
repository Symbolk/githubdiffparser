/**
 * Copyright 2016 Stuart Kent
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

/**
 * Copyright 2016 Stuart Kent
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.github.stkent.githubdiffparser;

import com.github.stkent.githubdiffparser.models.Diff;
import com.github.stkent.githubdiffparser.models.Hunk;
import com.github.stkent.githubdiffparser.models.Line;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

public class GitHubDiffParserTest {

    @Test
    public void testParsingActualDiff() throws Exception {
        // given
        GitHubDiffParser parser = new GitHubDiffParser();
        InputStream in = getClass().getResourceAsStream("github.diff");

        // when
        List<Diff> diffs = parser.parse(in);

        // then
        Assert.assertNotNull(diffs);
        Assert.assertEquals(4, diffs.size());

        Diff diff1 = diffs.get(0);
        Assert.assertEquals(".travis.yml", diff1.getFromFileName());
        Assert.assertEquals(".travis.yml", diff1.getToFileName());
        Assert.assertEquals(1, diff1.getHunks().size());

        List<String> headerLines = diff1.getHeaderLines();
        Assert.assertEquals(1, headerLines.size());

        Hunk hunk1 = diff1.getHunks().get(0);
        Assert.assertEquals(4, hunk1.getFromFileRange().getLineStart());
        Assert.assertEquals(6, hunk1.getFromFileRange().getLineCount());
        Assert.assertEquals(4, hunk1.getToFileRange().getLineStart());
        Assert.assertEquals(10, hunk1.getToFileRange().getLineCount());

        List<Line> lines = hunk1.getLines();
        Assert.assertEquals(10, lines.size());
        Assert.assertEquals(Line.LineType.TO, lines.get(3).getLineType());
        Assert.assertEquals(Line.LineType.NEUTRAL, lines.get(7).getLineType());
        Assert.assertEquals(Line.LineType.NEUTRAL, lines.get(8).getLineType());
    }
    
    @Test
    public void testParse_WhenHunkRangeLineCountNotSpecified_ShouldSetHunkRangeLineCountToOne() throws Exception {
        // given
        GitHubDiffParser parser = new GitHubDiffParser();
        String in = ""
                + "diff --git a/.file.txt b/.file.txt\n"
                + "index 6f8e7fa..ab40505 100644\n"
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
        GitHubDiffParser parser = new GitHubDiffParser();
        String in = ""
                + "diff --git a/.file.txt b/.file.txt\n"
                + "index 6f8e7fa..ab40505 100644\n"
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
