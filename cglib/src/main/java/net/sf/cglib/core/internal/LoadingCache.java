package net.sf.cglib.core.internal;

import java.util.concurrent.*;


/*****
 * 缓存对象，被包装在ClassLoaderData中GeneratedClass
 * get 返回相关对象
 * @param <K>  AbstractClassGenerator
 * @param <KK>  Object
 * @param <V>   Object
 *
 * new LoadingCache<AbstractClassGenerator, Object, Object>(GET_KEY, load);
 *
 *
 * 真正的缓存对象的提取
 *
 * Function  抽象一个行为，用于封装一些逻辑，将特定的K传入，返回特定的value
 *
 *
 *
 *
 */
public class LoadingCache<K, KK, V> {
    protected final ConcurrentMap<KK, Object> map;



    //封装了对AbstractClassGenerator中generator()运行时方法的调用
    protected final Function<K, V> loader;

    //封装了返回相应cachekey的逻辑
    protected final Function<K, KK> keyMapper;

    public static final Function IDENTITY = new Function() {
        public Object apply(Object key) {
            return key;
        }
    };

    /****
     *
     * @param keyMapper apply GET_KEY的实现，返回一个key
     *        private static final Function<AbstractClassGenerator, Object> GET_KEY = new Function<AbstractClassGenerator, Object>() {
     *             public Object apply(AbstractClassGenerator gen) {
     *                 return gen.key;
     *             }
     *         };
     *
     * @param loader             Function<AbstractClassGenerator, Object> load =
     *                     new Function<AbstractClassGenerator, Object>() {
     *                         public Object apply(AbstractClassGenerator gen) {
     *                             Class klass = gen.generate(ClassLoaderData.this);
     *                             return gen.wrapCachedClass(klass);
     *                         }
     *                     };
     */
    public LoadingCache(Function<K, KK> keyMapper, Function<K, V> loader) {
        this.keyMapper = keyMapper;
        this.loader = loader;
        this.map = new ConcurrentHashMap<KK, Object>();
    }

    @SuppressWarnings("unchecked")
    public static <K> Function<K, K> identity() {
        return IDENTITY;
    }

    public V get(K key) {
        final KK cacheKey = keyMapper.apply(key);
        Object v = map.get(cacheKey);
        if (v != null && !(v instanceof FutureTask)) {
            return (V) v;
        }


        return createEntry(key, cacheKey, v);
    }


    /******
     * 线程竞争，则只有一个线程会竞争成功，没有竞争成功的线程则使用get请求重新加载
     *
     * 当多个线程竞争同一个futuretask对象时，只有一个get方法在运行。
     *
     *
     */
    /**
     * Loads entry to the cache.
     * If entry is missing, put {@link FutureTask} first so other competing thread might wait for the result.
     * @param key original key that would be used to load the instance
     * @param cacheKey key that would be used to store the entry in internal map
     * @param v null or {@link FutureTask<V>}
     * @return newly created instance
     */
    protected V createEntry(final K key, KK cacheKey, Object v) {
        FutureTask<V> task;
        boolean creator = false;
        if (v != null) {
            // Another thread is already loading an instance
            task = (FutureTask<V>) v;
        } else {
            task = new FutureTask<V>(new Callable<V>() {
                public V call() throws Exception {
                    return loader.apply(key);
                }
            });
            //如果已经存在，则不会覆盖已有的值，直接返回已存在的值
            //如果不存在，则向map中添加该键值对，并返回null
            Object prevTask = map.putIfAbsent(cacheKey, task);
            if (prevTask == null) {
                // creator does the load
                creator = true;
                task.run();
            } else if (prevTask instanceof FutureTask) {
                task = (FutureTask<V>) prevTask;
            } else {
                return (V) prevTask;
            }
        }




        V result;
        try {
            result = task.get();
        } catch (InterruptedException e) {
            throw new IllegalStateException("Interrupted while loading cache item", e);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw ((RuntimeException) cause);
            }
            throw new IllegalStateException("Unable to load cache item", cause);
        }
        if (creator) {
            map.put(cacheKey, result);
        }
        return result;
    }
}
