package Network;

import Controller.CommandFactory.CommandFactory;
import Model.Game;
import Model.Player;
import Threadpool.ThreadPool;
import View.FieldRenderParameters;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server
{
    private ServerSocket socket;
    private final HashMap<Socket, String> connectedPlayers = new HashMap<>();
    private final HashMap<String, Player> playerByName = new HashMap<>();
    private ThreadPool pool;
    private final Game game = new Game();

    private Runnable getTaskForReadingCommands(Socket newConnection)
    {
        return ()->
        {
            try
            {
                var factory = new CommandFactory();
                var reader = new BufferedReader(new InputStreamReader(newConnection.getInputStream()));
                while(true)
                {
                    var line = reader.readLine();
                    var command = factory.parseCommand(line);
                    synchronized (connectedPlayers)
                    {
                        synchronized (playerByName)
                        {
                            game.addCommand(playerByName.get(connectedPlayers.get(newConnection)), command);
                        }
                    }
                }
            }
            catch (IOException e)
            {
                try
                {
                    newConnection.close();
                    synchronized (connectedPlayers)
                    {
                        connectedPlayers.remove(newConnection);
                    }
                }
                catch (IOException ex)
                {}
            }
        };
    }

    private Runnable getTaskForSendingInfo(Socket newConnection)
    {
        return ()->
        {
            try
            {
                var writer = new OutputStreamWriter(newConnection.getOutputStream());
                while (!game.ended())
                {
                    var info = game.getGameInfo();
                    sendFieldInfo(writer, info);
                    Thread.sleep(500);
                }
                writer.write("GAMEENDED\n");
                writer.flush();

                var lines = game.getScores().toStrings();
                for(int i = 0; i < lines.length; ++i)
                {
                    writer.write(lines[i].concat("\n"));
                    writer.flush();
                }
                writer.write("ENDOFSCORE\n");
                writer.flush();
            }
            catch(IOException | InterruptedException e)
            {
                try
                {
                    newConnection.close();
                }
                catch (IOException ex)
                {}
            }
        };
    }

    private Runnable listenForNewClients = () ->
    {
        while (true)
        {
            try
            {
                Socket newConnection = socket.accept();
                var reader = new BufferedReader(new InputStreamReader(newConnection.getInputStream()));
                synchronized (connectedPlayers)
                {
                    var name = reader.readLine();
                    connectedPlayers.put(newConnection, name);
                    synchronized (playerByName)
                    {
                        if(!playerByName.containsKey(name)) {
                            playerByName.put(name, new Player(name));
                        }
                    }
                }
                pool.execute(getTaskForReadingCommands(newConnection));
                pool.execute(getTaskForSendingInfo(newConnection));
            }
            catch (IOException e)
            {
                game.finish();
                return;
            }
        }
    };

    public Server(int maxConnections) throws IOException {
        pool = new ThreadPool(2 * maxConnections + 1);
        socket = new ServerSocket(8080, maxConnections);
    }

    public void start()
    {
        pool.execute(listenForNewClients);
        while (!game.ended())
        {
            game.makeTick();
            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException e)
            {}
        }
    }

    private void sendFieldInfo(OutputStreamWriter writer, FieldRenderParameters info) throws IOException
    {
        for(var record : info.playerInfo)
        {
            writer.write(record.toString());
        }
        for(var record : info.weaponInfo)
        {
            writer.write(record.toString());
        }
        writer.write("ENDOFFRAME\n");
        writer.flush();
    }

    public String getAddress()
    {
        return socket.getInetAddress().toString();
    }
}
