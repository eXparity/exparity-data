/**
 * 
 */

package org.exparity.data.types;

/**
 * @author Stewart Bissett
 */
public class KeyValue<K, V> {

    public static <K, V> KeyValue<K, V> create(final K key, final V value) {
        return new KeyValue<>(key, value);
    }

    private Pair<K, V> pair;

    public KeyValue(final K key, final V value) {
        this.pair = Pair.create(key, value);
    }

    public K getKey() {
        return pair.getValue1();
    }

    public V getValue() {
        return pair.getValue2();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || !o.getClass().equals(getClass())) return false;
        return ((KeyValue<?, ?>) o).pair.equals(pair);
    }

    @Override
    public int hashCode() {
        return pair.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " ['" + getKey() + "' => '" + getValue() + "']";
    }
}
