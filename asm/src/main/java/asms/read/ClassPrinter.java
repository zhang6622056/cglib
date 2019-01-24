package asms.read;

import org.objectweb.asm.ClassVisitor;

public class ClassPrinter extends ClassVisitor {
    public ClassPrinter(int api) {
        super(api);
    }


    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        System.out.println("this class name is "+name);
    }
}
