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

import java.util.ArrayList;
import java.util.List;

import jcmdarg.core.Command;
import jcmdarg.core.Option;

/**
 * <p>
 * A generic mechanism for parsing command-line options, which is perhaps
 * reminiscent of optarg, etc. The key here is the structure of command-line
 * arguments:
 * </p>
 *
 * <pre>
 * wy <tool / project options> (command <options> <values>)*
 * </pre>
 *
 * <p>
 * Each level corresponds to a deeper command within the hierarchy. Furthermore,
 * each also corresponds to entries in a configuration file as well.
 * </p>
 *
 * @author David J. Pearce
 *
 */
public class CommandParser<S,T> {
	/**
	 * The list of command roots.
	 */
	private final Command.Descriptor<S,T> root;

	public CommandParser(Command.Descriptor<S,T> root) {
		this.root = root;
	}

	/**
	 * Parse a given set of command-line arguments to produce an appropriate command
	 * template.
	 *
	 * @param args
	 */
	public Command.Arguments<S,T> parse(String[] args) {
		return parse(root,args,0);
	}

	/**
	 * Parse a given set of command-line arguments starting from a given index
	 * position to produce an appropriate command template.
	 *
	 * @param args
	 * @param index
	 */
	protected ConcreteInstance<S,T> parse(Command.Descriptor<S,T> root, String[] args, int index) {
		ArrayList<Option> options = new ArrayList<>();
		ArrayList<String> arguments = new ArrayList<>();
		// Parse command (if non-root)
		if(root.getName() != null && !root.getName().equals(args[index])) {
			throw new IllegalArgumentException(
					"unknown command encountered \"" + args[index] + "\", expecting \"" + root.getName() + "\"");
		} else if(root.getName() != null) {
			index = index + 1;
		}
		// Parse an options for this command, and any subcommands encountered.
		ConcreteInstance<S,T> sub = null;
		while (index < args.length) {
			String arg = args[index];
			if (isLongOption(arg)) {
				options.add(parseLongOption(root, args[index]));
			} else if (isCommand(arg, root.getCommands())) {
				Command.Descriptor<S,T> cmd = getCommandDescriptor(arg, root.getCommands());
				sub = parse(cmd, args, index);
				break;
			} else {
				arguments.add(arg);
			}
			index = index + 1;
		}
		//

		Option.Map optionMap = new Options.Map(options, root.getOptionDescriptors());
		//
		return new ConcreteInstance<S,T>(root, optionMap, arguments, sub);
	}

	protected boolean isLongOption(String arg) {
		return arg.startsWith("--");
	}

	public Option parseLongOption(Command.Descriptor<S,T> cmd, String arg) {
		List<Option.Descriptor> descriptors = cmd.getOptionDescriptors();
		arg = arg.replace("--", "");
		String[] splits = arg.split("=");
		String key = splits[0];
		String value = "true";
		if (splits.length > 1) {
			value = splits[1];
		} else if (splits.length > 2) {
			throw new IllegalArgumentException("invalid option: " + arg);
		}
		for (int i = 0; i != descriptors.size(); ++i) {
			Option.Descriptor descriptor = descriptors.get(i);
			if (descriptor.getName().equals(key)) {
				// matched
				return descriptor.Initialise(value);
			}
		}
		throw new IllegalArgumentException("invalid option: " + arg);
	}

	protected boolean isCommand(String arg, List<Command.Descriptor<S,T>> descriptors) {
		for (int i = 0; i != descriptors.size(); ++i) {
			Command.Descriptor<S,T> descriptor = descriptors.get(i);
			if (arg.equals(descriptor.getName())) {
				return true;
			}
		}
		return false;
	}

	protected Command.Descriptor<S,T> getCommandDescriptor(String arg, List<Command.Descriptor<S,T>> descriptors) {
		for (int i = 0; i != descriptors.size(); ++i) {
			Command.Descriptor<S,T> descriptor = descriptors.get(i);
			if (arg.equals(descriptor.getName())) {
				return descriptor;
			}
		}
		throw new IllegalArgumentException("invalid command: " + arg);
	}

	protected static class ConcreteInstance<S,T> implements Command.Arguments<S,T> {
		private final Command.Descriptor<S,T> descriptor;
		private final Option.Map options;
		private final List<String> arguments;
		private final ConcreteInstance<S,T> sub;

		public ConcreteInstance(Command.Descriptor<S,T> descriptor, Option.Map options, List<String> arguments,
				ConcreteInstance<S,T> sub) {
			this.descriptor = descriptor;
			this.options = options;
			this.arguments = arguments;
			this.sub = sub;
		}

		@Override
		public List<String> getArguments() {
			return arguments;
		}

		@Override
		public Option.Map getOptions() {
			return options;
		}

		@Override
		public Command<T> initialise(S state) {
			// Update state through command
			state = descriptor.apply(this, state);
			//
			if(sub == null) {
				return descriptor.initialise(state);
			} else {
				// Defer to sub-command
				return sub.initialise(state);
			}
		}

		@Override
		public String toString() {
			return descriptor.getName() + ":" + options.toString() + ":" + arguments;
		}
	}
}
