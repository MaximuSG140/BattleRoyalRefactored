package Network;

import Exceptions.BadServerMessageFormatException;
import Exceptions.InvalidServerIPException;
import Threadpool.DynamicThreadPool;
import Threadpool.IAsynchronousTaskExecutor;
import View.FieldRenderParameters;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

public class Client
{
    static final int SERVER_CONNECTION_TIMEOUT = 5000;
    static final int SERVER_CONNECTION_PORT = 8080;

    private Socket currentServer = new Socket();
    private DynamicThreadPool pool;
    private FieldRenderParameters cachedInfo = new FieldRenderParameters();
    private String player;
    private ArrayList<String> scores;
    private boolean gameRunning = true;
    private boolean serverIsUp = false;

    private Runnable serverOutputReadingTask = ()->
    {
        try {
            var reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(currentServer.getInputStream())));
            FieldRenderParameters info = new FieldRenderParameters();
            while (gameRunning)
            {
                var line = reader.readLine();
                if(line == null)
                {
                    continue;
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
        }
        catch (IOException | BadServerMessageFormatException e)
        {
            serverIsUp = false;
        }
    };



    public Client(String name)
    {
        pool = new DynamicThreadPool(3);
        player = name;
    }

    public void connect(String address) throws InvalidServerIPException
    {
        try
        {
            currentServer.connect(new InetSocketAddress(address, SERVER_CONNECTION_PORT), SERVER_CONNECTION_TIMEOUT);
            currentServer.getOutputStream().write(player.getBytes());
            currentServer.getOutputStream().write(System.lineSeparator().getBytes());
            currentServer.getOutputStream().flush();
        }
        catch (IOException e)
        {
            throw new InvalidServerIPException();
        }
        serverIsUp = true;
        pool.executeTask(serverOutputReadingTask, " reading map info");
    }

    public void send(String line) throws IOException
    {
        currentServer.getOutputStream().write(player.getBytes());
        currentServer.getOutputStream().write(' ');
        currentServer.getOutputStream().write(line.getBytes());
        currentServer.getOutputStream().flush();
    }

    public FieldRenderParameters getCachedInfo()
    {
        return cachedInfo;
    }

    public boolean isGameRunning()
    {
        return gameRunning;
    }

    public boolean isServerIsUp()
    {
        return serverIsUp;
    }

    public void disconnect()
    {
        try
        {
            currentServer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
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

    public ArrayList<String> getScores()
    {
        return scores;
    }
}