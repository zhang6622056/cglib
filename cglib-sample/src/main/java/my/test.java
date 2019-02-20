package my;


import callbacks.*;
import net.sf.cglib.proxy.*;
import proxybean.People;

import java.lang.reflect.Method;

/**
 *
 * Created by Nero on 2019-01-17.
 */
public class test{

    /*****
     *
     *
     * AEnh
     *
     * @param args
     */
    public static void main(String[] args) {
        //尝试使用Enhancer代理类
        People people = new People();
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(people.getClass());      //设置被代理类
        enhancer.setCallbacks(initCallBacks());
        enhancer.setCallbackFilter(initCallbackFilter());
//        enhancer.setCallback(NoOp.INSTANCE);
        enhancer.setUseCache(true);
        People people1 = (People) enhancer.create();
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
        DispatcherCallBack dispatcherCallBack = new DispatcherCallBack();
        Callback[] callbacks = new Callback[]{NoOp.INSTANCE,methodInterceptorCallback,lazyLoaderCallback,dispatcherCallBack,invocationHandlerCallback,fixValueCallback};
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
                //return 4;
                //return 2;
                return 1;
                //return 0;
            }
        };
    }

}
