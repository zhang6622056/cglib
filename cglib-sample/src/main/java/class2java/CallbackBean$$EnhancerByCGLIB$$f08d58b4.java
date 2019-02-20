package class2java;

import net.sf.cglib.core.ReflectUtils;
import net.sf.cglib.core.Signature;
import net.sf.cglib.proxy.*;
import proxybean.CallbackBean;

import java.lang.reflect.Method;

public class CallbackBean$$EnhancerByCGLIB$$f08d58b4 extends CallbackBean implements Factory {

    //存储当前线程的callbacks
    private static ThreadLocal CGLIB$THREAD_CALLBACKS;
    //默认的callbacks
    private static Callback[] CGLIB$STATIC_CALLBACKS;
    //控制只绑定一次callbacks
    private boolean CGLIB$BOUND;



    private static Object[] CGLIB$emptyArgs;
    private static Method CGLIB$methodForInterceptor$1$Method;
    private static MethodProxy CGLIB$methodForInterceptor$1$Proxy;
    private static Method CGLIB$methodForInvocationHandler$4;
    private Object CGLIB$LAZY_LOADER_2;


    //维护相应的callback
    private NoOp CGLIB$CALLBACK_0;
    private MethodInterceptor CGLIB$CALLBACK_1;
    private LazyLoader CGLIB$CALLBACK_2;
    private Dispatcher CGLIB$CALLBACK_3;
    private InvocationHandler CGLIB$CALLBACK_4;
    private FixedValue CGLIB$CALLBACK_5;


    static void CGLIB$STATICHOOK1() throws ClassNotFoundException, NoSuchMethodException {
        CGLIB$THREAD_CALLBACKS = new ThreadLocal();
        CGLIB$emptyArgs = new Object[0];
        Class var0 = Class.forName("proxybean.CallbackBean$$EnhancerByCGLIB$$f08d58b4");
        Class var1;
        CGLIB$methodForInterceptor$1$Method = ReflectUtils.findMethods(new String[]{"methodForInterceptor", "()V"}, (var1 = Class.forName("proxybean.CallbackBean")).getDeclaredMethods())[0];
        CGLIB$methodForInterceptor$1$Proxy = MethodProxy.create(var1, var0, "()V", "methodForInterceptor", "CGLIB$methodForInterceptor$1");
        CGLIB$methodForInvocationHandler$4 = Class.forName("proxybean.CallbackBean").getDeclaredMethod("methodForInvocationHandler");
    }



    public CallbackBean$$EnhancerByCGLIB$$f08d58b4() {
        CGLIB$BIND_CALLBACKS(this);
    }




    final void CGLIB$methodForInterceptor$1() {
        super.methodForInterceptor();
    }



    public final void methodForInterceptor() {
        MethodInterceptor var10000 = this.CGLIB$CALLBACK_1;

        //通过CGLIB$BIND_CALLBACKS，对CALLBACK赋值
        if (var10000 == null) {
            //首先验证ThreadLocal内有没有callbacks,然后验证静态成员是否有callbacks
            CGLIB$BIND_CALLBACKS(this);
            var10000 = this.CGLIB$CALLBACK_1;
        }

        if (var10000 != null) {
            try {
                var10000.intercept(this, CGLIB$methodForInterceptor$1$Method, CGLIB$emptyArgs, CGLIB$methodForInterceptor$1$Proxy);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        } else {
            super.methodForInterceptor();
        }
    }



    public final void methodForDispatcher() {
        Dispatcher var10000 = this.CGLIB$CALLBACK_3;
        if (var10000 == null) {
            CGLIB$BIND_CALLBACKS(this);
            var10000 = this.CGLIB$CALLBACK_3;
        }

        try {
            ((CallbackBean)var10000.loadObject()).methodForDispatcher();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final void methodForInvocationHandler() {
        try {
            InvocationHandler var10000 = this.CGLIB$CALLBACK_4;
            if (var10000 == null) {
                CGLIB$BIND_CALLBACKS(this);
                var10000 = this.CGLIB$CALLBACK_4;
            }

            var10000.invoke(this, CGLIB$methodForInvocationHandler$4, new Object[0]);
        } catch (RuntimeException var1) {
            throw var1;
        } catch (Throwable var2) {
            throw new UndeclaredThrowableException(var2);
        }
    }



    public final void methodForFixValue() {
        FixedValue var10000 = this.CGLIB$CALLBACK_5;
        if (var10000 == null) {
            CGLIB$BIND_CALLBACKS(this);
            var10000 = this.CGLIB$CALLBACK_5;
        }

        try {
            var10000.loadObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }






    public static MethodProxy CGLIB$findMethodProxy(Signature var0) {
        String var10000 = var0.toString();
        switch(var10000.hashCode()) {
            case 2054592888:
                if (var10000.equals("methodForInterceptor()V")) {
                    return CGLIB$methodForInterceptor$1$Proxy;
                }
        }

        return null;
    }


    public final void methodForLazy() {
        ((CallbackBean)this.CGLIB$LOAD_PRIVATE_2()).methodForLazy();
    }




    private final synchronized Object CGLIB$LOAD_PRIVATE_2() {
        Object var10000 = this.CGLIB$LAZY_LOADER_2;
        if (var10000 == null) {
            LazyLoader var10001 = this.CGLIB$CALLBACK_2;
            if (var10001 == null) {
                CGLIB$BIND_CALLBACKS(this);
                var10001 = this.CGLIB$CALLBACK_2;
            }

            try {
                var10000 = this.CGLIB$LAZY_LOADER_2 = var10001.loadObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return var10000;
    }











    //ThreadLocal缓存callbacks
    public static void CGLIB$SET_THREAD_CALLBACKS(Callback[] var0) {
        CGLIB$THREAD_CALLBACKS.set(var0);
    }
    //静态指向callbacks
    public static void CGLIB$SET_STATIC_CALLBACKS(Callback[] var0) {
        CGLIB$STATIC_CALLBACKS = var0;
    }


    //绑定callbacks
    //首先验证ThreadLocal内有没有callbacks,然后验证静态成员是否有callbacks
    private static final void CGLIB$BIND_CALLBACKS(Object var0) {
        CallbackBean$$EnhancerByCGLIB$$f08d58b4 var1 = (CallbackBean$$EnhancerByCGLIB$$f08d58b4)var0;
        if (!var1.CGLIB$BOUND) {
            var1.CGLIB$BOUND = true;
            Object var10000 = CGLIB$THREAD_CALLBACKS.get();
            if (var10000 == null) {
                var10000 = CGLIB$STATIC_CALLBACKS;
                if (var10000 == null) {
                    return;
                }
            }

            Callback[] var10001 = (Callback[])var10000;
            var1.CGLIB$CALLBACK_5 = (FixedValue)((Callback[])var10000)[5];
            var1.CGLIB$CALLBACK_4 = (InvocationHandler)var10001[4];
            var1.CGLIB$CALLBACK_3 = (Dispatcher)var10001[3];
            var1.CGLIB$CALLBACK_2 = (LazyLoader)var10001[2];
            var1.CGLIB$CALLBACK_1 = (MethodInterceptor)var10001[1];
            var1.CGLIB$CALLBACK_0 = (NoOp)var10001[0];
        }
    }






    public Object newInstance(Callback callback) {
        return null;
    }



    //callback数组的newInstance实现
    public Object newInstance(Callback[] callbacks) {
        CGLIB$SET_THREAD_CALLBACKS(callbacks);
        CallbackBean$$EnhancerByCGLIB$$f08d58b4 var10000 = new CallbackBean$$EnhancerByCGLIB$$f08d58b4();
        CGLIB$SET_THREAD_CALLBACKS((Callback[])null);
        return var10000;
    }




    public Object newInstance(Class[] var1, Object[] var2, Callback[] var3) {
        CGLIB$SET_THREAD_CALLBACKS(var3);
        CallbackBean$$EnhancerByCGLIB$$f08d58b4 var10000 = new CallbackBean$$EnhancerByCGLIB$$f08d58b4();
        switch(var1.length) {
            case 0:
                CGLIB$SET_THREAD_CALLBACKS((Callback[])null);
                return var10000;
            default:
                throw new IllegalArgumentException("Constructor not found");
        }
    }





    public Callback[] getCallbacks() {
        CGLIB$BIND_CALLBACKS(this);
        return new Callback[]{this.CGLIB$CALLBACK_0, this.CGLIB$CALLBACK_1, this.CGLIB$CALLBACK_2, this.CGLIB$CALLBACK_3, this.CGLIB$CALLBACK_4, this.CGLIB$CALLBACK_5};
    }




    public Callback getCallback(int var1) {
        CGLIB$BIND_CALLBACKS(this);
        Object var10000;
        switch(var1) {
            case 0:
                var10000 = this.CGLIB$CALLBACK_0;
                break;
            case 1:
                var10000 = this.CGLIB$CALLBACK_1;
                break;
            case 2:
                var10000 = this.CGLIB$CALLBACK_2;
                break;
            case 3:
                var10000 = this.CGLIB$CALLBACK_3;
                break;
            case 4:
                var10000 = this.CGLIB$CALLBACK_4;
                break;
            case 5:
                var10000 = this.CGLIB$CALLBACK_5;
                break;
            default:
                var10000 = null;
        }

        return (Callback)var10000;
    }


    public void setCallback(int var1, Callback var2) {
        switch(var1) {
            case 0:
                this.CGLIB$CALLBACK_0 = (NoOp)var2;
                break;
            case 1:
                this.CGLIB$CALLBACK_1 = (MethodInterceptor)var2;
                break;
            case 2:
                this.CGLIB$CALLBACK_2 = (LazyLoader)var2;
                break;
            case 3:
                this.CGLIB$CALLBACK_3 = (Dispatcher)var2;
                break;
            case 4:
                this.CGLIB$CALLBACK_4 = (InvocationHandler)var2;
                break;
            case 5:
                this.CGLIB$CALLBACK_5 = (FixedValue)var2;
        }

    }

    //设置相应的callback
    public void setCallbacks(Callback[] var1) {
        this.CGLIB$CALLBACK_0 = (NoOp)var1[0];
        this.CGLIB$CALLBACK_1 = (MethodInterceptor)var1[1];
        this.CGLIB$CALLBACK_2 = (LazyLoader)var1[2];
        this.CGLIB$CALLBACK_3 = (Dispatcher)var1[3];
        this.CGLIB$CALLBACK_4 = (InvocationHandler)var1[4];
        this.CGLIB$CALLBACK_5 = (FixedValue)var1[5];
    }



    static{
        try {
            CGLIB$STATICHOOK1();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
