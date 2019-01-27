package callbacks;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;


/****
 * 生成代理类字节码。对方法进行拦截调用
 */
public class MethodInterceptorCallback implements MethodInterceptor {
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        System.out.println("before invoke........");
        proxy.invokeSuper(obj,args);
        System.out.println("after invoke.........");
        return null;
    }
}
