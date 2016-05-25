package org.wickedsource.diffparser.api;

import org.junit.Assert;
import org.junit.Test;
import org.wickedsource.diffparser.api.model.Diff;
import org.wickedsource.diffparser.api.model.Hunk;
import org.wickedsource.diffparser.api.model.Line;

import java.io.InputStream;
import java.util.List;

public class GitHubDiffParserTest {

    @Test
    public void testParsingGitHubOutput() throws Exception {
        // given
        DiffParser parser = new GitHubDiffParser();
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
        Assert.assertEquals(2, headerLines.size());

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
    
}
