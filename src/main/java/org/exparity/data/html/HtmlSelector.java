/*
 *
 */

package org.exparity.data.html;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.exparity.data.HTML;

/**
 * Predicate for selecting {@link Tag} instances which match the defined criteria
 *
 * @author Stewart Bissett
 */
public abstract class HtmlSelector {

    /**
     * Selects tables from a {@link HTML} document
     */
    public static final HtmlSelector TABLE = HtmlSelector.byTagName(HTML.TABLE_TAG);

    /**
     * Selects the title from a {@link HTML} document
     */
    public static final HtmlSelector TITLE = HtmlSelector.byTagName(HTML.TITLE_TAG);

    /**
     * Selects HTML structures which can be represented as arrays from a {@link HTML} document
     */
    public static final HtmlSelector ARRAY = HtmlSelector.byTagName(HTML.ORDERED_LIST_TAG,
            HTML.UNORDERED_LIST_TAG,
            HTML.DEFINITION_LIST_TAG,
            HTML.SELECT_TAG);

    /**
     * Construct a {@link HtmlSelector} which matches {@link Tag} instances by it's ID
     */
    public static HtmlSelector byId(final String elementId) {
        return HtmlSelector.byAttributeValue("id", elementId);
    }

    /**
     * Construct a {@link HtmlSelector} which matches {@link Tag} instances by attribute name
     */
    public static HtmlSelector byAttributeName(final String... names) {
        return new AttributeSelector(names);
    }

    /**
     * Construct a {@link HtmlSelector} which matches {@link Tag} instances by attribute name and value
     */
    public static HtmlSelector byAttributeValue(final String name, final String value) {
        return new AttributeSelector(name, value);
    }

    /**
     * Construct a {@link HtmlSelector} which matches {@link Tag} instances by attribute name and who's value matches
     * the regular expression supplied in {@link Pattern}
     */
    public static HtmlSelector byAttributeRegex(final String name, final Pattern value) {
        return new AttributeRegexSelector(name, value);
    }

    /**
     * Construct a {@link HtmlSelector} which matches {@link Tag} instances by the tag name
     */
    public static HtmlSelector byTagName(final String... names) {
        return new TagSelector(names);
    }

    /**
     * Construct a {@link HtmlSelector} which matches {@link Tag} instances who's tag name matches the regular
     * expression supplied in {@link Pattern}
     */
    public static HtmlSelector byTagRegex(final Pattern pattern) {
        return new TagRegexSelector(pattern);
    }

    /**
     * Predicate function which returns <code>true</code> if the {@link Tag} matches the predicate, else
     * <code>false</code>
     */
    public abstract boolean matches(final Tag tag);

    private static class AttributeSelector extends HtmlSelector {

        private final String[] names;
        private final String value;

        public AttributeSelector(final String name, final String value) {
            this.names = new String[] { name };
            this.value = value;
        }

        public AttributeSelector(final String... names) {
            this.names = names;
            this.value = null;
        }

        @Override
        public boolean matches(final Tag tag) {
            for (String name : names) {
                String value = tag.getAttribute(name);
                if (value != null) {
                    if (this.value == null || this.value.equalsIgnoreCase(value)) {
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public String toString() {
            return "AttributeSelector [" + StringUtils.join(names, ", ") + ":" + value + "]";
        }
    }

    private static class TagSelector extends HtmlSelector {

        private final String[] names;

        public TagSelector(final String... names) {
            this.names = names;
        }

        @Override
        public boolean matches(final Tag tag) {
            for (String name : names) {
                if (name.equalsIgnoreCase(tag.getName())) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public String toString() {
            return "TagSelector [" + StringUtils.join(names, ",") + "]";
        }
    }

    private static class TagRegexSelector extends HtmlSelector {

        private final Pattern pattern;

        public TagRegexSelector(final Pattern pattern) {
            this.pattern = pattern;
        }

        @Override
        public boolean matches(final Tag tag) {
            return pattern.matcher(tag.getName()).matches();
        }

        @Override
        public String toString() {
            return "TagRegexSelector [" + pattern.pattern() + "]";
        }
    }

    private static class AttributeRegexSelector extends HtmlSelector {

        private final Pattern pattern;
        private final String name;

        public AttributeRegexSelector(final String name, final Pattern pattern) {
            this.name = name;
            this.pattern = pattern;
        }

        @Override
        public boolean matches(final Tag tag) {
            String value = tag.getAttribute(name);
            return value != null && pattern.matcher(value).matches();
        }

        @Override
        public String toString() {
            return "AttributeRegexSelector [" + pattern.pattern() + "]";
        }
    }

}
