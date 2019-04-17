超链接:   http://xxxxxxx.html?id=10
```
$(function(){
            var idPre = window.location.href.split("?")[1];
            var id = idPre.split("=")[1];
```
window.location.href   获取整个链接
window.location.href.split("?")[ 1 ] ;   从?切开，[1]的意思是取右边，即取出了 id = 10
再对=分割，取右边[1]，就得到了 数字10
