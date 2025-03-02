package com.ai.readme_generator.services;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class CodeSummarizerService {

    public static void summarizeFiles(String projectPath) throws Exception {

        List<File> javaFiles = Files.walk(Paths.get(projectPath))
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".java"))
                .map(path -> path.toFile())
                .toList();

        for (File file : javaFiles) {
            summarizeJavaFile(file);
        }
    }

    public static String summarizeJavaFile(File file) throws Exception {
        String content = Files.readString(file.toPath());
        CompilationUnit compilationUnit = new JavaParser().parse(content).getResult().orElse(null);

        if (compilationUnit == null) return ""; 
        StringBuilder summary = new StringBuilder();

        compilationUnit.findAll(ClassOrInterfaceDeclaration.class).forEach(cls -> {
            summary.append("→ Class: " + cls.getName());
            summary.append("→ Description: " + generateClassDescription(cls.getNameAsString()));
            cls.findAll(MethodDeclaration.class).forEach(method -> {
                summary.append(" → Method: " + method.getName() + "() → " + generateMethodDescription(method.getNameAsString()));
            });
            System.out.println(summary);
        });
        return summary.toString();
    }

    private static String generateClassDescription(String className) {
        if (className.contains("Controller")) return "Handles HTTP requests and exposes REST endpoints.";
        if (className.contains("Service")) return "Implements business logic.";
        if (className.contains("Repository")) return "Interacts with the database.";
        if (className.contains("Entity")) return "Represents a database table.";
        return splitCamelCase(className) + " class.";
    }

    private static String generateMethodDescription(String methodName) {
        if (methodName.startsWith("get")) return "Retrieves data.";
        if (methodName.startsWith("set")) return "Sets data.";
        if (methodName.startsWith("update")) return "Updates existing data.";
        if (methodName.startsWith("delete")) return "Deletes data.";
        if (methodName.startsWith("create") || methodName.startsWith("save")) return "Creates new data.";
        return extractMeaningfulName(methodName) + " method.";
    }

    // Split CamelCase into readable format
    public static String splitCamelCase(String s) {
        return s.replaceAll("([a-z])([A-Z])", "$1 $2");
    }

    // Extract meaningful method names
    public static String extractMeaningfulName(String methodName) {
        methodName = splitCamelCase(methodName); // Convert CamelCase
        methodName = methodName.replaceAll("(?i)^(get|set|fetch|process|handle|execute|validate|compute)\\s*", ""); // Remove common verbs
        return methodName.trim();
    }
}
