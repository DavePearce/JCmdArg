// Copyright 2011 The Whiley Project Developers
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package jcmdarg.util;

import java.util.List;
import java.util.function.Predicate;

import jcmdarg.core.Option;
import jcmdarg.core.Option.Descriptor;

/**
 * Helper functions for constructing options.
 *
 * @author David J. Pearce
 *
 */
public class Options {

	/**
	 * An integer option which cannot be negative.
	 *
	 * @param name
	 * @param argument
	 * @param description

	 * @return
	 */
	public static Option.Descriptor OPTION_NONNEGATIVE_INTEGER(String name, String description) {
		return OPTION_INTEGER(name, "<n>", description + " (non-negative)", (n) -> (n >= 0), null);
	}

	/**
	 * An integer option which cannot be negative.
	 *
	 * @param name
	 * @param argument
	 * @param description
	 * @param defaultValue
	 *            the default value to use
	 * @return
	 */
	public static Option.Descriptor OPTION_NONNEGATIVE_INTEGER(String name, String description, int defaultValue) {
		return OPTION_INTEGER(name, "<n>", description + " (non-negative, default " + defaultValue + ")",
				(n) -> (n >= 0), defaultValue);
	}


	/**
	 * An integer option which must be positive.
	 *
	 * @param name
	 * @param argument
	 * @param description
	 * @param defaultValue
	 *            the default value to use
	 * @return
	 */
	public static Option.Descriptor OPTION_POSITIVE_INTEGER(String name, String description, int defaultValue) {
		return OPTION_INTEGER(name, "<n>", description + " (positive, default " + defaultValue + ")", (n) -> (n > 0), defaultValue);
	}

	/**
	 * An integer option with a constraint
	 *
	 * @param name
	 * @param description
	 * @return
	 */
	public static Option.Descriptor OPTION_INTEGER(String name, String argument, String description,
			Predicate<Integer> constraint, Integer defaultValue) {
		return new AbstractDescriptor(name, argument, description, defaultValue) {
			@Override
			public Option Initialise(String arg) {
				int value = Integer.parseInt(arg);
				if (constraint.test(value)) {
					return new Value(this, value);
				} else {
					throw new IllegalArgumentException("invalid integer value");
				}
			}
		};
	}

	/**
	 * An integer option which cannot be negative.
	 *
	 * @param name
	 * @param argument
	 * @param description

	 * @return
	 */
	public static Option.Descriptor OPTION_BOUNDED_DOUBLE(String name, String description, double low, double high) {
		return OPTION_DOUBLE(name, "<n>", description + " (between " + low + ".." + high + ")",
				(n) -> (n >= low && n <= high), low, high, null);
	}

	/**
	 * A decimal option with a constraint
	 *
	 * @param name
	 * @param description
	 * @return
	 */
	public static Option.Descriptor OPTION_DOUBLE(String name, String argument, String description,
			Predicate<Double> constraint, double low, double high, Double defaultValue) {
		return new AbstractDescriptor(name, argument, description, defaultValue) {
			@Override
			public Option Initialise(String arg) {
				double value = Double.parseDouble(arg);
				if (constraint.test(value)) {
					return new Value(this, value);
				} else {
					throw new IllegalArgumentException("invalid double value");
				}
			}
		};
	}

	public static Option.Descriptor OPTION_FLAG(String name, String description) {
		return new AbstractDescriptor(name, null, description, null) {
			@Override
			public Option Initialise(String arg) {
				if(arg.equals("false") || arg.equals("true")) {
					// If specified then should be true
					return new Value(this, Boolean.parseBoolean(arg));
				} else {
					throw new IllegalArgumentException("invalid argument for " + name + " (expected nothing, \"true\" or \"false\")");
				}
			}
		};
	}


	public static Option.Descriptor OPTION_FLAG(String name, String description,
			boolean defaultValue) {
		return new AbstractDescriptor(name, null, description, defaultValue) {
			@Override
			public Option Initialise(String arg) {
				if(arg.equals("false") || arg.equals("true")) {
					// If specified then should be true
					return new Value(this, Boolean.parseBoolean(arg));
				} else {
					throw new IllegalArgumentException("invalid argument for " + name + " (expected nothing, \"true\" or \"false\")");
				}
			}
		};
	}


	/**
	 * An string option
	 *
	 * @param name
	 * @param argument
	 * @param description

	 * @return
	 */
	public static Option.Descriptor OPTION_STRING(String name, String description, String defaultValue) {
		return new AbstractDescriptor(name, null, description, defaultValue) {
			@Override
			public Option Initialise(String arg) {
				return new Value(this, arg);
			}
		};
	}

	/**
	 * A generic class for handling option descriptors.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static abstract class AbstractDescriptor implements Option.Descriptor {
		private final String name;
		private final String argDescription;
		private final String description;
		private final Object defaultValue;

		public AbstractDescriptor(String name, String argDescription, String description, Object defaultValue) {
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

	/**
	 * A simple implementation of <code>Option</code> which simply wraps an object
	 * value.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Value implements Option {
		private final Option.Descriptor descriptor;
		private final Object contents;

		public Value(Option.Descriptor descriptor, Object contents) {
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


	/**
	 * A simple flat map representation of an <code>Option.Map</code>.
	 *
	 * @author David J. Pearce
	 *
	 */
	public static class Map implements Option.Map {
		private Option.Descriptor[] descriptors;
		private Option[] options;

		public Map(List<Option> options, List<Option.Descriptor> descriptors) {
			this.options = options.toArray(new Option[options.size()]);
			this.descriptors = descriptors.toArray(new Option.Descriptor[descriptors.size()]);
		}

		@Override
		public boolean has(String name) {
			for (int i = 0; i != options.length; ++i) {
				Option option = options[i];
				if (option.getDescriptor().getName().equals(name)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public <T> T get(String name, Class<T> kind) {
			// Check for given values
			for (int i = 0; i != options.length; ++i) {
				Option option = options[i];
				if (option.getDescriptor().getName().equals(name)) {
					return option.get(kind);
				}
			}
			// Check for default values
			for (int i = 0; i != descriptors.length; ++i) {
				Option.Descriptor d = descriptors[i];
				Object val = d.getDefaultValue();
				if (kind.isInstance(val)) {
					return (T) val;
				}
			}
			throw new IllegalArgumentException("invalid option " + name);
		}

		@Override
		public String toString() {
			String r = "{";
			for (int i = 0; i != options.length; ++i) {
				if(i!=0) {
					r = r + ",";
				}
				Option option = options[i];
				r = r + option.getDescriptor().getName();
				r = r + "=" + option.get(Object.class);
			}
			return r + "}";
		}
	}
}
