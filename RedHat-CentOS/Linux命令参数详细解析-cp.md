
* 用法：cp [选项]... [-T] 源文件 目标文件
　或：cp [选项]... 源文件... 目录
　或：cp [选项]... -t 目录 源文件...
Copy SOURCE to DEST, or multiple SOURCE(s) to DIRECTORY

*  -a, --archive			等于-dR --preserve=all
      --attributes-only	仅复制属性而不复制数据      --backup[=CONTROL		为每个已存在的目标文件创建备份
*  -b				类似--backup 但不接受参数
      --copy-contents		在递归处理是复制特殊文件内容
*  -d				等于--no-dereference --preserve=links
*  -f, --force (强制)                 if an existing destination file cannot be
                                 opened, remove it and try again (this option
                                 is ignored when the -n option is also used)
*  -i, --interactive (互动)           prompt before overwrite (overrides a previous -n
                                  option)
*  -H                           follow command-line symbolic links in SOURCE
*  -l, --link                   hard link files instead of copying  链接硬链接文件而不是复制
*  -L, --dereference            always follow symbolic links in SOURCE
*  -n, --no-clobber		不要覆盖已存在的文件(使前面的 -i 选项失效)
*  -P, --no-dereference		不跟随源文件中的符号链接
*  -p				等于--preserve=模式,所有权,时间戳
      --preserve[=属性列表	保持指定的属性(默认：模式,所有权,时间戳)，如果
					可能保持附加属性：环境、链接、xattr 等
*  -c                           deprecated, same as --preserve=context
      --sno-preserve=属性列表	不保留指定的文件属性
      --parents			复制前在目标目录创建来源文件路径中的所有目录
*  -R, -r, --recursive		递归复制目录及其子目录内的所有内容
      --reflink[=WHEN]		控制克隆/CoW 副本。请查看下面的内如。
      --remove-destination	尝试打开目标文件前先删除已存在的目的地
					文件 (相对于 --force 选项)
      --sparse=WHEN		控制创建稀疏文件的方式
      --strip-trailing-slashes	删除参数中所有源文件/目录末端的斜杠
*  -s, --symbolic-link		只创建符号链接而不复制文件
*  -S, --suffix=后缀		自行指定备份文件的后缀
*  -t,  --target-directory=目录	将所有参数指定的源文件/目录
                                           复制至目标目录
*  -T, --no-target-directory	将目标目录视作普通文件
*  -u, --update			只在源文件比目标文件新，或目标文件
					不存在时才进行复制
*  -v, --verbose		显示详细的进行步骤
*  -x, --one-file-system	不跨越文件系统进行操作
*  -Z, --context[=CTX]          set SELinux security context of destination
                                 file to default type, or to CTX if specified
    *  --help		显示此帮助信息并退出
    *  --version		显示版本信息并退出
