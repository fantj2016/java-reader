```
<title>Js判断下拉框</title>
<script>
function ss(){
var slt=document.getElementById("aa");
if(slt.value==""){
alert("请选择一个项目");
return false;
}
return true;
}
</script>
<form method=post onsubmit=" return ss()">
<select id="aa">
<option value="">请选择</option>
<option value="1s">一</option>
<option value="2u">二</option>
</select>
<input type=submit value="提交">
</form>
```
实例：
```
<select name="typeId" id="select_type">
    <option value="">==请选择==</option>
    <option value="1">xx新闻</option>
    <option value="2">xx纵横</option>
    <option value="3">xx研究</option>
    <option value="4">xx法规</option>
    <option value="5">xx项目</option>
    <option value="6">xx动态</option>
    <option value="7">xx发布</option>
    <option value="8">xx</option>
    <option value="9">xx监测</option>
    <option value="10">xx审批</option>
</select>
<script>
    function sltCheck(){
        var slt=document.getElementById("select_type");
        if(slt.value===""){
            alert("请选择一个项目");
            return false;
        }
        return true;
    }
</script>

<input type="submit" value="提交" onclick="return sltCheck();">
```
