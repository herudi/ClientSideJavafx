/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testclientside.controller;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javax.jnlp.BasicService;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;
import testclientside.animation.FadeInUpTransition;
import testclientside.animation.FadeOutUpTransition;
import testclientside.implement.implPerson;
import testclientside.interfaces.interPerson;
import testclientside.model.Person;
import testclientside.model.dialog;

/**
 * FXML Controller class
 *
 * @author Herudi
 */
public class layoutController implements Initializable {
    @FXML
    private Label lblStatus;
    @FXML
    private ProgressBar bar;
    @FXML
    private Hyperlink RefID;
    @FXML
    private AnchorPane paneInput;
    @FXML
    private Label lblClose, lblId;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtHoby, txtSearch;
    @FXML
    private Button btnSave;
    @FXML
    private TableView<Person> tableData;
    @FXML
    private TableColumn<Person, String> colId;
    @FXML
    private TableColumn<Person, String> colNama;
    @FXML
    private TableColumn<Person, String> colHobi;
    @FXML
    private TableColumn colAction;
    @FXML
    private Button btnAdd, btnRefresh;
    interPerson ip = new implPerson();
    Integer statusAction;
    public static Integer statusConnection;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        statusAction = 0;
        colId.setCellValueFactory(new PropertyValueFactory("id"));
        colNama.setCellValueFactory(new PropertyValueFactory("nama"));
        colHobi.setCellValueFactory(new PropertyValueFactory("hobi"));
        colAction.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Object, Boolean>,ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Object, Boolean> p) {
                return new SimpleBooleanProperty(p.getValue() != null);
            }
        });
 
        colAction.setCellFactory(new Callback<TableColumn<Object, Boolean>, TableCell<Object, Boolean>>() {
            @Override
            public TableCell<Object, Boolean> call(TableColumn<Object, Boolean> p) {
                return new ButtonCell(tableData);
            }
        });
        lblClose.setCursor(Cursor.HAND);
        selectData();
        lblClose.setOnMouseClicked((MouseEvent event) -> {
            clear();
            selectData();
            new FadeOutUpTransition(paneInput).play();
        });
    } 
    
    private void autoID(){
        if (ip.select().size()==0) {
            lblId.setText("P-0001");
        }else{
            lblId.setText(ip.maxID());
        }
    }
    
    @FXML
    private void aksiRefId(ActionEvent ev){
        try {
            autoID();
        } catch (Exception e) {
        }
    }
    
    @FXML
    private void aksiAdd(ActionEvent ev){
        if (statusConnection==0) {
            dialog.dialog(Alert.AlertType.ERROR, "Error Connection, Please Check Connection And Refresh Data");
        }else{
            Platform.runLater(() -> {
                btnSave.setText("Save");
                new FadeInUpTransition(paneInput).play();
                txtName.requestFocus();
            });
            autoID();
        }
        
    }
    
    @FXML
    private void aksiRefresh(ActionEvent ev){
        if (statusConnection==0) {
            dialog.dialog(Alert.AlertType.ERROR, "Error Connection, Please Check Connection And Refresh Data");
        }else{
            clear();
            selectData();
            autoID();
        }
    }
    
    @FXML
    private void aksiTextCari(ActionEvent ev){
        aksiSearch(ev);
    }
    
    @FXML
    private void aksiSearch(ActionEvent ev){
        Service<ObservableList<Person>> service = new Service<ObservableList<Person>>() {
            @Override
            protected Task<ObservableList<Person>> createTask() {
                return new Task<ObservableList<Person>>() {           
                    @Override
                    protected ObservableList<Person> call() throws Exception {
                        ObservableList<Person> listTask = ip.search(txtSearch.getText());
                        tableData.setItems(listTask);
                        Integer max = listTask.size();
                        updateProgress(0, max);
                        for (int k = 0; k <= max; k++) {
                            try {
                                Thread.sleep(100);
                                updateProgress(k + 1, max);
                                updateMessage("Found : " + ((k + 1)-1) + " Data");
                            } catch (InterruptedException ex) {
                                Logger.getLogger(layoutController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        return listTask;
                    }
                };
            }
        };
        lblStatus.textProperty().bind(service.messageProperty());
        bar.progressProperty().bind(service.progressProperty());
        service.start();
        
    }
    
    @FXML
    private void aksiSave(ActionEvent ev){
        if (btnSave.getText().equals("Save")) {
            if (lblId.getText().isEmpty()) {
                dialog.dialog(Alert.AlertType.INFORMATION, "Id Not Empty");
            }else if (txtName.getText().isEmpty()) {
                dialog.dialog(Alert.AlertType.INFORMATION, "Name Not Empty");
            }else if(txtHoby.getText().isEmpty()){
                dialog.dialog(Alert.AlertType.INFORMATION, "Hoby Not Empty");
            }else{
                Person p = new Person();
                p.setId(lblId.getText());
                p.setNama(txtName.getText());
                p.setHobi(txtHoby.getText());
                ip.save(p);
                ip.select();
                clear();
                dialog.dialog(Alert.AlertType.INFORMATION, "Success Save Data . . .");
                autoID();
            }
        }else{
            if (txtName.getText().isEmpty()) {
                dialog.dialog(Alert.AlertType.INFORMATION, "Name Not Empty");
            }else if(txtHoby.getText().isEmpty()){
                dialog.dialog(Alert.AlertType.INFORMATION, "Hoby Not Empty");
            }else{
                Person p = new Person();
                p.setId(lblId.getText());
                p.setNama(txtName.getText());
                p.setHobi(txtHoby.getText());
                ip.update(p);
                dialog.dialog(Alert.AlertType.INFORMATION, "Success Update Data . . .");
            }
        } 
    }
    
    @FXML
    private void klikTableData(MouseEvent es){
        if (statusAction==1) {
            try {
                Person p = tableData.getSelectionModel().getSelectedItem();
                lblId.setText(p.getId());
                txtName.setText(p.getNama());
                txtHoby.setText(p.getHobi());
            } catch (Exception e) {
            }
        }
    }
    
    private void selectData(){
        Service<ObservableList<Person>> service = new Service<ObservableList<Person>>() {
            @Override
            protected Task<ObservableList<Person>> createTask() {
                return new Task<ObservableList<Person>>() {           
                    @Override
                    protected ObservableList<Person> call() throws Exception {
                        ObservableList<Person> listTask = ip.select();
                        tableData.setItems(listTask);
                        Integer max = listTask.size();
                        updateProgress(0, max);
                        for (int k = 0; k <= max; k++) {
                            try {
                                Thread.sleep(100);
                                updateProgress(k + 1, max);
                                updateMessage("Found : " + ((k + 1)-1) + " Data");
                            } catch (InterruptedException ex) {
                                Logger.getLogger(layoutController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        return listTask;
                    }
                };
            }
        };
        lblStatus.textProperty().bind(service.messageProperty());
        bar.progressProperty().bind(service.progressProperty());
        service.start();
    }
    
    private void clear(){
        txtName.clear();
        txtHoby.clear();
        txtSearch.clear();
    }
    
    private class ButtonCell extends TableCell<Object, Boolean> {
        final Hyperlink cellButtonDelete = new Hyperlink("Delete");
        final Hyperlink cellButtonEdit = new Hyperlink("Edit");
        final HBox hb = new HBox(cellButtonDelete,cellButtonEdit);
        ButtonCell(final TableView tblView){
            hb.setSpacing(4);
            cellButtonDelete.setOnAction((ActionEvent t) -> {
                statusAction = 1;
                int row = getTableRow().getIndex();
                tableData.getSelectionModel().select(row);
                klikTableData(null);
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are You Sure Delete Record "+txtName.getText()+" ?");
                alert.initStyle(StageStyle.UTILITY);
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    Person p = new Person();
                    p.setId(lblId.getText());
                    ip.delete(p);
                    clear();
                    selectData();
                }else{
                    clear();
                    selectData();
                    autoID();
                }
                statusAction = 0;
            });
            cellButtonEdit.setOnAction((ActionEvent event) -> {
                statusAction = 1;
                int row = getTableRow().getIndex();
                tableData.getSelectionModel().select(row);
                klikTableData(null);
                btnSave.setText("Update");
                new FadeInUpTransition(paneInput).play();
                txtName.requestFocus();
                statusAction = 0;
            });
        }

        @Override
        protected void updateItem(Boolean t, boolean empty) {
            super.updateItem(t, empty);
            if(!empty){
                setGraphic(hb);
            }else{
                setGraphic(null);
            }
        }
    }

}
