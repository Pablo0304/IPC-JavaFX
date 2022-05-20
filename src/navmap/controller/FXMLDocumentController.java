/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navmap.controller;

import DBAccess.NavegacionDAOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
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
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Navegacion;
import model.Problem;
import model.Session;
import model.User;

/**
 * @author Pablo Gonzálbez
 * @author Jose Marco
 */
public class FXMLDocumentController implements Initializable {

    // ======================================
    // la variable zoomGroup se utiliza para dar soporte al zoom
    // el escalado se realiza sobre este nodo, al escalar el Group no mueve sus nodos
    private Group zoomGroup;
    private boolean setTransportador = false;
    private boolean setRegla = false;
    private boolean setRLapiz = false;
    private boolean setCLapiz = false;
    private boolean setGoma = false;
    private boolean setCompas = false;
    private boolean setAnota = false;
    boolean coordenadasCheck = false;
    Color color = Color.BLACK;
    int grosor = 3;
    double circuloX;
    int problemaInt = -1;
    private double inicioXTrans;
    private double inicioYTrans;
    private double baseX;
    private double baseY;
    private double inicioXTrans2;
    private double inicioYTrans2;
    private double baseX2;
    private double baseY2;
    private int aciertos = 0;
    private int fallos = 0;
    Line linePainting;
    Circle circlePainting;
    Circle coordenada;
    Circle circlePaintingPoint;
    Circle circlePaintingPoint2;
    Session sesion;
    Object problema;
    User usuario;
    Navegacion database;
    
    @FXML
    private ScrollPane map_scrollpane;
    @FXML
    private Slider zoom_slider;
    @FXML
    private Label posicion;
    @FXML
    private Pane transportador;
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
    protected Label user;
    @FXML
    private Pane regla;
    @FXML
    private CheckMenuItem anotaCheck;
    @FXML
    private Pane anotacion;
    @FXML
    private Text textoProblema;
    @FXML
    private Button a;
    @FXML
    private Button b;
    @FXML
    private Button c;
    @FXML
    private Button d;
    @FXML
    private VBox vbox;
    @FXML
    private VBox vboxBut;
    @FXML
    private ImageView carta;
    @FXML
    private Label aciertosLab;
    @FXML
    private Label fallosLab;
    
    
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
    }
    
    public void userInit(User usuari){
        usuario = usuari;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb){
        try {    
            //initData();
            database = Navegacion.getSingletonNavegacion();
            
            System.out.println(database.getProblems().size());
        
        } catch (NavegacionDAOException ex) {
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
        
    }

    @FXML
    private void muestraPosicion(MouseEvent event) {
        posicion.setText("sceneX: " + (int) event.getSceneX() + ", sceneY: " + (int) event.getSceneY() + "\n"
                + "         X: " + (int) event.getX() + ",          Y: " + (int) event.getY());
    }

    @FXML
    private void cerrarAplicacion(ActionEvent event) throws NavegacionDAOException {
        usuario.addSession(new Session(LocalDateTime.now(), aciertos, fallos));
        ((Stage)zoom_slider.getScene().getWindow()).close();
    }

    @FXML
    private void mostrarResults(ActionEvent event) throws IOException {
//        Alert mensaje= new Alert(Alert.AlertType.INFORMATION);
//        mensaje.setTitle("Acerca de");
//        mensaje.setHeaderText("NavMap - 2022");
//        mensaje.showAndWait();
        FXMLLoader results = new FXMLLoader(getClass().getResource("/navmap/run/FXMLEvolucion.fxml"));
        Parent root = results.load();
        
        // Paso de parámetros:
            FXMLEvolucionController evController = results.getController();
            evController.userInit(usuario);
//        evController.user.setText(usuario.getNickName());
        //
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("Evolución");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    @FXML
    private void cerrarSesión(ActionEvent event) throws IOException, NavegacionDAOException {
        usuario.addSession(new Session(LocalDateTime.now(), aciertos, fallos));
        FXMLLoader registrarse = new FXMLLoader(getClass().getResource("/navmap/run/FXMLLog.fxml"));
        Parent root = registrarse.load();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("Inicio Sesión");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.show();
        ((Stage)zoom_slider.getScene().getWindow()).close();
    }

    @FXML
    private void modifPerfil(ActionEvent event) throws IOException {
        FXMLLoader modif = new FXMLLoader(getClass().getResource("/navmap/run/FXMLModifPerfil.fxml"));
        Parent root = modif.load();
        
        // Paso de parámetros:
        FXMLModifPerfilController modController = modif.getController();
        modController.userInit(usuario);
        //
        
        
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("Modificar Perfil");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.show();
        modController.user.setText("Nombre de usuario: " + usuario.getNickName()); 

    }

    @FXML
    private void rotaRegla(MouseEvent event) {
        double inicial = event.getX();
        double inicialRota = regla.getRotate();
        regla.setRotate(inicialRota + ((event.getX()+ event.getY()) / 2));
        event.consume();
    }
    
    @FXML
    private void reglaVisible(ActionEvent event) {
        if(setRegla == false){
            regla.visibleProperty().set(true);
            setRegla = true;
        } else{
            regla.visibleProperty().set(false);
            setRegla = false;
        }
    }
    
    @FXML
    private void reglaDragged(MouseEvent event) {
        double despX = event.getSceneX()-inicioXTrans2;
        double despY = event.getSceneY()-inicioYTrans2;
        regla.setTranslateX(baseX2+despX*2);
        regla.setTranslateY(baseY2+despY*2);
        event.consume();
    }

    @FXML
    private void reglaPressed(MouseEvent event) {
        inicioXTrans2 = event.getSceneX();
        inicioYTrans2 = event.getSceneY();
        baseX2 = regla.getTranslateX();
        baseY2 = regla.getTranslateY();
        event.consume ();
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
        event.consume();
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
        
        if(setAnota == true){
            event.consume();
            TextField texto = new TextField();
            zoomGroup.getChildren().add(texto);
            texto.setLayoutX(event.getX());
            texto.setLayoutY(event.getY());
            texto.requestFocus();
            texto.setOnAction(e -> {
                Text textoT = new Text(texto.getText());
                textoT.setX(texto.getLayoutX());
                textoT.setY(texto.getLayoutY());
                textoT.setStyle("-fx-font-family: Gafata; -fx-font-size: 20;");
                zoomGroup.getChildren().add(textoT);
                zoomGroup.getChildren().remove(texto);
                e.consume();                
            });
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
        
        // Latitud y Longitud:
        if(coordenadasCheck){
            this.mapaPanel.setOnContextMenuRequested(e->{
                ContextMenu menuContext = new ContextMenu();
                MenuItem coordenadas = new MenuItem("Coordenadas");
                if(coordenadasCheck){menuContext.getItems().add(coordenadas);}
                coordenadas.setOnAction(ev -> {
                    coordenada = new Circle(1);
                    coordenada.setStroke(Color.BLACK);
                    coordenada.setStrokeWidth(10);
                    zoomGroup.getChildren().add(coordenada);
                    coordenada.setCenterX(event.getX());
                    coordenada.setCenterY(event.getY());
                    Line coordenadaX = new Line(event.getX(), event.getY(), 0, event.getY());
                    Line coordenadaY = new Line(event.getX(), event.getY(), event.getX(), 0);
                    coordenadaX.setStrokeWidth(3);
                    coordenadaY.setStrokeWidth(3);
                    zoomGroup.getChildren().add(coordenadaX);
                    zoomGroup.getChildren().add(coordenadaY);
                    ev.consume();
                });
                menuContext.show(
                mapaPanel, e.getScreenX(), e.getScreenY());
                e.consume();
            });
        }
        //
            
        
        if(setRLapiz){
            // Borrar Línea:
            this.linePainting.setOnContextMenuRequested(e -> {
                ContextMenu menuContext = new ContextMenu();
                MenuItem borrarItem = new MenuItem("Eliminar");
                menuContext.getItems().add(borrarItem);
                borrarItem.setOnAction(ev -> {
                    zoomGroup.getChildren().remove((Node)e.getSource());
                    ev.consume();
                });if(goma.isVisible()){
                    menuContext.show(
                    linePainting, e.getScreenX(), e.getScreenY());}
                e.consume();
        });}
        //
            
            // Borrar Centro del Círculo:
//            if(goma.isVisible()){
//            circlePaintingPoint2.setOnContextMenuRequested(e -> {
//                ContextMenu menuContext = new ContextMenu();
//                MenuItem borrarItem = new MenuItem("eliminar");
//                menuContext.getItems().add(borrarItem);
//                borrarItem.setOnAction(ev -> {
//                    zoomGroup.getChildren().remove((Node)e.getSource());
//                    ev.consume();
//                });
//                    this.circlePaintingPoint2.setVisible(false);
//                    menuContext.show(
//                    circlePaintingPoint2, e.getScreenX(), e.getScreenY());
//                e.consume();
//            });
//            }
            //
            
            // Borrar Círculo:
//            if(goma.isVisible()){
//                circlePainting.setOnContextMenuRequested(e -> {
//                    ContextMenu menuContext = new ContextMenu();
//                    MenuItem borrarItem = new MenuItem("eliminar");
//                    menuContext.getItems().add(borrarItem);
//                    borrarItem.setOnAction(ev -> {
//                        zoomGroup.getChildren().remove((Node)e.getSource());
//                        ev.consume();
//                    });
//                        this.circlePainting.setVisible(false);
//                        menuContext.show(
//                        circlePainting, e.getScreenX(), e.getScreenY());
//                    e.consume();
//                });
//            }
            //
            
            // Borrar Vértice de Línea:
//            if(goma.isVisible()){
//            circlePaintingPoint.setOnContextMenuRequested(e -> {
//                ContextMenu menuContext = new ContextMenu();
//                MenuItem borrarItem = new MenuItem("eliminar");
//                menuContext.getItems().add(borrarItem);
//                borrarItem.setOnAction(ev -> {
//                    zoomGroup.getChildren().remove((Node)e.getSource());
//                    ev.consume();
//                });
//                    this.circlePaintingPoint.setVisible(false);
//                    menuContext.show(
//                    circlePaintingPoint, e.getScreenX(), e.getScreenY());
//                e.consume();
//            });
//            }
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
            anotacion.setVisible(false);
            setAnota = false;
            lapizC.setVisible(false);
            setCLapiz = false;
            goma.setVisible(false);
            setGoma = false;
            compas.setVisible(false);
            setCompas = false;
            lapizRCheck.setSelected(true);
            lapizCCheck.setSelected(false);
            anotaCheck.setSelected(false);
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
            anotacion.setVisible(false);
            setAnota = false;
            lapizR.setVisible(false);
            setRLapiz = false;
            goma.setVisible(false);
            setGoma = false;
            compas.setVisible(false);
            setCompas = false;
            lapizCCheck.setSelected(true);
            anotaCheck.setSelected(false);
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
            anotacion.setVisible(false);
            setAnota = false;
            lapizR.setVisible(false);
            setRLapiz = false;
            lapizC.setVisible(false);
            setCLapiz = false;
            compas.setVisible(false);
            setCompas = false;
            gomaCheck.setSelected(true);
            anotaCheck.setSelected(false);
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
    private void anotaVisible(ActionEvent event) throws FileNotFoundException {
        if(setAnota == false){
            anotacion.setVisible(true);
            setAnota = true;
            compas.setVisible(false);
            setCompas = false;
            goma.setVisible(false);
            setGoma = false;
            lapizR.setVisible(false);
            setRLapiz = false;
            lapizC.setVisible(false);
            setCLapiz = false;
            anotaCheck.setSelected(true);
            compasCheck.setSelected(false);
            gomaCheck.setSelected(false);
            lapizRCheck.setSelected(false);
            lapizCCheck.setSelected(false);
            Image herramientaUso = new Image(new FileInputStream(".\\src\\resources\\anota.jpg"));
            herramientaActual.setImage(herramientaUso);
        }else{
            anotacion.setVisible(false);
            setAnota = false;
            anotaCheck.setSelected(false);
            Image herramientaUso = new Image(new FileInputStream(".\\src\\resources\\anota.jpg"));
            herramientaActual.setImage(herramientaUso);
        }
    }
    
    @FXML
    private void coordenadas(ActionEvent event) {
        if(coordenadasCheck){
            coordenadasCheck = false;
        } else{coordenadasCheck = true;}
    }
    
    @FXML
    private void compasVisible(ActionEvent event) throws FileNotFoundException {
        if(setCompas == false){
            compas.setVisible(true);
            setCompas = true;
            anotacion.setVisible(false);
            setAnota = false;
            goma.setVisible(false);
            setGoma = false;
            lapizR.setVisible(false);
            setRLapiz = false;
            lapizC.setVisible(false);
            setCLapiz = false;
            compasCheck.setSelected(true);
            anotaCheck.setSelected(false);
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
            a.getStyleClass().removeAll("botonRojo");
            b.getStyleClass().removeAll("botonRojo");
            c.getStyleClass().removeAll("botonRojo");
            d.getStyleClass().removeAll("botonRojo");
            a.getStyleClass().removeAll("botonVerde");
            b.getStyleClass().removeAll("botonVerde");
            c.getStyleClass().removeAll("botonVerde");
            d.getStyleClass().removeAll("botonVerde");
            b.setDisable(false);
            c.setDisable(false);
            a.setDisable(false);
            d.setDisable(false);
        if(problemaInt != 0){
            // Agregar problema:
            vbox.setVisible(false);
            vboxBut.setVisible(true);
            List<Problem> problemas = database.getProblems();
            Random random = new Random(); 
            int index = random.nextInt(problemas.size() - 1);
            problema = problemas.get(index);
            problemaInt = index;
            textoProblema.setText(problemas.get(index).getText());
            a.setText(problemas.get(index).getAnswers().get(0).getText());
            b.setText(problemas.get(index).getAnswers().get(1).getText());
            c.setText(problemas.get(index).getAnswers().get(2).getText());
            d.setText(problemas.get(index).getAnswers().get(3).getText());
        }else {
            problemaInt = -1;
            // Quitar problema:
            vbox.setVisible(true);
            vboxBut.setVisible(false);
        }
    }
    
    @FXML
    private void probUno(ActionEvent event) {
            a.getStyleClass().removeAll("botonRojo");
            b.getStyleClass().removeAll("botonRojo");
            c.getStyleClass().removeAll("botonRojo");
            d.getStyleClass().removeAll("botonRojo");
            a.getStyleClass().removeAll("botonVerde");
            b.getStyleClass().removeAll("botonVerde");
            c.getStyleClass().removeAll("botonVerde");
            d.getStyleClass().removeAll("botonVerde");
            b.setDisable(false);
            c.setDisable(false);
            a.setDisable(false);
            d.setDisable(false);
        if(problemaInt != 1){
            problemaInt = 1;
            // Agregar problema:
            vbox.setVisible(false);
            vboxBut.setVisible(true);
            List<Problem> problemas = database.getProblems();
            textoProblema.setText(problemas.get(0).getText());
            a.setText(problemas.get(0).getAnswers().get(0).getText());
            b.setText(problemas.get(0).getAnswers().get(1).getText());
            c.setText(problemas.get(0).getAnswers().get(2).getText());
            d.setText(problemas.get(0).getAnswers().get(3).getText());
        }else {
            problemaInt = -1;
            // Quitar problema:
            vbox.setVisible(true);
            vboxBut.setVisible(false);
        }
    }

    @FXML
    private void probDos(ActionEvent event) {
            a.getStyleClass().removeAll("botonRojo");
            b.getStyleClass().removeAll("botonRojo");
            c.getStyleClass().removeAll("botonRojo");
            d.getStyleClass().removeAll("botonRojo");
            a.getStyleClass().removeAll("botonVerde");
            b.getStyleClass().removeAll("botonVerde");
            c.getStyleClass().removeAll("botonVerde");
            d.getStyleClass().removeAll("botonVerde");
            b.setDisable(false);
            c.setDisable(false);
            a.setDisable(false);
            d.setDisable(false);
        if(problemaInt != 2){
            problemaInt = 2;
            // Agregar problema:
            vbox.setVisible(false);
            vboxBut.setVisible(true);
            List<Problem> problemas = database.getProblems();
            textoProblema.setText(problemas.get(1).getText());
            a.setText(problemas.get(1).getAnswers().get(0).getText());
            b.setText(problemas.get(1).getAnswers().get(1).getText());
            c.setText(problemas.get(1).getAnswers().get(2).getText());
            d.setText(problemas.get(1).getAnswers().get(3).getText());
        }else {
            problemaInt = -1;
            // Quitar problema:
            vbox.setVisible(true);
            vboxBut.setVisible(false);
        }
    }

    @FXML
    private void probTres(ActionEvent event) {
            a.getStyleClass().removeAll("botonRojo");
            b.getStyleClass().removeAll("botonRojo");
            c.getStyleClass().removeAll("botonRojo");
            d.getStyleClass().removeAll("botonRojo");
            a.getStyleClass().removeAll("botonVerde");
            b.getStyleClass().removeAll("botonVerde");
            c.getStyleClass().removeAll("botonVerde");
            d.getStyleClass().removeAll("botonVerde");
            b.setDisable(false);
            c.setDisable(false);
            a.setDisable(false);
            d.setDisable(false);
        if(problemaInt != 3){
            problemaInt = 3;
            // Agregar problema:
            vbox.setVisible(false);
            vboxBut.setVisible(true);
            List<Problem> problemas = database.getProblems();
            textoProblema.setText(problemas.get(2).getText());
            a.setText(problemas.get(2).getAnswers().get(0).getText());
            b.setText(problemas.get(2).getAnswers().get(1).getText());
            c.setText(problemas.get(2).getAnswers().get(2).getText());
            d.setText(problemas.get(2).getAnswers().get(3).getText());
        }else {
            problemaInt = -1;
            // Quitar problema:
            vbox.setVisible(true);
            vboxBut.setVisible(false);
        }
    }

    @FXML
    private void probCuatro(ActionEvent event) {
            a.getStyleClass().removeAll("botonRojo");
            b.getStyleClass().removeAll("botonRojo");
            c.getStyleClass().removeAll("botonRojo");
            d.getStyleClass().removeAll("botonRojo");
            a.getStyleClass().removeAll("botonVerde");
            b.getStyleClass().removeAll("botonVerde");
            c.getStyleClass().removeAll("botonVerde");
            d.getStyleClass().removeAll("botonVerde");
            b.setDisable(false);
            c.setDisable(false);
            a.setDisable(false);
            d.setDisable(false);
        if(problemaInt != 4){
            problemaInt = 4;
            // Agregar problema:
            vbox.setVisible(false);
            vboxBut.setVisible(true);
            List<Problem> problemas = database.getProblems();
            textoProblema.setText(problemas.get(3).getText());
            a.setText(problemas.get(3).getAnswers().get(0).getText());
            b.setText(problemas.get(3).getAnswers().get(1).getText());
            c.setText(problemas.get(3).getAnswers().get(2).getText());
            d.setText(problemas.get(3).getAnswers().get(3).getText());
        }else {
            problemaInt = -1;
            // Quitar problema:
            vbox.setVisible(true);
            vboxBut.setVisible(false);
        }
    }

    @FXML
    private void probCinco(ActionEvent event) {
            a.getStyleClass().removeAll("botonRojo");
            b.getStyleClass().removeAll("botonRojo");
            c.getStyleClass().removeAll("botonRojo");
            d.getStyleClass().removeAll("botonRojo");
            a.getStyleClass().removeAll("botonVerde");
            b.getStyleClass().removeAll("botonVerde");
            c.getStyleClass().removeAll("botonVerde");
            d.getStyleClass().removeAll("botonVerde");
            b.setDisable(false);
            c.setDisable(false);
            a.setDisable(false);
            d.setDisable(false);
        if(problemaInt != 5){
            problemaInt = 5;
            // Agregar problema:
            vbox.setVisible(false);
            vboxBut.setVisible(true);
            List<Problem> problemas = database.getProblems();
            textoProblema.setText(problemas.get(4).getText());
            a.setText(problemas.get(4).getAnswers().get(0).getText());
            b.setText(problemas.get(4).getAnswers().get(1).getText());
            c.setText(problemas.get(4).getAnswers().get(2).getText());
            d.setText(problemas.get(4).getAnswers().get(3).getText());
        }else {
            problemaInt = -1;
            // Quitar problema:
            vbox.setVisible(true);
            vboxBut.setVisible(false);
        }
    }

    @FXML
    private void probSeis(ActionEvent event) {
            a.getStyleClass().removeAll("botonRojo");
            b.getStyleClass().removeAll("botonRojo");
            c.getStyleClass().removeAll("botonRojo");
            d.getStyleClass().removeAll("botonRojo");
            a.getStyleClass().removeAll("botonVerde");
            b.getStyleClass().removeAll("botonVerde");
            c.getStyleClass().removeAll("botonVerde");
            d.getStyleClass().removeAll("botonVerde");
            b.setDisable(false);
            c.setDisable(false);
            a.setDisable(false);
            d.setDisable(false);
        if(problemaInt != 6){
            problemaInt = 6;
            // Agregar problema:
            vbox.setVisible(false);
            vboxBut.setVisible(true);
            List<Problem> problemas = database.getProblems();
            textoProblema.setText(problemas.get(5).getText());
            a.setText(problemas.get(5).getAnswers().get(0).getText());
            b.setText(problemas.get(5).getAnswers().get(1).getText());
            c.setText(problemas.get(5).getAnswers().get(2).getText());
            d.setText(problemas.get(5).getAnswers().get(3).getText());
        }else {
            problemaInt = -1;
            // Quitar problema:
            vbox.setVisible(true);
            vboxBut.setVisible(false);
        }
    }

    @FXML
    private void probSiete(ActionEvent event) {
            a.getStyleClass().removeAll("botonRojo");
            b.getStyleClass().removeAll("botonRojo");
            c.getStyleClass().removeAll("botonRojo");
            d.getStyleClass().removeAll("botonRojo");
            a.getStyleClass().removeAll("botonVerde");
            b.getStyleClass().removeAll("botonVerde");
            c.getStyleClass().removeAll("botonVerde");
            d.getStyleClass().removeAll("botonVerde");
            b.setDisable(false);
            c.setDisable(false);
            a.setDisable(false);
            d.setDisable(false);
        if(problemaInt != 7){
            problemaInt = 7;
            // Agregar problema:
            vbox.setVisible(false);
            vboxBut.setVisible(true);
            List<Problem> problemas = database.getProblems();
            textoProblema.setText(problemas.get(6).getText());
            a.setText(problemas.get(6).getAnswers().get(0).getText());
            b.setText(problemas.get(6).getAnswers().get(1).getText());
            c.setText(problemas.get(6).getAnswers().get(2).getText());
            d.setText(problemas.get(6).getAnswers().get(3).getText());
        }else {
            problemaInt = -1;
            // Quitar problema:
            vbox.setVisible(true);
            vboxBut.setVisible(false);
        }
    }

    @FXML
    private void probOcho(ActionEvent event) {
            a.getStyleClass().removeAll("botonRojo");
            b.getStyleClass().removeAll("botonRojo");
            c.getStyleClass().removeAll("botonRojo");
            d.getStyleClass().removeAll("botonRojo");
            a.getStyleClass().removeAll("botonVerde");
            b.getStyleClass().removeAll("botonVerde");
            c.getStyleClass().removeAll("botonVerde");
            d.getStyleClass().removeAll("botonVerde");
            b.setDisable(false);
            c.setDisable(false);
            a.setDisable(false);
            d.setDisable(false);
        if(problemaInt != 8){
            problemaInt = 8;
            // Agregar problema:
            vbox.setVisible(false);
            vboxBut.setVisible(true);
            List<Problem> problemas = database.getProblems();
            textoProblema.setText(problemas.get(7).getText());
            a.setText(problemas.get(7).getAnswers().get(0).getText());
            b.setText(problemas.get(7).getAnswers().get(1).getText());
            c.setText(problemas.get(7).getAnswers().get(2).getText());
            d.setText(problemas.get(7).getAnswers().get(3).getText());
        }else {
            problemaInt = -1;
            // Quitar problema:
            vbox.setVisible(true);
            vboxBut.setVisible(false);
        }
    }

    @FXML
    private void probNueve(ActionEvent event) {
            a.getStyleClass().removeAll("botonRojo");
            b.getStyleClass().removeAll("botonRojo");
            c.getStyleClass().removeAll("botonRojo");
            d.getStyleClass().removeAll("botonRojo");
            a.getStyleClass().removeAll("botonVerde");
            b.getStyleClass().removeAll("botonVerde");
            c.getStyleClass().removeAll("botonVerde");
            d.getStyleClass().removeAll("botonVerde");
            b.setDisable(false);
            c.setDisable(false);
            a.setDisable(false);
            d.setDisable(false);
        if(problemaInt != 9){
            problemaInt = 9;
            // Agregar problema:
            vbox.setVisible(false);
            vboxBut.setVisible(true);
            List<Problem> problemas = database.getProblems();
            textoProblema.setText(problemas.get(8).getText());
            a.setText(problemas.get(8).getAnswers().get(0).getText());
            b.setText(problemas.get(8).getAnswers().get(1).getText());
            c.setText(problemas.get(8).getAnswers().get(2).getText());
            d.setText(problemas.get(8).getAnswers().get(3).getText());
        }else {
            problemaInt = -1;
            // Quitar problema:
            vbox.setVisible(true);
            vboxBut.setVisible(false);
        }
    }

    @FXML
    private void probDiez(ActionEvent event) {
            a.getStyleClass().removeAll("botonRojo");
            b.getStyleClass().removeAll("botonRojo");
            c.getStyleClass().removeAll("botonRojo");
            d.getStyleClass().removeAll("botonRojo");
            a.getStyleClass().removeAll("botonVerde");
            b.getStyleClass().removeAll("botonVerde");
            c.getStyleClass().removeAll("botonVerde");
            d.getStyleClass().removeAll("botonVerde");
            b.setDisable(false);
            c.setDisable(false);
            a.setDisable(false);
            d.setDisable(false);
        if(problemaInt != 10){
            problemaInt = 10;
            // Agregar problema:
            vbox.setVisible(false);
            vboxBut.setVisible(true);
            List<Problem> problemas = database.getProblems();
            textoProblema.setText(problemas.get(9).getText());
            a.setText(problemas.get(9).getAnswers().get(0).getText());
            b.setText(problemas.get(9).getAnswers().get(1).getText());
            c.setText(problemas.get(9).getAnswers().get(2).getText());
            d.setText(problemas.get(9).getAnswers().get(3).getText());
        }else {
            problemaInt = -1;
            // Quitar problema:
            vbox.setVisible(true);
            vboxBut.setVisible(false);
        }
    }

    @FXML
    private void probOnce(ActionEvent event) {
            a.getStyleClass().removeAll("botonRojo");
            b.getStyleClass().removeAll("botonRojo");
            c.getStyleClass().removeAll("botonRojo");
            d.getStyleClass().removeAll("botonRojo");
            a.getStyleClass().removeAll("botonVerde");
            b.getStyleClass().removeAll("botonVerde");
            c.getStyleClass().removeAll("botonVerde");
            d.getStyleClass().removeAll("botonVerde");
            b.setDisable(false);
            c.setDisable(false);
            a.setDisable(false);
            d.setDisable(false);
        if(problemaInt != 11){
            problemaInt = 11;
            // Agregar problema:
            vbox.setVisible(false);
            vboxBut.setVisible(true);
            List<Problem> problemas = database.getProblems();
            textoProblema.setText(problemas.get(10).getText());
            a.setText(problemas.get(10).getAnswers().get(0).getText());
            b.setText(problemas.get(10).getAnswers().get(1).getText());
            c.setText(problemas.get(10).getAnswers().get(2).getText());
            d.setText(problemas.get(10).getAnswers().get(3).getText());
        }else {
            problemaInt = -1;
            // Quitar problema:
            vbox.setVisible(true);
            vboxBut.setVisible(false);
        }
    }

    @FXML
    private void probDoce(ActionEvent event) {
            a.getStyleClass().removeAll("botonRojo");
            b.getStyleClass().removeAll("botonRojo");
            c.getStyleClass().removeAll("botonRojo");
            d.getStyleClass().removeAll("botonRojo");
            a.getStyleClass().removeAll("botonVerde");
            b.getStyleClass().removeAll("botonVerde");
            c.getStyleClass().removeAll("botonVerde");
            d.getStyleClass().removeAll("botonVerde");
            b.setDisable(false);
            c.setDisable(false);
            a.setDisable(false);
            d.setDisable(false);
        if(problemaInt != 12){
            problemaInt = 12;
            // Agregar problema:
            vbox.setVisible(false);
            vboxBut.setVisible(true);
            List<Problem> problemas = database.getProblems();
            textoProblema.setText(problemas.get(11).getText());
            a.setText(problemas.get(11).getAnswers().get(0).getText());
            b.setText(problemas.get(11).getAnswers().get(1).getText());
            c.setText(problemas.get(11).getAnswers().get(2).getText());
            d.setText(problemas.get(11).getAnswers().get(3).getText());
        }else {
            problemaInt = -1;
            // Quitar problema:
            vbox.setVisible(true);
            vboxBut.setVisible(false);
        }
    }

    @FXML
    private void probTrece(ActionEvent event) {
            a.getStyleClass().removeAll("botonRojo");
            b.getStyleClass().removeAll("botonRojo");
            c.getStyleClass().removeAll("botonRojo");
            d.getStyleClass().removeAll("botonRojo");
            a.getStyleClass().removeAll("botonVerde");
            b.getStyleClass().removeAll("botonVerde");
            c.getStyleClass().removeAll("botonVerde");
            d.getStyleClass().removeAll("botonVerde");
            b.setDisable(false);
            c.setDisable(false);
            a.setDisable(false);
            d.setDisable(false);
        if(problemaInt != 13){
            problemaInt = 13;
            // Agregar problema:
            vbox.setVisible(false);
            vboxBut.setVisible(true);
            List<Problem> problemas = database.getProblems();
            textoProblema.setText(problemas.get(12).getText());
            a.setText(problemas.get(12).getAnswers().get(0).getText());
            b.setText(problemas.get(12).getAnswers().get(1).getText());
            c.setText(problemas.get(12).getAnswers().get(2).getText());
            d.setText(problemas.get(12).getAnswers().get(3).getText());
        }else {
            problemaInt = -1;
            // Quitar problema:
            vbox.setVisible(true);
            vboxBut.setVisible(false);
        }
    }

    @FXML
    private void probCatorce(ActionEvent event) {
            a.getStyleClass().removeAll("botonRojo");
            b.getStyleClass().removeAll("botonRojo");
            c.getStyleClass().removeAll("botonRojo");
            d.getStyleClass().removeAll("botonRojo");
            a.getStyleClass().removeAll("botonVerde");
            b.getStyleClass().removeAll("botonVerde");
            c.getStyleClass().removeAll("botonVerde");
            d.getStyleClass().removeAll("botonVerde");
            b.setDisable(false);
            c.setDisable(false);
            a.setDisable(false);
            d.setDisable(false);
        if(problemaInt != 14){
            problemaInt = 14;
            // Agregar problema:
            vbox.setVisible(false);
            vboxBut.setVisible(true);
            List<Problem> problemas = database.getProblems();
            textoProblema.setText(problemas.get(13).getText());
            a.setText(problemas.get(13).getAnswers().get(0).getText());
            b.setText(problemas.get(13).getAnswers().get(1).getText());
            c.setText(problemas.get(13).getAnswers().get(2).getText());
            d.setText(problemas.get(13).getAnswers().get(3).getText());
        }else {
            problemaInt = -1;
            // Quitar problema:
            vbox.setVisible(true);
            vboxBut.setVisible(false);
        }
    }

    @FXML
    private void probQuince(ActionEvent event) {
            a.getStyleClass().removeAll("botonRojo");
            b.getStyleClass().removeAll("botonRojo");
            c.getStyleClass().removeAll("botonRojo");
            d.getStyleClass().removeAll("botonRojo");
            a.getStyleClass().removeAll("botonVerde");
            b.getStyleClass().removeAll("botonVerde");
            c.getStyleClass().removeAll("botonVerde");
            d.getStyleClass().removeAll("botonVerde");
            b.setDisable(false);
            c.setDisable(false);
            a.setDisable(false);
            d.setDisable(false);
        if(problemaInt != 15){
            problemaInt = 15;
            // Agregar problema:
            vbox.setVisible(false);
            vboxBut.setVisible(true);
            List<Problem> problemas = database.getProblems();
            textoProblema.setText(problemas.get(14).getText());
            a.setText(problemas.get(14).getAnswers().get(0).getText());
            b.setText(problemas.get(14).getAnswers().get(1).getText());
            c.setText(problemas.get(14).getAnswers().get(2).getText());
            d.setText(problemas.get(14).getAnswers().get(3).getText());
        }else {
            problemaInt = -1;
            // Quitar problema:
            vbox.setVisible(true);
            vboxBut.setVisible(false);
        }
    }

    @FXML
    private void probDieciseis(ActionEvent event) {
            a.getStyleClass().removeAll("botonRojo");
            b.getStyleClass().removeAll("botonRojo");
            c.getStyleClass().removeAll("botonRojo");
            d.getStyleClass().removeAll("botonRojo");
            a.getStyleClass().removeAll("botonVerde");
            b.getStyleClass().removeAll("botonVerde");
            c.getStyleClass().removeAll("botonVerde");
            d.getStyleClass().removeAll("botonVerde");
            b.setDisable(false);
            c.setDisable(false);
            a.setDisable(false);
            d.setDisable(false);
        if(problemaInt != 16){
            problemaInt = 16;
            // Agregar problema:
            vbox.setVisible(false);
            vboxBut.setVisible(true);
            List<Problem> problemas = database.getProblems();
            textoProblema.setText(problemas.get(15).getText());
            a.setText(problemas.get(15).getAnswers().get(0).getText());
            b.setText(problemas.get(15).getAnswers().get(1).getText());
            c.setText(problemas.get(15).getAnswers().get(2).getText());
            d.setText(problemas.get(15).getAnswers().get(3).getText());
        }else {
            problemaInt = -1;
            // Quitar problema:
            vbox.setVisible(true);
            vboxBut.setVisible(false);
        }
    }

    @FXML
    private void probDiecisiete(ActionEvent event) {
            a.getStyleClass().removeAll("botonRojo");
            b.getStyleClass().removeAll("botonRojo");
            c.getStyleClass().removeAll("botonRojo");
            d.getStyleClass().removeAll("botonRojo");
            a.getStyleClass().removeAll("botonVerde");
            b.getStyleClass().removeAll("botonVerde");
            c.getStyleClass().removeAll("botonVerde");
            d.getStyleClass().removeAll("botonVerde");
            b.setDisable(false);
            c.setDisable(false);
            a.setDisable(false);
            d.setDisable(false);
        if(problemaInt != 17){
            problemaInt = 17;
            // Agregar problema:
            vbox.setVisible(false);
            vboxBut.setVisible(true);
            List<Problem> problemas = database.getProblems();
            textoProblema.setText(problemas.get(16).getText());
            a.setText(problemas.get(16).getAnswers().get(0).getText());
            b.setText(problemas.get(16).getAnswers().get(1).getText());
            c.setText(problemas.get(16).getAnswers().get(2).getText());
            d.setText(problemas.get(16).getAnswers().get(3).getText());
        }else {
            problemaInt = -1;
            // Quitar problema:
            vbox.setVisible(true);
            vboxBut.setVisible(false);
        }
    }

    @FXML
    private void probDieciocho(ActionEvent event) {
            a.getStyleClass().removeAll("botonRojo");
            b.getStyleClass().removeAll("botonRojo");
            c.getStyleClass().removeAll("botonRojo");
            d.getStyleClass().removeAll("botonRojo");
            a.getStyleClass().removeAll("botonVerde");
            b.getStyleClass().removeAll("botonVerde");
            c.getStyleClass().removeAll("botonVerde");
            d.getStyleClass().removeAll("botonVerde");
            b.setDisable(false);
            c.setDisable(false);
            a.setDisable(false);
            d.setDisable(false);
        if(problemaInt != 18){
            problemaInt = 18;
            // Agregar problema:
            vbox.setVisible(false);
            vboxBut.setVisible(true);
            List<Problem> problemas = database.getProblems();
            textoProblema.setText(problemas.get(17).getText());
            a.setText(problemas.get(17).getAnswers().get(0).getText());
            b.setText(problemas.get(17).getAnswers().get(1).getText());
            c.setText(problemas.get(17).getAnswers().get(2).getText());
            d.setText(problemas.get(17).getAnswers().get(3).getText());
        }else {
            problemaInt = -1;
            // Quitar problema:
            vbox.setVisible(true);
            vboxBut.setVisible(false);
        }
    }

    @FXML
    private void clickA(ActionEvent event) {
        List<Problem> problemas = database.getProblems();
        if(problemas.get(problemaInt-1).getAnswers().get(0).getValidity()){
            aciertos++;
            a.getStyleClass().add("botonVerde");
            b.setDisable(true);
            c.setDisable(true);
            d.setDisable(true);
            aciertosLab.setText("Aciertos: " + aciertos);
        }else{
            fallos++;
            a.getStyleClass().add("botonRojo");
            b.setDisable(true);
            c.setDisable(true);
            d.setDisable(true);
            fallosLab.setText("Fallos: " + fallos);
        }
    }

    @FXML
    private void clickB(ActionEvent event) {
        List<Problem> problemas = database.getProblems();
        if(problemas.get(problemaInt-1).getAnswers().get(1).getValidity()){
            aciertos++;
            b.getStyleClass().add("botonVerde");
            a.setDisable(true);
            c.setDisable(true);
            d.setDisable(true);
            aciertosLab.setText("Aciertos: " + aciertos);
        }else{
            fallos++;
            b.getStyleClass().add("botonRojo");
            a.setDisable(true);
            c.setDisable(true);
            d.setDisable(true);
            fallosLab.setText("Fallos: " + fallos);
        }
    }

    @FXML
    private void clickC(ActionEvent event) {
        List<Problem> problemas = database.getProblems();
        if(problemas.get(problemaInt-1).getAnswers().get(2).getValidity()){
            aciertos++;
            c.getStyleClass().add("botonVerde");
            b.setDisable(true);
            a.setDisable(true);
            d.setDisable(true);
            aciertosLab.setText("Aciertos: " + aciertos);
        }else{
            fallos++;
            c.getStyleClass().add("botonRojo");
            b.setDisable(true);
            a.setDisable(true);
            d.setDisable(true);
            fallosLab.setText("Fallos: " + fallos);
        }
    }

    @FXML
    private void clickD(ActionEvent event) {
        List<Problem> problemas = database.getProblems();
        if(problemas.get(problemaInt-1).getAnswers().get(3).getValidity()){
            aciertos++;
            d.getStyleClass().add("botonVerde");
            b.setDisable(true);
            c.setDisable(true);
            a.setDisable(true);
            aciertosLab.setText("Aciertos: " + aciertos);
        }else{
            fallos++;
            d.getStyleClass().add("botonRojo");
            b.setDisable(true);
            c.setDisable(true);
            a.setDisable(true);
            fallosLab.setText("Fallos: " + fallos);
        }
    }

}
