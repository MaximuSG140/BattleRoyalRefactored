package Network;

import Exceptions.BadServerMessageFormatException;
import Exceptions.GameEndedException;
import Exceptions.InvalidServerIPException;
import Threadpool.ThreadPool;
import View.EndGameData;
import View.FieldRenderParameters;
import View.PlayerRenderParameters;
import View.WeaponRenderParameters;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Logger;

public class Client {
    private Socket currentServer = new Socket();
    private ThreadPool pool;
    private FieldRenderParameters cachedInfo = new FieldRenderParameters();
    private String player;
    private ArrayList<String> scores;
    private boolean gameRunning = true;

    private Runnable serverOutputReadingTask = ()->
    {
        try {
            var reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(currentServer.getInputStream())));
            FieldRenderParameters info = new FieldRenderParameters();
            while (gameRunning)
            {
                var line = reader.readLine();
                String[] args = line.split(" ");
                if(args.length == 1)
                {
                    switch(args[0])
                    {
                        case "ENDOFFRAME":
                            synchronized (cachedInfo)
                            {
                                cachedInfo = info;
                                info = new FieldRenderParameters();
                            }
                            gameRunning = true;
                            break;
                        case "GAMEENDED":
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
            gameRunning = false;
        }
    };



    public Client(String name) {
        pool = new ThreadPool(3);
        player = name;
    }

    public void connect(String address) throws InvalidServerIPException
    {
        try
        {
            currentServer.connect(new InetSocketAddress(address, 8080), 5000);
            currentServer.getOutputStream().write(player.getBytes());
            currentServer.getOutputStream().write(System.lineSeparator().getBytes());
        }
        catch (IOException e)
        {
            throw new InvalidServerIPException();
        }
        pool.execute(serverOutputReadingTask);
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
        while(!line.equals("ENDOFSCORE"))
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