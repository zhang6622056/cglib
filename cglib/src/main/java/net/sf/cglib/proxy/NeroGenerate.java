package net.sf.cglib.proxy;

import net.sf.cglib.core.AbstractClassGenerator;
import org.objectweb.asm.ClassVisitor;

public class NeroGenerate {











    class Generator extends AbstractClassGenerator{
        protected Generator(Source source){
            super(source);
        }



        protected ClassLoader getDefaultClassLoader() {
            return null;
        }

        protected Object firstInstance(Class type) throws Exception {
            return null;
        }

        protected Object nextInstance(Object instance) throws Exception {
            return null;
        }

        public void generateClass(ClassVisitor v) throws Exception {

        }
    }



}
