/*
 * 
 */

package org.exparity.data.text;

import java.util.function.Function;

import org.exparity.data.Text;

/**
 * @author Stewart Bissett
 */
public interface TextSelector<T> extends Function<Text, T> {

}
