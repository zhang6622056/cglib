package test;

/**
 * Created by admin on 2019-01-17.
 */
public class People {

    private boolean isLazy = false;





    public void sayHello(){
        System.out.println("hello........");
    }


    public boolean isLazy() {
        System.out.println("getlazy.....");
        return isLazy;
    }

    public void setLazy(boolean lazy) {
        isLazy = lazy;
    }
}
