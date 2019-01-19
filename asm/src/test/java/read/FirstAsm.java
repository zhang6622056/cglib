package read;


import org.objectweb.asm.ClassVisitor;

/**
 * Created by Nero on 2019-01-18.
 */
public class FirstAsm extends ClassVisitor {
    public FirstAsm(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }
}
