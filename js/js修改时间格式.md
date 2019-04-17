```
function getDateStr(str)
                    {
                     var strDate = new Date(str);
                     var sDate = strDate.toLocaleString().split(' ')[0];
                     return sDate.replace(/年|月/g, '-').replace(/日/g, '');
                    }

```
