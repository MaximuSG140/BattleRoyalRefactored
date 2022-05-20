package Controller.Command;

import Model.Field;
import Model.Game;
import Model.Player;

public class PickWeaponCommand implements ICommand
{

    @Override
    public void apply(Player player, Game game, Field field, String[] arguments)
    {
        if(!player.hasPawn())
        {
            return;
        }
        field.pickWeapon(player.getControlledPawn());
    }
}
