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
    private String player;
    private boolean serverIsUp = false;
    private PlayerController controller;
    private PlayerView view;


    private Runnable serverOutputReadingTask = ()->
    {
        try
        {
            while (view.gameRunning())
            {
                view.getNewInfo();
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
        controller = new PlayerController(currentServer, player);
        try
        {
            view = new PlayerView(currentServer);
        }
        catch (IOException e)
        {}
        serverIsUp = true;
        pool.executeTask(serverOutputReadingTask, " reading map info");
    }

    public void send(String line) throws IOException
    {
        controller.send(line);
    }

    public FieldRenderParameters getCachedInfo()
    {
        return view.getFieldInfo();
    }

    public boolean isGameRunning()
    {
        return view.gameRunning();
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


    public ArrayList<String> getScores()
    {
        return view.getScores();
    }
}