package asms;

import jdk.internal.org.objectweb.asm.Opcodes;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/****
 * 通过ASM字节码生成class文件
 */
public class ClassGenerater {
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
}
