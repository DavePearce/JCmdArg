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
public interface Command<T> {

	/**
	 * Execute this command returning a given value.
	 */
	public T execute();

	/**
	 * Provides a descriptive information about this command. This includes
	 * information such as the name of the command, a description of the command as
	 * well as the set of arguments which are accepted.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Descriptor<S,T> {
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
		 * Initialise a command from this descriptor using a given piece of state.
		 *
		 * @param state
		 * @return
		 */
		public Command<T> initialise(S state);

		/**
		 * Apply this descriptor to a given instance and current state. This allows the
		 * descriptor to update the configuration based on options and arguments passed
		 * to it. The final state is intented for a sub-command downstream which will
		 * eventually be the one executed.
		 *
		 * @param instance
		 * @param state
		 * @return
		 */
		public S apply(Arguments<S,T> instance, S state);

		/**
		 * Get descriptors for any sub-commands of this command.
		 *
		 * @return
		 */
		public List<Descriptor<S,T>> getCommands();
	}

	/**
	 * Represents an instantiated set of arguments for a given command.
	 *
	 * @author David J. Pearce
	 *
	 */
	public interface Arguments<S,T> {

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
		 * Initialise the actual command to be executed from a given piece of starting
		 * state.
		 *
		 * @param state
		 * @return
		 */
		public Command<T> initialise(S state);
	}

	/**
	 * The main entry point for parsing a given set of command-line options.
	 *
	 * @param root
	 * @param args
	 * @return
	 */
	public static <S, T> Command.Arguments<S, T> parse(Command.Descriptor<S, T> root, String... args) {
		return new CommandParser<>(root).parse(args);
	}
}
