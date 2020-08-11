# LocalBroadcastManagerSample
# 参考文献
# https://www.jianshu.com/p/96bf387f79ef
# https://www.jianshu.com/p/fba2eec47976
# 断点APT
# https://www.jianshu.com/p/80ca9ad7e346


# init()
 * 每一个注解处理器类都必须有一个空的构造函数
 * 然而，这里有一个特殊的init()方法，它会被注解处理工具调用，并输入ProcessingEnviroment参数。
 * ProcessingEnviroment提供很多有用的工具类如Elements, Types和Filer等。

# getSupportedAnnotationTypes()
 * 这里你必须指定，这个注解处理器是注册给哪个注解的。注意，告知Processor哪些注解需要处理。
 返回一个Set集合，集合内容为 自定义注解的包名+类名。

# getSupportedSourceVersion()
 * 用来指定你使用的Java版本。通常这里返回SourceVersion.latestSupported()。

#  process()
 * 这相当于每个处理器的主函数main()。你在这里写你的扫描、评估和处理注解的代码，
 * 以及生成Java文件。输入参数RoundEnviroment，可以让你查询出包含特定注解的被注解元素。
 
 
 
 #JavaPoet中有几个常用的类：
 MethodSpec，代表一个构造函数或方法声明。
 TypeSpec，代表一个类，接口，或者枚举声明。
 FieldSpec，代表一个成员变量，一个字段声明。
 JavaFile，包含一个顶级类的Java文件。
 
 $L相当于一个占位符，代表的是一个字面量
 $S for Strings，代表一个字符串
 $T for Types，代表一个类型，使用它会自动import导入包
 $N for Names，代表我们自己生成的方法名或者变量名等等
 