##### 1. mysql Error 1040 too many connection
当最大连接数比较小时，可能会出现“1040 too many connection”错误。
解决方法：
1. 首先需要重启mysql服务，执行命令：service mysql restar
2. 登录mysql:mysql -uroot -p
3. 登录成功后执行以下语句查询当前的最大连接数：select VARIABLE_VALUE from information_schema.GLOBAL_VARIABLES where VARIABLE_NAME='MAX_CONNECTIONS';
4. 执行以下语句修改最大连接数：set global max_connections = 3600;
