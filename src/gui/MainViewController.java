package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import model.services.CategoryService;
import model.services.ElectricVehicleService;
import model.services.GasolineVehicleService;

public class MainViewController implements Initializable{
	
	@FXML
	private MenuItem menuItemElectricVehicle;
	
	@FXML
	private MenuItem menuItemGasolineVehicle;
	
	@FXML
	private MenuItem menuItemCategory;
	
	@FXML
	private MenuItem menuItemDevelopers;
	
	@FXML
	public void onMenuItemElectricVehicleAction() {
		loadView("/gui/ElectricVehicleList.fxml", (ElectricVehicleListController controller) -> {
			controller.setElectricVehicleService(new ElectricVehicleService());
			controller.updateTableView();
		});
	}
	
	@FXML
	public void onMenuItemGasolineVehicleAction() {
		loadView("/gui/GasolineVehicleList.fxml", (GasolineVehicleListController controller) -> {
			controller.setGasolineVehicleService(new GasolineVehicleService());
			controller.updateTableView();
		});
	}
	
	@FXML
	public void onMenuItemCategoryAction() {
		loadView("/gui/CategoryList.fxml", (CategoryListController controller) -> {
			controller.setCategoryService(new CategoryService());
			controller.updateTableView();
		});
	}
	
	@FXML
	public void onMenuItemDevelopersAction() {
		loadView("/gui/Developers.fxml", x -> {});
	}

	@Override
	public void initialize(URL uri, ResourceBundle rb) {
	}
	
	private synchronized <T> void loadView(String absoluteName, Consumer<T> initializingAction) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVbox = loader.load();
			
			Scene mainScene = Main.getMainScene();
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();
			
			Node mainMenu = mainVBox.getChildren().get(0);
			mainVBox.getChildren().clear();
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(newVbox.getChildren());
			
			T controller = loader.getController();
			initializingAction.accept(controller);
			
		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

}