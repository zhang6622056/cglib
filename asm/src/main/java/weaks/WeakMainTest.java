package weaks;

import simple.BasicBean;

import java.lang.ref.WeakReference;


/*****
 * WeakReference 弱引用测试
 *
 *
 */
public class WeakMainTest {


    public static void main(String[] args) {
        WeakReference<String> wr = new WeakReference<String>(new String("2"));
        System.gc();
        try {
            Thread.sleep(1000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("weak Reference:"+wr.get());
    }



}
