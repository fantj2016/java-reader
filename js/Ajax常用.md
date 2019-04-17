### 获取class下的子标签，并替换标签内容
```
$('.r strong').text(result.totalElements);
```

### checkbox 上传
```
    function datadel(){
        var array = new Array();
        $.each($('input:checkbox:checked'),function(){
			array.push($(this).val());
            window.alert("你选了："+
                $('input[type=checkbox]:checked').length+"个，其中有："+$(this).val());
        });
        // alert(array.toString())
        // alert(JSON.stringify(array));
        $.ajax({
            url: "http://127.0.0.1:8081/student/deleteInBatch/",
            type: "post",
            data: {"ids":array.toString()},
            dataType: "json",
            async: false,
            success: function (result) {
                console.log(result);
                alert("批量删除成功!");
            }
        });
	}
```
