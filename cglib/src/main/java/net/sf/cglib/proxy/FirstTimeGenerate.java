package net.sf.cglib.proxy;

import net.sf.cglib.core.*;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;

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




    private static final Type ILLEGAL_ARGUMENT_EXCEPTION =
            TypeUtils.parseType("IllegalArgumentException");

    private static final Signature CSTRUCT_NULL =
            TypeUtils.parseConstructor("");
    private static final Signature BIND_CALLBACKS =
            TypeUtils.parseSignature("void CGLIB$BIND_CALLBACKS(Object)");
    private static final Signature THREAD_LOCAL_SET =
            TypeUtils.parseSignature("void set(Object)");
    private static final Signature SET_THREAD_CALLBACKS =
            new Signature(SET_THREAD_CALLBACKS_NAME, Type.VOID_TYPE, new Type[]{ CALLBACK_ARRAY });
    private static final Signature SET_STATIC_CALLBACKS =
            new Signature(SET_STATIC_CALLBACKS_NAME, Type.VOID_TYPE, new Type[]{ CALLBACK_ARRAY });
    private static final Signature THREAD_LOCAL_GET =
            TypeUtils.parseSignature("Object get()");
    private static final Signature GET_CALLBACK =
            new Signature("getCallback", CALLBACK, new Type[]{ Type.INT_TYPE });
    private static final Signature SET_CALLBACK =
            new Signature("setCallback", Type.VOID_TYPE, new Type[]{ Type.INT_TYPE, CALLBACK });
    private static final Signature SET_CALLBACKS =
            new Signature("setCallbacks", Type.VOID_TYPE, new Type[]{ CALLBACK_ARRAY });
    private static final Signature GET_CALLBACKS =
            new Signature("getCallbacks", CALLBACK_ARRAY, new Type[0]);
    private static final Signature NEW_INSTANCE =
            new Signature("newInstance", Constants.TYPE_OBJECT, new Type[]{ CALLBACK_ARRAY });
    private static final Signature SINGLE_NEW_INSTANCE =
            new Signature("newInstance", Constants.TYPE_OBJECT, new Type[]{ CALLBACK });
    private static final Signature MULTIARG_NEW_INSTANCE =
            new Signature("newInstance", Constants.TYPE_OBJECT, new Type[]{
                    Constants.TYPE_CLASS_ARRAY,
                    Constants.TYPE_OBJECT_ARRAY,
                    CALLBACK_ARRAY,
            });
    private static final Type ILLEGAL_STATE_EXCEPTION =
            TypeUtils.parseType("IllegalStateException");













    private CallbackFilter filter;





    //todo-zl 缓存proxy对象实例用到，unknow
    private boolean useFactory = true;
    private EnhancerFactoryData currentData;
    private boolean classOnly;
    private Object[] arguments;



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





        if (currentData == null) {
            emitMethods(e, methods, actualMethods);
            emitConstructors(e, constructorInfo);
        } else {
            emitDefaultConstructor(e);
        }
        emitSetThreadCallbacks(e);
        emitSetStaticCallbacks(e);
        emitBindCallbacks(e);

        if (useFactory || currentData != null) {
            int[] keys = getCallbackKeys();
            emitNewInstanceCallbacks(e);
            emitNewInstanceCallback(e);
            emitNewInstanceMultiarg(e, constructorInfo);
            emitGetCallback(e, keys);
            emitSetCallback(e, keys);
            emitGetCallbacks(e);
            emitSetCallbacks(e);
        }


        e.end_class();
    }


    private int[] getCallbackKeys() {
        int[] keys = new int[callbackTypes.length];
        for (int i = 0; i < callbackTypes.length; i++) {
            keys[i] = i;
        }
        return keys;
    }

    private void emitGetCallback(ClassEmitter ce, int[] keys) {
        final CodeEmitter e = ce.begin_method(Constants.ACC_PUBLIC, GET_CALLBACK, null);
        e.load_this();
        e.invoke_static_this(BIND_CALLBACKS);
        e.load_this();
        e.load_arg(0);
        e.process_switch(keys, new ProcessSwitchCallback() {
            public void processCase(int key, Label end) {
                e.getfield(getCallbackField(key));
                e.goTo(end);
            }
            public void processDefault() {
                e.pop(); // stack height
                e.aconst_null();
            }
        });
        e.return_value();
        e.end_method();
    }

    private void emitSetCallback(ClassEmitter ce, int[] keys) {
        final CodeEmitter e = ce.begin_method(Constants.ACC_PUBLIC, SET_CALLBACK, null);
        e.load_arg(0);
        e.process_switch(keys, new ProcessSwitchCallback() {
            public void processCase(int key, Label end) {
                e.load_this();
                e.load_arg(1);
                e.checkcast(callbackTypes[key]);
                e.putfield(getCallbackField(key));
                e.goTo(end);
            }
            public void processDefault() {
                // TODO: error?
            }
        });
        e.return_value();
        e.end_method();
    }

    private void emitSetCallbacks(ClassEmitter ce) {
        CodeEmitter e = ce.begin_method(Constants.ACC_PUBLIC, SET_CALLBACKS, null);
        e.load_this();
        e.load_arg(0);
        for (int i = 0; i < callbackTypes.length; i++) {
            e.dup2();
            e.aaload(i);
            e.checkcast(callbackTypes[i]);
            e.putfield(getCallbackField(i));
        }
        e.return_value();
        e.end_method();
    }

    private void emitGetCallbacks(ClassEmitter ce) {
        CodeEmitter e = ce.begin_method(Constants.ACC_PUBLIC, GET_CALLBACKS, null);
        e.load_this();
        e.invoke_static_this(BIND_CALLBACKS);
        e.load_this();
        e.push(callbackTypes.length);
        e.newarray(CALLBACK);
        for (int i = 0; i < callbackTypes.length; i++) {
            e.dup();
            e.push(i);
            e.load_this();
            e.getfield(getCallbackField(i));
            e.aastore();
        }
        e.return_value();
        e.end_method();
    }

    private void emitNewInstanceCallbacks(ClassEmitter ce) {
        CodeEmitter e = ce.begin_method(Constants.ACC_PUBLIC, NEW_INSTANCE, null);
        Type thisType = getThisType(e);
        e.load_arg(0);
        e.invoke_static(thisType, SET_THREAD_CALLBACKS);
        emitCommonNewInstance(e);
    }

    private Type getThisType(CodeEmitter e) {
        if (currentData == null) {
            return e.getClassEmitter().getClassType();
        } else {
            //TODO-ZL 暂时关闭缓存
//            return Type.getType(currentData.generatedClass);
            return null;
        }
    }

    private void emitCommonNewInstance(CodeEmitter e) {
        Type thisType = getThisType(e);
        e.new_instance(thisType);
        e.dup();
        e.invoke_constructor(thisType);
        e.aconst_null();
        e.invoke_static(thisType, SET_THREAD_CALLBACKS);
        e.return_value();
        e.end_method();
    }

    private void emitNewInstanceCallback(ClassEmitter ce) {
        CodeEmitter e = ce.begin_method(Constants.ACC_PUBLIC, SINGLE_NEW_INSTANCE, null);
        switch (callbackTypes.length) {
            case 0:
                // TODO: make sure Callback is null
                break;
            case 1:
                // for now just make a new array; TODO: optimize
                e.push(1);
                e.newarray(CALLBACK);
                e.dup();
                e.push(0);
                e.load_arg(0);
                e.aastore();
                e.invoke_static(getThisType(e), SET_THREAD_CALLBACKS);
                break;
            default:
                e.throw_exception(ILLEGAL_STATE_EXCEPTION, "More than one callback object required");
        }
        emitCommonNewInstance(e);
    }

    private void emitNewInstanceMultiarg(ClassEmitter ce, List constructors) {
        final CodeEmitter e = ce.begin_method(Constants.ACC_PUBLIC, MULTIARG_NEW_INSTANCE, null);
        final Type thisType = getThisType(e);
        e.load_arg(2);
        e.invoke_static(thisType, SET_THREAD_CALLBACKS);
        e.new_instance(thisType);
        e.dup();
        e.load_arg(0);
        EmitUtils.constructor_switch(e, constructors, new ObjectSwitchCallback() {
            public void processCase(Object key, Label end) {
                MethodInfo constructor = (MethodInfo)key;
                Type types[] = constructor.getSignature().getArgumentTypes();
                for (int i = 0; i < types.length; i++) {
                    e.load_arg(1);
                    e.push(i);
                    e.aaload();
                    e.unbox(types[i]);
                }
                e.invoke_constructor(thisType, constructor.getSignature());
                e.goTo(end);
            }
            public void processDefault() {
                e.throw_exception(ILLEGAL_ARGUMENT_EXCEPTION, "Constructor not found");
            }
        });
        e.aconst_null();
        e.invoke_static(thisType, SET_THREAD_CALLBACKS);
        e.return_value();
        e.end_method();
    }

    private void emitConstructors(ClassEmitter ce, List constructors) {
        boolean seenNull = false;
        for (Iterator it = constructors.iterator(); it.hasNext();) {
            MethodInfo constructor = (MethodInfo)it.next();
            if (currentData != null && !"()V".equals(constructor.getSignature().getDescriptor())) {
                continue;
            }
            CodeEmitter e = EmitUtils.begin_method(ce, constructor, Constants.ACC_PUBLIC);
            e.load_this();
            e.dup();
            e.load_args();
            Signature sig = constructor.getSignature();
            seenNull = seenNull || sig.getDescriptor().equals("()V");
            e.super_invoke_constructor(sig);
            if (currentData == null) {
                e.invoke_static_this(BIND_CALLBACKS);
                if (!interceptDuringConstruction) {
                    e.load_this();
                    e.push(1);
                    e.putfield(CONSTRUCTED_FIELD);
                }
            }
            e.return_value();
            e.end_method();
        }
        if (!classOnly && !seenNull && arguments == null)
            throw new IllegalArgumentException("Superclass has no null constructors but no arguments were given");
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





    private void emitMethods(final ClassEmitter ce, List methods, List actualMethods) {
        CallbackGenerator[] generators = CallbackInfo.getGenerators(callbackTypes);

        Map groups = new HashMap();
        final Map indexes = new HashMap();
        final Map originalModifiers = new HashMap();
        final Map positions = CollectionUtils.getIndexMap(methods);
        final Map declToBridge = new HashMap();

        Iterator it1 = methods.iterator();
        Iterator it2 = (actualMethods != null) ? actualMethods.iterator() : null;

        while (it1.hasNext()) {
            MethodInfo method = (MethodInfo)it1.next();
            Method actualMethod = (it2 != null) ? (Method)it2.next() : null;
            int index = filter.accept(actualMethod);
            if (index >= callbackTypes.length) {
                throw new IllegalArgumentException("Callback filter returned an index that is too large: " + index);
            }
            originalModifiers.put(method, new Integer((actualMethod != null) ? actualMethod.getModifiers() : method.getModifiers()));
            indexes.put(method, new Integer(index));
            List group = (List)groups.get(generators[index]);
            if (group == null) {
                groups.put(generators[index], group = new ArrayList(methods.size()));
            }
            group.add(method);

            // Optimization: build up a map of Class -> bridge methods in class
            // so that we can look up all the bridge methods in one pass for a class.
            if (TypeUtils.isBridge(actualMethod.getModifiers())) {
                Set bridges = (Set)declToBridge.get(actualMethod.getDeclaringClass());
                if (bridges == null) {
                    bridges = new HashSet();
                    declToBridge.put(actualMethod.getDeclaringClass(), bridges);
                }
                bridges.add(method.getSignature());
            }
        }

        final Map bridgeToTarget = new BridgeMethodResolver(declToBridge, getClassLoader()).resolveAll();

        Set seenGen = new HashSet();
        CodeEmitter se = ce.getStaticHook();
        se.new_instance(THREAD_LOCAL);
        se.dup();
        se.invoke_constructor(THREAD_LOCAL, CSTRUCT_NULL);
        se.putfield(THREAD_CALLBACKS_FIELD);

        final Object[] state = new Object[1];
        CallbackGenerator.Context context = new CallbackGenerator.Context() {
            public ClassLoader getClassLoader() {
                return FirstTimeGenerate.this.getClassLoader();
            }
            public int getOriginalModifiers(MethodInfo method) {
                return ((Integer)originalModifiers.get(method)).intValue();
            }
            public int getIndex(MethodInfo method) {
                return ((Integer)indexes.get(method)).intValue();
            }
            public void emitCallback(CodeEmitter e, int index) {
                emitCurrentCallback(e, index);
            }
            public Signature getImplSignature(MethodInfo method) {
                return rename(method.getSignature(), ((Integer)positions.get(method)).intValue());
            }
            public void emitLoadArgsAndInvoke(CodeEmitter e, MethodInfo method) {
                // If this is a bridge and we know the target was called from invokespecial,
                // then we need to invoke_virtual w/ the bridge target instead of doing
                // a super, because super may itself be using super, which would bypass
                // any proxies on the target.
                Signature bridgeTarget = (Signature)bridgeToTarget.get(method.getSignature());
                if (bridgeTarget != null) {
                    // checkcast each argument against the target's argument types
                    for (int i = 0; i < bridgeTarget.getArgumentTypes().length; i++) {
                        e.load_arg(i);
                        Type target = bridgeTarget.getArgumentTypes()[i];
                        if (!target.equals(method.getSignature().getArgumentTypes()[i])) {
                            e.checkcast(target);
                        }
                    }

                    e.invoke_virtual_this(bridgeTarget);

                    Type retType = method.getSignature().getReturnType();
                    // Not necessary to cast if the target & bridge have
                    // the same return type.
                    // (This conveniently includes void and primitive types,
                    // which would fail if casted.  It's not possible to
                    // covariant from boxed to unbox (or vice versa), so no having
                    // to box/unbox for bridges).
                    // TODO: It also isn't necessary to checkcast if the return is
                    // assignable from the target.  (This would happen if a subclass
                    // used covariant returns to narrow the return type within a bridge
                    // method.)
                    if (!retType.equals(bridgeTarget.getReturnType())) {
                        e.checkcast(retType);
                    }
                } else {
                    e.load_args();
                    e.super_invoke(method.getSignature());
                }
            }
            public CodeEmitter beginMethod(ClassEmitter ce, MethodInfo method) {
                CodeEmitter e = EmitUtils.begin_method(ce, method);
                if (!interceptDuringConstruction &&
                        !TypeUtils.isAbstract(method.getModifiers())) {
                    Label constructed = e.make_label();
                    e.load_this();
                    e.getfield(CONSTRUCTED_FIELD);
                    e.if_jump(e.NE, constructed);
                    e.load_this();
                    e.load_args();
                    e.super_invoke();
                    e.return_value();
                    e.mark(constructed);
                }
                return e;
            }
        };
        for (int i = 0; i < callbackTypes.length; i++) {
            CallbackGenerator gen = generators[i];
            if (!seenGen.contains(gen)) {
                seenGen.add(gen);
                final List fmethods = (List)groups.get(gen);
                if (fmethods != null) {
                    try {
                        gen.generate(ce, context, fmethods);
                        gen.generateStatic(se, context, fmethods);
                    } catch (RuntimeException x) {
                        throw x;
                    } catch (Exception x) {
                        throw new CodeGenerationException(x);
                    }
                }
            }
        }
        se.return_value();
        se.end_method();
    }

    private Signature rename(Signature sig, int index) {
        return new Signature("CGLIB$" + sig.getName() + "$" + index,
                sig.getDescriptor());
    }




    private void emitSetThreadCallbacks(ClassEmitter ce) {
        CodeEmitter e = ce.begin_method(Constants.ACC_PUBLIC | Constants.ACC_STATIC,
                SET_THREAD_CALLBACKS,
                null);
        e.getfield(THREAD_CALLBACKS_FIELD);
        e.load_arg(0);
        e.invoke_virtual(THREAD_LOCAL, THREAD_LOCAL_SET);
        e.return_value();
        e.end_method();
    }

    private void emitSetStaticCallbacks(ClassEmitter ce) {
        CodeEmitter e = ce.begin_method(Constants.ACC_PUBLIC | Constants.ACC_STATIC,
                SET_STATIC_CALLBACKS,
                null);
        e.load_arg(0);
        e.putfield(STATIC_CALLBACKS_FIELD);
        e.return_value();
        e.end_method();
    }

    private void emitCurrentCallback(CodeEmitter e, int index) {
        e.load_this();
        e.getfield(getCallbackField(index));
        e.dup();
        Label end = e.make_label();
        e.ifnonnull(end);
        e.pop(); // stack height
        e.load_this();
        e.invoke_static_this(BIND_CALLBACKS);
        e.load_this();
        e.getfield(getCallbackField(index));
        e.mark(end);
    }

    private void emitBindCallbacks(ClassEmitter ce) {
        CodeEmitter e = ce.begin_method(Constants.PRIVATE_FINAL_STATIC,
                BIND_CALLBACKS,
                null);
        Local me = e.make_local();
        e.load_arg(0);
        e.checkcast_this();
        e.store_local(me);

        Label end = e.make_label();
        e.load_local(me);
        e.getfield(BOUND_FIELD);
        e.if_jump(e.NE, end);
        e.load_local(me);
        e.push(1);
        e.putfield(BOUND_FIELD);

        e.getfield(THREAD_CALLBACKS_FIELD);
        e.invoke_virtual(THREAD_LOCAL, THREAD_LOCAL_GET);
        e.dup();
        Label found_callback = e.make_label();
        e.ifnonnull(found_callback);
        e.pop();

        e.getfield(STATIC_CALLBACKS_FIELD);
        e.dup();
        e.ifnonnull(found_callback);
        e.pop();
        e.goTo(end);

        e.mark(found_callback);
        e.checkcast(CALLBACK_ARRAY);
        e.load_local(me);
        e.swap();
        for (int i = callbackTypes.length - 1; i >= 0; i--) {
            if (i != 0) {
                e.dup2();
            }
            e.aaload(i);
            e.checkcast(callbackTypes[i]);
            e.putfield(getCallbackField(i));
        }

        e.mark(end);
        e.return_value();
        e.end_method();
    }

    private static String getCallbackField(int index) {
        return "CGLIB$CALLBACK_" + index;
    }



}