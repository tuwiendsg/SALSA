package at.ac.tuwien.dsg.cloud.salsa.informationmanagement.clientgui.globalmanager;

import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.client.QueryManager;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.client.cache.CacheDelises;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.client.cache.CacheGateway;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.clientgui.localmanager.FXMLLocalController;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.clientgui.localmanager.ControlPointTableModel;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.clientgui.localmanager.DataPointTableModel;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.clientgui.UserSettings;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.messagePayloads.DeliseMeta;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.protocol.InfoSourceSettings;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.SoftwareDefinedGateway;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualNetworkResource.VNF;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.ResizeFeatures;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Callback;
import org.eclipse.paho.client.mqttv3.internal.ClientDefaults;
import sun.awt.X11.XConstants;

public class FXMLController implements Initializable {

    @FXML
    private Label label;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private TextFlow textFlow;

    @FXML
    private void handleButtonAction_GatewayManager(ActionEvent event) {
        System.out.println("You clicked me the gateway manager!");
        Button refreshBtn = new Button("Update list");
        final Label infoLabel = new Label("The list is being loaded...");
        mainPane.getChildren().add(infoLabel);
        AnchorPane.setTopAnchor(infoLabel, 60.0);
        AnchorPane.setLeftAnchor(infoLabel, 10.0);

        refreshBtn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("Reload the list");
                infoLabel.setText("Querying information ... ");
                TableView table = getDeliseMetaTable(getDeliseMetaQuery());
                mainPane.getChildren().addAll(table);

                anchorNodeIntoPane(table, 100.0, 0.0, 0.0, 0.0);

                infoLabel.setText("The gateway list is updated at: " + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()));
            }
        });
        mainPane.getChildren().add(refreshBtn);
        AnchorPane.setTopAnchor(refreshBtn, 10.0);
        AnchorPane.setLeftAnchor(refreshBtn, 10.0);

        // Refresh button
        TableView table = getDeliseMetaTable(getDeliseMetaFromCache());
        mainPane.getChildren().addAll(table);
        infoLabel.setText("The gateway list is loaded from cache !");

        anchorNodeIntoPane(table, 100.0, 0.0, 0.0, 0.0);

//        AnchorPane.setTopAnchor(table, 100.0);
//        AnchorPane.setLeftAnchor(table, 0.0);
//        AnchorPane.setRightAnchor(table, 0.0);
//        AnchorPane.setBottomAnchor(table, 0.0);
    }

    private void anchorNodeIntoPane(Node obj, double top, double left, double right, double bottom) {
        AnchorPane.setTopAnchor(obj, top);
        AnchorPane.setLeftAnchor(obj, left);
        AnchorPane.setRightAnchor(obj, right);
        AnchorPane.setBottomAnchor(obj, bottom);
    }

    private List<DeliseMeta> getDeliseMetaFromCache() {
        CacheDelises cache = new CacheDelises();
        List<DeliseMeta> meta = cache.loadDelisesCache();
//        System.out.println("Load delises from cache: " + meta.size() + " items");
        return meta;
    }

    private List<DeliseMeta> getDeliseMetaQuery() {
        QueryManager query = new QueryManager(UserSettings.getUserName(), UserSettings.getBroker(), UserSettings.getBrokerType());
        List<DeliseMeta> meta = query.synDelise(3000);
        System.out.println("Query delises: " + meta.size() + " items");
        return meta;
    }

    private TableView getDeliseMetaTable(List<DeliseMeta> metas) {
        TableView table = new TableView();

        TableColumn uuidCol = new TableColumn("Gateway UUID");
        uuidCol.setCellValueFactory(new PropertyValueFactory<DeliseMetaModel, String>("uuid"));

        TableColumn ipCol = new TableColumn("IP");
        ipCol.setCellValueFactory(new PropertyValueFactory<DeliseMetaModel, String>("ip"));

        TableColumn adaptorCol = new TableColumn("Adaptor");
        adaptorCol.setCellValueFactory(new PropertyValueFactory<DeliseMetaModel, String>("adaptor"));

        TableColumn dataSourceTypeCol = new TableColumn("Source type");
        dataSourceTypeCol.setCellValueFactory(new PropertyValueFactory<DeliseMetaModel, String>("sourceType"));

        TableColumn dataSourceCol = new TableColumn("Information source");
        dataSourceCol.setCellValueFactory(new PropertyValueFactory<DeliseMetaModel, String>("source"));

        TableColumn settingsCol = new TableColumn("Settings");
        settingsCol.getColumns().addAll(adaptorCol, dataSourceTypeCol, dataSourceCol);

        table.getColumns().addAll(uuidCol, ipCol, settingsCol);

        // add data
        ObservableList<DeliseMetaModel> data = FXCollections.observableArrayList();
        if (metas != null) {
            for (DeliseMeta meta : metas) {
                InfoSourceSettings settings = InfoSourceSettings.fromJson(meta.getSettings());
                for (InfoSourceSettings.InfoSource source : settings.getSource()) {
                    String transClass = source.getTransformerClass();
                    System.out.println("data add: " + meta.getUuid());
                    data.add(new DeliseMetaModel(meta.getUuid(), meta.getIp(), transClass.substring(transClass.lastIndexOf(".") + 1), source.getType().toString(), source.getEndpoint(), source.getSettings()));
                }
            }
        }
        // associate data with table        
        table.setItems(data);
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
//        table.setColumnResizePolicy(new Callback<TableView.ResizeFeatures, Boolean>() {
//            @Override
//            public Boolean call(ResizeFeatures p) {
//                return true;
//            }
//        });

        // event row select        
        table.setRowFactory(new Callback<TableView<DeliseMetaModel>, TableRow<DeliseMetaModel>>() {
            @Override
            public TableRow<DeliseMetaModel> call(final TableView<DeliseMetaModel> param) {
                final TableRow<DeliseMetaModel> row = new TableRow<>();
                row.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        DeliseMetaModel meta = param.getItems().get(row.getIndex());
                        System.out.println("Click uuid: " + meta.uuidProperty().get());
                        showLocalManager(meta);
                    }
                });
                return row;
            }
        }
        );
        return table;
    }

    Scene secondScene;
    Stage secondStage = new Stage();

    private void showLocalManager(DeliseMetaModel tableData) {
        System.out.println("Start to open local manager windows. UUID: " + tableData.uuidProperty().get());
        try {
            FXMLLocalController controller = new FXMLLocalController();
            System.out.println("debug 1");
            controller.setMetadata(tableData);
            System.out.println("debug 2");

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/LocalScene.fxml"));
            System.out.println("debug 3");
            fxmlLoader.setController(controller);
            System.out.println("debug 4");
            Parent root = fxmlLoader.load();
            System.out.println("Load controller");

            secondScene = new Scene(root);
            secondStage.setTitle("Resource " + tableData.ipProperty().get());
            secondStage.setScene(secondScene);
            secondStage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
}
