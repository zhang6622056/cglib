package write;


import example.AsmClassLoader;
import jdk.internal.org.objectweb.asm.Opcodes;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.io.*;

/**
 *
 *
 *
 * Created by nero on 2019-01-18.
 */
public class ClassGenerater {





    public static void main(String[] args) throws ClassNotFoundException, IOException {
        //生成二进制字节码
        ClassGenerater classGenerater = new ClassGenerater();
        //byte[] datas = classGenerater.generateByteClass();
        byte[] datas = classGenerater.getClassData("C://Users/admin/Desktop/QueryRateRequest.class");

        AsmClassLoader asmClassLoader = new AsmClassLoader(datas);
        Class clazz = asmClassLoader.loadClass("com.secoo.overseas.easyship.entity.QueryRateRequest");
        System.out.println(clazz.getName());
    }


    /****
     * 使用ASM动态生成字节码文件
     */
    private byte[] generateByteClass(){
        ClassWriter classWriter = new ClassWriter(0);
        classWriter.visit(Opcodes.V1_8,Opcodes.ACC_PUBLIC,"focus/nero/UserBean",null,"java/lang/Object",null);
        //设置一个字段
        classWriter.visitField(Opcodes.ACC_PRIVATE,"username","Ljava/lang/String;",null,null);
        //设置方法
        MethodVisitor methodVisitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC,"setUsername","(Ljava/lang/String;)V",null,null);
        //动态方法体
        methodVisitor.visitCode();
        methodVisitor.visitEnd();
        byte[] data = classWriter.toByteArray();
        outputFile(data);
        return data;
    }


    /****
     * 导出file
     * @param data
     */
    private void outputFile(byte[] data){
        try {
            FileOutputStream fos = new FileOutputStream(new File("C://Users/admin/Desktop/UserBean.class"));
            fos.write(data);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /****
     * 获取文件byte数组
     * @param path
     * @return
     * @throws IOException
     */
    private byte[] getFileBytes(String path) throws IOException {
        FileInputStream fileIn = new FileInputStream(path);
        byte[] bb = new byte[2048];
        fileIn.read(bb);
        fileIn.close();
        return bb;
    }






    private byte[] getClassData(String path) {
        try {
            InputStream ins = new FileInputStream(path);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int bufferSize = 4096;
            byte[] buffer = new byte[bufferSize];
            int bytesNumRead = 0;
            while ((bytesNumRead = ins.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesNumRead);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }









}
