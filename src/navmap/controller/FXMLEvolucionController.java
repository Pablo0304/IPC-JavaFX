/**
 * @author Pablo Gonzálbez Cabo
 */

package navmap.controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import model.Navegacion;
import model.User;

public class FXMLEvolucionController implements Initializable {

    User usuario;
    Navegacion database;
    int acierto = 0;
    int fallo = 0;
    @FXML
    private VBox principal;
    @FXML
    private TextField aciertoText;
    @FXML
    private TextField falloText;
    @FXML
    private DatePicker fecha;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        fecha.setDayCellFactory((DatePicker picker) -> {
            return new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
            super.updateItem(date, empty);
            setDisable(empty || date.compareTo(LocalDate.now()) > -1);
            }
            };
        });
    }
    
    public void userInit(User usuari){
        usuario = usuari;
    }
    
    @FXML
    private void fechas(ActionEvent event) {
        int i = 0;
        acierto = 0;
        fallo = 0;
        while(i < usuario.getSessions().size()){
            if(usuario.getSessions().get(i).getLocalDate().isAfter(fecha.getValue())){
                acierto += usuario.getSessions().get(i).getHits();
                fallo += usuario.getSessions().get(i).getFaults();
            }
            i++;
        }
        aciertoText.setText(String.valueOf(acierto));
        falloText.setText(String.valueOf(fallo));
    }
    
}