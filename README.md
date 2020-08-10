# LocalBroadcastManagerSample
# 参考文献
# https://www.jianshu.com/p/96bf387f79ef
# https://www.jianshu.com/p/fba2eec47976


# init()
 * 每一个注解处理器类都必须有一个空的构造函数
 * 然而，这里有一个特殊的init()方法，它会被注解处理工具调用，并输入ProcessingEnviroment参数。
 * ProcessingEnviroment提供很多有用的工具类如Elements, Types和Filer等。

# getSupportedAnnotationTypes()
 * 这里你必须指定，这个注解处理器是注册给哪个注解的。注意，它的返回值是一个字符串的集合，
 * 包含本处理器想要处理的注解类型的合法全称。

# getSupportedSourceVersion()
 * 用来指定你使用的Java版本。通常这里返回SourceVersion.latestSupported()。

#  process()
 * 这相当于每个处理器的主函数main()。你在这里写你的扫描、评估和处理注解的代码，
 * 以及生成Java文件。输入参数RoundEnviroment，可以让你查询出包含特定注解的被注解元素。