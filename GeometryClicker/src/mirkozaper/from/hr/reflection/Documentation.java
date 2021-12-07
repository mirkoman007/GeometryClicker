/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mirkozaper.from.hr.reflection;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import mirkozaper.from.hr.Main;

/**
 *
 * @author mirko
 */
public class Documentation {

    public static void Generate() {
        try (FileWriter htmlDoc = new FileWriter("htmlDocumentation.html")) {

            htmlDoc.write("<!DOCTYPE html>");
            htmlDoc.write("<html>");
            htmlDoc.write("<head>");
            htmlDoc.write("<title>Class documentation</title>");
            htmlDoc.write("</head>");
            htmlDoc.write("<body>");

            Set<String> files = new HashSet<>();
            listOfPackage("src/", files);

            for (String packageName : files) {
                List<String> filesInPackage = Files
                        .list(Paths.get(GetPackageLocation(packageName)))
                        .filter(f -> f.toFile().isFile())
                        .map(file -> file.toFile().getName())
                        .filter(f -> f.endsWith(".class"))
                        .collect(Collectors.toList());

                for (String fileName : filesInPackage) {

                    if (fileName.indexOf(".") > 0) {
                        fileName = fileName.substring(0, fileName.lastIndexOf("."));
                    }

                    Class<?> unknownObject = Class.forName(packageName + "." + fileName);

                    htmlDoc.write("<h1>Class name: " + unknownObject.getName()
                            + " </h1>");

                    Field[] fields = unknownObject.getDeclaredFields();

                    htmlDoc.write("<h2>Fields:</h2>");

                    for (Field field : fields) {
                        Integer modifiers = field.getModifiers();
                        boolean isPublic = (modifiers % 2) == 1;
                        boolean isPrivate = (modifiers % 2) == 0;
                        if (isPublic) {
                            htmlDoc.write("public ");
                        } else if (isPrivate) {
                            htmlDoc.write("private ");
                        }
                        htmlDoc.write(field.getType().getName() + " ");
                        htmlDoc.write(field.getName() + "<br />");
                    }

                    htmlDoc.write("<h2>Constructors:</h2>");

                    Constructor[] constructors = unknownObject.getConstructors();

                    for (Constructor con : constructors) {
                        Parameter[] params = con.getParameters();

                        if (params.length > 0) {

                            htmlDoc.write(
                                    "<h3>Constructor parameters: </h3>");

                            for (Parameter param : params) {
                                htmlDoc.write("Parameter: "
                                        + param.getType().getName());
                                htmlDoc.write(" "
                                        + param.getName() + "<br />");
                            }
                        } else {
                            htmlDoc.write(
                                    "<h3>Default constructor without parameters"
                                    + "</h3>");
                        }
                    }

                    htmlDoc.write("<h2>Methods:</h2>");

                    Method[] methods = unknownObject.getDeclaredMethods();
                    for (Method method : methods) {
                        htmlDoc.append("<br />");
                        htmlDoc
                                .append(Modifier.toString(method.getModifiers()))
                                .append(" ")
                                .append(method.getReturnType().toString())
                                .append(" ")
                                .append(method.getName());
                        StringBuilder sb = new StringBuilder();

                        sb.append("(");
                        for (Parameter parameter : method.getParameters()) {
                            sb
                                    .append(parameter.toString())
                                    .append(", ");
                        }
                        if (sb.toString().endsWith(", ")) {

                            sb.delete(sb.length() - 2, sb.length());
                        }
                        sb.append(")");

                        htmlDoc.append(sb);
                    }
                    htmlDoc.append("<br />");

                }
            }

            htmlDoc.write("</body>");
            htmlDoc.write("</html>");

            htmlDoc.flush();

            System.out.println("Documentation generation successfuly done!\n");

        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void listOfPackage(String directoryName, Set<String> pack) {
        File directory = new File(directoryName);

        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                String path = file.getPath();
                String packName = path.substring(path.indexOf("src") + 4, path.lastIndexOf('\\'));
                pack.add(packName.replace('\\', '.'));
            } else if (file.isDirectory()) {

                listOfPackage(file.getAbsolutePath(), pack);
            }
        }
    }

    private static String GetPackageLocation(String packageName) {
        packageName += ".";
        return System.getProperty("user.dir") + "\\build\\classes\\" + packageName.replace(".", "\\");
    }
}
