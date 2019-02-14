package proxybean;

public class CallbackBean {

    public void methodForDispatcher(){
        System.out.println("methodForDispatcher...");
    }

    public void methodForFixValue(){
        System.out.println("methodForFixValue...");
    }

    public void methodForInvocationHandler(){
        System.out.println("methodForInvocationHandler...");
    }

    public void methodForLazy(){
        System.out.println("methodForLazy...");
    }

    public void methodForInterceptor(){
        System.out.println("methodForInterceptor...");
    }

    public void methodForNoop(){
        System.out.println("methodForNoop...");
    }
}
