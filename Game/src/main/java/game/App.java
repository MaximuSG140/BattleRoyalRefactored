package game;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.fxml.*;

import java.io.IOException;
import java.net.URL;

import static com.sun.javafx.scene.control.skin.Utils.getResource;


/**
 * JavaFX App
 */
public class App extends Application
{
    public static void switchToMenu(Stage stage)
    {
        FXMLLoader loader = new FXMLLoader();
        URL xmlUrl = App.class.getResource("/mainMenu.fxml");
        loader.setLocation(xmlUrl);
        Parent root = null;
        try
        {
            root = loader.load();
        }
        catch (IOException e)
        {
            Alert badIpAlert = new Alert(Alert.AlertType.INFORMATION);
            badIpAlert.setTitle("Internal error");
            badIpAlert.setHeaderText("Invalid resource");
            badIpAlert.setContentText("Please reinstall game");
            badIpAlert.show();
            stage.close();
            return;
        }
        stage.setResizable(false);
        stage.setScene(new Scene(root));
        stage.sizeToScene();
        stage.show();
    }
    @Override
    public void start(Stage stage)
    {
        switchToMenu(stage);
    }

    public static void main(String[] args) {
        launch();
    }

}