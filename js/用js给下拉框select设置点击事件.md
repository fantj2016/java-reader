```
<select id="select_table">
          <option value ="-1">==全部分类==</option>
          <option value ="0">工作案例</option>
          <option value="1">微党课</option>
          <option value="2">教师党支部推荐展示</option>
          <option value="3">学生党支部推荐展示</option>
</select>
```
```
$(function(){
            $("#select_table").change(function(){
                var typeCodePre =  $("#select_table").val();
```
