# LocalBroadcastManagerSample
# 参考文献
# https://www.jianshu.com/p/96bf387f79ef
# https://www.jianshu.com/p/fba2eec47976
# 断点APT
#ctrl+f9 前要先删除Build 文件  否则不会执行断点
# https://www.jianshu.com/p/80ca9ad7e346
https://www.jianshu.com/p/bcddc376c0ef
//
使用本库会有一个问题 父类的Action  不能接受到事件
因为父类 BroadcastUtil.register(this)  this 指向的是子类
注册的时候根据host 判断了  所以导致父类没成功注册  故而不能接受到事件

如果不加判斷可能會有廣播重复注册问题

# 使用说明
   ```
    implementation 'com.github.wang:local-broadcast:1.0.0'
    annotationProcessor 'com.github.wang:local-broadcast-compiler:1.0.0'
    ```
