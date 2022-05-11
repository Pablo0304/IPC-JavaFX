/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package poiupv.controller;

import com.sun.javafx.logging.PlatformLogger.Level;
import java.io.IOException;
import java.lang.System.Logger;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import DBAccess.NavegacionDAO;
import DBAccess.NavegacionDAOException;
import model.Navegacion;
import model.Answer;
import model.User;
import model.Session;

/**
 * FXML Controller class
 *
 * @author gonca
 */
public class FXMLLogController implements Initializable {

    @FXML
    private TextField user;
    @FXML
    private TextField pssw;
    @FXML
    private VBox top;
    @FXML
    private Label errorLogin;
    
    Navegacion database;
    User usuario;
            
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            database = Navegacion.getSingletonNavegacion();
        } catch (Exception e) {
        }
    }    

    @FXML
    private void Aceptar(ActionEvent event) throws IOException {
        if(database.loginUser(user.getText(), pssw.getText()) == null){
            errorLogin.setText("Error de logueo, credenciales no válidas.");
            user.setText("");
            pssw.setText("");
        } else{
            // Iniciar sesión como usuario:
            
            //
            FXMLLoader mapa = new FXMLLoader(getClass().getResource("/poiupv/run/FXMLDocument.fxml"));
            Parent root = mapa.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            ((Stage)pssw.getScene().getWindow()).close();
            stage.show();
        }
    }

    @FXML
    private void Registrarse(ActionEvent event) throws Exception  {
        FXMLLoader registrarse = new FXMLLoader(getClass().getResource("/poiupv/run/FXMLRegister.fxml"));
        Parent root = registrarse.load();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.show();
    }

}