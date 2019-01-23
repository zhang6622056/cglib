import loaders.AsmClassLoader;
import util.BytesUtil;

public class AsmApplication {






    public static void main(String[] args) throws ClassNotFoundException {
        String classFileName = "";
        byte[] bytes = BytesUtil.getClassBytes(classFileName);


        AsmClassLoader asmClassLoader = new AsmClassLoader(bytes);
        String classPackagePath = "";
        Class clazz = asmClassLoader.loadClass(classPackagePath);

        System.out.println(clazz.getName());
    }



}
