package proxybean;

/**
 * Created by admin on 2019-01-17.
 */
public class People {

    private boolean isLazy = false;





    public void sayHello(){
        System.out.println("hello........");
    }

    //私有方法
    private void sayHelloPrivate(){
        System.out.println("sayHelloPrivate.....");
    }

    //protect方法
    protected void sayHelloProtect(){
        System.out.println("sayHelloProtect");
    }

    //static方法
    static void sayHelloStatic(){
        System.out.println("sayHelloStatic");
    }





    public boolean isLazy() {
        System.out.println("getlazy.....");
        return isLazy;
    }

    public void setLazy(boolean lazy) {
        isLazy = lazy;
    }
}
