package jcmdarg.util;

import jcmdarg.lang.Option;
import jcmdarg.lang.Option.Descriptor;

/**
 * A generic class for handling option descriptors.
 *
 * @author David J. Pearce
 *
 */
public abstract class AbstractOptionDescriptor implements Option.Descriptor {
	private final String name;
	private final String argDescription;
	private final String description;
	private final Object defaultValue;

	public AbstractOptionDescriptor(String name, String argDescription, String description, Object defaultValue) {
		this.name = name;
		this.argDescription = argDescription;
		this.description = description;
		this.defaultValue = defaultValue;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getArgumentDescription() {
		return argDescription;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public Object getDefaultValue() {
		return defaultValue;
	}
}