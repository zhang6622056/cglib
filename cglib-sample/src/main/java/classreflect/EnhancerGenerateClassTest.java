package classreflect;

import org.objectweb.asm.Opcodes;
import proxybean.People;

import java.lang.reflect.Method;

public class EnhancerGenerateClassTest {





    public static void main(String[] args) throws Exception {
        EnhancerGenerateClassTest enhancerGenerateClassTest = new EnhancerGenerateClassTest();
        enhancerGenerateClassTest.generateClass(People.class,"sayHello");
    }


    private void generateClass(Class clazz,String methodName) throws Exception {
            if ((clazz.getModifiers() & Opcodes.ACC_FINAL) != 0) throw new Exception("can't proxy the class with final modifier");

            Method method = clazz.getMethod(methodName);
            if ((method.getModifiers()
                    & Opcodes.ACC_FINAL
                    & Opcodes.ACC_PRIVATE
                    & Opcodes.ACC_STATIC) != 0){
                throw new Exception("can't proxy the method with final modifier");
            }

            System.out.println(method.getModifiers());
    }




















}
