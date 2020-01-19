package schulscheduler.collections;

import schulscheduler.model.base.IDElement;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Like a HashSet, but only hashes the ID of the values. This means that the other contents of the value object can be
 * modified without messing up the hashes.
 */
@SuppressWarnings("NullableProblems")
public class IDElementSet<V extends IDElement> implements Set<V> {

    private final Map<V, Boolean> innerMap = new IDElementMap<>();

    @Override
    public int size() {
        return innerMap.size();
    }

    @Override
    public boolean isEmpty() {
        return innerMap.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        //noinspection SuspiciousMethodCalls
        return innerMap.containsKey(o);
    }

    @Override
    public Iterator<V> iterator() {
        return innerMap.keySet().iterator();
    }

    @Override
    public Object[] toArray() {
        return innerMap.keySet().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        //noinspection SuspiciousToArrayCall
        return innerMap.keySet().toArray(a);
    }

    @Override
    public boolean add(V v) {
        return innerMap.put(v, Boolean.TRUE) != null;
    }

    @Override
    public boolean remove(Object o) {
        return innerMap.remove(o) != null;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        //noinspection SuspiciousMethodCalls
        return c.stream().allMatch(innerMap::containsKey);
    }

    @Override
    public boolean addAll(Collection<? extends V> c) {
        return c.stream().anyMatch(this::add);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return c.stream().anyMatch(this::remove);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return innerMap.keySet().stream().filter(k -> !c.contains(k)).anyMatch(this::remove);
    }

    @Override
    public void clear() {
        innerMap.clear();
    }
}
