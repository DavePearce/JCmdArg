package jcmdarg.util;

import jcmdarg.lang.Option;
import jcmdarg.lang.Option.Descriptor;

public class OptionValue implements Option {
	private final Option.Descriptor descriptor;
	private final Object contents;

	public OptionValue(Option.Descriptor descriptor, Object contents) {
		this.descriptor = descriptor;
		this.contents = contents;
	}

	@Override
	public Descriptor getDescriptor() {
		return descriptor;
	}

	@Override
	public <T> T get(Class<T> kind) {
		if(kind.isInstance(contents)) {
			return (T) contents;
		} else {
			throw new IllegalArgumentException(
					"expected option value " + kind.getSimpleName() + ", got " + contents);
		}
	}

	@Override
	public String toString() {
		return descriptor.getName() + "=" + contents;
	}
}