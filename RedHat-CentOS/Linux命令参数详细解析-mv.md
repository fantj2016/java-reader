* 用法：mv [选项]... [-T] 源文件 目标文件
　或：mv [选项]... 源文件... 目录
　或：mv [选项]... -t 目录 源文件...
Rename SOURCE to DEST, or move SOURCE(s) to DIRECTORY.

* --backup[=CONTROL]       为每个已存在的目标文件创建备份
*  -b                           类似--backup 但不接受参数
*  -f, --force                  覆盖前不询问
*  -i, --interactive            覆盖前询问
*  -n, --no-clobber             不覆盖已存在文件
**如果您指定了-i、-f、-n 中的多个，仅最后一个生效。**
      --strip-trailing-slashes	去掉每个源文件参数尾部的斜线
*  -S, --suffix=SUFFIX		替换常用的备份文件后缀
*  -t, --target-directory=DIRECTORY  move all SOURCE arguments into DIRECTORY
*  -T, --no-target-directory    treat DEST as a normal file
*  -u, --update                 move only when the SOURCE file is newer
                                 than the destination file or when the
                                 destination file is missing
*  -v, --verbose                explain what is being done 详细解释正在做什么
*  -Z, --context                set SELinux security context of destination
                                 file to default type
     * --help		显示此帮助信息并退出
     * --version		显示版本信息并退出
