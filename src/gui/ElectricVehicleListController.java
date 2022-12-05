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
import model.entities.ElectricVehicle;
import model.services.CategoryService;
import model.services.ElectricVehicleService;

public class ElectricVehicleListController implements Initializable, DataChangeListener {

	private ElectricVehicleService service;

	@FXML
	private TableView<ElectricVehicle> tableViewElectricVehicle;

	@FXML
	private TableColumn<ElectricVehicle, Integer> tableColumnId;
	
	@FXML
	private TableColumn<ElectricVehicle, Integer> tableColumnTypeId;

	@FXML
	private TableColumn<ElectricVehicle, String> tableColumnModel;
	
	@FXML
	private TableColumn<ElectricVehicle, String> tableColumnBrand;
	
	@FXML
	private TableColumn<ElectricVehicle, String> tableColumnManufacturingYear;
	
	@FXML
	private TableColumn<ElectricVehicle, String> tableColumnEngineHorsePower;
	
	@FXML
	private TableColumn<ElectricVehicle, String> tableColumnBatteryCapacity;
	
	@FXML
	private TableColumn<ElectricVehicle, Integer> tableColumnNumberOfBatteries;

	@FXML
	private TableColumn<ElectricVehicle, ElectricVehicle> tableColumnEDIT;

	@FXML
	private TableColumn<ElectricVehicle, ElectricVehicle> tableColumnREMOVE;

	@FXML
	private Button btNew;

	private ObservableList<ElectricVehicle> obsList;

	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		ElectricVehicle obj = new ElectricVehicle();
		createDialogForm(obj, "/gui/ElectricVehicleForm.fxml", parentStage);
	}

	public void setElectricVehicleService(ElectricVehicleService service) {
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
		tableColumnBatteryCapacity.setCellValueFactory(new PropertyValueFactory<>("batteryCapacity"));
		tableColumnNumberOfBatteries.setCellValueFactory(new PropertyValueFactory<>("numberOfBatteries"));
		

		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewElectricVehicle.prefHeightProperty().bind(stage.heightProperty());
	}

	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		List<ElectricVehicle> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewElectricVehicle.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}

	private void createDialogForm(ElectricVehicle obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			ElectricVehicleFormController controller = loader.getController();
			controller.setElectricVehicle(obj);
			controller.setServices(new ElectricVehicleService(), new CategoryService());
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
		tableColumnEDIT.setCellFactory(param -> new TableCell<ElectricVehicle, ElectricVehicle>() {
			private final Button button = new Button("Editar");

			@Override
			protected void updateItem(ElectricVehicle obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/ElectricVehicleForm.fxml", Utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<ElectricVehicle, ElectricVehicle>() {
			private final Button button = new Button("Deletar");

			@Override
			protected void updateItem(ElectricVehicle obj, boolean empty) {
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

	private void removeEntity(ElectricVehicle obj) {
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
