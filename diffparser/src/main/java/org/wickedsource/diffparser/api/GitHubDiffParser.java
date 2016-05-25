package org.wickedsource.diffparser.api;

import org.wickedsource.diffparser.api.model.Diff;

@SuppressWarnings("WeakerAccess")
public class GitHubDiffParser extends UnifiedDiffParser {

    @Override
    protected void parseFromFile(final Diff currentDiff, final String currentLine) {
        String fileName = cutAfterTab(currentLine.substring(4)).trim();
        
        /* 
         * GitHub diff "from file" rows include an a/ prefix. We remove this to compute the actual (relative) path to
         * the file.
         */
        if (fileName.startsWith("a/")) {
            fileName = fileName.substring(2);
        }
        
        currentDiff.setFromFileName(fileName);
    }

    @Override
    protected void parseToFile(final Diff currentDiff, final String currentLine) {
        String fileName = cutAfterTab(currentLine.substring(4)).trim();
        
        /* 
         * GitHub diff "to file" rows include a b/ prefix. We remove this to compute the actual (relative) path to the
         * file.
         */
        if (fileName.startsWith("b/")) {
            fileName = fileName.substring(2);
        }

        currentDiff.setToFileName(fileName);
    }
    
}
