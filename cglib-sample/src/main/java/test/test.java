package test;


import callbacks.FixValueCallback;
import callbacks.InvocationHandlerCallback;
import callbacks.LazyLoaderCallback;
import callbacks.MethodInterceptorCallback;
import net.sf.cglib.proxy.*;

import java.lang.reflect.Method;

/**
 *
 * Created by Nero on 2019-01-17.
 */
public class test{


    public static void main(String[] args) {
        People people = new People();
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(people.getClass());      //设置被代理类
        enhancer.setCallbacks(initCallBacks());
        enhancer.setCallbackFilter(initCallbackFilter());
        People people1 = (People) enhancer.create();
        System.out.println(people.toString());
        System.out.println(people1.isLazy());
    }


    /****
     * 初始化callback拦截数组
     * @return
     */
    private static Callback[] initCallBacks(){
        MethodInterceptorCallback methodInterceptorCallback = new MethodInterceptorCallback();
        LazyLoaderCallback lazyLoaderCallback = new LazyLoaderCallback();
        InvocationHandlerCallback invocationHandlerCallback = new InvocationHandlerCallback();
        FixValueCallback fixValueCallback = new FixValueCallback();
        Callback[] callbacks = new Callback[]{NoOp.INSTANCE,methodInterceptorCallback,lazyLoaderCallback,invocationHandlerCallback,fixValueCallback};
        return callbacks;
    }


    /****
     *
     * 初始化callback filter
     * @return
     */
    private static CallbackFilter initCallbackFilter(){
        return new CallbackFilter() {
            public int accept(Method method) {
//                if (!method.getName().contains("say")){
//                    return 0;
//                }
//                return 0;
 //               return 3;
                return 4;
                //return 2;
            }
        };
    }

}
