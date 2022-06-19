package Network;

import Model.Game;
import View.FieldRenderParameters;

public class BotView implements IView
{
    private Game game;

    public BotView(Game g)
    {
        game = g;
    }
    @Override
    public FieldRenderParameters getFieldInfo() {
        return game.getGameInfo();
    }

    @Override
    public boolean gameRunning()
    {
        return !game.ended();
    }

    public boolean gameEnded()
    {
        return game.ended();
    }
}
