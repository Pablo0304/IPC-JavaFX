/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package navmap.controller;

import DBAccess.NavegacionDAOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Navegacion;
import model.User;

/**
 * FXML Controller class
 *
 * @author gonca
 */
public class FXMLModifPerfilController implements Initializable {

    @FXML
    private Pane panelImagen1;
    @FXML
    private PasswordField psswReg;
    @FXML
    private PasswordField pssw2Reg;
    @FXML
    private Text mensajeError;
    @FXML
    private TextField correo;
    @FXML
    private DatePicker fecha;
    @FXML
    private ImageView imagen;
    Navegacion database;
    User usuario;
    @FXML
    protected Label user;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            database = Navegacion.getSingletonNavegacion();
        } catch (Exception e) {
        }
        
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -12);
        cal.add(Calendar.DAY_OF_YEAR, -1);
        Date str = cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy"); 
        String doce = dateFormat.format(str);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate fechaReg = LocalDate.parse(doce, fmt);
        fecha.setDayCellFactory((DatePicker picker) -> {
            return new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
            super.updateItem(date, empty);
            setDisable(empty || date.compareTo(fechaReg) > 0);
            }
            };
        });
        
        
    }    

    public void userInit(User usuari){
        usuario = usuari;
    }
    
    public void dataInit(Navegacion data){
        database = data;
    }
    
    @FXML
    private void atras(ActionEvent event) {
        ((Stage)correo.getScene().getWindow()).close();
    }

    @FXML
    private void continuar(ActionEvent event) throws FileNotFoundException, NavegacionDAOException {
        String p1 = psswReg.getText();
        String p2 = pssw2Reg.getText();
        String error = "";
        
        if(usuario.checkPassword(psswReg.getText()) && p1.equals(p2) && usuario.checkEmail(correo.getText())){
            // Actualizar Usuario:
            usuario.setPassword(psswReg.getText());
            usuario.setEmail(correo.getText());
            usuario.setBirthdate(fecha.getValue());
            usuario.setAvatar(imagen.getImage());
            //
            
            ((Stage)pssw2Reg.getScene().getWindow()).close();
        } else{
            error += "Error de registro en";
            if(!usuario.checkPassword(pssw2Reg.getText()) || !(p1).equals(p2)){
                psswReg.setText("");
                pssw2Reg.setText("");
                error += "  CONTRASEÑA  ";
            }
            if(!usuario.checkEmail(correo.getText())){
                error += "  CORREO  ";
                correo.setText("");
            }
            mensajeError.setText(error);
        }
    }

    @FXML
    private void cambiarImagen(MouseEvent event) throws FileNotFoundException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir fichero");
        fileChooser.getExtensionFilters().addAll(
        new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpeg", "*.jpg", "*.gif"));
        fileChooser.setInitialDirectory(new File (".\\src\\resources\\avatares"));
        File selectedFile = fileChooser.showOpenDialog(
        ((Node)event.getSource()).getScene().getWindow());
        if (selectedFile != null) {
            Image avatar = new Image(new FileInputStream(selectedFile));
            imagen.setImage(avatar);
        }
    }
    
}
