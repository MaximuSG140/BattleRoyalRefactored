package game;

import Exceptions.InvalidServerIPException;
import Model.Field;
import Model.Game;
import Network.Client;
import Network.Server;
import View.FieldRenderParameters;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class menuController
{
    static final int CELL_SIZE_PIXEL = 30;

    @FXML
    public TextField serverAddressField;
    @FXML
    public TextField playerNicknameField;

    private Server s;
    private Client c;

    private clientWindowController clientController;



    public void runServer()
    {
        try {
            s = new Server(10);
        } catch (IOException e) {
            e.printStackTrace();
        }
        s.start();
    }

    public void runClient()
    {
        try {
            clientController = new clientWindowController(serverAddressField.getText(), playerNicknameField.getText());
        } catch (InvalidServerIPException e) {
            Alert serverNotRespond = new Alert(Alert.AlertType.INFORMATION);
            serverNotRespond.setTitle("Error");
            serverNotRespond.setHeaderText("Server not responds");
            serverNotRespond.setContentText("Try again later");
            serverNotRespond.show();
            return;
        }
        FXMLLoader loader = new FXMLLoader();
        URL xmlUrl = getClass().getResource("/clientWindow.fxml");
        loader.setLocation(xmlUrl);
        loader.setController(clientController);
        Parent root = null;
        try
        {
            root = loader.load();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return;
        }
        var stage = (Stage)serverAddressField.getScene().getWindow();
        stage.setResizable(false);
        stage.setScene(new Scene(root));
        stage.sizeToScene();
        stage.show();
    }
}
