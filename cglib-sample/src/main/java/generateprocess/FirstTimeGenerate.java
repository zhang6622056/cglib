package generateprocess;

import net.sf.cglib.core.AbstractClassGenerator;
import org.objectweb.asm.ClassVisitor;

public class FirstTimeGenerate extends AbstractClassGenerator {

    protected FirstTimeGenerate(Source source) {
        super(source);
    }

    protected ClassLoader getDefaultClassLoader() {






        return null;
    }







    public void generateClass(ClassVisitor v) throws Exception {










    }



    //
    protected Object firstInstance(Class type) throws Exception {
        return type.newInstance();
    }

    protected Object nextInstance(Object instance) throws Exception {
        return instance;
    }





}