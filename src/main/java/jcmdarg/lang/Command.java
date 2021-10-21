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
package jcmdarg.lang;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;

import jcmdarg.util.AbstractOptionDescriptor;
import jcmdarg.util.OptionValue;

/**
 * A command which can be executed (e.g. from the command-line)
 *
 * @author David J. Pearce
 *
 */
public interface Command {

	/**
	 * Get a descriptor for this command.
	 *
	 * @return
	 */
	public Descriptor getDescriptor();

	/**
	 * Perform any necessary initialisation for this command (e.g. opening
	 * resources).
	 */
	public void initialise();

	/**
	 * Perform any necessary finalisation for this command (e.g. closing resources).
	 */
	public void finalise();

	/**
	 * Execute this command with the given arguments. Every invocation of this
	 * function occurs after a single call to <code>initialise()</code> and before
	 * any calls are made to <code>finalise()</code>. Observer, however, that this
	 * command may be executed multiple times.
	 */
	public boolean execute(Path path, Template template) throws Exception;

	/**
	 * Provides a descriptive information about this command. This includes
	 * information such as the name of the command, a description of the command as
	 * well as the set of arguments which are accepted.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Descriptor {
		/**
		 * Get the name of this command. This should uniquely identify the command in
		 * question.
		 *
		 * @return
		 */
		public String getName();

		/**
		 * Get a description of this command.
		 *
		 * @return
		 */
		public String getDescription();

		/**
		 * Get the list of configurable options for this command.
		 *
		 * @return
		 */
		public List<Option.Descriptor> getOptionDescriptors();

		/**
		 * Get descriptors for any sub-commands of this command.
		 *
		 * @return
		 */
		public List<Descriptor> getCommands();
	}

	/**
	 * A generic interface for access command options.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Options {
		/**
		 * Check whether a given option is given.
		 *
		 * @param name
		 * @return
		 */
		public boolean has(String name);
		/**
		 * Get the value associate with a given named option.
		 *
		 * @param kind
		 * @return
		 */
		public <T> T get(String name, Class<T> kind);
	}

	public interface Template {
		/**
		 * Get the command being described by this template.
		 *
		 * @return
		 */
		public Command.Descriptor getCommandDescriptor();

		/**
		 * Get the options described by this template, in the order in which they should
		 * be applied.
		 *
		 * @return
		 */
		public Command.Options getOptions();

		/**
		 * Get the arguments described by this template, in the order in which they
		 * should be applied.
		 *
		 * @return
		 */
		public List<String> getArguments();

		/**
		 * Get the child template (if any) given for this template. If no template, then
		 * this returns <code>null</code>.
		 *
		 * @return
		 */
		public Template getChild();
	}

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
		return new AbstractOptionDescriptor(name, argument, description, defaultValue) {
			@Override
			public Option Initialise(String arg) {
				int value = Integer.parseInt(arg);
				if (constraint.test(value)) {
					return new OptionValue(this, value);
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
		return new AbstractOptionDescriptor(name, argument, description, defaultValue) {
			@Override
			public Option Initialise(String arg) {
				double value = Double.parseDouble(arg);
				if (constraint.test(value)) {
					return new OptionValue(this, value);
				} else {
					throw new IllegalArgumentException("invalid double value");
				}
			}
		};
	}

	public static Option.Descriptor OPTION_FLAG(String name, String description) {
		return new AbstractOptionDescriptor(name, null, description, null) {
			@Override
			public Option Initialise(String arg) {
				if(arg.equals("false") || arg.equals("true")) {
					// If specified then should be true
					return new OptionValue(this, Boolean.parseBoolean(arg));
				} else {
					throw new IllegalArgumentException("invalid argument for " + name + " (expected nothing, \"true\" or \"false\")");
				}
			}
		};
	}


	public static Option.Descriptor OPTION_FLAG(String name, String description,
			boolean defaultValue) {
		return new AbstractOptionDescriptor(name, null, description, defaultValue) {
			@Override
			public Option Initialise(String arg) {
				if(arg.equals("false") || arg.equals("true")) {
					// If specified then should be true
					return new OptionValue(this, Boolean.parseBoolean(arg));
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
		return new AbstractOptionDescriptor(name, null, description, defaultValue) {
			@Override
			public Option Initialise(String arg) {
				return new OptionValue(this, arg);
			}
		};
	}

}
