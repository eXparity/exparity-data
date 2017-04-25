/*
 * 
 */

package org.exparity.data.text;

import java.util.List;
import java.util.regex.Pattern;

import org.exparity.data.Text;

/**
 * @author Stewart Bissett
 */
public class RegexSelector implements TextSelector<List<String>> {
    private final Pattern regex;

    public RegexSelector(final Pattern regex) {
        this.regex = regex;
    }

    public RegexSelector(final String regex) {
        this(Pattern.compile(regex));
    }

    @Override
    public List<String> apply(final Text source) {
        List<String> matches = source.findByRegex(regex);
        for (String match : matches) {
            matches.add(match);
        }
        return matches;
    }
}
