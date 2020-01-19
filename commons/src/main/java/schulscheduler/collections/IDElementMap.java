package schulscheduler.collections;

import schulscheduler.model.base.IDElement;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Like a HashMap, but only hashes the ID of the key. This means that the other contents of the key object can be
 * modified without messing up the hashes.
 */
@SuppressWarnings("NullableProblems")
public class IDElementMap<K extends IDElement, V> implements Map<K, V> {

    private static class IDEntry<K extends IDElement, V> implements Map.Entry<K, V> {
        private final K key;
        private V value;

        private IDEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }
    }

    // These two always contain exactly the same keys, hence they also always have the same size.
//    private final Map<Integer, K> keyMap = new HashMap<>();
//    private final Map<Integer, V> valueMap = new HashMap<>();

    private final Map<Integer, IDEntry<K, V>> entryMap = new HashMap<>();

    @Override
    public int size() {
        return entryMap.size();
    }

    @Override
    public boolean isEmpty() {
        return entryMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return key instanceof IDElement && entryMap.containsKey(((IDElement) key).getId());
    }

    @Override
    public boolean containsValue(Object value) {
        if (value == null) return false;
        return entryMap.values().stream().anyMatch(entry -> value.equals(entry.value));
    }

    @Override
    public V get(Object key) {
        if (!(key instanceof IDElement)) return null;
        IDEntry<K, V> entry = entryMap.get(((IDElement) key).getId());
        return entry == null ? null : entry.value;
    }

    @Override
    public V put(K key, V value) {
//        K previousKey = keyMap.put(key.getId(), key);
//        if (previousKey != null && !previousKey.equals(key)) {
//            throw new IllegalArgumentException("Got two keys with the same ID " + key.getId());
//        }
//        return valueMap.put(key.getId(), value);
        IDEntry<K, V> previousEntry = entryMap.put(key.getId(), new IDEntry<>(key, value));
        if (previousEntry == null) return null;
        if (!previousEntry.key.equals(key)) {
            throw new IllegalArgumentException("Got two keys with the same ID " + key.getId());
        }
        return previousEntry.value;
    }

    @Override
    public V remove(Object key) {
        if (!(key instanceof IDElement)) return null;
        IDEntry<K, V> entry = entryMap.remove(((IDElement) key).getId());
        return entry == null ? null : entry.value;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        m.forEach(this::put);
    }

    @Override
    public void clear() {
        entryMap.clear();
    }

    @Override
    public Set<K> keySet() {
        return entryMap.values().stream().map(e -> e.key).collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Collection<V> values() {
        return entryMap.values().stream().map(e -> e.value).collect(Collectors.toUnmodifiableList());
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return entryMap.values().stream().collect(Collectors.toUnmodifiableSet());
    }
}
