package com.wang.broadcast.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * @author wnagwei
 * @date 2019/5/18
 */
public class BroadcastProxy {

    private TypeElement typeElement;
    private String packageName;
    private String className;

    private String proxyClassName;

    public Map<String, ExecutableElement> injectVariables = new HashMap<>();

    private static final String PROXY = "BroadcastInject";

    private static final ClassName ARRAY_LIST = ClassName.get("java.util", "ArrayList");
    private static final ClassName CONTEXT = ClassName.get("android.content", "Context");
    private static final ClassName INTENT = ClassName.get("android.content", "Intent");
    private static final ClassName BROADCAST_RECEIVER = ClassName.get("android.content", "BroadcastReceiver");
    private static final ClassName LOCAL_BROADCAST_MANAGER = ClassName.get("androidx.localbroadcastmanager.content", "LocalBroadcastManager");
    private static final ClassName INTENT_FILTER = ClassName.get("android.content", "IntentFilter");

    private static final ClassName injectClassName = ClassName.get("com.wang.broadcast", PROXY);
    private final ClassName hostClassName;

    BroadcastProxy(Elements elementUtil, TypeElement element) {
        typeElement = element;

        PackageElement packageElement = elementUtil.getPackageOf(element);
        packageName = packageElement.getQualifiedName().toString();
        className = getClassName(typeElement, packageName);
        proxyClassName = className + "$$" + PROXY;

        hostClassName = ClassName.get(packageName, className);
    }

    public TypeSpec generateClass() {
        TypeSpec.Builder builder = TypeSpec.classBuilder(proxyClassName)//类名
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)//修饰符
                .addSuperinterface(ParameterizedTypeName.get(injectClassName, ClassName.get(typeElement)))//添加接口实现
                .addField(FieldSpec.builder(CONTEXT, "mContext", Modifier.PRIVATE).build())//成员变量mContext
                .addField(FieldSpec.builder(ParameterizedTypeName.get(ARRAY_LIST, hostClassName), "mHosts", Modifier.PRIVATE).build())//成员变量
                .addField(FieldSpec.builder(TypeName.INT, "mCounter", Modifier.PRIVATE).build())//成员变量
                .addField(makeReceiverField())
                .addMethod(makeRegisterMethod())
                .addMethod(makeUnregisterMethod());
        return builder.build();
    }

    //mReceiver成员变量
    private FieldSpec makeReceiverField() {
        return FieldSpec.builder(BROADCAST_RECEIVER, "mReceiver")
                .addModifiers(Modifier.PRIVATE)
                .initializer(CodeBlock.of("$L", makeReceiverClass()))
                .build();
    }

    private TypeSpec makeReceiverClass() {
        return TypeSpec.anonymousClassBuilder("")
                .addSuperinterface(BROADCAST_RECEIVER)
                .addMethod(makeReceiverMethod())
                .build();
    }

    private MethodSpec makeReceiverMethod() {
        return MethodSpec.methodBuilder("onReceive")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(CONTEXT, "context")
                .addParameter(INTENT, "intent")
                .returns(TypeName.VOID)
                .addCode(generateReceiver())
                .build();
    }

    private MethodSpec makeRegisterMethod() {
        return MethodSpec.methodBuilder("register")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(CONTEXT, "context")
                .addParameter(hostClassName, "host")
                .addCode(makeRegisterCode())
                .returns(TypeName.VOID)
                .build();
    }

    private MethodSpec makeUnregisterMethod() {
        return MethodSpec.methodBuilder("unregister")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(hostClassName, "host")
                .addCode(CodeBlock.builder()
                        .beginControlFlow("if (mContext == null)")
                        .addStatement("return")
                        .endControlFlow()
                        .addStatement("mCounter = Math.max(0, mCounter - 1)")
                        .beginControlFlow("if (mHosts != null && mHosts.contains(host))")
                        .addStatement("mHosts.remove(host)")
                        .endControlFlow()
                        .beginControlFlow("if (mCounter == 0)")
                        .addStatement("$T.getInstance(mContext).unregisterReceiver(mReceiver)", LOCAL_BROADCAST_MANAGER)
                        .endControlFlow()
                        .build())
                .returns(TypeName.VOID)
                .build();
    }

    private CodeBlock makeRegisterCode() {
        return CodeBlock.builder()
                .beginControlFlow("if (context == null)")
                .addStatement("return")
                .endControlFlow()
                .addStatement("mContext = context.getApplicationContext()")
                .beginControlFlow("if (mHosts == null)")
                .addStatement("mHosts = new ArrayList<>()")
                .endControlFlow()
                .beginControlFlow("if (!mHosts.contains(host))")
                .addStatement("mHosts.add(host)")
                .endControlFlow()
                .addStatement("mCounter++")
                .beginControlFlow("if (mCounter == 1)")
                .addStatement("$T filter = new $T()", INTENT_FILTER, INTENT_FILTER)

                .add(generateAddAction())
                .addStatement("$T.getInstance(mContext).registerReceiver(mReceiver, filter)", LOCAL_BROADCAST_MANAGER)
                .endControlFlow()
                .build();
    }

    private CodeBlock generateAddAction() {
        CodeBlock.Builder builder = CodeBlock.builder();

        for (String action : injectVariables.keySet()) {
            builder.addStatement("filter.addAction($S)", action);
        }
        return builder.build();
    }

    private CodeBlock generateReceiver() {
        CodeBlock.Builder builder = CodeBlock.builder();

        builder.beginControlFlow("for (int i = 0; mHosts != null && i < mHosts.size(); i++)");

        builder.addStatement("$T host = mHosts.get(i)", hostClassName);

        for (String action : injectVariables.keySet()) {
            ExecutableElement element = injectVariables.get(action);

            builder.beginControlFlow("if ($S.equals(intent.getAction()))", action);

            builder.addStatement(generateInvokeAction(element));

            builder.endControlFlow();
        }

        builder.endControlFlow();

        return builder.build();
    }

    private String generateInvokeAction(ExecutableElement element) {
        StringBuilder builder = new StringBuilder();

        builder.append("host.").append(element.getSimpleName().toString()).append("(");

        int paramSize = element.getParameters().size();
        for (int i = 0; i < paramSize; i++) {
            String type = element.getParameters().get(i).asType().toString();
            if (i != 0) {
                builder.append(",");
            }
            if ("android.os.Bundle".equals(type)) {
                builder.append("intent.getExtras()");
            }
            if ("java.lang.String".equals(type)) {
                builder.append("intent.getAction()");
            }
        }
        builder.append(")");
        return builder.toString();
    }

    public String getPackageName() {
        return packageName;
    }

    public String getProxyClassFullName() {
        return packageName + "." + proxyClassName;
    }

    public TypeElement getTypeElement() {
        return typeElement;
    }

    public static String getClassName(TypeElement type, String packageName) {
        int packageLen = packageName.length() + 1;
        return type.getQualifiedName().toString().substring(packageLen)
                .replace('.', '$');
    }
}
