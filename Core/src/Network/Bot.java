package Network;

import Model.Direction;
import Model.Field;
import Util.Position;
import View.FieldRenderParameters;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Bot
{
    private enum Action
    {
        MOVE_UP,
        MOVE_RIGHT,
        MOVE_DOWN,
        MOVE_LEFT,
        PICK_UP
    }
    public final int INTERVAL_BETWEEN_TURNS_MILLISECONDS = 450;
    private BotController controller;
    private BotView view;

    private boolean hasWeapon = false;

    public Bot(BotController c, BotView v)
    {
        controller = c;
        view = v;
    }

    static private final HashMap<Action, String> actionVector = new HashMap<>(Map.of(Action.MOVE_DOWN, "BOT MOVE D",
            Action.MOVE_LEFT, "BOT MOVE L",
            Action.MOVE_RIGHT, "BOT MOVE R",
            Action.MOVE_UP, "BOT MOVE U",
            Action.PICK_UP, "BOT PICK"));

    public Runnable taskForBotControl = ()->
    {
        while(!view.gameEnded())
        {
            var actions = pathToClosest(view.getFieldInfo(), controller.getName());
            for(var action : actions)
            {
                try {
                    controller.send(actionVector.get(action));
                }
                catch (IOException e)
                {
                }
                if(action == Action.PICK_UP)
                {
                    hasWeapon = true;
                }
                try {
                    Thread.sleep(INTERVAL_BETWEEN_TURNS_MILLISECONDS);
                }
                catch (InterruptedException e)
                {
                }
            }
        }
    };

    private Action[] pathToClosest(FieldRenderParameters info, String name)
    {
        Position beginningPoint = null;
        for(var record : info.getPlayerInfo())
        {
            if(name.equals(record.name))
            {
                beginningPoint = new Position(record.x, record.y);
            }
        }
        if(beginningPoint == null)
        {
            return null;
        }
        Position closestObject = null;
        int minimalDistance = 2 * Field.FIELD_SIZE;
        boolean isWeapon = false;
        if(!hasWeapon)
        {
            for (var record : info.getWeaponInfo()) {
                var position = new Position(record.x, record.y);
                if(minimalDistance > beginningPoint.calculateDistance(position))
                {
                    minimalDistance = beginningPoint.calculateDistance(position);
                    closestObject = position;
                    isWeapon = true;
                }
            }
        }
        for (var record : info.getPlayerInfo()) {
            if(record.name == name)
            {
                continue;
            }
            var position = new Position(record.x, record.y);
            if(minimalDistance > beginningPoint.calculateDistance(position))
            {
                minimalDistance = beginningPoint.calculateDistance(position);
                closestObject = position;
                isWeapon = false;
            }
        }
        Action[] res = new Action[minimalDistance + (isWeapon ? 1:0)];
        if(closestObject == null)
        {
            return null;
        }
        var verticalDifference = beginningPoint.getY() - closestObject.getY();
        var horizontalDifference = beginningPoint.getX() - closestObject.getX();
        int k = 0;
        for(; verticalDifference > 0; --verticalDifference)
        {
            res[k] = Action.MOVE_UP;
            k++;
        }
        for(; verticalDifference < 0; ++verticalDifference)
        {
            res[k] = Action.MOVE_DOWN;
            k++;
        }
        for(; horizontalDifference > 0; --horizontalDifference)
        {
            res[k] = Action.MOVE_LEFT;
            k++;
        }
        for(; horizontalDifference < 0; ++horizontalDifference)
        {
            res[k] = Action.MOVE_RIGHT;
            k++;
        }
        if(isWeapon)
        {
            res[k] = Action.PICK_UP;
        }
        return res;
    }
}
