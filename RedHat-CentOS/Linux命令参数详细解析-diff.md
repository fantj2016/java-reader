###diff
test
```
redhat test
diff-test
~                                                                                      
~            
```
test2
```
redhat test2
diff-test
~                                                                                      
~            
```
diff test test2
![diff .png](http://upload-images.jianshu.io/upload_images/5786888-4aa022b7dc7e01a3.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
* 功能说明：比较文件的差异。
* 语 法：diff [-abBcdefHilnNpPqrstTuvwy][-<行数>][-C <行数>][-D <巨集名称>][-I <
字符或字符串>][-S <文件>][-W <宽度>][-x <文件或目录>][-X <文
件>][--help][--left-column][--suppress-common-line][文件或目录 1][文件或目录 2]
补充说明：diff 以逐行的方式，比较文本文件的异同处。所是指定要比较目录，则 diff 会比
较目录中相同文件名的文件，但不会比较其中子目录。
* 参 数：
-<行数> 指定要显示多少行的文本。此参数必须与-c 或-u 参数一并使用。
-a 或--text diff 预设只会逐行比较文本文件。
-b 或--ignore-space-change 不检查空格字符的不同。
-B 或--ignore-blank-lines 不检查空白行。
-c 显示全部内文，并标出不同之处。 
-C<行数>或--context<行数> 与执行"-c-<行数>"指令相同。
-d 或--minimal 使用不同的演算法，以较小的单位来做比较。
-D<巨集名称>或 ifdef<巨集名称> 此参数的输出格式可用于前置处理器巨集。
-e 或--ed 此参数的输出格式可用于 ed 的 script 文件。
-f 或-forward-ed 输出的格式类似 ed 的 script 文件，但按照原来文件的顺序来显示不同
处。
-H 或--speed-large-files 比较大文件时，可加快速度。
-l<字符或字符串>或--ignore-matching-lines<字符或字符串> 若两个文件在某几行有所
不同，而这几行同时都包含了选项中指定的字符或字符串，则不显示这两个文件的差异。
-i 或--ignore-case 不检查大小写的不同。
-l 或--paginate 将结果交由 pr 程序来分页。
-n 或--rcs 将比较结果以 RCS 的格式来显示。
-N 或--new-file 在比较目录时，若文件 A 仅出现在某个目录中，预设会显示：
Only in 目录：文件 A 若使用-N 参数，则 diff 会将文件 A 与一个空白的文件比较。
-p 若比较的文件为 C 语言的程序码文件时，显示差异所在的函数名称。
-P 或--unidirectional-new-file 与-N 类似，但只有当第二个目录包含了一个第一个目录
所没有的文件时，才会将这个文件与空白的文件做比较。
-q 或--brief 仅显示有无差异，不显示详细的信息。
-r 或--recursive 比较子目录中的文件。
-s 或--report-identical-files 若没有发现任何差异，仍然显示信息。
-S<文件>或--starting-file<文件> 在比较目录时，从指定的文件开始比较。
-t 或--expand-tabs 在输出时，将 tab 字符展开。
-T 或--initial-tab 在每行前面加上 tab 字符以便对齐。
-u,-U<列数>或--unified=<列数> 以合并的方式来显示文件内容的不同。
-v 或--version 显示版本信息。
-w 或--ignore-all-space 忽略全部的空格字符。
-W<宽度>或--width<宽度> 在使用-y 参数时，指定栏宽。
-x<文件名或目录>或--exclude<文件名或目录> 不比较选项中所指定的文件或目录。
-X<文件>或--exclude-from<文件> 您可以将文件或目录类型存成文本文件，然后在=<文件>
中指定此文本文件。
-y 或--side-by-side 以并列的方式显示文件的异同之处。
--help 显示帮助。
--left-column 在使用-y 参数时，若两个文件某一行内容相同，则仅在左侧的栏位显示该
行内容。
--suppress-common-lines 在使用-y 参数时，仅显示不同之处。


####diffstat(differential status)
* 功能说明：根据 diff 的比较结果，显示统计数字。
* 语 法：diff [-wV][-n <文件名长度>][-p <文件名长度>]
* 补充说明：diffstat 读取 diff 的输出结果，然后统计各文件的插入，删除，修改等差异计量。
* 参 数：
-n<文件名长度> 指定文件名长度，指定的长度必须大于或等于所有文件中最长的文件名。
-p<文件名长度> 与-n 参数相同，但此处的<文件名长度>包括了文件的路径。 
-w 指定输出时栏位的宽度。
-V 显示版本信息。
