package net.sf.cglib.core.internal;


/****
 * 抽象一个行为，用于封装一些逻辑，将特定的K传入，返回特定的value
 * @param <K>
 * @param <V>
 */
public interface Function<K, V> {
    V apply(K key);
}
