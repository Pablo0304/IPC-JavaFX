/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package navmap.controller;

import java.io.IOException;
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
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Navegacion;
import model.User;

/**
 * FXML Controller class
 * @author Pablo Gonzálbez Cabo
 * @author Jose Marco
 */
public class FXMLLogController implements Initializable {

    @FXML
    private TextField user;
    @FXML
    private TextField pssw;
    @FXML
    private Label errorLogin;
    
    public Navegacion database;
    public User usuario;
            
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
    private void Aceptar(ActionEvent event) throws IOException, InterruptedException {
        if(database.loginUser(user.getText(), pssw.getText()) == null){
            errorLogin.setText("Error de logueo, credenciales no válidas.");
            user.setText("");
            pssw.setText("");
        } else{
            // Iniciar sesión como usuario:
            usuario = database.loginUser(user.getText(), pssw.getText());
            //
            FXMLLoader mapa = new FXMLLoader(getClass().getResource("/navmap/run/FXMLDocument.fxml"));
            Parent root = mapa.load();
            // Paso de parámetros:
            FXMLDocumentController docController = mapa.getController();
            docController.userInit(usuario);
            //
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("NavMap");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.show();
            docController.user.setText(usuario.getNickName());
            ((Stage)pssw.getScene().getWindow()).close();
            
        }
    }

    @FXML
    private void Registrarse(ActionEvent event) throws Exception  {
        FXMLLoader registrarse = new FXMLLoader(getClass().getResource("/navmap/run/FXMLRegister.fxml"));
        Parent root = registrarse.load();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("Registro");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.show();
    }

}