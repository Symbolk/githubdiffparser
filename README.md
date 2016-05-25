### diffparser

Parse diffs with Java.

### Code example

For a diff in [Unified format](https://en.wikipedia.org/wiki/Diff_utility#Unified_format):

```
DiffParser parser = new UnifiedDiffParser();
InputStream in = new FileInputStream("/path/to/file.diff");
List<Diff> diff = parser.parse(in);
```

### Maven coordinates
TODO: update

### What Diff formats can be parsed?
Currently, the only implementation of the DiffParser interface is UnifiedDiffParser, which supports parsing of diffs like the following:
```diff
Modified: trunk/test1.txt
===================================================================
--- /trunk/test1.txt	2013-10-23 19:41:56 UTC (rev 46)
+++ /trunk/test1.txt	2013-10-23 19:44:39 UTC (rev 47)
@@ -1,4 +1,3 @@
 test1
-test1
+test234
 
-test1
\ No newline at end of file
@@ -5,9 +6,10 @@
-test1
-test1
+aösdhasd
+asdasd
```

An input stream may contain several sections like the above, delimited by an empty line. Each such section will be parsed into an object
of class Diff.

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
