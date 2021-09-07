package edu.uestc.antfuzzer.framework.core;

import edu.uestc.antfuzzer.framework.annotation.Component;
import edu.uestc.antfuzzer.framework.annotation.Fuzzer;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

public class ClassLoader {
    private static final String packageName = "edu.uestc.antfuzzer";

    @Getter
    private Set<Class<?>> classSet;
    @Getter
    private Set<Class<?>> fuzzerSet;
    @Getter
    private Set<Class<?>> componentSet;

    public ClassLoader() {
        load();
    }

    private void load() {
        classSet = new HashSet<>();
        try {
            Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(packageName.replaceAll("\\.", "/"));
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                String protocol = resource.getProtocol();
                if (protocol.equalsIgnoreCase("file")) {
                    String packagePath = resource.getPath();
                    loadClass(classSet, packageName, packagePath);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        loadComponentSet();
        loadFuzzerSet();
    }


    private void loadClass(Set<Class<?>> classSet, String packageName, String packagePath) {
        File[] files = new File(packagePath).listFiles(pathname -> pathname.isDirectory() || (pathname.isFile() && pathname.getName().endsWith(".class")));
        if (files != null && files.length > 0) {
            for (File file : files) {
                String fileName = file.getName();
                if (file.isFile()) {
                    if (packageName != null && !packageName.equals(""))
                        fileName = packageName + "." + fileName.substring(0, fileName.lastIndexOf("."));
                    Class<?> clazz = getClass(fileName);
                    classSet.add(clazz);
                } else {
                    String subPackageName = fileName;
                    String subPackagePath = fileName;
                    if (packageName != null) {
                        if (!packageName.equals(""))
                            subPackageName = packageName + "." + subPackageName;
                        if (!packagePath.equals(""))
                            subPackagePath = packagePath + "/" + subPackagePath;
                    }
                    loadClass(classSet, subPackageName, subPackagePath);
                }
            }
        }
    }

    private void loadComponentSet() {
        componentSet = new HashSet<>();
        if (classSet != null) {
            for (Class<?> clazz : classSet) {
                if (clazz.getAnnotation(Component.class) != null) {
                    componentSet.add(clazz);
                }
            }
        }
    }


    private void loadFuzzerSet() {
        fuzzerSet = new HashSet<>();
        if (classSet != null) {
            for (Class<?> clazz : classSet) {
                if (clazz.getAnnotation(Fuzzer.class) != null) {
                    fuzzerSet.add(clazz);
                }
            }
        }
    }

    private Class<?> getClass(String className) {
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return clazz;
    }
}