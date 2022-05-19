/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package navmap.controller;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import model.Navegacion;
import model.Session;
import model.User;

/**
 * FXML Controller class
 *
 * @author gonca
 */
public class FXMLEvolucionController implements Initializable {

    User usuario;
    Navegacion database;
    int acierto = 0;
    int fallo = 0;
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
    public void funciontoguapa(){
        for(int i = 0; i < usuario.getSessions().size(); i++){
            acierto += usuario.getSessions().get(i).getHits();
            fallo += usuario.getSessions().get(i).getFaults();
        }
    }
    
}
