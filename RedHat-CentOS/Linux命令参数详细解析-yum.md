* check          检查 RPM 数据库问题
* check-update   检查是否有可用的软件包更新
* clean          删除缓存数据
* deplist        列出软件包的依赖关系
* distribution-synchronization 已同步软件包到最新可用版本
* downgrade      降级软件包
* erase          从系统中移除一个或多个软件包
* fs             Creates filesystem snapshots, or lists/deletes current snapshots.
* fssnapshot     Creates filesystem snapshots, or lists/deletes current snapshots.
* groups         显示或使用、组信息
* help           显示用法提示
* history        显示或使用事务历史
* info           显示关于软件包或组的详细信息
* install        向系统中安装一个或多个软件包
* langavailable  Check available languages
* langinfo       List languages information
* langinstall    Install appropriate language packs for a language
* langlist       List installed languages
* langremove     Remove installed language packs for a language
* list           列出一个或一组软件包
* load-transaction 从文件名中加载一个已存事务
* makecache      创建元数据缓存
* provides       查找提供指定内容的软件包
* reinstall      覆盖安装软件包
* repo-pkgs      将一个源当作一个软件包组，这样我们就可以一次性安装/移除全部软件包。
* repolist       显示已配置的源
* search         在软件包详细信息中搜索指定字符串
* shell          运行交互式的 yum shell
* swap           Simple way to swap packages, instead of using shell
* update         更新系统中的一个或多个软件包
* update-minimal Works like upgrade, but goes to the 'newest' package match which fixes a problem that affects your system
* updateinfo     Acts on repository update information
* upgrade        更新软件包同时考虑软件包取代关系
* version        显示机器和/或可用的源版本。

*  -h, --help            显示此帮助消息并退出
*  -t, --tolerant        忽略错误
*  -C, --cacheonly       完全从系统缓存运行，不升级缓存
*  -c [config file], --config=[config file]
                        配置文件路径
*  -R [minutes], --randomwait=[minutes]
                        命令最长等待时间
*  -d [debug level], --debuglevel=[debug level]
                        调试输出级别
*  --showduplicates      在 list/search 命令下，显示源里重复的条目
*  -e [error level], --errorlevel=[error level]
                        错误输出级别
*  --rpmverbosity=[debug level name]
                        RPM 调试输出级别
*  -q, --quiet           静默执行
*  -v, --verbose         详尽的操作过程
*  -y, --assumeyes       回答全部问题为是
*  --assumeno            回答全部问题为否
*  --version             显示 Yum 版本然后退出
*  --installroot=[path]  设置安装根目录
*  --enablerepo=[repo]   启用一个或多个软件源(支持通配符)
  --disablerepo=[repo]  禁用一个或多个软件源(支持通配符)
*  -x [package], --exclude=[package]
                        采用全名或通配符排除软件包
*  --disableexcludes=[repo]
                        禁止从主配置，从源或者从任何位置排除
*  --disableincludes=[repo]
                        disable includepkgs for a repo or for everything
*  --obsoletes           更新时处理软件包取代关系
*  --noplugins           禁用 Yum 插件
*  --nogpgcheck          禁用 GPG 签名检查
*  --disableplugin=[plugin]
                        禁用指定名称的插件
*  --enableplugin=[plugin]
                        启用指定名称的插件
*  --skip-broken         忽略存在依赖关系问题的软件包
*  --color=COLOR         配置是否使用颜色
*  --releasever=RELEASEVER
                        在 yum 配置和 repo 文件里设置 $releasever 的值
*  --downloadonly        仅下载而不更新
*  --downloaddir=DLDIR   指定一个其他文件夹用于保存软件包
*  --setopt=SETOPTS      设置任意配置和源选项
*  --bugfix              Include bugfix relevant packages, in updates
*  --security            Include security relevant packages, in updates
*  --advisory=ADVS, --advisories=ADVS
                        Include packages needed to fix the given advisory, in
                        updates
*  --bzs=BZS             Include packages needed to fix the given BZ, in
                        updates
*  --cves=CVES           Include packages needed to fix the given CVE, in
                        updates
*  --sec-severity=SEVS, --secseverity=SEVS
                        Include security relevant packages matching the
                        severity, in updates
