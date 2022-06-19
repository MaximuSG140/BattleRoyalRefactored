package Network;

import Controller.CommandFactory.CommandFactory;
import Model.Game;
import Model.Player;

import java.io.IOException;

public class BotController implements IController
{
    private Player player;
    private Game game;
    private CommandFactory factory;

    public BotController(Game g, Player player)
    {
        this.player = player;
        game = g;
        factory = new CommandFactory();
    }

    @Override
    public void send(String line) throws IOException
    {
        var parsedCommand = factory.parseCommand(line);
        game.addCommand(player, parsedCommand);
    }

    public String getName()
    {
        return player.getName();
    }

}
