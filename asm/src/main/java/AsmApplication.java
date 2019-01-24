import asms.read.ClassPrinter;
import org.objectweb.asm.ClassReader;

import java.io.IOException;

public class AsmApplication {


    public static void main(String[] args) throws IOException {
        ClassReader classReader = new ClassReader("simple.BasicBean");
        ClassPrinter classPrinter = new ClassPrinter(458752);
        classReader.accept(classPrinter,458752);
    }











}
