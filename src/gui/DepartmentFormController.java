package gui;

import java.net.URL;
import java.util.ResourceBundle;

import gui.util.Constraints;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;

public class DepartmentFormController implements Initializable{
	
	private Department entityDepartment;

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
	public void onButtonSaveAction() {
		System.out.println("SALVOU");
	}
	
	public void setDepartment(Department entity) {
		this.entityDepartment = entity;
	}
	
	@FXML
	public void onButtonCancelAction() {
		System.out.println("CANCELOU");
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
