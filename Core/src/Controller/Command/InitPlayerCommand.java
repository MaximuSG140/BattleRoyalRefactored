package Controller.Command;

import Exceptions.PlayerDuplicateException;
import Model.Field;
import Model.Game;
import Model.Player;

public class InitPlayerCommand implements ICommand
{

    @Override
    public void apply(Player player, Game game, Field field, String[] arguments)
    {
        if(player.hasPawn())
        {
            return;
        }
        game.addPlayer(player);
        game.addPawn(player);
    }
}
