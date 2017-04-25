/**
 * 
 */

package org.exparity.data.types;

import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * @author Stewart Bissett
 */
public final class Pair<V1, V2> {

    public static <V1, V2> Pair<V1, V2> create(final V1 value1, final V2 value2) {
        return new Pair<>(value1, value2);
    }

    private final V1 value1;
    private final V2 value2;

    public Pair(final V1 value1, final V2 value2) {
        this.value1 = value1;
        this.value2 = value2;
    }

    public V1 getValue1() {
        return value1;
    }

    public V2 getValue2() {
        return value2;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair)) return false;
        Pair<V1, V2> rhs = (Pair) o;
        return value1.equals(rhs.value1) && value2.equals(rhs.value2);
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder(35, 67);
        builder.append(value1);
        builder.append(value2);
        return builder.toHashCode();
    }

    @Override
    public String toString() {
        return "Pair ['" + value1 + "' => '" + value2 + "']";
    }
}
