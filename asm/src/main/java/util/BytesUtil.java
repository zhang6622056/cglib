package util;

import java.io.*;


/****
 * IO设置输出class类
 */
public class BytesUtil {


    /****
     * 读取class文件
     * @param path
     * @return
     */
    public static byte[] getClassBytes(String path){
        InputStream ins = null;
        try {
            ins = new FileInputStream(path);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int bufferSize = 4096;
            byte[] buffer = new byte[bufferSize];
            int bytesNumRead = 0;
            while ((bytesNumRead = ins.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesNumRead);
            }
            return baos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /****
     * 输出Java class文件
     * @param path
     */
    public static void outputJavaClassFile(String path,byte[] bytes) throws IOException {
        if (null == path) return;
        FileOutputStream fileOutputStream = new FileOutputStream(path);
        fileOutputStream.write(bytes);
        fileOutputStream.flush();
        fileOutputStream.close();
    }







}
