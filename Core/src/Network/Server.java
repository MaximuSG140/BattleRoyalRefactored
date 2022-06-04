package Network;

import Controller.CommandFactory.CommandFactory;
import Model.Game;
import Model.Player;
import Threadpool.DynamicThreadPool;
import Threadpool.IAsynchronousTaskExecutor;
import View.FieldRenderParameters;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class Server
{
    static final int GAME_TIME_QUANTUM_SIZE_MILLISECONDS = 100;
    static final int SEND_TIME_QUANTUM_SIZE_MILLISECONDS = 500;
    public static final String SERVER_MESSAGE_ON_GAME_ENDED = "GAMEENDED";
    public static final String SERVER_MESSAGE_ON_FRAME_ENDED = "ENDOFFRAME";
    public static final String SERVER_MESSAGE_ON_SCORES_ENDED = "ENDOFSCORE";
    
    private ServerSocket socket;
    private final HashMap<Socket, String> connectedPlayers = new HashMap<>();
    private final HashMap<String, Player> playerByName = new HashMap<>();
    private DynamicThreadPool pool;
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
                    if(line == null)
                    {
                        try
                        {
                            synchronized (newConnection)
                            {
                                if (!newConnection.isClosed())
                                {
                                    newConnection.close();
                                    synchronized (connectedPlayers)
                                    {
                                        connectedPlayers.remove(newConnection);
                                    }
                                }
                            }
                        }
                        catch (IOException ex)
                        {}
                        return;
                    }
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
                    synchronized (newConnection)
                    {
                        if (!newConnection.isClosed())
                        {
                            newConnection.close();
                            synchronized (connectedPlayers)
                            {
                                connectedPlayers.remove(newConnection);
                            }
                        }
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
                    try
                    {
                        Thread.sleep(SEND_TIME_QUANTUM_SIZE_MILLISECONDS);
                    }
                    catch(InterruptedException e)
                    {}
                }
                writer.write(SERVER_MESSAGE_ON_GAME_ENDED.concat("\n"));
                writer.flush();

                var lines = game.getScores().toStrings();
                for(int i = 0; i < lines.length; ++i)
                {
                    writer.write(lines[i].concat("\n"));
                    writer.flush();
                }
                writer.write(SERVER_MESSAGE_ON_SCORES_ENDED.concat("\n"));
                writer.flush();
            }
            catch(IOException e)
            {
                try {
                    synchronized (newConnection) {
                        if (!newConnection.isClosed()) {
                            newConnection.close();
                            synchronized (connectedPlayers)
                            {
                                connectedPlayers.remove(newConnection);
                            }
                        }
                    }
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
                synchronized (connectedPlayers) {
                    var name = reader.readLine();
                    connectedPlayers.put(newConnection, name);
                    synchronized (playerByName) {
                        if (!playerByName.containsKey(name)) {
                            playerByName.put(name, new Player(name));
                        }
                    }
                    pool.executeTask(getTaskForReadingCommands(newConnection), connectedPlayers.get(newConnection) + " receive commands");
                    pool.executeTask(getTaskForSendingInfo(newConnection), connectedPlayers.get(newConnection) + " send frames");
                }
            }
            catch (IOException e)
            {
                game.finish();
                if(!socket.isClosed())
                {
                    try
                    {
                        socket.close();
                    }
                    catch (IOException ex)
                    {}

                }
                return;
            }
        }
    };

    public Server(int maxConnections) throws IOException
    {
        pool = new DynamicThreadPool(2 * maxConnections + 1);
        socket = new ServerSocket(8080, maxConnections);
    }

    public void start()
    {
        pool.executeTask(listenForNewClients, "ListenForClients");
        Timer gameIterationExecutor = new Timer();
        gameIterationExecutor.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run()
            {
                    if(!game.ended())
                    {
                        game.makeTick();
                    }
                    else
                    {
                        try
                        {
                            socket.close();
                            gameIterationExecutor.cancel();
                            synchronized (gameIterationExecutor)
                            {
                                gameIterationExecutor.notify();
                            }
                        }
                        catch (IOException e)
                        {
                        }
                    }

            }
        }, 0, GAME_TIME_QUANTUM_SIZE_MILLISECONDS);
        while(!game.ended()) {
            try {
                synchronized (gameIterationExecutor) {
                    gameIterationExecutor.wait();
                }
            }
            catch (InterruptedException e) {
            }
        }
    }

    private void sendFieldInfo(OutputStreamWriter writer, FieldRenderParameters info) throws IOException
    {
        if(info == null)
        {
            return;
        }
        for(var record : info.playerInfo)
        {
            writer.write(record.toString());
        }
        for(var record : info.weaponInfo)
        {
            writer.write(record.toString());
        }
        writer.write(SERVER_MESSAGE_ON_FRAME_ENDED.concat("\n"));
        writer.flush();
    }

}
