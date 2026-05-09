package clases;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

import VISTAS.CursorManager;
import VISTAS.MusicManager;

public class MAIN_PRUEBAS extends Application {

	@Override
	public void start(Stage stage) throws Exception {

	    Parent root = FXMLLoader.load(getClass().getResource("/VISTAS/PantallaLogin.fxml"));

	    Scene scene = new Scene(root);
	    stage.setScene(scene);
	    stage.setTitle("El Joc del Pingu — Login");
	    stage.setMaximized(true);
	    stage.show();

	    // Aplicar cursors personalitzats (cursor1 = fletxa, cursor2 = mà als elements interactius)
	    CursorManager.apply(scene);

	    // Start background music (loops indefinitely across all screens)
	    try {
	        MusicManager.getInstance().play();
	    } catch (NoClassDefFoundError | Exception e) {
	        System.out.println("⚠ No s'ha pogut iniciar la música: " + e.getMessage());
	        System.out.println("  Afegeix javafx-media-XX.jar a la User Library 'JavaFX' d'Eclipse.");
	    }
	}

    public static void main(String[] args) {
        launch(args);
    }
}