## Overview

A small library for describing and parsing commands and subcommands.
Every command supports a <i>descriptor</i> which dictates what options
it can accept, and provides a human-readable description.

Let's consider a minimal command:

```Java
class Main implements Command<Boolean> {
  private final String state;

  public Main(String state) {
    this.state = state;
  }

  public Boolean execute() {
    System.out.println("TEST(" + state + ")");
    return true;
  }
}
```

This is _instantiated_ with some given state, and can then be
_executed_ producing a given return value.  A simple descriptor for
this command is the following:

```Java
Command.Descriptor<String, Boolean> DESCRIPTOR = new Command.Descriptor<>() {

  public List<Option.Descriptor> getOptionDescriptors() {
    return Arrays.asList(Options.OPTION_FLAG("flag", "Toggle something"));
  }

  public String getName() {
    return null;
  }

  public String getDescription() {
    return "This is a simple test command";
  }

  public List<Descriptor<String,Boolean>> getCommands() {
    return Collections.emptyList();
  }

  public Main initialise(String state) {
    return new Main(state);
  }

  public String apply(Arguments<String, Boolean> args, String state) {
    if(args.getOptions().has("flag")) {
      return state + "{flagged}";
    } else {
      return state + "{}";
    }
  }
};
```

This configures the command as a _root_ command (i.e. since
`getName()` returns `null`) that has no _subcommands_ (i.e. since
`getCommands()` returns an empty list) and which accepts a single
option (`--flag`) which is either provided or not.  The descriptor is
then applied to a given set of _arguments_ to generate appropriate
state for initialising our command `Main`.

Finally, we can use this to parse some command-line arguments as
follows:

```Java
// Parse command-line arguments
Command.Arguments<String, Boolean> args = Command.parse(DESCRIPTOR, _args);
// Initialise command and execute it!
Boolean b = args.initialise("").execute();
//
System.out.println("RESULT = " + b);
```