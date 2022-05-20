package Controller.CommandFactory;

import Controller.Command.*;
import Model.CommandInstance;

import java.util.HashMap;

public class CommandFactory
{
    private final HashMap<String, ICommand> commandByName = new HashMap<>();

    public CommandFactory()
    {
        commandByName.put("LOGIN", new InitPlayerCommand());
        commandByName.put("LOGOFF", new LogOffPlayerCommand());
        commandByName.put("MOVE", new MoveCommand());
        commandByName.put("STOP", new EndGameCommand());
        commandByName.put("INCONSISTENT", new EmptyCommand());
        commandByName.put("PICK", new PickWeaponCommand());
    }

    public CommandInstance parseCommand(String line)
    {
        String[] arguments = line.split(" ");
        if (arguments.length <= 1 || !commandByName.containsKey(arguments[1]))
        {
            return new CommandInstance(commandByName.get("INCONSISTENT"), arguments);
        }

        return new CommandInstance(commandByName.get(arguments[1]), arguments);
    }
}
