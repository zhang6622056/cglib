package callbacks;

import net.sf.cglib.proxy.InvocationHandler;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;





public class InvocationHandlerCallback implements InvocationHandler {
    /*****
     * invocationHandler的invoke方法传入的method和proxy都是代理本身对象
     * 切忌重复调用，会循环调用
     * @param proxy 代理类本身
     * @param method 代理类内部的方法
     * @param args  参数
     * @return
     * @throws Throwable
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("invocationHandlerCallback Before....");
        method.invoke(proxy.getClass().getSuperclass().newInstance(),args);
        //会无限循环
        //method.invoke(proxy,args);
        System.out.println("invocationHandlerCallback after....");
        return null;
    }
}
