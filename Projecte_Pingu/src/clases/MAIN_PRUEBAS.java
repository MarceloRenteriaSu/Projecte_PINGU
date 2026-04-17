package clases;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class MAIN_PRUEBAS extends Application {

	@Override
	public void start(Stage stage) throws Exception {

	    Parent root = FXMLLoader.load(getClass().getResource("/VISTAS/PantallaLogin.fxml"));

	    Scene scene = new Scene(root);
	    stage.setScene(scene);
	    stage.setTitle("El Joc del Pingu — Login");
	    stage.setMaximized(true);
	    stage.show();
	}

    public static void main(String[] args) {
        launch(args);
    }
}