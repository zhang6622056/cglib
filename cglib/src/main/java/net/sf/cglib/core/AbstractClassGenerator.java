/*
 * Copyright 2003,2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.cglib.core;

import net.sf.cglib.core.internal.Function;
import net.sf.cglib.core.internal.LoadingCache;
import org.objectweb.asm.ClassReader;
import util.BytesUtil;

import java.lang.ref.WeakReference;
import java.security.ProtectionDomain;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.WeakHashMap;

/**
 * Abstract class for all code-generating CGLIB utilities.
 * In addition to caching generated classes for performance, it provides hooks for
 * customizing the <code>ClassLoader</code>, name of the generated class, and transformations
 * applied before generation.
 */
abstract public class AbstractClassGenerator<T>
implements ClassGenerator
{
    //current对象保存了当前实例的AbstractClassGenerator对象
    //在methodProxy中有用到，用来获取AbstractClassGenerator相关的信息
    //用ThreadLocal保存了一个运行时比较重要的类信息。


    //CURRENT保存了当前线程的AbstractClassGenerator，用于调用其方法生成类对象
    //            AbstractClassGenerator fromEnhancer = AbstractClassGenerator.getCurrent();
    //            if (fromEnhancer != null) {
    //                namingPolicy = fromEnhancer.getNamingPolicy();
    //                strategy = fromEnhancer.getStrategy();
    //                attemptLoad = fromEnhancer.getAttemptLoad();
    //            }
    private static final ThreadLocal CURRENT = new ThreadLocal();


    //定义缓存，当key被移除的时候，其映射的值也会被丢失，节省内存开销
    //https://www.cnblogs.com/skywang12345/p/3311092.html
    private static volatile Map<ClassLoader, ClassLoaderData> CACHE = new WeakHashMap<ClassLoader, ClassLoaderData>();

    //是否默认使用缓存
    private static final boolean DEFAULT_USE_CACHE =
        Boolean.parseBoolean(System.getProperty("cglib.useCache", "true"));
    private boolean useCache = DEFAULT_USE_CACHE;



    //类生成策略和名字生成策略
    private GeneratorStrategy strategy = DefaultGeneratorStrategy.INSTANCE;
    private NamingPolicy namingPolicy = DefaultNamingPolicy.INSTANCE;

    //source只维护了一个name，但尤其重要，该name是生成代理类必须的因素
    //作为构造方法的入参，用来拼装类名称
    private Source source;
    private String namePrefix;
    //用于维护最终生成的className
    private String className;


    //维护一个加载器用来加载当前的代理类
    private ClassLoader classLoader;

    //对应LoadingCache类中的map对应的key，
    //用于获取缓存对象
    private Object key;


    //TODO-ZL 是否尝试加载，这在MethodProxy中有用到。需要结合ASM理解其含义
    private boolean attemptLoad;


    //保存classLoader对应的classloaderdata数据
    //ClassLoaderData与LoadingCache相配合，维护了一个缓存机制，用于获取相应代理class对象
    protected static class ClassLoaderData {
        private final Set<String> reservedClassNames = new HashSet<String>();


        /***
         * LoadingCache维护了一个map用来缓存<key,value>,key为AbstractClassGenerator中的key
         */
        /**
         * {@link AbstractClassGenerator} here holds "cache key" (e.g. {@link net.sf.cglib.proxy.Enhancer}
         * configuration), and the value is the generated class plus some additional values
         * (see {@link #unwrapCachedValue(Object)}.
         * <p>The generated classes can be reused as long as their classloader is reachable.</p>
         * <p>Note: the only way to access a class is to find it through generatedClasses cache, thus
         * the key should not expire as long as the class itself is alive (its classloader is alive).</p>
         */
        private final LoadingCache<AbstractClassGenerator, Object, Object> generatedClasses;

        /**
         * Note: ClassLoaderData object is stored as a value of {@code WeakHashMap<ClassLoader, ...>} thus
         * this classLoader reference should be weak otherwise it would make classLoader strongly reachable
         * and alive forever.
         * Reference queue is not required since the cleanup is handled by {@link WeakHashMap}.
         */
        private final WeakReference<ClassLoader> classLoader;

        private final Predicate uniqueNamePredicate = new Predicate() {
            public boolean evaluate(Object name) {
                return reservedClassNames.contains(name);
            }
        };


        /****
         * 该key用于LoadingCache类中map缓存get方法
         * 用于获取相关的Object对象
         */
        private static final Function<AbstractClassGenerator, Object> GET_KEY = new Function<AbstractClassGenerator, Object>() {
            public Object apply(AbstractClassGenerator gen) {
                return gen.key;
            }
        };




        public ClassLoaderData(ClassLoader classLoader) {
            if (classLoader == null) {
                throw new IllegalArgumentException("classLoader == null is not yet supported");
            }
            this.classLoader = new WeakReference<ClassLoader>(classLoader);
            Function<AbstractClassGenerator, Object> load =
                    new Function<AbstractClassGenerator, Object>() {
                        public Object apply(AbstractClassGenerator gen) {
                            Class klass = gen.generate(ClassLoaderData.this);
                            return gen.wrapCachedClass(klass);
                        }
                    };
            generatedClasses = new LoadingCache<AbstractClassGenerator, Object, Object>(GET_KEY, load);
        }

        public ClassLoader getClassLoader() {
            return classLoader.get();
        }

        public void reserveName(String name) {
            reservedClassNames.add(name);
        }

        public Predicate getUniqueNamePredicate() {
            return uniqueNamePredicate;
        }

        public Object get(AbstractClassGenerator gen, boolean useCache) {
            //判定是否使用缓存
            if (!useCache) {
                //不使用缓存
              return gen.generate(ClassLoaderData.this);
            } else {
                //使用缓存
                //enhancerkey net.sf.cglib.proxy.Enhancer$EnhancerKey$$KeyFactoryByCGLIB$$7fb24d72
                Object cachedValue = generatedClasses.get(gen);
                return gen.unwrapCachedValue(cachedValue);
            }
        }
    }


    //对缓存class对象进行包装缓存或接触缓存
    //这里更多指的是弱引用
    protected T wrapCachedClass(Class klass) {
        return (T) new WeakReference(klass);
    }
    protected Object unwrapCachedValue(T cached) {
        return ((WeakReference) cached).get();
    }



    //维护了一个内部类，该name是代理类name组装的一部分
    protected static class Source {
        String name;
        public Source(String name) {
            this.name = name;
        }
    }


    protected AbstractClassGenerator(Source source) {
        this.source = source;
    }



    protected void setNamePrefix(String namePrefix) {
        this.namePrefix = namePrefix;
    }
    final protected String getClassName() {
        return className;
    }

    private void setClassName(String className) {
        this.className = className;
    }

    private String generateClassName(Predicate nameTestPredicate) {
        return namingPolicy.getClassName(namePrefix, source.name, key, nameTestPredicate);
    }
    /**
     * Override the default naming policy.
     * @see DefaultNamingPolicy
     * @param namingPolicy the custom policy, or null to use the default
     */
    public void setNamingPolicy(NamingPolicy namingPolicy) {
        if (namingPolicy == null)
            namingPolicy = DefaultNamingPolicy.INSTANCE;
        this.namingPolicy = namingPolicy;
    }

    /**
     * @see #setNamingPolicy
     */
    public NamingPolicy getNamingPolicy() {
        return namingPolicy;
    }


    /**
     * Set the <code>ClassLoader</code> in which the class will be generated.
     * Concrete subclasses of <code>AbstractClassGenerator</code> (such as <code>Enhancer</code>)
     * will try to choose an appropriate default if this is unset.
     * <p>
     * Classes are cached per-<code>ClassLoader</code> using a <code>WeakHashMap</code>, to allow
     * the generated classes to be removed when the associated loader is garbage collected.
     * @param classLoader the loader to generate the new class with, or null to use the default
     */
    //设置代理类的类加载器
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /****
     *
     * 获取classLoader
     * @return
     */
    public ClassLoader getClassLoader() {
        ClassLoader t = classLoader;
        if (t == null) {
            t = getDefaultClassLoader();
        }
        if (t == null) {
            t = getClass().getClassLoader();
        }
        if (t == null) {
            t = Thread.currentThread().getContextClassLoader();
        }
        if (t == null) {
            throw new IllegalStateException("Cannot determine classloader");
        }
        return t;
    }


    /**
     * Whether use and update the static cache of generated classes
     * for a class with the same properties. Default is <code>true</code>.
     */
    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }

    /**
     * @see #setUseCache
     */
    public boolean getUseCache() {
        return useCache;
    }




    /**
     * If set, CGLIB will attempt to load classes from the specified
     * <code>ClassLoader</code> before generating them. Because generated
     * class names are not guaranteed to be unique, the default is <code>false</code>.
     */
    public void setAttemptLoad(boolean attemptLoad) {
        this.attemptLoad = attemptLoad;
    }

    public boolean getAttemptLoad() {
        return attemptLoad;
    }
    
    /**
     * Set the strategy to use to create the bytecode from this generator.
     * By default an instance of {@see DefaultGeneratorStrategy} is used.
     */
    public void setStrategy(GeneratorStrategy strategy) {
        if (strategy == null)
            strategy = DefaultGeneratorStrategy.INSTANCE;
        this.strategy = strategy;
    }

    /**
     * @see #setStrategy
     */
    public GeneratorStrategy getStrategy() {
        return strategy;
    }

    /**
     * Used internally by CGLIB. Returns the <code>AbstractClassGenerator</code>
     * that is being used to generate a class in the current thread.
     */
    public static AbstractClassGenerator getCurrent() {
        return (AbstractClassGenerator)CURRENT.get();
    }





    //提出机制，获取默认的类加载器
    abstract protected ClassLoader getDefaultClassLoader();

    /**
     * Returns the protection domain to use when defining the class.
     * <p>
     * Default implementation returns <code>null</code> for using a default protection domain. Sub-classes may
     * override to use a more specific protection domain.
     * </p>
     *
     * @return the protection domain (<code>null</code> for using a default)
     */
    protected ProtectionDomain getProtectionDomain() {
    	return null;
    }






    /*****
     * 创建一个被代理对象，
     * 该类被所有AbstractClassGenerator的子类Generator所调用，
     * @param key
     * @return
     */
    protected Object create(Object key) {
        try {
            //一级缓存loader作为缓存key
            ClassLoader loader = getClassLoader();

            //获取对应的一级缓存Value
            Map<ClassLoader, ClassLoaderData> cache = CACHE;
            ClassLoaderData data = cache.get(loader);

            //static volatile 只有一个实例，存在线程竞争
            if (data == null) {
                synchronized (AbstractClassGenerator.class) {
                    cache = CACHE;
                    data = cache.get(loader);
                    if (data == null) {
                        Map<ClassLoader, ClassLoaderData> newCache = new WeakHashMap<ClassLoader, ClassLoaderData>(cache);
                        data = new ClassLoaderData(loader);

                        newCache.put(loader, data);
                        CACHE = newCache;
                    }
                }
            }
//            Object key = KEY_FACTORY.newInstance((superclass != null) ? superclass.getName() : null,
//                    ReflectUtils.getNames(interfaces),
//                    filter == ALL_ZERO ? null : new WeakCacheKey<CallbackFilter>(filter),
//                    callbackTypes,
//                    useFactory,
//                    interceptDuringConstruction,
//                    serialVersionUID);
            this.key = key;

            //通过ClassLoaderData获取实例，存在判定是否用到缓存的逻辑
            Object obj = data.get(this, getUseCache());
            if (obj instanceof Class) {
                return firstInstance((Class) obj);
            }
            return nextInstance(obj);
        } catch (RuntimeException e) {
            throw e;
        } catch (Error e) {
            throw e;
        } catch (Exception e) {
            throw new CodeGenerationException(e);
        }
    }





    
    /*****
     * 生成代理类的code function
     * 该类为LoadingCache中维护的loader，用于生成类
     * 用来创建字节码class，这里调用了基类实现的生成器
     * @param data
     * @return
     */
    protected Class generate(ClassLoaderData data) {
        Class gen;
        Object save = CURRENT.get();
        CURRENT.set(this);
        try {
            ClassLoader classLoader = data.getClassLoader();
            if (classLoader == null) {
                throw new IllegalStateException("ClassLoader is null while trying to define class " +
                        getClassName() + ". It seems that the loader has been expired from a weak reference somehow. " +
                        "Please file an issue at cglib's issue tracker.");
            }
            synchronized (classLoader) {
              String name = generateClassName(data.getUniqueNamePredicate());              
              data.reserveName(name);
              this.setClassName(name);
            }
            if (attemptLoad) {
                try {
                    gen = classLoader.loadClass(getClassName());
                    return gen;
                } catch (ClassNotFoundException e) {
                    // ignore
                }
            }

            //调用基类实现的ASM增加字节码
            byte[] b = strategy.generate(this);
            String className = ClassNameReader.getClassName(new ClassReader(b));

            //转化成为javaclass文件
            final String path = "/Users/nero/cglibclassfile/"+className+".class";
            BytesUtil.outputJavaClassFile(path,b);



            ProtectionDomain protectionDomain = getProtectionDomain();
            synchronized (classLoader) { // just in case
                if (protectionDomain == null) {
                    gen = ReflectUtils.defineClass(className, b, classLoader);
                } else {
                    gen = ReflectUtils.defineClass(className, b, classLoader, protectionDomain);
                }
            }
            return gen;
        } catch (RuntimeException e) {
            throw e;
        } catch (Error e) {
            throw e;
        } catch (Exception e) {
            throw new CodeGenerationException(e);
        } finally {
            CURRENT.set(save);
        }
    }



    //抽象两个方法，用于返回相应的对象实例，注意两个方法的参数
    //firstInstance Class参数
    //nextInstance  instance参数
    abstract protected Object firstInstance(Class type) throws Exception;
    abstract protected Object nextInstance(Object instance) throws Exception;
}
