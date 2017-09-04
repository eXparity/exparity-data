/*
 * 
 */

package org.exparity.data.html;

import java.util.List;

import org.exparity.data.BadConversionException;
import org.exparity.data.types.Array;
import org.exparity.data.types.Table;

/**
 * @author Stewart Bissett
 */
public abstract class Tag {

    public abstract List<Tag> getChildren();

    public abstract List<Attribute> getAttributes();

    /**
     * Returns the text contained within the tag and includes the content of any nested tags
     */
    public abstract String getText();

    /**
     * Returns the text contained within the tag and includes the content of any nested tags. It also retains any
     * formatting such as line breaks or paragraphs defined in the tag
     */
    public abstract String getFormattedText();

    public abstract String getName();

    public abstract Tag getParent();

    public abstract String getAttribute(final String attributeName);

    public abstract boolean isType(String... types);

    public abstract Array toArray() throws BadConversionException;

    public abstract Table toTable() throws BadConversionException;
}
