/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package navmap.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import model.Navegacion;
import model.User;

/**
 * FXML Controller class
 * @author Pablo Gonzálbez Cabo
 * @author Jose Marco
 */
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
        // TODO
    }
    
    public void userInit(User usuari){
        usuario = usuari;
    }
//    usuario.addSession(new Session(LocalDateTime.now(), aciertos, fallos));
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