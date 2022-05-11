/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package poiupv.controller;

import DBAccess.NavegacionDAOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import static javafx.scene.input.KeyCode.C;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import model.Navegacion;
import model.User;

/**
 * FXML Controller class
 *
 * @author gonca
 */
public class FXMLRegisterController implements Initializable {

    @FXML
    private TextField userReg;
    @FXML
    private TextField correo;
    @FXML
    private DatePicker fecha;
    @FXML
    private ImageView imagen;
    @FXML
    private Pane panelImagen;
    @FXML
    private PasswordField psswReg;
    @FXML
    private PasswordField pssw2Reg;
    @FXML
    private Text mensajeError;
    
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

    @FXML
    private void atras(ActionEvent event) throws IOException {
        ((Stage)userReg.getScene().getWindow()).close();
    }

    @FXML
    private void continuar(ActionEvent event) throws IOException, NavegacionDAOException {
        String p1 = psswReg.getText();
        String p2 = pssw2Reg.getText();
        String error = "";
        
        if(!database.exitsNickName(userReg.getText()) && usuario.checkPassword(psswReg.getText()) && p1.equals(p2) &&  usuario.checkNickName(userReg.getText()) && usuario.checkEmail(correo.getText())){
            mensajeError.setText("");
            // Registrar Usuario:
            database.registerUser(userReg.getText(), correo.getText(), psswReg.getText(), imagen.getImage(), fecha.getValue());
            //
            ((Stage)userReg.getScene().getWindow()).close();
        } else{
            error += "Error de registro en";
            if(!usuario.checkNickName(userReg.getText()) || database.exitsNickName(userReg.getText())){
                error += "  USUARIO  ";
                userReg.setText("");
            }
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
        new ExtensionFilter("Imágenes", "*.png", "*.jpeg", "*.jpg", "*.gif"));
        fileChooser.setInitialDirectory(new File (".\\src\\resources\\avatares"));
        File selectedFile = fileChooser.showOpenDialog(
        ((Node)event.getSource()).getScene().getWindow());
        if (selectedFile != null) {
            Image avatar = new Image(new FileInputStream(selectedFile));
            imagen.setImage(avatar);
        }
    }
}
