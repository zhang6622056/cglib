package enhancer.CURRENT;

import net.sf.cglib.proxy.Enhancer;

public class CurrentRun {


    public static void main(String[] args) throws InterruptedException {
        Enhancer e = new Enhancer();

        Thread t = new Thread(new Runnable() {
            public void run() {
                Enhancer e1 = new Enhancer();
            }
        });
        t.start();
        t.join();

    }


}
