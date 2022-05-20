package Controller.Command;

import Exceptions.GameEndedException;
import Model.Field;
import Model.Game;
import Model.Player;

public class EndGameCommand implements ICommand
{

    @Override
    public void apply(Player player, Game game, Field field, String[] arguments)
    {
        game.finish();
    }
}
