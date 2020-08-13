# LocalBroadcastManagerSample
# 参考文献
# https://www.jianshu.com/p/96bf387f79ef
# https://www.jianshu.com/p/fba2eec47976
# 断点APT
# https://www.jianshu.com/p/80ca9ad7e346
https://www.jianshu.com/p/bcddc376c0ef

# 使用说明
   ```
    implementation 'com.github.wang:local-broadcast:1.0.0'
    annotationProcessor 'com.github.wang:local-broadcast-compiler:1.0.0'
    ```
1. 注册广播

    ```java
    BroadcastUtil.register(Fragment fragment);
    BroadcastUtil.register(Activity activity);
    BroadcastUtil.register(Context context, Object host);
    ```
    其中，host 为包含监听方法的类

2. 监听广播

    ```
    - 给方法添加注解 `com.wang.broadcast.annotation.Action(Sting[] values)` 即可监听 `values` 对应的广播
    - 方法必须为 `public`
    - 支持同一个方法监听多个广播，可以添加**String类型参数**区分不同的广播
    - 广播传递的数据为**Bundle类型**
    ```

3. 取消注册

    ```
   java
    BroadcastUtil.unregister(Object host);
    ```
4. 发送广播

    ```
    java
    BroadcastUtil.sendBroadcast(Context context, String... actions);
    BroadcastUtil.sendBroadcast(Context context, Bundle bundle, String... actions);
    ```

5. 混淆配置

    ```
    proguard
    -keep class * implements com.wang.broadcast.BroadcastInject
    -keep class * {
        @com.wang.annotation.broadcast.* <methods>;
    }
    ```

# 发布说明

1. 修改 `gradle.properties` 文件中 `VERSION_NAME` 为新版本号
2. 在根目录下执行 `uploadArchives` 任务发布到 Nexus 仓库