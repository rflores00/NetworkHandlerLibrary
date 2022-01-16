package cl.rf.compiler;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

public class RepositoriesFileWriter {
    private JavaFileObject f;
    private PrintWriter pw;
    private static String subF = "Repo";
    private final ProcessingEnvironment processingEnv;
    private Writer w;

    public RepositoriesFileWriter(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
    }


    public void init(String interfaceName){
        interfaceName += subF;
        try {
            f = processingEnv.getFiler().
                    createSourceFile("cl.rf.networkhandler.autogenerate."+ interfaceName);
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                    "Creating " + f.toUri());

            w = f.openWriter();
            pw = new PrintWriter(w);

            pw.println("package cl.rf.networkhandler.autogenerate;");
            pw.println("import androidx.lifecycle.LiveData;");
            pw.println("import cl.rf.networkhandler.model.Resource;");
            pw.println("");
            pw.println("public class " + interfaceName + "{");
            pw.println("");



        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void addMethod(String methodName, String methodModel, String returnMethodName, ExecutableElement method){
        pw.println(getMethodLine(methodModel, methodName, method));
        pw.println(getMethodReturnLine(returnMethodName, method));
        pw.println("    }");
    }

    private String getMethodLine(String methodModel, String methodName, ExecutableElement method){
        StringBuilder line = new StringBuilder("    public static LiveData<Resource<" + methodModel + ">>" + methodName + "(");

        for(VariableElement variableElement : method.getParameters()){
            String type = variableElement.asType().toString();
            type = type.substring(type.lastIndexOf(".")+1);
            line.append(type).append(" ").append(variableElement.getSimpleName().toString()).append(", ");

        }

        if(line.toString().endsWith(" "))
            line = new StringBuilder(line.substring(0, line.length() - 2));
        line.append("){");

        return line.toString();
    }

    private String getMethodReturnLine(String returnMethodName, ExecutableElement method){
        StringBuilder line = new StringBuilder("        return new " + returnMethodName + "().request(");
        int i = 0;
        for(VariableElement variableElement : method.getParameters()){
            line.append(variableElement.getSimpleName().toString()).append(", ");
        }

        if(line.toString().endsWith(" "))
            line = new StringBuilder(line.substring(0, line.length() - 2));
        line.append(");");

        return line.toString();

    }
    public void endFile(){
        pw.println("}");
        pw.flush();
        try {
            w.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
