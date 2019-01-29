package classnew;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class FutureTaskLearn {


    public static void main(String[] args) throws InterruptedException, ExecutionException {


        FutureTask<String> futureTask = new FutureTask<String>(new Callable<String>() {
            public String call() throws Exception {
                System.out.println(Thread.currentThread().getName());
                return "1234";
            }
        });


        futureTask.run();
        String a = futureTask.get();
        System.out.println("_____"+Thread.currentThread().getName());

        Thread.sleep(1000);

        System.out.println(a);
        System.out.println(futureTask);

    }







}
