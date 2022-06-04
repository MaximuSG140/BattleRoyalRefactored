package Controller.Command;

import Model.Direction;
import Model.Field;
import Model.Game;
import Model.Player;

public class MoveCommand implements ICommand
{
    @Override
    public void apply(Player player, Game game, Field field, String[] arguments)
    {
        Direction direction;
        if(arguments.length != 3)
        {
            return;
        }
        if(!game.hasPlayer(player))
        {
            return;
        }
        switch(arguments[2])
        {
            case "U":
                direction = Direction.UP;
                break;
            case "D":
                direction = Direction.DOWN;
                break;
            case "L":
                direction = Direction.LEFT;
                break;
            case "R":
                direction = Direction.RIGHT;
                break;
            default:
                return;
        }
        field.movePawn(player.getControlledPawn(), direction);
    }
}
