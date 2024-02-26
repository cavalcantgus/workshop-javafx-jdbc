package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable{
	
	private Department entityDepartment;
	
	private DepartmentService departmentService;
	
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private TextField textFieldDepartmentId;
	
	@FXML
	private TextField textFieldDepartmentName;
	
	@FXML
	private Label labelErrorName;
	
	@FXML
	private Button buttonSave;
	
	@FXML
	private Button buttonCancel;
	
	@FXML
	public void onButtonSaveAction(ActionEvent event) {
		if (entityDepartment == null) {
			throw new IllegalStateException("Entity was null");
		}
		if(departmentService == null) {
			throw new IllegalStateException("Service was null");
		}
		try {
			entityDepartment = getFormData();
			departmentService.saveOrUpdate(entityDepartment);
			notifyDataChangeListeners();
			Utils.currentStage(event).close();
		}
		catch(DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
		
	}
	
	private void notifyDataChangeListeners() {
		for(DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
	}

	private Department getFormData() {
		Department obj = new Department();
		obj.setId(Utils.tryParseToInt(textFieldDepartmentId.getText()));
		obj.setName(textFieldDepartmentName.getText());
		return obj;
	}

	public void setDepartment(Department entity) {
		this.entityDepartment = entity;
	}
	
	public void setDepartmentService(DepartmentService service) {
		this.departmentService = service;
	}
	
	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}
	
	@FXML
	public void onButtonCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(textFieldDepartmentId);
		Constraints.setTextFieldMaxLength(textFieldDepartmentName, 30);
	}
	
	public void updateFormData() {
		if(entityDepartment == null) {
			throw new IllegalStateException("Entity was null");
		}
		
		textFieldDepartmentId.setText(String.valueOf(entityDepartment.getId()));
		textFieldDepartmentName.setText(entityDepartment.getName());
	}
}
