package VISTAS;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.stage.Stage;

public class PantallaAjustes {

    @FXML private Slider  volumeSlider;
    @FXML private Label   volumeLabel;
    @FXML private CheckBox muteCheck;

    private double lastVolume;

    @FXML
    private void initialize() {
        MusicManager mm = MusicManager.getInstance();

        lastVolume = mm.getVolume();
        volumeSlider.setValue(lastVolume * 100);
        updateLabel((int)(lastVolume * 100));

        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!muteCheck.isSelected()) {
                double vol = newVal.doubleValue() / 100.0;
                mm.setVolume(vol);
                updateLabel(newVal.intValue());
            }
        });

        muteCheck.selectedProperty().addListener((obs, wasOn, isOn) -> {
            if (isOn) {
                lastVolume = volumeSlider.getValue() / 100.0;
                mm.setVolume(0.0);
                volumeSlider.setDisable(true);
                volumeLabel.setText("0%");
            } else {
                volumeSlider.setDisable(false);
                mm.setVolume(lastVolume);
                updateLabel((int)(lastVolume * 100));
            }
        });
    }

    private void updateLabel(int pct) {
        volumeLabel.setText(pct + "%");
    }

    @FXML
    private void handleCerrar() {
        Stage stage = (Stage) volumeSlider.getScene().getWindow();
        stage.close();
    }
}
