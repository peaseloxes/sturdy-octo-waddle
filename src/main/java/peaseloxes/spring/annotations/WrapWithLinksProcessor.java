package peaseloxes.spring.annotations;

import java.util.Set;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

/**
 * @author peaseloxes
 */
@SupportedAnnotationTypes({
        "peaseloxes.spring.annotations.WrapWithLink",
        "peaseloxes.spring.annotations.WrapWithLinks"
})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class WrapWithLinksProcessor extends AbstractProcessor {

    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        System.out.println("Running WrapWithLinks Processor.. please stand by while your @WrapWithLink annotations are being scrutinized.");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement te : annotations) {
            for (Element e : roundEnv.getElementsAnnotatedWith(te)) {
                if (!(e.getKind().equals(ElementKind.METHOD))) {
                    error(e, "@WrapWithLink must be used on methods or fields only");
                }
                if(e instanceof ExecutableElement) {
                    ExecutableElement executableElement = (ExecutableElement) e;
                    if (executableElement.getReturnType().getKind() == TypeKind.DECLARED) {
                        DeclaredType type = (DeclaredType) executableElement.getReturnType();
                        for (TypeMirror typeMirror : type.getTypeArguments()) {
                            if (!typeMirror.toString().contains("HateoasResponse")) {
                                error(e, "@WrapWithLink must be used on methods with a return type that has a HateoasResponse type parameter");
                            }
                        }
                    } else {
                        error(e, "@WrapWithLink must be used on methods with a declared return type");
                    }
                    boolean containsRequest = false;
                    for (VariableElement variableElement : executableElement.getParameters()) {
                        if (variableElement.asType().toString().equals("javax.servlet.http.HttpServletRequest")) {
                            containsRequest = true;
                        }
                    }
                    if (!containsRequest) {
                        error(e, "@WrapWithLink must be used on methods with a HttpServletRequest parameter.");
                    }
                }
            }
        }
        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /**
     * Will cause maven build to go boom.
     *
     * @param e    the element the error occurred on.
     * @param msg  the message to print.
     * @param args optional args.
     */
    private void error(final Element e, final String msg, final Object... args) {
        messager.printMessage(
                Diagnostic.Kind.ERROR,
                String.format(msg, args),
                e);
    }
}
