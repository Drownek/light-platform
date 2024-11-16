
# light-platform

Platform for creating bukkit plugins, with use of:
- a dependency injector to remove need for manually creating instances of service/component classes
- LiteCommands - a simple commands library, the platform automatically registers classes annotated with `@Command` or `@RootCommand` with no need to register it yourself
## Usage/Examples

Main class:
```java
@Scan(deep = true) // allows to automatically add classes annotated with @Component or @Service to the injector
public class Plugin extends OkaeriBukkitPlugin { // you must extend OkaeriBukkitPlugin

    @Planned(ExecutionPhase.STARTUP) // equal to onEnable()
    public void onStartup(
        LiteCommandsBuilder<CommandSender, ?, ?> commands // you can get a instance of LiteCommandsBuilder here to add some arguments etc. 
    ) {
        // add example argument and create it using createInstance to automatically fulfill fields annotated with @Inject
        commands.argument(User.class, this.createInstance(UserArgument.class));
    }

    @Planned(ExecutionPhase.PRE_SHUTDOWN) // equal to onDisable()
    public void onShutdown(Listenery listenery) {
    }
}
```

Example UserManager class:
```java
@Component
public class UserManager {

    private final @Getter Map<UUID, User> usersByUuid = new ConcurrentHashMap<>();
}
```

Example command that don't have to be registered anywhere:
```java
@Command(name = "test")
@Permission("test.admin")
public class TestCommand {

    private @Inject Plugin plugin;
    private @Inject UserManager userManager; // you can now use UserManager

    @Execute
    void test(@Context Player player) {
        DataGatherer.builder(this.plugin) // example of data gatherer, with step based input for creating arenas etc.
            .steps(
                new ChatMessageStep("Type something in chat", input -> // predefined chat message step
                    player.sendMessage("Your input: " + input)
                )
            )
            .endAction(() -> player.sendMessage("All done!"))
            .build()
            .start(player);
    }
}
```


### Contributions and Credits
- Code from [okaeri-platform](https://github.com/OkaeriPoland/okaeri-platform) by Okaeri, Dawid Sawicki, licensed under the MIT License.
- This project also uses [LiteCommands](https://github.com/Rollczi/LiteCommands) by Rollczi, licensed under the Apache License 2.0.
