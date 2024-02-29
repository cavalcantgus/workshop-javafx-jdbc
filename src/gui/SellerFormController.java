package gui;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.SellerService;

public class SellerFormController implements Initializable{
	
	private Seller entitySeller;
	
	private SellerService departmentService;
	
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private TextField textFieldSellerId;
	
	@FXML
	private TextField textFieldSellerName;
	
	@FXML
	private TextField textFieldSellerEmail;
	
	@FXML
	private DatePicker datePickerSellerBirthDate;
	
	@FXML
	private TextField textFieldSellerBaseSalary;
	
	@FXML
	private Label labelErrorName;
	
	@FXML
	private Label labelErrorEmail;
	
	@FXML
	private Label labelErrorBirthDate;
	
	@FXML
	private Label labelErrorBaseSalary;
	
	@FXML
	private Button buttonSave;
	
	@FXML
	private Button buttonCancel;
	
	@FXML
	public void onButtonSaveAction(ActionEvent event) {
		if (entitySeller == null) {
			throw new IllegalStateException("Entity was null");
		}
		if(departmentService == null) {
			throw new IllegalStateException("Service was null");
		}
		try {
			entitySeller = getFormData();
			departmentService.saveOrUpdate(entitySeller);
			notifyDataChangeListeners();
			Utils.currentStage(event).close();
		}
		catch(ValidationException e) {
			setErrorMessages(e.getErrors());
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

	private Seller getFormData() {
		Seller obj = new Seller();
		
		ValidationException exception = new ValidationException("Validation error");
		
		obj.setId(Utils.tryParseToInt(textFieldSellerId.getText()));
		
		if(textFieldSellerName.getText() == null || textFieldSellerName.getText().trim().equals("")){
			exception.addError("name", "Field can't be empty");
		}
		obj.setName(textFieldSellerName.getText());
		
		if(exception.getErrors().size() > 0) {
			throw exception;
		}
		return obj;
	}

	public void setSeller(Seller entity) {
		this.entitySeller = entity;
	}
	
	public void setSellerService(SellerService service) {
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
		Constraints.setTextFieldInteger(textFieldSellerId);
		Constraints.setTextFieldMaxLength(textFieldSellerName, 70);
		Constraints.setTextFieldDouble(textFieldSellerBaseSalary);
		Constraints.setTextFieldMaxLength(textFieldSellerEmail, 60);
		Utils.formatDatePicker(datePickerSellerBirthDate, "dd/MM/yyyy");

	}
	
	public void updateFormData() {
		if(entitySeller == null) {
			throw new IllegalStateException("Entity was null");
		}
		
		textFieldSellerId.setText(String.valueOf(entitySeller.getId()));
		textFieldSellerName.setText(entitySeller.getName());
		textFieldSellerEmail.setText(entitySeller.getEmail());
		Locale.setDefault(Locale.US);
		textFieldSellerBaseSalary.setText(String.format("%.2f", entitySeller.getBaseSalary()));
		if(entitySeller.getBirthDate() != null) {
			datePickerSellerBirthDate.setValue(LocalDate.ofInstant(entitySeller.getBirthDate().toInstant(), ZoneId.systemDefault()));
		}
	}
	
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		
		if(fields.contains("name")) {
			labelErrorName.setText(errors.get("name"));
		}
	}
}
