/*
 *
 *  Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.wso2.javaagent;

import javassist.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Arrays;

public class JDBCClassTransformer implements ClassFileTransformer {

    final static Logger logger = Logger.getLogger(JDBCClassTransformer.class);

    /**
     * Create a copy of currently processing class. Since javassist instrument methods with body,
     * for each Class iterate through all the methods defined to find respective methods.
     * Instrument method body by injecting required code and return
     * the class file of the modified class.
     *
     * @param loader the defining loader of the class to be transformed, may be null if the
     *               bootstrap loader
     * @param className the name of the class in the internal form of fully qualified class
     *            and interface names as defined in The Java Virtual Machine Specification
     * @param classBeingRedefined if this is triggered by a redefine or retransform,
     *            the class being redefined or retransformed;
     *            if this is a class load, null
     * @param protectionDomain the protection domain of the class being defined or redefined
     * @param classfileBuffer the input byte buffer in class file format
     * @return a well-formed class file buffer (the result of the transform),
     * or null if no transform is performed
     * @throws IllegalClassFormatException
     */
    public byte[] transform(ClassLoader loader, String className, Class classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer)
            throws IllegalClassFormatException {

        byte[] byteCode = classfileBuffer;

        ClassPool classPool = ClassPool.getDefault();
//        try {
//            classPool.appendClassPath(System.getProperty("java.class.path"));
//        } catch (NotFoundException e) {
//            e.printStackTrace();
//        }
//        classPool.insertClassPath(new ByteArrayClassPath(className, classfileBuffer));
//        classPool.appendClassPath(new LoaderClassPath(pluginLoader));
//        classPool.appendClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
//        classPool.appendSystemPath();
//        classPool.appendClassPath(new LoaderClassPath(this.getClass().getClassLoader()));
        try {
            CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));

            if (!ctClass.isInterface()) {
                CtMethod[] methods = ctClass.getDeclaredMethods();
//                if(ctClass.getName().equals("ConnectionImpl")) {
//                    CtMethod[] methods = ctClass.getDeclaredMethods();
//                System.out.println("Class name : " + ctClass.getName());
//                for (CtMethod method : methods) {
//                    System.out.println("        Method name : "+ method.getName());
//                    if (method.getName().equals("prepareStatement")) {
//                        method.insertAt(1, true,
//                                "org.wso2.javaagent.JDBCAgentPublisher.setObtainedQuery($1);");
//                    }
//                }
//            }
//                    if (method.getName().equals("getConnection")) {
//                        this.testConnectSignature(method);
//                    }
//                    logger.info("Instrumenting method in " + ctClass.getName());

//                    if(ctClass.getName().equals("com/mysql/jdbc/PreparedStatement")) {
//                    System.out.println("******************" + ctClass.getName() + "*******************");
//                    CtMethod[] methods = ctClass.getDeclaredMethods();
//                    for (CtMethod method : methods) {
//                Class[] interfaces =classBeingRedefined.getInterfaces();
//                if(className != null){
//                String fullyQualifiedClassName = className.replace('/','.');
//                System.out.println(fullyQualifiedClassName);
//                Class currentClass = Class.forName(fullyQualifiedClassName);
//                c.getInterfaces();
//                Class[] interfaces = currentClass.getInterfaces();
//                Class[] interfaces =className.getClass().getInterfaces();

                for (CtMethod method : methods) {
                                if (method.getName().equals("executeQuery")) {
//                                    System.out.println(ctClass.getName());
                                    CtClass[] interfaces = ctClass.getInterfaces();
                                    if(interfaces.length!=0){
                                        for(CtClass c : interfaces){
                                            System.out.println(c.getName());
                                            if(c.getName().equals("java.sql.PreparedStatement")){
                                                System.out.println(Arrays.asList(interfaces));
                                            }
                                        }
                                    }
                                }
                            }


//                Class[] interfaces =classBeingRedefined.getInterfaces();
//                System.out.println(className);
//                    for (Class c : interfaces) {
//                        System.out.println(c.getName());
//                        if (c.getName().equals("java.sql.PreparedStatement")) {
//                            System.out.println("Instrumenting executeQuery method " + ctClass.getName());
//                            for (CtMethod method : methods) {
//                                if (method.getName().equals("executeQuery")) {
//                                    this.testSignature(method);
//                                }
//                            }
//                        }
//                    }




//                }else{
//                    System.out.println("ClassName is null");
//                }

//                    }else{
//                        System.out.println("*************");
//                    }
//                }

//                        for (CtMethod method : methods) {
//                    if (method.getName().equals("executeQuery")) {
//                        Class[] interfaces =classBeingRedefined.getInterfaces();
////                        CtClass[] implementedInterfaces = ctClass.getInterfaces();
////                        if (implementedInterfaces.length != 0) {
////                            for (CtClass c : implementedInterfaces) {
////                        if(interfaces.length != 0){
//                            for (Class c : interfaces) {
//                                if (c.getName().equals("java.sql.PreparedStatement")) {
//                                    System.out.println("Instrumenting executeQuery method " + ctClass.getName());
//                                    this.testSignature(method);
//                                }else{
//                                    System.out.println("********");
//                                }
//                            }
////                        }
//                        }

//                  }
//                }
//                CtClass[] implementedInterfaces = ctClass.getInterfaces();
////                System.out.println(implementedInterfaces);
//                    if(implementedInterfaces.length != 0){
//                        for (CtClass c : implementedInterfaces) {
//                            System.out.println("*****************");
////                        System.out.println(Arrays.asList(c));
//                            if (c.getName().equals("java.sql.PreparedStatement")) {
//                                System.out.println("Instrumenting executeQuery method" + ctClass.getName());
//                                for (CtMethod method : methods) {
//                                    if (method.getName().equals("executeQuery")) {
//                                        this.testSignature(method);
//                                    }
//                                }
//                            }else{
//                                System.out.println("           *************88");
//                            }
//                        }
//                    }
//                    if (method.getName().equals("executeQuery")) {
//                        System.out.println("Instrumenting executeQuery method" + ctClass.getName());
//                        logger.info("Instrumenting executeQuery method in " + ctClass.getName());
//                        this.testSignature(method);
//                    }
                    }
//                }
//                    if (method.getName().equals("executeUpdate")) {
//                        this.testExecuteUpdateSignature(method);
//                    }
//                    this.injectSetVariableMethods(method);
//                    if (method.getName().equals("addBatch")) {
//                        this.reimplementQuery(method);
//                    }

//            }

                byteCode = ctClass.toBytecode();
                ctClass.detach();

        } catch (Throwable e) {
            e.printStackTrace();
        }
        return byteCode;
    }

    /**
     * Checks whether a given method's signature contains any parameters.
     * If contains, use the parameter as the query, else derive original query.
     * Instrument the passed method by inserting to the first line of method body
     * $1 stands for the first parameter in the method signature
     *
     * @param method currently processing method as a ctMethod object
     * @return {@code true} if the method signature contain parameters, {@code false} otherwise
     */
    private void testSignature(CtMethod method) {
        System.out.println("Instrumenting execute Query method....");
        String method_Signature = method.getSignature();
        if ((method_Signature.substring(method_Signature.indexOf('('),
                method_Signature.indexOf(')') + 1)).equals("()")) {
            try {
//                System.out.println("******************" + method_Signature + "*******************");
                logger.info("Test signature of executeQuery");
//                method.insertAt(
//                        1,
//                        true,
//                        "org.wso2.javaagent.JDBCAgentPublisher.publishEvents(" +
//                                "org.wso2.javaagent.JDBCAgentPublisher.getDataPublisher(), " +
//                                "org.wso2.javaagent.JDBCAgentPublisher.getStreamId(), " +
//                                "org.wso2.javaagent.JDBCAgentPublisher.getConnectionURL(), " +
//                                "org.wso2.javaagent.JDBCAgentPublisher.getOriginalQuery());");
                method.insertAt(1, true, "System.out.println(org.wso2.javaagent.JDBCAgentPublisher.getOriginalQuery());");
//                        "org.wso2.javaagent.JDBCAgentPublisher.getProducer().onData(" +
//                                "System.currentTimeMillis()," +
//                                "org.wso2.javaagent.JDBCAgentPublisher.getOriginalQuery());");
                org.wso2.javaagent.JDBCAgentPublisher.getProducer().onData(
                        System.currentTimeMillis(),
                        org.wso2.javaagent.JDBCAgentPublisher.getOriginalQuery());
                logger.info("instrumentation of executeQuery complete");
            } catch (CannotCompileException e) {
                e.printStackTrace();
            }
        } else {
            try {
//                method.insertAt(
//                        1,
//                        true,
//                        "if(!$1.startsWith(\"/*\")){" +
//                                "org.wso2.javaagent.JDBCAgentPublisher.publishEvents(" +
//                                "org.wso2.javaagent.JDBCAgentPublisher.getDataPublisher(), " +
//                                "org.wso2.javaagent.JDBCAgentPublisher.getStreamId(), " +
//                                "org.wso2.javaagent.JDBCAgentPublisher.getConnectionURL(), $1);" +
//                        "}");
                method.insertAt(1, true, "System.out.println($1);");

//                        "if(!$1.startsWith(\"/*\")){" +
//                                "org.wso2.javaagent.JDBCAgentPublisher.getProducer().onData(" +
//                                "System.currentTimeMillis()," +
//                                "$1);}");
                logger.info("instrumentation of executeQuery complete");
            } catch (CannotCompileException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Instrument Set methods used in batch processing by injecting following
     * line to first line of method body
     * Injected line would pass the second argument (actual variable value)
     * and uppercase name of the currently executing set method to a ListArray
     *
     * @param method currently processing method as a ctMethod object
     */
    private void injectSetVariableMethods(CtMethod method) {
        if (isInEnum(method.getName().toUpperCase(), SetMethods.class)) {
            try {
                method.insertAt(1, true,
                        "org.wso2.javaagent.JDBCAgentPublisher.fillArrayList(String.valueOf($2), " +
                        "Thread.currentThread().getStackTrace()[1].getMethodName().toUpperCase());"
                );
            } catch (CannotCompileException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Check whether method name available in enum
     *
     * @param value uppercase name of the processing method
     * @param enumClass enumeration class
     * @param <E> The enum type subclass
     * @return {@code true} if the method name in enum, {@code false} otherwise
     */
    public <E extends Enum<E>> boolean isInEnum(String value, Class<E> enumClass) {
        for (E enumValue : enumClass.getEnumConstants()) {
            if (enumValue.name().equals(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Instrument by adding following to the first line of method body.
     * Injected lines would publish the modified query to DAS with relevant parameters
     * 
     * @param method currently processing method as a ctMethod object
     */
    private void reimplementQuery(CtMethod method) {
        try {
            method.insertAt(
                    1,
                    true,
                    "String modifiedQuery " +
                            "= org.wso2.javaagent.JDBCAgentPublisher.modifyOriginalQuery();" +
                    "org.wso2.javaagent.JDBCAgentPublisher.publishEvents(" +
                            "org.wso2.javaagent.JDBCAgentPublisher.getDataPublisher(), " +
                            "org.wso2.javaagent.JDBCAgentPublisher.getStreamId(), " +
                            "org.wso2.javaagent.JDBCAgentPublisher.getConnectionURL(), " +
                            "modifiedQuery);");
        } catch (CannotCompileException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check the signature of executeUpdate method and instrument methods without any argument
     * and method with single argument. Call reimplementQuery, for methods which don not
     * contain any parameters
     * 
     * @param method currently processing method as a ctMethod object
     * @throws NotFoundException
     */
    private void testExecuteUpdateSignature(CtMethod method) throws NotFoundException {
        String methodSignature = method.getSignature();
        if ((methodSignature.substring(methodSignature.indexOf('('),
                methodSignature.indexOf(')') + 1)).equals("()")
                || StringUtils.countMatches(methodSignature, "(Ljava/lang/String;)") == 1) {
            if (StringUtils.countMatches(methodSignature, "(Ljava/lang/String;)") == 1) {
                try {
                    method.insertAt(
                            1,
                            true,
                            "org.wso2.javaagent.JDBCAgentPublisher.publishEvents(" +
                                    "org.wso2.javaagent.JDBCAgentPublisher.getDataPublisher(), " +
                                    "org.wso2.javaagent.JDBCAgentPublisher.getStreamId(), " +
                                    "org.wso2.javaagent.JDBCAgentPublisher.getConnectionURL(), " +
                                    "$1);");
                } catch (CannotCompileException e) {
                    e.printStackTrace();
                }
            } else {
                this.reimplementQuery(method);
            }
        }
    }

    /**
     * check getConnection method signature and instrument the method to load
     * org.wso2.javaagent.JDBCPublisher class, as it throws classDefNotFoundError
     * due to class loading conflict. Instrument the method that match the given signature.
     *
     * @param method currently processing method as a ctMethod object
     */
    private void testConnectSignature(CtMethod method) {
        String methodSignature = method.getSignature();

        if (methodSignature.equals(
                "(Ljava/lang/String;Ljava/util/Properties;Ljava/lang/Class;)Ljava/sql/Connection;"))
        {
            try {
                method.insertAt(
                        7,
                        true,
                        "ClassLoader classLoader = ClassLoader.getSystemClassLoader();\n" +
                           "try {\n" +
                           "Class publisherClass = classLoader.loadClass(" +
                                "\"org.wso2.javaagent.JDBCAgentPublisher\");\n" +
                           "Object publisherNewInstance = publisherClass.newInstance();\n" +
                           "java.lang.reflect.Method publisherMethod = " +
                                "publisherClass.getMethod(\"setConnectionURL\"," +
                                "new Class[] { String.class });\n" +
                           "publisherMethod.invoke(publisherNewInstance, new Object[] {$1});\n" +
                           "} catch (ClassNotFoundException e) {\n    e.printStackTrace();\n" +
                           "} catch (NoSuchMethodException e) {\n     e.printStackTrace();\n" +
                           "} catch (java.lang.reflect.InvocationTargetException e) {\n" +
                                   "    e.printStackTrace();\n" +
                           "} catch (InstantiationException e) {\n    e.printStackTrace();\n" +
                           "} catch (IllegalAccessException e) {\n    e.printStackTrace();\n" +
                           "}");
            } catch (CannotCompileException e) {
                e.printStackTrace();
            }
        }
    }

    public enum SetMethods {
        SETINT, SETDOUBLE, SETLONG, SETFLOAT, SETSTRING
    }
}
