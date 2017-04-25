/*
 * 
 */

package org.exparity.data.types;

import static java.util.stream.Collectors.toList;

import java.io.PrintStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author Stewart Bissett
 */
public class Array implements Iterable<Scalar> {

    /**
     * Factory method to create an empty array
     * @return a {@link Array}
     */
    public static Array empty() {
        return new Array(new ArrayList<Scalar>());
    }

    /**
     * Factory method to create an array from a collection of scalars
     * @param data a {@link Scalar} array
     * @return a {@link Row}
     */
    public static Array of(final Collection<Scalar> data) {
        return new Array(data);
    }

    /**
     * Factory method to create an array from an array of scalars
     * @param data a {@link Scalar} array
     * @return a {@link Row}
     */
    public static Array of(final Scalar[] data) {
        return new Array(Arrays.asList(data));
    }

    /**
     * Factory method to create an array from an array of scalars
     * @param data a {@link Scalar} array
     * @return a {@link Array}
     */
    public static Array of(final Object[] array) {
        if (array == null) {
            return Array.empty();
        } else {
            Scalar[] values = new Scalar[array.length];
            for (int i = 0; i < array.length; ++i) {
                values[i] = new Scalar(array[i]);
            }
            return of(values);
        }
    }

    /**
     * Factory method to create an array from an array of scalars
     * @param data a {@link Scalar} array
     * @return a {@link Array}
     */
    public static Array of(final String[] array) {
        if (array == null) {
            return Array.empty();
        } else {
            Scalar[] values = new Scalar[array.length];
            for (int i = 0; i < array.length; ++i) {
                values[i] = new Scalar(array[i]);
            }
            return of(values);
        }
    }

    private final List<Scalar> values = new ArrayList<>();

    private Array(final Collection<Scalar> values) {
        this.values.addAll(values);
    }

    public Array(final Scalar... values) {
        this(Arrays.asList(values));
    }

    public Array(final Array source) {
        this.values.addAll(source.values);
    }

    public Stream<Scalar> values() {
        return values.stream();
    }

    public String getValueAsString(final int index) {
        return (index >= 0 && index < values.size()) ? values.get(index).getValueAsString() : null;
    }

    public Boolean getValueAsBoolean(final int index) {
        return (index >= 0 && index < values.size()) ? values.get(index).getValueAsBoolean() : null;
    }

    public Double getValueAsDouble(final int index) {
        return (index >= 0 && index < values.size()) ? values.get(index).getValueAsDouble() : null;
    }

    public Long getValueAsLong(final int index) {
        return (index >= 0 && index < values.size()) ? values.get(index).getValueAsLong() : null;
    }

    public int getValueAsInteger(final int index) {
        return (index >= 0 && index < values.size()) ? values.get(index).getValueAsInteger() : null;
    }

    public LocalDate getValueAsDate(final int index, final DateTimeFormatter format) {
        return (index >= 0 && index < values.size()) ? values.get(index).getValueAsDate(format) : null;
    }

    public LocalTime getValueAsTime(final int index) {
        return getValueAsTime(index, DateTimeFormatter.ISO_TIME);
    }

    public LocalTime getValueAsTime(final int index, final DateTimeFormatter format) {
        return values.get(index).getValueAsTime(format);
    }

    public Scalar getValue(final int index) {
        return (index >= 0 && index < values.size()) ? values.get(index) : null;
    }

    @Override
    public Iterator<Scalar> iterator() {
        return Collections.unmodifiableList(values).iterator();
    }

    public int getNumOfValues() {
        return values.size();
    }

    public Stream<Scalar> parallelStream() {
        return values.parallelStream();
    }

    public Stream<Scalar> stream() {
        return values.stream();
    }

    public boolean isEmpty() {
        return CollectionUtils.isEmpty(values);
    }

    public void writeTo(final PrintStream out, final String seperator) {
        out.print(StringUtils.join(values, seperator));
    }

    public String[] asStringArray() {
        return values().map(Scalar::getValueAsString).collect(toList()).toArray(new String[]{});
    }

    @Override
    public String toString() {
        return "Array [" + values.size() + "]";
    }
}