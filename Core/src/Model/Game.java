package Model;

import Exceptions.GameEndedException;
import Exceptions.PlayerDuplicateException;
import Util.Position;
import View.EndGameData;
import View.FieldRenderParameters;

import java.util.*;

public class Game
{
    public static final int BEGINNING_PAWN_HP = 100;
    public static final int WEAPON_GENERATION_ANTI_COEFFICIENT = 40;

    private final HashMap<Player, CommandInstance> playerCommand = new HashMap<>();
    private final Field gameField = new Field();
    private final Random rng = new Random();
    private boolean finished = false;

    public void addPlayer(Player newPlayer)
    {
        synchronized (playerCommand)
        {
            playerCommand.put(newPlayer, null);
        }
    }

    public boolean hasPlayer(Player p)
    {
        synchronized (playerCommand)
        {
            return playerCommand.containsKey(p);
        }
    }

    public void addPawn(Player owner)
    {
        owner.attachPawn(new Pawn(BEGINNING_PAWN_HP));
        int x = rng.nextInt(Field.FIELD_SIZE);
        int y = rng.nextInt(Field.FIELD_SIZE);
        while(gameField.checkoutCell(new Position(x, y)) != null)
        {
            x = rng.nextInt(Field.FIELD_SIZE);
            y = rng.nextInt(Field.FIELD_SIZE);
        }
        gameField.placePawn(owner.getControlledPawn(), new Position(x, y));
    }

    public void removePawn(Player p)
    {
        gameField.removePawn(p.getControlledPawn());
        p.removePawn();
    }

    public void logOffPlayer(Player p)
    {
        gameField.removePawn(p.getControlledPawn());
        synchronized (playerCommand)
        {
            playerCommand.remove(p);
        }
    }

    public void finish()
    {
        finished = true;
    }

    public void addCommand(Player sender, CommandInstance instance)
    {
        synchronized (playerCommand)
        {
            playerCommand.put(sender, instance);
        }
    }

    public void makeTick() {
        if (ended()) {
            return;
        }

        if (rng.nextInt(WEAPON_GENERATION_ANTI_COEFFICIENT) == 0) {
            gameField.generateWeapon(rng);
        }
        synchronized (playerCommand) {
            playerCommand.forEach((Player p, CommandInstance c) ->
            {
                if (p.hasPawn() && !p.getControlledPawn().isAlive()) {
                    removePawn(p);
                    addPawn(p);
                    return;
                }
                if (c != null) {
                    c.apply(p, this, gameField);
                    playerCommand.put(p, null);
                }
            });
        }
    }

    public FieldRenderParameters getGameInfo()
    {
        return gameField.getRenderInfo();
    }

    public boolean ended()
    {
        return finished;
    }

    public EndGameData getScores()
    {
        var data = new EndGameData();
        Set<Player> players;
        synchronized (playerCommand)
        {
            players = new HashSet<>(playerCommand.keySet());
        }
        for(var player : players)
        {
            data.register(player.getName(), player.getScore());
        }
        return data;
    }
}
