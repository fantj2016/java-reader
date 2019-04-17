```
package com.jvm;

import java.util.ArrayList;
import java.util.List;

/**
 * 制造堆溢出
 * Created by Fant.J.
 */
public class Main {
    public static void main(String[] args) {
        List<Test> list = new ArrayList<>();
        while (true){
            list.add(new Test());
        }
    }
}

```

```
public class Test {
}
```

![控制台报错](https://upload-images.jianshu.io/upload_images/5786888-999e6eb041639420.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
12G的ROM让我等了好久才报错！
