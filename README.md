### githubdiffparser

[ ![Download](https://api.bintray.com/packages/stkent/java-libraries/githubdiffparser/images/download.svg) ](https://bintray.com/stkent/java-libraries/githubdiffparser/_latestVersion)

Parse GitHub diffs with Java. Modified fork of [diffparser](https://github.com/thombergs/diffparser) v1.2. Version numbers are now maintained independently.

### Getting Started

1. Specify githubdiffparser as a dependency in your build.gradle file:

    ```groovy
    repositories {
        jcenter()
    }

    dependencies {
        compile 'com.github.stkent:githubdiffparser:1.0.1'
    }
    ```

2. Parse GitHub diffs!

    ```
    GitHubDiffParser parser = new GitHubDiffParser();
    InputStream in = new FileInputStream("/path/to/file.diff");
    List<Diff> diff = parser.parse(in);
    ```

### GitHub diff format

_// TODO: give an example; explain how GitHub diffs appear to deviate from the unified diff spec._

The most detailed description of the unified diff format may be found [here](http://www.gnu.org/software/diffutils/manual/html_node/Detailed-Unified.html#Detailed-Unified):

> The unified output format starts with a two-line header, which looks like this:
>
>     --- from-file from-file-modification-time
>     +++ to-file to-file-modification-time
>The time stamp looks like `2002-02-21 23:30:39.942229878 -0800` to indicate the date, time with fractional seconds, and time zone. The fractional seconds are omitted on hosts that do not support fractional time stamps.
>
>You can change the header's content with the `--label=label` option. See [Alternate Names](http://www.gnu.org/software/diffutils/manual/html_node/Alternate-Names.html#Alternate-Names).
>
>Next come one or more hunks of differences; each hunk shows one area where the files differ. Unified format hunks look like this:
>
>     @@ from-file-line-numbers to-file-line-numbers @@
>      line-from-either-file
>      line-from-either-file...
>If a hunk contains just one line, only its start line number appears. Otherwise its line numbers look like ‘start,count’. An empty hunk is considered to start at the line that follows the hunk.

>If a hunk and its context contain two or more lines, its line numbers look like ‘start,count’. Otherwise only its end line number appears. An empty hunk is considered to end at the line that precedes the hunk.
>
>The lines common to both files begin with a space character. The lines that actually differ between the two files have one of the following indicator characters in the left print column:
>
> `+`: a line was added here to the first file.
> 
> `-`: a line was removed here from the first file.
