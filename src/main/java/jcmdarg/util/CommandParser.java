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
import jcmdarg.core.Command.Instance;

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
public class CommandParser {
	/**
	 * The list of command roots.
	 */
	private final Command.Descriptor root;

	public CommandParser(Command.Descriptor root) {
		this.root = root;
	}

	/**
	 * Parse a given set of command-line arguments to produce an appropriate command
	 * template.
	 *
	 * @param args
	 */
	public Command.Instance parse(String[] args) {
		return parse(root,args,0);
	}

	/**
	 * Parse a given set of command-line arguments starting from a given index
	 * position to produce an appropriate command template.
	 *
	 * @param args
	 * @param index
	 */
	protected Command.Instance parse(Command.Descriptor root, String[] args, int index) {
		ArrayList<Option> options = new ArrayList<>();
		ArrayList<String> arguments = new ArrayList<>();
		//
		Command.Instance sub = null;
		while (index < args.length) {
			String arg = args[index];
			if (isLongOption(arg)) {
				options.add(parseLongOption(root, args[index]));
			} else if (isCommand(arg, root.getCommands())) {
				Command.Descriptor cmd = getCommandDescriptor(arg, root.getCommands());
				sub = parse(cmd, args, index + 1);
				break;
			} else {
				arguments.add(arg);
			}
			index = index + 1;
		}
		//

		Option.Map optionMap = new Options.Map(options, root.getOptionDescriptors());
		//
		return new ConcreteTemplate(root, optionMap, arguments, sub);
	}

	protected boolean isLongOption(String arg) {
		return arg.startsWith("--");
	}

	public Option parseLongOption(Command.Descriptor cmd, String arg) {
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

	protected boolean isCommand(String arg, List<Command.Descriptor> descriptors) {
		for (int i = 0; i != descriptors.size(); ++i) {
			Command.Descriptor descriptor = descriptors.get(i);
			if (arg.equals(descriptor.getName())) {
				return true;
			}
		}
		return false;
	}

	protected Command.Descriptor getCommandDescriptor(String arg, List<Command.Descriptor> descriptors) {
		for (int i = 0; i != descriptors.size(); ++i) {
			Command.Descriptor descriptor = descriptors.get(i);
			if (arg.equals(descriptor.getName())) {
				return descriptor;
			}
		}
		throw new IllegalArgumentException("invalid command: " + arg);
	}

	protected static class ConcreteTemplate implements Command.Instance {
		private final Command.Descriptor descriptor;
		private final Option.Map options;
		private final List<String> arguments;
		private final Command.Instance sub;

		public ConcreteTemplate(Command.Descriptor descriptor,  Option.Map options, List<String> arguments,
				Command.Instance sub) {
			this.descriptor = descriptor;
			this.options = options;
			this.arguments = arguments;
			this.sub = sub;
		}

		@Override
		public Command.Descriptor getCommandDescriptor() {
			return descriptor;
		}

		@Override
		public List<String> getArguments() {
			return arguments;
		}

		@Override
		public Instance getChild() {
			return sub;
		}

		@Override
		public Option.Map getOptions() {
			return options;
		}
	}
}
