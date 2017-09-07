/*
 *
 */

package org.exparity.data.types;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.math.NumberUtils;

/**
 * @author Stewart Bissett
 */
public class Scalar implements Comparable<Scalar> {

    private final String value;
    private final Integer intValue;
    private final Long longValue;
    private final Double doubleValue;
    private final Boolean booleanValue;

    public Scalar(final Object object) {
        if (object instanceof String) {
            this.value = (String) object;
            this.doubleValue = NumberUtils.isNumber(value) ? Double.valueOf(value) : null;
            this.intValue = NumberUtils.isNumber(value) ? doubleValue.intValue() : null;
            this.longValue = NumberUtils.isNumber(value) ? doubleValue.longValue() : null;
            this.booleanValue = null;
        } else
            if (object instanceof Number) {
                this.value = String.valueOf(object);
                this.intValue = ((Number) object).intValue();
                this.longValue = ((Number) object).longValue();
                this.doubleValue = ((Number) object).doubleValue();
                this.booleanValue = null;
            } else
                if (object instanceof Boolean) {
                    this.value = ((Boolean) object).toString();
                    this.intValue = null;
                    this.longValue = null;
                    this.doubleValue = null;
                    this.booleanValue = (Boolean) object;
                } else {
                    throw new IllegalArgumentException("Scalar only supports String, Number, or Boolean values");
                }
    }

    public String getValueAsString() {
        return value;
    }

    /**
     * Return the value of the scalar as a boolean
     */
    public boolean getValueAsBoolean() {
        return booleanValue != null ? booleanValue : Boolean.valueOf(value);
    }

    /**
     * Return the value of the scalar as a double or throw a {@link NumberFormatException} if the value is not
     * convertible to a double
     */
    public double getValueAsDouble() {
        return doubleValue != null ? doubleValue : Double.valueOf(value);
    }

    /**
     * Return the value of the scalar as a double or throw a {@link NumberFormatException} if the value is not
     * convertible to a double
     */
    public BigDecimal getValueAsDecimal() {
        return doubleValue != null ? BigDecimal.valueOf(doubleValue) : BigDecimal.valueOf(Double.valueOf(value));
    }

    /**
     * Return the value of the scalar as a long or throw a {@link NumberFormatException} if the value is not convertible
     * to a long
     */
    public long getValueAsLong() {
        return longValue != null ? longValue : Long.valueOf(value);
    }

    /**
     * Return the value of the scalar as a integer or throw a {@link NumberFormatException} if the value is not
     * convertible to an integer
     */
    public int getValueAsInteger() {
        return intValue != null ? intValue : Integer.valueOf(value);
    }

    /**
     * Return the value of the scalar as a date or throw a {@link ParseException} if the value is not convertible to a
     * date using the supplied formats
     */
    public LocalDate getValueAsDate(final DateTimeFormatter format) {
        return LocalDate.parse(value, format);
    }

    public LocalTime getValueAsTime() {
        return LocalTime.parse(value);
    }

    public LocalTime getValueAsTime(final DateTimeFormatter format) {
        return LocalTime.parse(value, format);
    }

    @Override
    public int compareTo(final Scalar o) {
        try {
            if (intValue != null) {
                return intValue.compareTo(o.getValueAsInteger());
            } else
                if (longValue != null) {
                    return longValue.compareTo(o.getValueAsLong());
                } else
                    if (doubleValue != null) {
                        return doubleValue.compareTo(o.getValueAsDouble());
                    } else
                        if (booleanValue != null) {
                            return booleanValue.compareTo(o.getValueAsBoolean());
                        } else {
                            return value.compareTo(o.value);
                        }
        } catch (NumberFormatException e) {
            return value.compareTo(o.value);
        }
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Scalar)) return false;
        final Scalar other = (Scalar) obj;
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(11, 21).append(value).toHashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
