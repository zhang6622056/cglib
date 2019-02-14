package generateprocess;

import jdk.internal.org.objectweb.asm.Opcodes;
import net.sf.cglib.core.*;
import net.sf.cglib.proxy.*;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import proxybean.People;

import javax.xml.transform.Source;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

public class FirstTimeGenerate extends AbstractClassGenerator {

    //代理类继承的父类
    private Class superClass;
    //TODO-ZL 为了简单，暂时不考虑被代理类实现接口的情况
    private Class[] interfaces;
    private Type[] callbackTypes;





    static final Source source = new Source(FirstTimeGenerate.class.getName());


    public FirstTimeGenerate() {
        super(source);
    }

    //设置被代理的父类
    public void setSuperClass(Class superClass) {
        this.superClass = superClass;
    }

    //获取classloader
    protected ClassLoader getDefaultClassLoader() {
        return superClass.getClassLoader();
    }


    protected Object firstInstance(Class type) throws Exception {
        return type.newInstance();
    }

    protected Object nextInstance(Object instance) throws Exception {
        return instance;
    }



    private static final Type FACTORY =
            TypeUtils.parseType("net.sf.cglib.proxy.Factory");
    private static final String BOUND_FIELD = "CGLIB$BOUND";
    private static final String FACTORY_DATA_FIELD = "CGLIB$FACTORY_DATA";
    private static final String THREAD_CALLBACKS_FIELD = "CGLIB$THREAD_CALLBACKS";
    private static final String STATIC_CALLBACKS_FIELD = "CGLIB$STATIC_CALLBACKS";
    private static final String SET_THREAD_CALLBACKS_NAME = "CGLIB$SET_THREAD_CALLBACKS";
    private static final String SET_STATIC_CALLBACKS_NAME = "CGLIB$SET_STATIC_CALLBACKS";
    private static final String CONSTRUCTED_FIELD = "CGLIB$CONSTRUCTED";
    private static final Type OBJECT_TYPE =
            TypeUtils.parseType("Object");
    private static final Type THREAD_LOCAL =
            TypeUtils.parseType("ThreadLocal");
    private static final Type CALLBACK =
            TypeUtils.parseType("net.sf.cglib.proxy.Callback");
    private static final Type CALLBACK_ARRAY =
            Type.getType(Callback[].class);
    private static final String CALLBACK_FILTER_FIELD = "CGLIB$CALLBACK_FILTER";








    //todo-zl 缓存proxy对象实例用到，unknow
    private boolean useFactory = true;
    private EnhancerFactoryData currentData;






    //TODO-ZL unknwo
    private boolean interceptDuringConstruction = true;
    private Long serialVersionUID;























    /****
     * 过滤掉私有的构造
     * @param sc
     * @param constructors
     */
    private void filterConstructors(Class sc,List constructors) throws Exception {
        //将过滤逻辑与遍历删除逻辑拆分开来，抽象与上层的设计很棒
        CollectionUtils.filter(constructors,new VisibilityPredicate(sc,true));
        if (constructors.size() == 0)
            throw new Exception("there is no construcor can be extend at " + sc);
    }





    //proxy添加默认构造
    private void emitDefaultConstructor(ClassEmitter ce) {
        Constructor<Object> declaredConstructor;
        try {
            declaredConstructor = Object.class.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Object should have default constructor ", e);
        }
        MethodInfo constructor = (MethodInfo) MethodInfoTransformer.getInstance().transform(declaredConstructor);
        CodeEmitter e = EmitUtils.begin_method(ce, constructor, Constants.ACC_PUBLIC);
        e.load_this();
        e.dup();
        Signature sig = constructor.getSignature();
        e.super_invoke_constructor(sig);
        e.return_value();
        e.end_method();
    }



    private static String getCallbackField(int index) {
        return "CGLIB$CALLBACK_" + index;
    }





    //反射提取class所有的method，放到对应的methods与interfaces位置
    private void getMethods(Class sc, Class[] interfaces,List methods, List interfaceMethods, Set forcePublic){
        //包括父类与接口的方法全部一次性提取
        ReflectUtils.addAllMethods(sc,methods);


        //TODO-ZL copy过来，由于源码是new出来的，所以执行结果一样，暂不做考虑
//        List target = (interfaceMethods != null) ? interfaceMethods : methods;
//        if (interfaces != null) {
//            for (int i = 0; i < interfaces.length; i++) {
//                if (interfaces[i] != Factory.class) {
//                    ReflectUtils.addAllMethods(interfaces[i], target);
//                }
//            }
//        }
//
//        if (interfaceMethods != null) {
//            if (forcePublic != null) {
//                forcePublic.addAll(MethodWrapper.createSet(interfaceMethods));
//            }
//            methods.addAll(interfaceMethods);
//        }

        //过滤静态方法
        CollectionUtils.filter(methods, new RejectModifierPredicate(Constants.ACC_STATIC));
        //过滤私有方法
        CollectionUtils.filter(methods, new VisibilityPredicate(sc, true));
        //TODO-zl 重复方法过滤，具体尚未深入
        CollectionUtils.filter(methods, new DuplicatesPredicate(methods));
        //过滤final方法
        CollectionUtils.filter(methods, new RejectModifierPredicate(Constants.ACC_FINAL));
    }






    //利用ClassVisitor对ASM封装操作字节码，
    public void generateClass(ClassVisitor v) throws Exception {
        Class sc = (superClass == null) ? Object.class : superClass;
        //-验证类修饰符，isFinal相关操作的实现采用二进制的与运算。可以借鉴这种思想
        if (TypeUtils.isFinal(sc.getModifiers()))
            throw new IllegalArgumentException("Cannot subclass final class " + sc.getName());


        //-过滤掉私有的构造函数
        List constructors = new ArrayList(Arrays.asList(sc.getDeclaredConstructors()));
        filterConstructors(sc, constructors);


        //提取相关方法，过滤掉static final 的，非public的，以及重复的。
        // Order is very important: must add superclass, then
        // its superclass chain, then each interface and
        // its superinterfaces.
        List actualMethods = new ArrayList();
        List interfaceMethods = new ArrayList();
        final Set forcePublic = new HashSet();
        getMethods(sc, null, actualMethods, interfaceMethods, forcePublic);




        //至此ASM转换正式开始！！！！！！！！！！！
        //--将List<Method>转换为List<MethodInfo>对象
        //其中调用了ASM new Signature(member.getName(), Type.getMethodDescriptor((Method)member));
        //MethodInfo中维护了新的signature成员
        List methods = CollectionUtils.transform(actualMethods, new Transformer() {
            public Object transform(Object value) {
                Method method = (Method)value;
                int modifiers = Constants.ACC_FINAL
                        | (method.getModifiers()
                        & ~Constants.ACC_ABSTRACT
                        & ~Constants.ACC_NATIVE
                        & ~Constants.ACC_SYNCHRONIZED);
                if (forcePublic.contains(MethodWrapper.create(method))) {
                    modifiers = (modifiers & ~Constants.ACC_PROTECTED) | Constants.ACC_PUBLIC;
                }
                return ReflectUtils.getMethodInfo(method, modifiers);
            }
        });


        //-Asm ClassVisitor的子类
        //定义类信息。ClassInfo
        ClassEmitter e = new ClassEmitter(v);
        if (currentData == null) {
            e.begin_class(Constants.V1_2,
                    Constants.ACC_PUBLIC,
                    getClassName(),
                    Type.getType(sc),
                    (useFactory ?
                            TypeUtils.add(TypeUtils.getTypes(interfaces), FACTORY) :
                            TypeUtils.getTypes(interfaces)),
                    Constants.SOURCE_FILE);
        } else {
            e.begin_class(Constants.V1_2,
                    Constants.ACC_PUBLIC,
                    getClassName(),
                    null,
                    new Type[]{FACTORY},
                    Constants.SOURCE_FILE);
        }

        //将构造方法转换为List<MethodInfo>
        List constructorInfo = CollectionUtils.transform(constructors, MethodInfoTransformer.getInstance());


        //TODO-ZL 成员unknow
        e.declare_field(Constants.ACC_PRIVATE, BOUND_FIELD, Type.BOOLEAN_TYPE, null);
        e.declare_field(Constants.ACC_PUBLIC | Constants.ACC_STATIC, FACTORY_DATA_FIELD, OBJECT_TYPE, null);
        if (!interceptDuringConstruction) {
            e.declare_field(Constants.ACC_PRIVATE, CONSTRUCTED_FIELD, Type.BOOLEAN_TYPE, null);
        }
        e.declare_field(Constants.PRIVATE_FINAL_STATIC, THREAD_CALLBACKS_FIELD, THREAD_LOCAL, null);
        e.declare_field(Constants.PRIVATE_FINAL_STATIC, STATIC_CALLBACKS_FIELD, CALLBACK_ARRAY, null);
        if (serialVersionUID != null) {
            e.declare_field(Constants.PRIVATE_FINAL_STATIC, Constants.SUID_FIELD_NAME, Type.LONG_TYPE, serialVersionUID);
        }


        for (int i = 0; i < callbackTypes.length; i++) {
            e.declare_field(Constants.ACC_PRIVATE, getCallbackField(i), callbackTypes[i], null);
        }
        // This is declared private to avoid "public field" pollution
        e.declare_field(Constants.ACC_PRIVATE | Constants.ACC_STATIC, CALLBACK_FILTER_FIELD, OBJECT_TYPE, null);





//        if (currentData == null) {
//            emitMethods(e, methods, actualMethods);
//            emitConstructors(e, constructorInfo);
//        } else {
//            emitDefaultConstructor(e);
//        }
//        emitSetThreadCallbacks(e);
//        emitSetStaticCallbacks(e);
//        emitBindCallbacks(e);
//
//        if (useFactory || currentData != null) {
//            int[] keys = getCallbackKeys();
//            emitNewInstanceCallbacks(e);
//            emitNewInstanceCallback(e);
//            emitNewInstanceMultiarg(e, constructorInfo);
//            emitGetCallback(e, keys);
//            emitSetCallback(e, keys);
//            emitGetCallbacks(e);
//            emitSetCallbacks(e);
//        }


        e.end_class();
    }




    /****
     *
     * @return
     */
    public Object create(){
        //TODO-ZL 1-代理继承的验证  2-key的生成
        String key = UUID.randomUUID().toString();
        return super.create(key);
    }









    //TODO-ZL 暂不打开注释  缓存proxy对象实例
    static class EnhancerFactoryData {
//        public final Class generatedClass;
//        private final Method setThreadCallbacks;
//        private final Class[] primaryConstructorArgTypes;
//        private final Constructor primaryConstructor;
//
//        public EnhancerFactoryData(Class generatedClass, Class[] primaryConstructorArgTypes, boolean classOnly) {
//            this.generatedClass = generatedClass;
//            try {
//                setThreadCallbacks = getCallbacksSetter(generatedClass, SET_THREAD_CALLBACKS_NAME);
//                if (classOnly) {
//                    this.primaryConstructorArgTypes = null;
//                    this.primaryConstructor = null;
//                } else {
//                    this.primaryConstructorArgTypes = primaryConstructorArgTypes;
//                    this.primaryConstructor = ReflectUtils.getConstructor(generatedClass, primaryConstructorArgTypes);
//                }
//            } catch (NoSuchMethodException e) {
//                throw new CodeGenerationException(e);
//            }
//        }
//
//        /**
//         * Creates proxy instance for given argument types, and assigns the callbacks.
//         * Ideally, for each proxy class, just one set of argument types should be used,
//         * otherwise it would have to spend time on constructor lookup.
//         * Technically, it is a re-implementation of {@link Enhancer#createUsingReflection(Class)},
//         * with "cache {@link #setThreadCallbacks} and {@link #primaryConstructor}"
//         *
//         * @see #createUsingReflection(Class)
//         * @param argumentTypes constructor argument types
//         * @param arguments constructor arguments
//         * @param callbacks callbacks to set for the new instance
//         * @return newly created proxy
//         */
//        public Object newInstance(Class[] argumentTypes, Object[] arguments, Callback[] callbacks) {
//            setThreadCallbacks(callbacks);
//            try {
//                // Explicit reference equality is added here just in case Arrays.equals does not have one
//                if (primaryConstructorArgTypes == argumentTypes ||
//                        Arrays.equals(primaryConstructorArgTypes, argumentTypes)) {
//                    // If we have relevant Constructor instance at hand, just call it
//                    // This skips "get constructors" machinery
//                    return ReflectUtils.newInstance(primaryConstructor, arguments);
//                }
//                // Take a slow path if observing unexpected argument types
//                return ReflectUtils.newInstance(generatedClass, argumentTypes, arguments);
//            } finally {
//                // clear thread callbacks to allow them to be gc'd
//                setThreadCallbacks(null);
//            }
//
//        }
//
//        private void setThreadCallbacks(Callback[] callbacks) {
//            try {
//                setThreadCallbacks.invoke(generatedClass, (Object) callbacks);
//            } catch (IllegalAccessException e) {
//                throw new CodeGenerationException(e);
//            } catch (InvocationTargetException e) {
//                throw new CodeGenerationException(e.getTargetException());
//            }
//        }
    }











}