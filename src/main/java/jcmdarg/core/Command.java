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
package jcmdarg.core;

import java.nio.file.Path;
import java.util.List;

import jcmdarg.util.CommandParser;

/**
 * A command which can be executed (e.g. from the command-line). Every command
 * includes a <i>descriptor</i> which gives a name to the command, describes the
 * options supported by the command and identifies any <i>sub-commands</i>.
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
	 * any calls are made to <code>finalise()</code>. Observe, however, that this
	 * command may be executed multiple times.
	 */
	public boolean execute(Path path, Instance template) throws Exception;

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
	 * Represents an instantiated command from a given set of command-line
	 * arguments.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Instance {
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
		public Option.Map getOptions();

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
		public Instance getChild();
	}

	/**
	 * The main entry point for parsing a given set of command-line options.
	 *
	 * @param root
	 * @param args
	 * @return
	 */
	public static Command.Instance parse(Command.Descriptor root, String... args) {
		return new CommandParser(root).parse(args);
	}
}
