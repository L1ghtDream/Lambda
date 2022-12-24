package dev.lightdream.lambda.reflection;

import lombok.SneakyThrows;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtils {

    @SneakyThrows
    public static List<Method> getMethodsAnnotatedWith(String packageName, Class<? extends Annotation> annotation) {
        List<Method> output = new ArrayList<>();

        for (String clazz : new Reflections(packageName).getAll(Scanners.SubTypes)) {
            for (Method method : Class.forName(clazz).getMethods()) {
                if (method.isAnnotationPresent(annotation)) {
                    output.add(method);
                }
            }
        }

        return output;
    }

}