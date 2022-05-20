package Controller.Command;

import Exceptions.GameEndedException;
import Model.Field;
import Model.Game;
import Model.Player;

public interface ICommand
{
    void apply(Player player, Game game, Field field, String[] arguments);
}
