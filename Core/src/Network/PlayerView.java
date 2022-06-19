package Network;

import Exceptions.BadServerMessageFormatException;
import View.EndGameData;
import View.FieldRenderParameters;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class PlayerView implements IView
{
    private FieldRenderParameters cachedInfo = new FieldRenderParameters();
    private FieldRenderParameters info = new FieldRenderParameters();
    private ArrayList<String> scores;
    private boolean gameRunning = true;
    private Socket server;
    private BufferedReader reader;

    @Override
    public boolean gameRunning()
    {
        return gameRunning;
    }

    public PlayerView(Socket serverSocket) throws IOException
    {
        server = serverSocket;
        reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(server.getInputStream())));
    }

    public void getNewInfo() throws IOException, BadServerMessageFormatException
    {
        var line = reader.readLine();
        if(line == null)
        {
            return;
        }
        String[] args = line.split(" ");
        if(args.length == 1)
        {
            switch(args[0])
            {
                case Server.SERVER_MESSAGE_ON_FRAME_ENDED:
                    synchronized (cachedInfo)
                    {
                        cachedInfo = info;
                        info = new FieldRenderParameters();
                    }
                    gameRunning = true;
                    break;
                case Server.SERVER_MESSAGE_ON_GAME_ENDED:
                    scores = readScores(reader);
                    gameRunning = false;
                    break;
                default:
                    throw new BadServerMessageFormatException();
            }
        }
        else
        {
            info.update(args);
        }
    }

    public ArrayList<String> getScores()
    {
        return scores;
    }

    @Override
    public FieldRenderParameters getFieldInfo()
    {
        return cachedInfo;
    }

    private ArrayList<String> readScores(BufferedReader reader) throws IOException
    {
        ArrayList<String> lines = new ArrayList<>();
        String line = reader.readLine();
        while(!line.equals(Server.SERVER_MESSAGE_ON_SCORES_ENDED))
        {
            lines.add(line);
            line = reader.readLine();
        }
        return lines;
    }
}
