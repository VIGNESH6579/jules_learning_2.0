package com.interview.portal.service;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

@Service
public class CodeExecutionService {

    public String executeJavaCode(String code) {
        // Set the security policy
        System.setProperty("java.security.policy", "classpath:java.policy");
        System.setSecurityManager(new SecurityManager());

        try {
            // Create a temporary directory to store the compiled code
            Path tempDir = Files.createTempDirectory("java-execution");
            Path sourceFile = Paths.get(tempDir.toString(), "DynamicClass.java");
            Files.write(sourceFile, code.getBytes());

            // Compile the code
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            int compilationResult = compiler.run(null, null, null, sourceFile.toString());
            if (compilationResult != 0) {
                return "Compilation failed.";
            }

            // Load the compiled class
            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{tempDir.toUri().toURL()});
            Class<?> dynamicClass = Class.forName("DynamicClass", true, classLoader);

            // Redirect standard output to capture the result
            PrintStream originalOut = System.out;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            System.setOut(new PrintStream(baos));

            // Execute the main method
            Method mainMethod = dynamicClass.getMethod("main", String[].class);
            mainMethod.invoke(null, (Object) new String[]{});

            // Restore standard output and return the captured output
            System.setOut(originalOut);
            return baos.toString();

        } catch (Exception e) {
            return "Execution failed: " + e.getMessage();
        }
    }
}
