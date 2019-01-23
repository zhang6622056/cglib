package example;

/**
 * asm字节码加载器
 * Created by Nero on 2019-01-22.
 */
public class AsmClassLoader extends ClassLoader{


    private byte[] bytes;

    public AsmClassLoader(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class clazz = defineClass(name,bytes,0,bytes.length,null);
        return clazz;
    }





}
