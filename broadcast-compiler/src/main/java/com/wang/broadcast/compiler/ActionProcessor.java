package com.wang.broadcast.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import com.wang.broadcast.annotation.Action;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * @author wnagwei
 * @date 2019/5/18
 */
@AutoService(Processor.class)
public class ActionProcessor extends AbstractProcessor {

    private Messager messager;

    private Elements elementsUtil;

    private Map<String, BroadcastProxy> mProxyMap = new HashMap<>();

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotationTypes = new LinkedHashSet<>();
        annotationTypes.add(Action.class.getCanonicalName());
        return annotationTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        elementsUtil = processingEnv.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnv) {
        mProxyMap.clear();
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Action.class);
        for (Element element : elements) {
            if (!checkAnnotationValid(element, Action.class)) {
                return false;
            }

            ExecutableElement executableElement = (ExecutableElement) element;

            TypeElement typeElement = (TypeElement) executableElement.getEnclosingElement();
            String qualifiedName = typeElement.getQualifiedName().toString();

            BroadcastProxy proxyInfo = mProxyMap.get(qualifiedName);
            if (proxyInfo == null) {
                proxyInfo = new BroadcastProxy(elementsUtil, typeElement);
                mProxyMap.put(qualifiedName, proxyInfo);
            }

            Action annotation = executableElement.getAnnotation(Action.class);
            String[] ids = annotation.value();
            if (ids.length <= 0) {
                error(element, "Action can not be empty.");
                return false;
            }
            for (String id : ids) {
                proxyInfo.injectVariables.put(id, executableElement);
            }
        }

        for (String key : mProxyMap.keySet()) {
            BroadcastProxy proxyInfo = mProxyMap.get(key);

            String packageName = proxyInfo.getPackageName();
            TypeSpec generateClass = proxyInfo.generateClass();

            JavaFile javaFile = JavaFile.builder(packageName, generateClass).build();
            try {
                javaFile.writeTo(processingEnv.getFiler());
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private boolean checkAnnotationValid(Element element, Class annotationClass) {
        if (element.getKind() != ElementKind.METHOD) {
            error(element, "%s must be declared on method.", annotationClass.getSimpleName());
            return false;
        }
        if (!element.getModifiers().contains(Modifier.PUBLIC)) {
            error(element, "%s() must be public.", element.getSimpleName());
            return false;
        }
        ExecutableElement executableElement = (ExecutableElement) element;
//        if (executableElement.getParameters().size() > 1) {
//            error(element, "parameter size must be 0 or 1.");
//            return false;
//        }
        int paramSize = executableElement.getParameters().size();
        for (int i = 0; i < paramSize; i++) {
            String type = executableElement.getParameters().get(i).asType().toString();
            if (!"android.os.Bundle".equals(type) && !"java.lang.String".equals(type)) {
                error(element, "parameter type(%s) can only be android.os.Bundle or java.lang.String.", type);
                return false;
            }
        }
        return true;
    }

    private void error(Element element, String message, Object... args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }
        messager.printMessage(Diagnostic.Kind.ERROR, message, element);
    }
}
