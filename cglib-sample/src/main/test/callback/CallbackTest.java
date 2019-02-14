package callback;

import callbacks.*;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;
import org.junit.Test;
import proxybean.CallbackBean;
import java.lang.reflect.Method;

public class CallbackTest {

    @Test
    public void testCallback(){
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(CallbackBean.class);
        enhancer.setCallbacks(initCallBacks());
        enhancer.setCallbackFilter(initCallbackFilter());
        CallbackBean callbackBean = (CallbackBean) enhancer.create();

        callbackBean.methodForNoop();
        callbackBean.methodForInterceptor();
        callbackBean.methodForLazy();
        callbackBean.methodForDispatcher();
        callbackBean.methodForInvocationHandler();
        callbackBean.methodForFixValue();
    }





    /****
     * 初始化callback拦截数组
     * @return
     */
    private static final Callback[] initCallBacks(){
        MethodInterceptorCallback methodInterceptorCallback = new MethodInterceptorCallback();
        LazyLoaderCallback lazyLoaderCallback = new LazyLoaderCallback();
        InvocationHandlerCallback invocationHandlerCallback = new InvocationHandlerCallback();
        FixValueCallback fixValueCallback = new FixValueCallback();
        DispatcherCallBack dispatcherCallBack = new DispatcherCallBack();
        Callback[] callbacks = new Callback[]{NoOp.INSTANCE,methodInterceptorCallback,lazyLoaderCallback,dispatcherCallBack,invocationHandlerCallback,fixValueCallback};
        return callbacks;
    }



    private static final CallbackFilter initCallbackFilter(){
        return new CallbackFilter() {
            public int accept(Method method) {
                if (method.getName().equals("methodForNoop")){
                    return 0;
                }
                if (method.getName().equals("methodForInterceptor")){
                    return 1;
                }
                if (method.getName().equals("methodForLazy")){
                    return 2;
                }
                if (method.getName().equals("methodForDispatcher")){
                    return 3;
                }
                if (method.getName().equals("methodForInvocationHandler")){
                    return 4;
                }
                if (method.getName().equals("methodForFixValue")){
                    return 5;
                }
                return 0;
            }
        };

    }
}
