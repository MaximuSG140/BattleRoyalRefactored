package game;

import Exceptions.InvalidServerIPException;
import Model.Field;
import Model.Game;
import Network.Client;
import View.FieldRenderParameters;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

import static game.menuController.CELL_SIZE_PIXEL;
import static javafx.scene.input.KeyEvent.KEY_PRESSED;

public class clientWindowController
{
    @FXML
    public Canvas canvas;

    private Client c;

    private Thread drawer;

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
    }

    private void startDrawing()
    {
        drawer = new Thread(()->
        {
            try {
                while (c.isGameRunning()) {
                    drawInfo(c.getCachedInfo());
                    Thread.sleep(500);
                }
                if(c.getScores() != null)
                {
                    drawScores(c.getScores());
                }
            }
            catch (InterruptedException e)
            {}
        });
        drawer.start();
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
            StringBuilder imageName = new StringBuilder("/".concat(slicedName[slicedName.length - 1]));
            imageName.append(".png");
            field.drawImage(ImageFactory.getImage(imageName.toString()), record.x * CELL_SIZE_PIXEL, record.y * CELL_SIZE_PIXEL);
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }

    @FXML
    public void spawnButtonClick()
    {
        try
        {
            c.send("LOGIN\n");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
