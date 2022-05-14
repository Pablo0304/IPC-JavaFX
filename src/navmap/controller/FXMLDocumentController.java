/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navmap.controller;

import java.awt.BasicStroke;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import navmap.model.Poi;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Navegacion;
import model.Problem;
import model.Session;
import model.User;

/**
 *
 * @author jsoler
 */
public class FXMLDocumentController implements Initializable {

    //=======================================
    // hashmap para guardar los puntos de interes POI
    private final HashMap<String, Poi> hm = new HashMap<>();
    // ======================================
    // la variable zoomGroup se utiliza para dar soporte al zoom
    // el escalado se realiza sobre este nodo, al escalar el Group no mueve sus nodos
    private Group zoomGroup;
    private boolean setTransportador = false;
    private boolean setRLapiz = false;
    private boolean setCLapiz = false;
    private boolean setGoma = false;
    private boolean setCompas = false;
    Color color = Color.BLACK;
    int grosor = 3;
    double circuloX;
    int problemaInt = -1;
    private double inicioXTrans;
    private double inicioYTrans;
    private double baseX;
    private double baseY;
    Line linePainting;
    Circle circlePainting;
    Circle circlePaintingPoint;
    Circle circlePaintingPoint2;
    Session sesion;
    Navegacion database;
    List<Problem> problemas;
    Problem problema;
    
    @FXML
    private ScrollPane map_scrollpane;
    @FXML
    private Slider zoom_slider;
    @FXML
    private Label posicion;
    @FXML
    private Pane transportador;
    private Pane lapiz;
    @FXML
    private Pane goma;
    @FXML
    private ImageView herramientaActual;
    @FXML
    private CheckMenuItem gomaCheck;
    @FXML
    private Pane mapaPanel;
    @FXML
    private CheckMenuItem lapizRCheck;
    @FXML
    private CheckMenuItem lapizCCheck;
    @FXML
    private Pane lapizR;
    @FXML
    private Pane lapizC;
    @FXML
    private Pane compas;
    @FXML
    private CheckMenuItem compasCheck;
    @FXML
    private TextArea textoProblema;
    @FXML
    protected Label user;
    @FXML
    protected Label password;
    
    
    @FXML
    void zoomIn(ActionEvent event) {
        //================================================
        // el incremento del zoom dependerá de los parametros del 
        // slider y del resultado esperado
        double sliderVal = zoom_slider.getValue();
        zoom_slider.setValue(sliderVal += 0.1);
    }

    @FXML
    void zoomOut(ActionEvent event) {
        double sliderVal = zoom_slider.getValue();
        zoom_slider.setValue(sliderVal + -0.1);
    }
    
    // esta funcion es invocada al cambiar el value del slider zoom_slider
    private void zoom(double scaleValue) {
        //===================================================
        //guardamos los valores del scroll antes del escalado
        double scrollH = map_scrollpane.getHvalue();
        double scrollV = map_scrollpane.getVvalue();
        //===================================================
        // escalamos el zoomGroup en X e Y con el valor de entrada
        zoomGroup.setScaleX(scaleValue);
        zoomGroup.setScaleY(scaleValue);
        //===================================================
        // recuperamos el valor del scroll antes del escalado
        map_scrollpane.setHvalue(scrollH);
        map_scrollpane.setVvalue(scrollV);
    }

    private void initData() throws IOException {
//        FXMLLoader log = new FXMLLoader(getClass().getResource("./FXMLLogController.fxml"));
//        FXMLLogController controladorLog = log.getController();
//        usuario = controladorLog.userInit();

    }

    @Override
    public void initialize(URL url, ResourceBundle rb){
        try {
            // TODO
            initData();
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        //==========================================================
        // inicializamos el slider y enlazamos con el zoom
        zoom_slider.setMin(0.25);
        zoom_slider.setMax(1.5);
        zoom_slider.setValue(1.0);
        zoom_slider.valueProperty().addListener((o, oldVal, newVal) -> zoom((Double) newVal));
        
        //=========================================================================
        //Envuelva el contenido de scrollpane en un grupo para que 
        //ScrollPane vuelva a calcular las barras de desplazamiento tras el escalado
        Group contentGroup = new Group();
        zoomGroup = new Group();
        contentGroup.getChildren().add(zoomGroup);
        zoomGroup.getChildren().add(map_scrollpane.getContent());
        map_scrollpane.setContent(contentGroup);
        
        try {
            database = Navegacion.getSingletonNavegacion();
        } catch (Exception e) {
        }
    }

    @FXML
    private void muestraPosicion(MouseEvent event) {
        posicion.setText("sceneX: " + (int) event.getSceneX() + ", sceneY: " + (int) event.getSceneY() + "\n"
                + "         X: " + (int) event.getX() + ",          Y: " + (int) event.getY());
    }

    @FXML
    private void cerrarAplicacion(ActionEvent event) {
       // usuario.addSession(sesion);
        ((Stage)zoom_slider.getScene().getWindow()).close();
    }

    @FXML
    private void acercaDe(ActionEvent event) {
        Alert mensaje= new Alert(Alert.AlertType.INFORMATION);
        mensaje.setTitle("Acerca de");
        mensaje.setHeaderText("IPC - 2022");
        mensaje.showAndWait();
    }

    @FXML
    private void cerrarSesión(ActionEvent event) throws IOException {
       // usuario.addSession(sesion);
        FXMLLoader registrarse = new FXMLLoader(getClass().getResource("/navmap/run/FXMLLog.fxml"));
        Parent root = registrarse.load();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.show();
        ((Stage)zoom_slider.getScene().getWindow()).close();
    }

    @FXML
    private void modifPerfil(ActionEvent event) throws IOException {
        FXMLLoader registrarse = new FXMLLoader(getClass().getResource("/navmap/run/FXMLModifPerfil.fxml"));
        Parent root = registrarse.load();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void transportadorVisible(ActionEvent event) {
        if(setTransportador == false){
            transportador.visibleProperty().set(true);
            setTransportador = true;
        } else{
            transportador.visibleProperty().set(false);
            setTransportador = false;
        }
    }

    @FXML
    private void rotaTransportador(MouseEvent event) {
        double inicial = event.getX();
        double inicialRota = transportador.getRotate();
        transportador.setRotate(inicialRota + ((event.getX()+ event.getY()) / 2));
        event.consume();
    }

    @FXML
    private void transportadorDragged(MouseEvent event) {
        double despX = event.getSceneX()-inicioXTrans;
        double despY = event.getSceneY()-inicioYTrans;
        transportador.setTranslateX(baseX+despX*3);
        transportador.setTranslateY(baseY+despY*3);
        event.consume();
        
    }

    @FXML
    private void transportadorPressed(MouseEvent event) {
        inicioXTrans = event.getSceneX();
        inicioYTrans = event.getSceneY();
        baseX = transportador.getTranslateX();
        baseY = transportador.getTranslateY();
        event.consume ();
    }

    @FXML
    private void lineai(MouseEvent event) {
        if(setRLapiz == true || setCLapiz == true){
            linePainting = new Line(event.getX(), event.getY(), event.getX(), event.getY());
            linePainting.setStroke(color);
            linePainting.setStrokeWidth(grosor);
            zoomGroup.getChildren().add(linePainting);
        }
        
        if(setRLapiz == true){
            circlePaintingPoint = new Circle(1);
            circlePaintingPoint.setStroke(Color.RED);
            circlePaintingPoint.setStrokeWidth(grosor + 5);
            circlePaintingPoint.setFill(Color.TRANSPARENT);
            zoomGroup.getChildren().add(circlePaintingPoint);
            circlePaintingPoint.setCenterX(event.getX());
            circlePaintingPoint.setCenterY(event.getY());
        }
        
        if(setCompas == true){
            // Centro:
            circlePaintingPoint2 = new Circle(1);
            circlePaintingPoint2.setStroke(Color.RED);
            circlePaintingPoint2.setStrokeWidth(grosor);
            circlePaintingPoint2.setFill(Color.TRANSPARENT);
            zoomGroup.getChildren().add(circlePaintingPoint2);
            circlePaintingPoint2.setCenterX(event.getX());
            circlePaintingPoint2.setCenterY(event.getY());
            //
            
            // Círculo:
            circlePainting = new Circle(1);
            circlePainting.setStroke(color);
            circlePainting.setStrokeWidth(grosor);
            circlePainting.setFill(Color.TRANSPARENT);
            zoomGroup.getChildren().add(circlePainting);
            circlePainting.setCenterX(event.getX());
            circlePainting.setCenterY(event.getY());
            circuloX = event.getX();
            //
        }
        
        if(setGoma == true){
            // Borrar Línea:
            linePainting.setOnContextMenuRequested(e -> {
                ContextMenu menuContext = new ContextMenu();
                MenuItem borrarItem = new MenuItem("eliminar");
                menuContext.getItems().add(borrarItem);
                borrarItem.setOnAction(ev -> {
                    zoomGroup.getChildren().remove((Node)e.getSource());
                    ev.consume();
                });
                    menuContext.show(
                    linePainting, e.getScreenX(), e.getScreenY());
                e.consume();
            });
        }
        //
        
    }

    @FXML
    private void lineaf(MouseEvent event) {
        
        if(setRLapiz == true){linePainting.getStyleClass().add("linea");}
        
        if(compas.isVisible()){
            double radio = Math.abs(event.getX() - circuloX);
            circlePainting.setRadius(radio);
            event.consume();
        }
        
        if(setRLapiz == true){
            linePainting.setStroke(color);
            linePainting.setStrokeWidth(grosor);
            linePainting.setEndX(event.getX());
            linePainting.setEndY(event.getY());
            event.consume();
        }
        if(setCLapiz == true){
            linePainting = new Line(event.getX(), event.getY(), event.getX(), event.getY());
            linePainting.setStroke(color);
            linePainting.setStrokeWidth(grosor);
            zoomGroup.getChildren().add(linePainting);
            linePainting.setEndX(event.getX());
            linePainting.setEndY(event.getY());
            event.consume();
        }
    }
    
    @FXML
    private void soltarLinea(MouseEvent event) {
        if(setRLapiz == true){
            circlePaintingPoint = new Circle(1);
            circlePaintingPoint.setStroke(Color.RED);
            circlePaintingPoint.setStrokeWidth(grosor + 5);
            circlePaintingPoint.setFill(Color.TRANSPARENT);
            zoomGroup.getChildren().add(circlePaintingPoint);
            circlePaintingPoint.setCenterX(event.getX());
            circlePaintingPoint.setCenterY(event.getY());
        }
    }
    
    @FXML
    private void lapizRVisible(ActionEvent event) throws FileNotFoundException {
        if(setRLapiz == false){
            lapizR.setVisible(true);
            setRLapiz = true;
            lapizC.setVisible(false);
            setCLapiz = false;
            goma.setVisible(false);
            setGoma = false;
            compas.setVisible(false);
            setCompas = false;
            lapizRCheck.setSelected(true);
            lapizCCheck.setSelected(false);
            gomaCheck.setSelected(false);
            compasCheck.setSelected(false);
            Image herramientaUso = new Image(new FileInputStream(".\\src\\resources\\lapiz.jpg"));
            herramientaActual.setImage(herramientaUso);
        }else{
            lapizR.setVisible(false);
            setRLapiz = false;
            lapizRCheck.setSelected(false);
            Image herramientaUso = new Image(new FileInputStream(".\\src\\resources\\negro.jpg"));
            herramientaActual.setImage(herramientaUso);
        }
    }
    
    @FXML
    private void lapizCVisible(ActionEvent event) throws FileNotFoundException {
        if(setCLapiz == false){
            lapizC.setVisible(true);
            setCLapiz = true;
            lapizR.setVisible(false);
            setRLapiz = false;
            goma.setVisible(false);
            setGoma = false;
            compas.setVisible(false);
            setCompas = false;
            lapizCCheck.setSelected(true);
            lapizRCheck.setSelected(false);
            gomaCheck.setSelected(false); 
            compasCheck.setSelected(false);
            Image herramientaUso = new Image(new FileInputStream(".\\src\\resources\\lapiz.jpg"));
            herramientaActual.setImage(herramientaUso);
        }else{
            lapizC.setVisible(false);
            setCLapiz = false;
            lapizCCheck.setSelected(false);
            Image herramientaUso = new Image(new FileInputStream(".\\src\\resources\\negro.jpg"));
            herramientaActual.setImage(herramientaUso);
        }
    }

    @FXML
    private void gomaVisible(ActionEvent event) throws FileNotFoundException {
        if(setGoma == false){
            goma.setVisible(true);
            setGoma = true;
            lapizR.setVisible(false);
            setRLapiz = false;
            lapizC.setVisible(false);
            setCLapiz = false;
            compas.setVisible(false);
            setCompas = false;
            gomaCheck.setSelected(true);
            lapizRCheck.setSelected(false);
            lapizCCheck.setSelected(false);
            compasCheck.setSelected(false);
            Image herramientaUso = new Image(new FileInputStream(".\\src\\resources\\goma.jpg"));
            herramientaActual.setImage(herramientaUso);
        }else{
            goma.setVisible(false);
            setGoma = false;
            gomaCheck.setSelected(false);
            Image herramientaUso = new Image(new FileInputStream(".\\src\\resources\\negro.jpg"));
            herramientaActual.setImage(herramientaUso);
        }
    }

    @FXML
    private void compasVisible(ActionEvent event) throws FileNotFoundException {
        if(setCompas == false){
            compas.setVisible(true);
            setCompas = true;
            goma.setVisible(false);
            setGoma = false;
            lapizR.setVisible(false);
            setRLapiz = false;
            lapizC.setVisible(false);
            setCLapiz = false;
            compasCheck.setSelected(true);
            gomaCheck.setSelected(false);
            lapizRCheck.setSelected(false);
            lapizCCheck.setSelected(false);
            Image herramientaUso = new Image(new FileInputStream(".\\src\\resources\\compas.jpg"));
            herramientaActual.setImage(herramientaUso);
        }else{
            compas.setVisible(false);
            setCompas = false;
            compasCheck.setSelected(false);
            Image herramientaUso = new Image(new FileInputStream(".\\src\\resources\\negro.jpg"));
            herramientaActual.setImage(herramientaUso);
        }
    }

    @FXML
    private void setColorAzul(ActionEvent event) {
        color = Color.BLUE;
    }

    @FXML
    private void setColorVerde(ActionEvent event) {
        color = Color.GREEN;
    }

    @FXML
    private void setColorRojo(ActionEvent event) {
        color = Color.RED;
    }

    @FXML
    private void setColorAmarillo(ActionEvent event) {
        color = Color.YELLOW;
    }

    @FXML
    private void setColorNegro(ActionEvent event) {
        color = Color.BLACK;
    }

    @FXML
    private void setGrosor3(ActionEvent event) {
        grosor = 3;
    }
    
    @FXML
    private void setGrosor7(ActionEvent event) {
        grosor = 7;
    }
    
    @FXML
    private void setGrosor10(ActionEvent event) {
        grosor = 10;
    }
    
    @FXML
    private void setGrosor12(ActionEvent event) {
        grosor = 12;
    }

    @FXML
    private void setGrosor15(ActionEvent event) {
        grosor = 15;
    }

    @FXML
    private void problAleatorio(ActionEvent event) {
        if(problemaInt != 0){
            problemas = database.getProblems();
            Random random = new Random(); 
            int index = random.nextInt(problemas.size() - 1);
            problema = problemas.get(index);
            problemaInt = index;
        }else {problemaInt = -1;}
    }
    
    @FXML
    private void probUno(ActionEvent event) {
        if(problemaInt != 1){
            problemas = database.getProblems();
            problema = problemas.get(2);
            problemaInt = 1;
            // Agregar problema:
            
        }else {
            problemaInt = -1;
            // Quitar problema:
            
        }
    }

    @FXML
    private void probDos(ActionEvent event) {
        if(problemaInt != 2){
            problemas = database.getProblems();
            problema = problemas.get(2);
            problemaInt = 2;
        }else {problemaInt = -1;}
    }

    @FXML
    private void probTres(ActionEvent event) {
        if(problemaInt != 3){
            problemas = database.getProblems();
            problema = problemas.get(3);
            problemaInt = 3;
        }else {problemaInt = -1;}
    }

    @FXML
    private void probCuatro(ActionEvent event) {
        if(problemaInt != 4){
            problemas = database.getProblems();
            problema = problemas.get(4);
            problemaInt = 4;
        }else {problemaInt = -1;}
    }

    @FXML
    private void probCinco(ActionEvent event) {
        if(problemaInt != 5){
            problemas = database.getProblems();
            problema = problemas.get(5);
            problemaInt = 5;
        }else {problemaInt = -1;}
    }

    @FXML
    private void probSeis(ActionEvent event) {
        if(problemaInt != 6){
            problemas = database.getProblems();
            problema = problemas.get(6);
            problemaInt = 6;
        }else {problemaInt = -1;}
    }

    @FXML
    private void probSiete(ActionEvent event) {
        if(problemaInt != 7){
            problemas = database.getProblems();
            problema = problemas.get(7);
            problemaInt = 7;
        }else {problemaInt = -1;}
    }

    @FXML
    private void probOcho(ActionEvent event) {
        if(problemaInt != 8){
            problemas = database.getProblems();
            problema = problemas.get(8);
            problemaInt = 8;
        }else {problemaInt = -1;}
    }

    @FXML
    private void probNueve(ActionEvent event) {
        if(problemaInt != 9){
            problemas = database.getProblems();
            problema = problemas.get(9);
            problemaInt = 9;
        }else {problemaInt = -1;}
    }

    @FXML
    private void probDiez(ActionEvent event) {
        if(problemaInt != 10){
            problemas = database.getProblems();
            problema = problemas.get(10);
            problemaInt = 10;
        }else {problemaInt = -1;}
    }

    @FXML
    private void probOnce(ActionEvent event) {
        if(problemaInt != 11){
            problemas = database.getProblems();
            problema = problemas.get(11);
            problemaInt = 11;
        }else {problemaInt = -1;}
    }

    @FXML
    private void probDoce(ActionEvent event) {
        if(problemaInt != 12){
            problemas = database.getProblems();
            problema = problemas.get(12);
            problemaInt = 12;
        }else {problemaInt = -1;}
    }

    @FXML
    private void probTrece(ActionEvent event) {
        if(problemaInt != 13){
            problemas = database.getProblems();
            problema = problemas.get(13);
            problemaInt = 13;
        }else {problemaInt = -1;}
    }

    @FXML
    private void probCatorce(ActionEvent event) {
        if(problemaInt != 14){
            problemas = database.getProblems();
            problema = problemas.get(14);
            problemaInt = 14;
        }else {problemaInt = -1;}
    }

    @FXML
    private void probQuince(ActionEvent event) {
        if(problemaInt != 15){
            problemas = database.getProblems();
            problema = problemas.get(15);
            problemaInt = 15;
        }else {problemaInt = -1;}
    }

    @FXML
    private void probDieciseis(ActionEvent event) {
        if(problemaInt != 16){
            problemas = database.getProblems();
            problema = problemas.get(16);
            problemaInt = 16;
        }else {problemaInt = -1;}
    }

    @FXML
    private void probDiecisiete(ActionEvent event) {
        if(problemaInt != 17){
            problemas = database.getProblems();
            problema = problemas.get(17);
            problemaInt = 17;
        }else {problemaInt = -1;}
    }

    @FXML
    private void probDieciocho(ActionEvent event) {
        if(problemaInt != 18){
            problemas = database.getProblems();
            problema = problemas.get(18);
            problemaInt = 18;
        }else {problemaInt = -1;}
    }
    
}
