package rectest.index;

public interface ReadOnlyKVStore<K, V> {

    V get(K key);
}
