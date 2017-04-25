/*
 * 
 */

package org.exparity.data;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.exparity.data.text.TextMatcher;
import org.exparity.data.text.TextSelector;

/**
 * Base class for all non-binary document types.
 *
 * @author Stewart Bissett
 */
public abstract class Text {

    /**
     * Return the contents of the document as an {@link InputStream}
     */
    public InputStream asStream() {
        return new ByteArrayInputStream(getText().getBytes());
    }

    /**
     * Return the contents of the document as an {@link InputStream}
     */
    public Reader asReader() {
        return new StringReader(getText());
    }

    /**
     * Write the contents of the documents to the {@link OutputStream}
     */
    public void writeTo(final OutputStream os) throws IOException {
        os.write(getText().getBytes());
    }

    /**
     * Write the contents of the document to the {@link OutputStream} using the defined encoding system
     */
    public void writeTo(final OutputStream os, final String encoding) throws IOException {
        os.write(getText().getBytes(encoding));
    }

    /**
     * Write the contents of the document to the {@link Writer}
     */
    public void writeTo(final Writer writer) throws IOException {
        writer.write(getText());
    }

    /**
     * Get the text of the document
     */
    public abstract String getText();

    /**
     * Return the first regular expression match in the document for the {@link Pattern}. Returns null if the pattern
     * does not exist
     */
    public String findUniqueByRegex(final Pattern regex) {
        List<String> matches = findByRegex(regex);
        return CollectionUtils.isEmpty(matches) ? null : matches.get(0);
    }

    /**
     * Return the first regular expression match in the document. Returns null if the pattern does not exist
     */
    public String findUniqueByRegex(final String regex) {
        return findUniqueByRegex(Pattern.compile(regex));
    }

    /**
     * Return all the matches in the document for the specified regular expression
     */
    public List<String> findByRegex(final String regex) {
        return findByRegex(Pattern.compile(regex));
    }

    /**
     * Return all the matches in the document for the specifiec {@link Pattern}.
     */
    public List<String> findByRegex(final Pattern regex) {
        final List<String> matches = new ArrayList<>();
        final String source = getText();

        final Matcher matcher = regex.matcher(source);
        while (matcher.find()) {
            for (int i = 0; i < matcher.groupCount(); ++i) {
                matches.add(matcher.group(i + 1));
            }
        }

        return matches;
    }

    /**
     * Test if this document matches the condition
     */
    public boolean has(final TextMatcher matcher) {
        return matcher.test(this);
    }

    /**
     * Get the items in this document which match the selector
     */
    public <T> T find(final TextSelector<T> selector) {
        return selector.apply(this);
    }
}
