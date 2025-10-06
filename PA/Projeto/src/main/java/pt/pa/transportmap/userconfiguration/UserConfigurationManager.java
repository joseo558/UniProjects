package pt.pa.transportmap.userconfiguration;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to manage the user configuration. Command pattern.
 */
public class UserConfigurationManager {
    /** Map to associate command types with their respective stacks */
    private final Map<Class<? extends Command>, Deque<Command>> commands;

    /**
     * Constructor for the UserConfigurationManager
     */
    public UserConfigurationManager() {
        commands = new HashMap<>();
        commands.put(RouteCommand.class, new ArrayDeque<>());
        commands.put(RouteTransportTypeCommand.class, new ArrayDeque<>());
        commands.put(BicycleDurationScaleCommand.class, new ArrayDeque<>());
    }

    /**
     * Get the last command of a given type as a string
     * @param commandType Class<? extends Command> the type of command to get
     * @return String the last command of a given type as a string
     */
    public String getLastRouteCommand(Class<? extends Command> commandType) {
        Deque<Command> stack = commands.get(commandType);
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        return stack.peek().toString();
    }

    /**
     * Execute a command and add it to the stack
     * @param command Command the command to execute
     * @throws IllegalArgumentException if the command type is unknown
     */
    public void execute(Command command) throws IllegalArgumentException {
        Deque<Command> stack = commands.get(command.getClass().getInterfaces()[0]);
        if (stack == null) {
            throw new IllegalArgumentException("Unknown command type: " + command.getClass().getSimpleName());
        }
        command.execute();
        stack.push(command);
    }

    /**
     * Undo the last command
     * @param commandType Class<? extends Command> the type of command to undo
     * @throws IllegalArgumentException if the command type is unknown
     */
    public void undo(Class<? extends Command> commandType)  throws IllegalArgumentException {
        Deque<Command> stack = commands.get(commandType);
        if (stack == null || stack.isEmpty()) {
            throw new IllegalArgumentException("No commands to undo for this type.");
        }
        Command command = stack.pop();
        command.undo();
    }

    /**
     * Resets the disabled routes and transport types.
     * @param userConfiguration UserConfiguration the user configuration
     */
    public void resetDisabledRoutes(UserConfiguration userConfiguration) {
        Deque<Command> routesStack = commands.get(RouteCommand.class);
        Deque<Command> transportStack = commands.get(RouteTransportTypeCommand.class);

        if (transportStack == null) {
            throw new IllegalArgumentException("No disabled transport types to reset.");
        }
        if (routesStack == null) {
            throw new IllegalArgumentException("No disabled routes to reset.");
        }

        routesStack.clear();
        transportStack.clear();
        userConfiguration.resetDisabledRoutes();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("User configurations:");
        commands.forEach((type, stack) -> {
            sb.append("\n").append(type.getSimpleName()).append(" Commands:");
            for (Command command : stack) {
                sb.append("\n\t").append(command);
            }
        });
        return sb.toString();
    }
}