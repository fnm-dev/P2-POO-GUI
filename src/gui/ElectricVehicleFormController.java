package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import javafx.scene.control.Alert.AlertType;
import model.entities.Category;
import model.entities.ElectricVehicle;
import model.exceptions.ValidationException;
import model.services.CategoryService;
import model.services.ElectricVehicleService;

public class ElectricVehicleFormController implements Initializable {

	private ElectricVehicle entity;

	private ElectricVehicleService service;

	private CategoryService categoryService;

	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtTypeId;

	@FXML
	private TextField txtModel;

	@FXML
	private TextField txtBrand;

	@FXML
	private TextField txtManufacturingYear;
	
	@FXML
	private TextField txtEngineHorsePower;

	@FXML
	private ComboBox<Category> comboBoxCategory;
	
	@FXML
	private TextField txtBatteryCapacity;
	
	@FXML
	private TextField txtNumberOfBatteries;

	@FXML
	private Label labelErrorModel;

	@FXML
	private Label labelErrorBrand;

	@FXML
	private Label labelErrorManufacturingYear;

	@FXML
	private Label labelErrorEngineHorsePower;
	
	@FXML
	private Label labelErrorBatteryCapacity;
	
	@FXML
	private Label labelErrorNumberOfBatteries;

	@FXML
	private Button btSave;

	@FXML
	private Button btCancel;

	private ObservableList<Category> obsList;

	public void setElectricVehicle(ElectricVehicle entity) {
		this.entity = entity;
	}

	public void setServices(ElectricVehicleService service, CategoryService CategoryService) {
		this.service = service;
		this.categoryService = CategoryService;
	}

	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangeListeners();
			Utils.currentStage(event).close();
		} catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		} catch (DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
	}

	private ElectricVehicle getFormData() {
		ElectricVehicle obj = new ElectricVehicle();

		ValidationException exception = new ValidationException("Validation error");
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		obj.setTypeId(1);

		if (txtModel.getText() == null || txtModel.getText().trim().equals("")) {
			exception.addError("model", "Campo não pode ser vazio!");
		}
		obj.setModel(txtModel.getText());
		
		if (txtBrand.getText() == null || txtBrand.getText().trim().equals("")) {
			exception.addError("brand", "Campo não pode ser vazio!");
		}
		obj.setBrand(txtBrand.getText());
		
		if (txtManufacturingYear.getText() == null || txtManufacturingYear.getText().trim().equals("")) {
			exception.addError("manufacturingYear", "Campo não pode ser vazio!");
		}
		obj.setManufacturingYear(txtManufacturingYear.getText());
		
		if (txtEngineHorsePower.getText() == null || txtEngineHorsePower.getText().trim().equals("")) {
			exception.addError("engineHorsePower", "Campo não pode ser vazio!");
		}
		obj.setEngineHorsePower(txtEngineHorsePower.getText());
		
		obj.setCategory(comboBoxCategory.getValue());	
		
		if (txtBatteryCapacity.getText() == null || txtBatteryCapacity.getText().trim().equals("")) {
			exception.addError("batteryCapacity", "Campo não pode ser vazio!");
		}
		obj.setBatteryCapacity(txtBatteryCapacity.getText());
		
		if(txtNumberOfBatteries.getText() == null || txtNumberOfBatteries.getText().trim().equals("")) {
			exception.addError("numberOfBatteries", "Campo não pode ser vazio!");
		}
		obj.setNumberOfBatteries(Utils.tryParseToInt(txtNumberOfBatteries.getText()));

		if (exception.getErrors().size() > 0) {
			throw exception;
		}

		return obj;
	}

	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldInteger(txtTypeId);
		Constraints.setTextFieldMaxLength(txtModel, 50);
		Constraints.setTextFieldMaxLength(txtBrand, 50);
		Constraints.setTextFieldMaxLength(txtManufacturingYear, 4);
		Constraints.setTextFieldMaxLength(txtEngineHorsePower, 5);
		Constraints.setTextFieldMaxLength(txtBatteryCapacity, 10);
		Constraints.setTextFieldInteger(txtNumberOfBatteries);
		
		initializeComboBoxCategory();
	}

	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtTypeId.setText(String.valueOf(entity.getTypeId()));
		txtModel.setText(entity.getModel());
		txtBrand.setText(entity.getBrand());
		txtManufacturingYear.setText(entity.getManufacturingYear());
		txtEngineHorsePower.setText(entity.getEngineHorsePower());
		if(entity.getCategory() == null) {
			comboBoxCategory.getSelectionModel().selectFirst();
		}else {
			comboBoxCategory.setValue(entity.getCategory());
		}
		txtBatteryCapacity.setText(entity.getBatteryCapacity());
		txtNumberOfBatteries.setText(String.valueOf(entity.getNumberOfBatteries()));
	}

	public void loadAssociatedObjects() {
		if (categoryService == null) {
			throw new IllegalStateException("CategoryService was null");
		}
		List<Category> list = categoryService.findAll();
		obsList = FXCollections.observableArrayList(list);
		comboBoxCategory.setItems(obsList);
	}

	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		
		labelErrorModel.setText(fields.contains("model") ? errors.get("model") : "");
		labelErrorBrand.setText(fields.contains("brand") ? errors.get("brand") : "");
		labelErrorManufacturingYear.setText(fields.contains("manufacturingYear") ? errors.get("manufacturingYear") : "");
		labelErrorEngineHorsePower.setText(fields.contains("engineHorsePower") ? errors.get("engineHorsePower") : "");
		labelErrorBatteryCapacity.setText(fields.contains("batteryCapacity") ? errors.get("batteryCapacity") : "");
		labelErrorNumberOfBatteries.setText(fields.contains("numberOfBatteries") ? errors.get("numberOfBatteries") : "");
	}

	private void initializeComboBoxCategory() {
		Callback<ListView<Category>, ListCell<Category>> factory = lv -> new ListCell<Category>() {
			@Override
			protected void updateItem(Category item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		comboBoxCategory.setCellFactory(factory);
		comboBoxCategory.setButtonCell(factory.call(null));
	}

}
