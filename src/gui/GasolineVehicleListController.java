package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.GasolineVehicle;
import model.services.CategoryService;
import model.services.GasolineVehicleService;

public class GasolineVehicleListController implements Initializable, DataChangeListener {

	private GasolineVehicleService service;

	@FXML
	private TableView<GasolineVehicle> tableViewGasolineVehicle;

	@FXML
	private TableColumn<GasolineVehicle, Integer> tableColumnId;
	
	@FXML
	private TableColumn<GasolineVehicle, Integer> tableColumnTypeId;

	@FXML
	private TableColumn<GasolineVehicle, String> tableColumnModel;
	
	@FXML
	private TableColumn<GasolineVehicle, String> tableColumnBrand;
	
	@FXML
	private TableColumn<GasolineVehicle, String> tableColumnManufacturingYear;
	
	@FXML
	private TableColumn<GasolineVehicle, String> tableColumnEngineHorsePower;
	
	@FXML
	private TableColumn<GasolineVehicle, String> tableColumnFuelTankCapacity;
	
	@FXML
	private TableColumn<GasolineVehicle, Integer> tableColumnNumberOfEngineCylinders;

	@FXML
	private TableColumn<GasolineVehicle, GasolineVehicle> tableColumnEDIT;

	@FXML
	private TableColumn<GasolineVehicle, GasolineVehicle> tableColumnREMOVE;

	@FXML
	private Button btNew;

	private ObservableList<GasolineVehicle> obsList;

	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		GasolineVehicle obj = new GasolineVehicle();
		createDialogForm(obj, "/gui/GasolineVehicleForm.fxml", parentStage);
	}

	public void setGasolineVehicleService(GasolineVehicleService service) {
		this.service = service;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnTypeId.setCellValueFactory(new PropertyValueFactory<>("typeId"));
		tableColumnModel.setCellValueFactory(new PropertyValueFactory<>("model"));
		tableColumnBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
		tableColumnManufacturingYear.setCellValueFactory(new PropertyValueFactory<>("manufacturingYear"));
		tableColumnEngineHorsePower.setCellValueFactory(new PropertyValueFactory<>("engineHorsePower"));
		tableColumnFuelTankCapacity.setCellValueFactory(new PropertyValueFactory<>("fuelTankCapacity"));
		tableColumnNumberOfEngineCylinders.setCellValueFactory(new PropertyValueFactory<>("numberOfEngineCylinders"));

		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewGasolineVehicle.prefHeightProperty().bind(stage.heightProperty());
	}

	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		List<GasolineVehicle> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewGasolineVehicle.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}

	private void createDialogForm(GasolineVehicle obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			GasolineVehicleFormController controller = loader.getController();
			controller.setGasolineVehicle(obj);
			controller.setServices(new GasolineVehicleService(), new CategoryService());
			controller.loadAssociatedObjects();
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Insira os dados do veículo");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDataChanged() {
		updateTableView();
	}

	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<GasolineVehicle, GasolineVehicle>() {
			private final Button button = new Button("Editar");

			@Override
			protected void updateItem(GasolineVehicle obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/GasolineVehicleForm.fxml", Utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<GasolineVehicle, GasolineVehicle>() {
			private final Button button = new Button("Deletar");

			@Override
			protected void updateItem(GasolineVehicle obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}

	private void removeEntity(GasolineVehicle obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmação", "Você tem certeza em deletar?");
		
		if(result.get() == ButtonType.OK) {
			if(service == null) {
				throw new IllegalStateException("Service was null");
			}
			try {
				service.remove(obj);
				updateTableView();
			} catch (DbIntegrityException e) {
				Alerts.showAlert("Error removing object", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}

}
