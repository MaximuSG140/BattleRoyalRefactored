package Controller.Command;

import Model.Field;
import Model.Game;
import Model.Player;

public class EmptyCommand implements ICommand
{

    @Override
    public void apply(Player player, Game game, Field field, String[] arguments)
    {}
}
