* 概要
  ping  [-aAbBdDfhLnOqrRUvV] [-c count] [-F flowlabel] [-i interval] [-I inter‐
       face] [-l preload] [-m mark] [-M pmtudisc_option]  [-N  nodeinfo_option]  [-w
       deadline]  [-W timeout] [-p pattern] [-Q tos] [-s packetsize] [-S sndbuf] [-t
       ttl] [-T timestamp option] [hop ...] destination
* 描述
        ping uses the ICMP protocol's mandatory ECHO_REQUEST datagram  to  elicit  an
       ICMP   ECHO_RESPONSE   from   a  host  or  gateway.   ECHO_REQUEST  datagrams  (''pings'') have an IP and ICMP header, followed by a struct timeval and then
       an arbitrary number of ``pad'' bytes used to fill out the packet.
       ping6  is  IPv6  version  of ping, and can also send Node Information Queries
       (RFC4620).  Intermediate hops may not be allowed, because IPv6 source routing
       was deprecated (RFC5095).

* 操作

       -a     Audible ping.

       -A     Adaptive ping. Interpacket interval adapts to round-trip time, so that
              effectively not more than one (or more, if preload is set)  unanswered
              probe  is   present in the network. Minimal interval is 200msec for not
              super-user.  On networks with low rtt this mode is essentially equiva‐
              lent to flood mode.

       -b     Allow pinging a broadcast address.

       -B     Do  not allow ping to change source address of probes.  The address is
              bound to one selected when ping starts.

       -c count
              Stop after sending count ECHO_REQUEST packets. With  deadline  option,
              ping waits for count ECHO_REPLY packets, until the timeout expires.

       -d     Set  the  SO_DEBUG option on the socket being used.  Essentially, this
              socket option is not used by Linux kernel.

       -D     Print timestamp (unix time + microseconds as in  gettimeofday)  before
              each line.

       -f     Flood  ping.  For  every  ECHO_REQUEST sent a period ``.'' is printed,
              while for ever ECHO_REPLY received a backspace is printed.  This  pro‐
              vides  a  rapid  display  of  how  many packets are being dropped.  If
              interval is not given, it sets interval to zero and outputs packets as
              fast  as  they come back or one hundred times per second, whichever is
              more.  Only the super-user may use this option with zero interval.

       -F flow label
              ping6 only.  Allocate and set 20 bit  flow  label  (in  hex)  on  echo
              request  packets.   If  value  is  zero,  kernel allocates random flow
              label.

       -h     Show help.

       -i interval
              Wait interval seconds between sending each packet.  The default is  to
              wait  for  one  second between each packet normally, or not to wait in
              flood mode. Only super-user may set interval to values less  0.2  sec‐
              onds.
       -I interface
              interface is either an address, or an interface name.  If interface is
              an address, it sets source address to specified interface address.  If
              interface  in an interface name, it sets source interface to specified
              interface.  For ping6, when doing ping to a link-local scope  address,
              link  specification  (by  the  '%'-notation in destination, or by this
              option) is required.

       -l preload
              If preload is specified, ping sends that many packets not waiting  for
              reply.  Only the super-user may select preload more than 3.

       -L     Suppress loopback of multicast packets.  This flag only applies if the
              ping destination is a multicast address.

       -m mark
              use mark to tag the packets going out. This is useful for  variety  of
              reasons  within the kernel such as using policy routing to select spe‐
              cific outbound processing.
       -n     Numeric output only.  No attempt will be made to lookup symbolic names
              for host addresses.

       -O     Report  outstanding  ICMP ECHO reply before sending next packet.  This
              is useful together with the timestamp -D to log output to a diagnostic
              file and search for missing answers.

       -p pattern
              You  may  specify  up  to  16 ``pad'' bytes to fill out the packet you
              send.  This is useful for diagnosing data-dependent problems in a net‐
              work.  For example, -p ff will cause the sent packet to be filled with
              all ones.

       -q     Quiet output.  Nothing  is  displayed  except  the  summary  lines  at
              startup time and when finished.

       -Q tos Set  Quality  of  Service -related bits in ICMP datagrams.  tos can be
              decimal (ping only) or hex number.
      -r     Bypass the normal routing tables and send directly to  a  host  on  an
              attached  interface.   If  the host is not on a directly-attached net‐
              work, an error is returned.  This option can be used to ping  a  local
              host  through  an  interface that has no route through it provided the
              option -I is also used.

       -R     ping only.  Record route.  Includes the  RECORD_ROUTE  option  in  the
              ECHO_REQUEST packet and displays the route buffer on returned packets.
              Note that the IP header is only large enough  for  nine  such  routes.
              Many hosts ignore or discard this option.

       -s packetsize
              Specifies  the  number  of  data bytes to be sent.  The default is 56,
              which translates into 64 ICMP data bytes  when  combined  with  the  8
              bytes of ICMP header data.

       -S sndbuf
              Set socket sndbuf. If not specified, it is selected to buffer not more
              than one packet.
       -t ttl ping only.  Set the IP Time to Live.

       -T timestamp option
              Set special IP timestamp options.   timestamp  option  may  be  either
              tsonly  (only  timestamps),  tsandaddr  (timestamps  and addresses) or
              tsprespec host1 [host2 [host3 [host4]]] (timestamp prespecified hops).

       -U     Print full user-to-user latency (the  old  behaviour).  Normally  ping
              prints network round trip time, which can be different f.e. due to DNS
              failures.

       -v     Verbose output.

       -V     Show version and exit.

      -w deadline
              Specify a timeout, in seconds, before ping  exits  regardless  of  how
              many  packets  have  been sent or received. In this case ping does not
              stop after count packet are sent, it waits either for deadline  expire
              or until count probes are answered or for some error notification from
              network.

       -W timeout
              Time to wait for a response, in seconds. The option affects only time‐
              out in absence of any responses, otherwise ping waits for two RTTs.


|参数 | 详解 |
|:-------:|:--------:|
| -a | Audible ping. |
| -A | 自适应ping，根据ping包往返时间确定ping的速度； |
| -b | 允许ping一个广播地址； |
| -B | 不允许ping改变包头的源地址； |
| **-c count** | ping指定次数后停止ping；  |
| -d  | 使用Socket的SO_DEBUG功能； |
| -F flow_label | 为ping回显请求分配一个20位的“flow label”，如果未设置，内核会为ping随机分配； |
| **-f** | 极限检测，快速连续ping一台主机，ping的速度达到100次每秒； |
| -i interval | 设定间隔几秒发送一个ping包，默认一秒ping一次； |
| -I interface | 指定网卡接口、或指定的本机地址送出数据包； |
| -l preload | 设置在送出要求信息之前，先行发出的数据包； |
| -L | 抑制组播报文回送，只适用于[ping](http://aiezu.com/article/linux_ping_command.html)的目标为一个组播地址 |
| -n  | 不要将ip地址转换成主机名； |
| -p pattern | 指定填充ping数据包的十六进制内容，在诊断与数据有关的网络错误时这个选项就非常有用，如：“-p ff”； |
| -q | 不显示任何传送封包的信息，只显示最后的结果 |
| -Q tos | 设置Qos(Quality of Service)，它是ICMP数据报相关位；可以是十进制或十六进制数，详见rfc1349和rfc2474文档； |
| -R | 记录ping的路由过程(IPv4 only)；
注意：由于IP头的限制，最多只能记录9个路由，其他会被忽略； |
| -r | 忽略正常的路由表，直接将数据包送到远端主机上，通常是查看本机的网络接口是否有问题；如果主机不直接连接的网络上，则返回一个错误。 |
| -S sndbuf | Set socket sndbuf. If not specified, it is selected to buffer not more than one packet. |
| -s packetsize | 指定每次ping发送的数据字节数，默认为“56字节”+“28字节”的ICMP头，一共是84字节；
包头+内容不能大于65535，所以最大值为65507（linux:65507, windows:65500）； |
| -t ttl | 设置TTL(Time To Live)为指定的值。该字段指定IP包被路由器丢弃之前允许通过的最大网段数； |
| -T timestamp_option | 设置IP timestamp选项,可以是下面的任何一个：'tsonly' (only timestamps)'tsandaddr' (timestamps and addresses)'tsprespec host1 [host2 [host3]]' (timestamp prespecified hops).  |
| -M hint  | 设置MTU（最大传输单元）分片策略。可设置为：'do'：禁止分片，即使包被丢弃；'want'：当包过大时分片；'dont'：不设置分片标志（DF flag）； |
| -m mark | 设置mark； |
| -v | 使ping处于verbose方式，它要ping命令除了打印ECHO-RESPONSE数据包之外，还打印其它所有返回的ICMP数据包； |
| -U  | Print full user-to-user latency (the old behaviour).Normally ping prints network round trip time, which can be different f.e. due to DNS failures. |
| -W timeout | 以毫秒为单位设置ping的超时时间； |
| -w deadline | deadline； |

