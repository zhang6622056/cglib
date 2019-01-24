package test;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 *
 * Created by Nero on 2019-01-17.
 */
public class test implements MethodInterceptor{


    public static void main(String[] args) {
        People people = new People();
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(people.getClass());      //设置被代理类
        test t = new test();
        enhancer.setCallback(t);         //
        People people1 = (People) enhancer.create();
        people1.sayHello();
    }


    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        System.out.println("befQore Hello...");
        proxy.invokeSuper(obj,args);



//        method.invoke(obj);
        return null;
    }
}
