package game;

import Exceptions.InvalidServerIPException;
import Model.Field;
import Model.Game;
import Network.Client;
import View.FieldRenderParameters;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static game.menuController.CELL_SIZE_PIXEL;


public class clientWindowController
{
    static final int TIME_BETWEEN_FRAMES_MILLISECONDS = 100;
    @FXML
    public Canvas canvas;

    private Client c;

    private Alert serverNotRespond = new Alert(Alert.AlertType.ERROR);

    final private Timer fieldDrawTimer = new Timer();

    public void initialize()
    {
        canvas.setHeight(Field.FIELD_SIZE * CELL_SIZE_PIXEL);
        canvas.setWidth(Field.FIELD_SIZE * CELL_SIZE_PIXEL);
        startDrawing();
    }

    public clientWindowController(String address, String name) throws InvalidServerIPException
    {
        c = new Client(name);
        c.connect(address);
        serverNotRespond.setTitle("Error");
        serverNotRespond.setHeaderText("Server not responds");
        serverNotRespond.setContentText("Try to reconnect");
    }


    public void startKeyListening()
    {
        var scene = canvas.getScene();
        scene.setOnKeyPressed(keyEvent -> {
            switch(keyEvent.getCode())
            {
                case W:
                    moveUpButtonClick();
                    break;
                case A:
                    moveLeftButtonClick();
                    break;
                case S:
                    moveDownButtonClick();
                    break;
                case D:
                    moveRightButtonClick();
                    break;
                case E:
                    pickButtonClick();
                    break;
                case ESCAPE:
                    backToMenu();
                    break;
                case F:
                    spawnButtonClick();
                    break;
            }
        });
    }

    private void startDrawing()
    {
        fieldDrawTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run()
            {
                if(c.isServerIsUp() && c.isGameRunning())
                {
                    drawInfo(c.getCachedInfo());
                }
                else
                {
                    if(!c.isGameRunning())
                    {
                        drawScores(c.getScores());
                    }
                    fieldDrawTimer.cancel();
                }
            }
        },
                0,
                TIME_BETWEEN_FRAMES_MILLISECONDS);
    }

    private void drawInfo(FieldRenderParameters info)
    {
        if(info == null) {
            return;
        }
        canvas.setHeight(Field.FIELD_SIZE * CELL_SIZE_PIXEL);
        canvas.setWidth(Field.FIELD_SIZE * CELL_SIZE_PIXEL);
        var field = canvas.getGraphicsContext2D();
        field.setFill(Color.WHITE);
        field.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        for(var record : info.weaponInfo)
        {
            var slicedName = record.type.split("\\.");
            field.drawImage(ImageFactory.getImage("/".concat(slicedName[slicedName.length - 1]) + ".png"), record.x * CELL_SIZE_PIXEL, record.y * CELL_SIZE_PIXEL);
        }
        for(var record : info.playerInfo)
        {
            field.drawImage(ImageFactory.getImage("/Pawn.png"), record.x * CELL_SIZE_PIXEL, record.y * CELL_SIZE_PIXEL);
            field.setFill(Color.BLACK);
            field.fillText(record.name, record.x* CELL_SIZE_PIXEL, record.y* CELL_SIZE_PIXEL);
            field.setFill(Color.RED);
            field.fillRect(record.x * CELL_SIZE_PIXEL, (record.y + 1) * CELL_SIZE_PIXEL - 5, CELL_SIZE_PIXEL * record.hp / Game.BEGINNING_PAWN_HP, 5);
        }
    }

    private void drawScores( ArrayList<String> scores)
    {
        var field = canvas.getGraphicsContext2D();
        field.setFill(Color.WHITE);
        field.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        field.setFill(Color.BLACK);
        for(int i = 0; i < scores.size(); ++i)
        {
            field.fillText(scores.get(i), 0, i * CELL_SIZE_PIXEL + 50);
        }
    }

    public void backToMenu()
    {
        try
        {
            c.disconnect();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        var stage = (Stage)canvas.getScene().getWindow();
        App.switchToMenu(stage);
    }

    public void moveUpButtonClick()
    {
        try
        {
            c.send("MOVE U\n");
        }
        catch (IOException e)
        {
            serverNotRespond.show();
            App.switchToMenu((Stage)canvas.getScene().getWindow());
        }
    }

    public void moveDownButtonClick()
    {
        try
        {
            c.send("MOVE D\n");
        }
        catch (IOException e)
        {
            serverNotRespond.show();
            App.switchToMenu((Stage)canvas.getScene().getWindow());
        }
    }

    public void moveLeftButtonClick()
    {
        try
        {
            c.send("MOVE L\n");
        }
        catch (IOException e)
        {
            serverNotRespond.show();
            App.switchToMenu((Stage)canvas.getScene().getWindow());
        }
    }

    public void moveRightButtonClick()
    {
        try
        {
            c.send("MOVE R\n");
        }
        catch (IOException e)
        {
            serverNotRespond.show();
            App.switchToMenu((Stage)canvas.getScene().getWindow());
        }
    }

    public void pickButtonClick()
    {
        try
        {
            c.send("PICK\n");
        }
        catch (IOException e)
        {
            serverNotRespond.show();
            App.switchToMenu((Stage)canvas.getScene().getWindow());
        }
    }

    public void spawnButtonClick()
    {
        try
        {
            c.send("LOGIN\n");
        }
        catch (IOException e)
        {
            serverNotRespond.show();
            App.switchToMenu((Stage)canvas.getScene().getWindow());
        }
    }

    public void stopButtonClick()
    {
        try
        {
            c.send("STOP\n");
        }
        catch(IOException e)
        {
            serverNotRespond.show();
            App.switchToMenu((Stage)canvas.getScene().getWindow());
        }
    }
}
