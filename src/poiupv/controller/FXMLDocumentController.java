/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poiupv.controller;

import java.awt.BasicStroke;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import poiupv.model.Poi;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
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
    private double inicioXTrans;
    private double inicioYTrans;
    private double baseX;
    private double baseY;
    Line linePainting;
    Circle circlePainting;
    Circle circlePaintingPoint;
    Circle circlePaintingPoint2;
    Session sesion;
    
    @FXML
    private ListView<Poi> map_listview;
    @FXML
    private ScrollPane map_scrollpane;
    @FXML
    private Slider zoom_slider;
    @FXML
    private MenuButton map_pin;
    @FXML
    private MenuItem pin_info;
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

    @FXML
    void listClicked(MouseEvent event) {
        Poi itemSelected = map_listview.getSelectionModel().getSelectedItem();

        // Animación del scroll hasta la posicion del item seleccionado
        double mapWidth = zoomGroup.getBoundsInLocal().getWidth();
        double mapHeight = zoomGroup.getBoundsInLocal().getHeight();
        double scrollH = itemSelected.getPosition().getX() / mapWidth;
        double scrollV = itemSelected.getPosition().getY() / mapHeight;
        final Timeline timeline = new Timeline();
        final KeyValue kv1 = new KeyValue(map_scrollpane.hvalueProperty(), scrollH);
        final KeyValue kv2 = new KeyValue(map_scrollpane.vvalueProperty(), scrollV);
        final KeyFrame kf = new KeyFrame(Duration.millis(500), kv1, kv2);
        timeline.getKeyFrames().add(kf);
        timeline.play();

        // movemos el objto map_pin hasta la posicion del POI
        double pinW = map_pin.getBoundsInLocal().getWidth();
        double pinH = map_pin.getBoundsInLocal().getHeight();
        map_pin.setLayoutX(itemSelected.getPosition().getX());
        map_pin.setLayoutY(itemSelected.getPosition().getY());
        pin_info.setText(itemSelected.getDescription());
        map_pin.setVisible(true);
    }

    private void initData() {
        hm.put("2F", new Poi("2F", "Edificion del DSIC", 325, 225));
        hm.put("Agora", new Poi("Agora", "Agora", 600, 360));
        map_listview.getItems().add(hm.get("2F"));
        map_listview.getItems().add(hm.get("Agora"));
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        initData();
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

    }

    @FXML
    private void muestraPosicion(MouseEvent event) {
        posicion.setText("sceneX: " + (int) event.getSceneX() + ", sceneY: " + (int) event.getSceneY() + "\n"
                + "         X: " + (int) event.getX() + ",          Y: " + (int) event.getY());
    }

    @FXML
    private void cerrarAplicacion(ActionEvent event) {
        usuario.addSession(sesion);
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
        usuario.addSession(sesion);
        FXMLLoader registrarse = new FXMLLoader(getClass().getResource("/poiupv/run/FXMLLog.fxml"));
        Parent root = registrarse.load();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.show();
        ((Stage)zoom_slider.getScene().getWindow()).close();
    }

    @FXML
    private void problAleatorio(ActionEvent event) {
    }

    @FXML
    private void modifPerfil(ActionEvent event) throws IOException {
        FXMLLoader registrarse = new FXMLLoader(getClass().getResource("/poiupv/run/FXMLModifPerfil.fxml"));
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
        
        // Borrar Línea:
        linePainting.setOnContextMenuRequested(e -> {
            ContextMenu menuContext = new ContextMenu();
            MenuItem borrarItem = new MenuItem("eliminar");
            menuContext.getItems().add(borrarItem);
            borrarItem.setOnAction(ev -> {
                zoomGroup.getChildren().remove((Node)e.getSource());
                ev.consume();
            });
            if(setRLapiz == false && setCLapiz == false && setCompas == false){
                menuContext.show(
                linePainting, e.getScreenX(), e.getScreenY());
            }
            e.consume();
        });
        //
        
    }

    @FXML
    private void lineaf(MouseEvent event) {
        
        linePainting.getStyleClass().add("linea");
        
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

}
