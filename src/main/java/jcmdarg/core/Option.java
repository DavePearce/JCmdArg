package jcmdarg.core;

/**
 * Describes a configurable option for a command. Every option has a
 * <i>descriptor</i> which provides information about the option, as well as the
 * ability to construct an option from a <code>String</code>.
 *
 * @author David J. Pearce
 *
 */
public interface Option {

	/**
	 * Get the descriptor from which this instance was created.
	 *
	 * @return
	 */
	public Option.Descriptor getDescriptor();

	/**
	 * Get the value associate with this option.
	 *
	 * @param kind
	 * @return
	 */
	public <T> T get(Class<T> kind);

	/**
	 * Provides a descriptor for the option.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Descriptor {
		/**
		 * Get the option name.
		 *
		 * @return
		 */
		public String getName();

		/**
		 * Get the description for the argument
		 * @return
		 */
		public String getArgumentDescription();

		/**
		 * Get a suitable description for the option.
		 *
		 * @return
		 */
		public String getDescription();

		/**
		 * Get the default value for this option (or null if no suitable default).
		 *
		 * @return
		 */
		public Object getDefaultValue();

		/**
		 * Construct a given option from a given argument string.
		 *
		 * @param arg
		 * @return
		 */
		public Option Initialise(String arg);
	}

	/**
	 * A generic interface for accessing command options.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Map {
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
		public <T> T get(Class<T> kind, String name);
	}
}