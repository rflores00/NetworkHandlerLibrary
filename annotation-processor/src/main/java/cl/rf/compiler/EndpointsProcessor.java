package cl.rf.compiler;

import com.sun.org.apache.xalan.internal.xsltc.compiler.CompilerException;
import com.sun.source.util.Trees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.util.Context;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.lang.annotation.ElementType;
import java.util.Collections;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;

import cl.rf.annotation.Endpoints;

public class EndpointsProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        Set<? extends Element> elementsToBind = roundEnv.getElementsAnnotatedWith(Endpoints.class);
        elementsToBind.forEach(element -> {
            RepositoriesFileWriter r = new RepositoriesFileWriter(processingEnv);
            r.init(element.getSimpleName().toString());

            String baseUrl = element.getAnnotation(Endpoints.class).baseUrl();

            if(baseUrl.isEmpty()) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Note: The url field must not be empty", element);
            }
            if(!baseUrl.startsWith("http")) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,"Note: The url must starts with http or https");
            }
            if(!baseUrl.endsWith("/")) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,"Note: The url must ends with / ");
            }

            for(Element methodDeclaration : element.getEnclosedElements()){
                if(methodDeclaration instanceof ExecutableElement){
                    ExecutableElement method = (ExecutableElement) methodDeclaration;

                    //String className = element.getSimpleName().toString()+"_"+method.getSimpleName().toString()
                    String interfaceName = getPackage(element) +"."+element.getSimpleName().toString();
                    String className = "Endpoint_"+method.getSimpleName().toString();
                    Model model = getModel(method.getReturnType().toString(), className, interfaceName, getMethodLine(method), baseUrl);

                    r.addMethod(method.getSimpleName().toString(), model.modelPackage +"."+model.modelName, className, method);
                    generateEndpointFile(model);
                }

            }

            r.endFile();

        });

        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(
                Endpoints.class.getCanonicalName());
    }
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


    private void message(String message){
        Messager messager = processingEnv.getMessager();
        messager.printMessage(Diagnostic.Kind.NOTE, message);
    }

    private PackageElement getPackage(Element element) {
        while (element.getKind() != ElementKind.PACKAGE) {
            element = element.getEnclosingElement();
        }

        return (PackageElement) element;
    }

    private void generateEndpointFile(Model model){
        try {
            JavaFileObject f = processingEnv.getFiler().
                    createSourceFile("cl.rf.networkhandler.autogenerate."+ model.className);
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                    "Creating " + f.toUri());
            try (Writer w = f.openWriter()) {
                PrintWriter pw = new PrintWriter(w);

                pw.println("package cl.rf.networkhandler.autogenerate;");
                pw.println("import androidx.annotation.NonNull;");
                pw.println("import androidx.lifecycle.LiveData;");
                pw.println("import cl.rf.networkhandler.core.GenericRepository;");
                pw.println("import cl.rf.networkhandler.core.ApiResponse;");
                pw.println("");
                pw.println("//Modelo para manejar la respuesta del API");
                pw.println("import " + model.modelPackage + "." + model.modelName + ";");
                pw.println("");
                pw.println("public class " + model.className + " extends GenericRepository<" + model.modelName + "> {");
                pw.println("");
                pw.println("    @NonNull");
                pw.println("    @Override");
                pw.println("    protected LiveData<ApiResponse<" + model.modelName + ">> attachedEndpoint() {");
                pw.println("        return cl.rf.networkhandler.core.NetworkClient.builder()");
                pw.println("        .baseUrl(\""+ model.baseURL +"\")");
                pw.println("        .showLogs(true)");
                pw.println("        .build()");
                pw.println("        .create(" + model.interfaceName + ".class)");
                pw.println("        "+ model.methodLine);

                pw.println("    }");
                pw.println("}");
                pw.flush();
            }
        } catch (IOException x) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                    x.toString());
        }
    }

    private String getMethodLine(ExecutableElement method){
        StringBuilder line = new StringBuilder("." + method.getSimpleName().toString() + "(");
        int i=0;
        for(VariableElement variableElement : method.getParameters()){
            String type = variableElement.asType().toString();
            type = type.substring(type.lastIndexOf(".")+1);
            line.append("(").append(type).append(") params.get(").append(i).append("), ");

            i++;
        }
        if(line.toString().endsWith(" "))
            line = new StringBuilder(line.substring(0, line.length() - 2));
        line.append(");");

        return line.toString();
    }

    private Model getModel(String type, String className, String interfaceName, String methodLine, String baseURL){
        String subText = type.replace("androidx.lifecycle.LiveData<cl.rf.networkhandler.core.ApiResponse","");
        subText = subText.substring(0, subText.indexOf(">"));
        String modelPackage = subText.substring(1, subText.lastIndexOf("."));
        String modelName = subText.substring(subText.lastIndexOf(".")+1);

        return new Model(className, modelName, modelPackage, interfaceName, methodLine, baseURL);
    }

    private static class Model{
        private final String className;
        private final String modelName;
        private final String modelPackage;
        private final String interfaceName;
        private final String methodLine;
        private final String baseURL;

        public Model(String className, String modelName, String modelPackage, String interfaceName, String methodLine, String baseURL) {
            this.className = className;
            this.modelName = modelName;
            this.modelPackage = modelPackage;
            this.interfaceName = interfaceName;
            this.methodLine = methodLine;
            this.baseURL = baseURL;
        }
    }

    protected static JavaFileManager getJavaFileManager(ProcessingEnvironment env) {
        JavaFileManager fm = null;

        if (env instanceof JavacProcessingEnvironment) {
            Context context = ((JavacProcessingEnvironment) env).getContext();

            fm = context.get(JavaFileManager.class);
        }

        return fm;
    }
}
