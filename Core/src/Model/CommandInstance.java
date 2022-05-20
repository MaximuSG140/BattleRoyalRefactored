package Model;

import Controller.Command.ICommand;
import Exceptions.GameEndedException;

public class CommandInstance
{
    private final ICommand command;
    private final String[] arguments;

    public CommandInstance(ICommand command, String[] arguments)
    {
        this.arguments = arguments;
        this.command = command;
    }

    public void apply(Player player, Game game, Field field) {
        command.apply(player, game, field, arguments);
    }
}
