
package org.exparity.data.xml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Stewart Bissett
 */
public class ValidationResult
{
	private final List<String> faults = new ArrayList<String>();
	private final boolean isValid;

	public ValidationResult(final boolean isValid, final List<String> faults)
	{
		this.isValid = isValid;
		this.faults.addAll(faults);
	}

	public ValidationResult(final boolean isValid, final String... faults)
	{
		this(isValid, Arrays.asList(faults));
	}

	public boolean isValid()
	{
		return isValid;
	}

	public List<String> getFaults()
	{
		return Collections.unmodifiableList(faults);
	}
}