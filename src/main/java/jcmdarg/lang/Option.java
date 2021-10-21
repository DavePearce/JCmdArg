package jcmdarg.lang;

/**
 * Describes a configurable option for a given command.
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
}