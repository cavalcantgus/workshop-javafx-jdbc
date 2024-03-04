package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerFormController implements Initializable {

	private Seller entitySeller;

	private SellerService sellerService;

	private DepartmentService departmentService;

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
	private ComboBox<Department> comboBoxDepartment;

	@FXML
	private Label labelErrorName;

	@FXML
	private Label labelErrorEmail;

	@FXML
	private Label labelErrorBirthDate;

	@FXML
	private Label labelErrorBaseSalary;
	
	@FXML
	private Label labelErrorDepartment;

	@FXML
	private Button buttonSave;

	@FXML
	private Button buttonCancel;

	private ObservableList<Department> obsList;

	public void setSeller(Seller entity) {
		this.entitySeller = entity;
	}

	public void setServices(SellerService sellerService, DepartmentService departmentService) {
		this.sellerService = sellerService;
		this.departmentService = departmentService;
	}

	@FXML
	public void onButtonSaveAction(ActionEvent event) {
		if (entitySeller == null) {
			throw new IllegalStateException("Entity was null");
		}
		if (sellerService == null) {
			throw new IllegalStateException("Service was null");
		}
		try {
			entitySeller = getFormData();
			sellerService.saveOrUpdate(entitySeller);
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

	private Seller getFormData() {
		Seller obj = new Seller();

		ValidationException exception = new ValidationException("Validation error");

		obj.setId(Utils.tryParseToInt(textFieldSellerId.getText()));

		if (textFieldSellerName.getText() == null || textFieldSellerName.getText().trim().equals("")) {
			exception.addError("name", "Field can't be empty");
		}
	
		obj.setName(textFieldSellerName.getText());
		
		if (textFieldSellerEmail.getText() == null || textFieldSellerEmail.getText().trim().equals("")) {
			exception.addError("email", "Field can't be empty");
		}
		obj.setEmail(textFieldSellerEmail.getText());
		
		if(datePickerSellerBirthDate.getValue() == null) {
			exception.addError("birthDate", "Field can't be empty");
		}
		else {
			Instant instant = Instant.from(datePickerSellerBirthDate.getValue().atStartOfDay(ZoneId.systemDefault()));
			obj.setBirthDate(Date.from(instant));
		}
		
		if (textFieldSellerBaseSalary.getText() == null || textFieldSellerBaseSalary.getText().trim().equals("")) {
			exception.addError("baseSalary", "Field can't be empty");
		}
		obj.setBaseSalary(Utils.tryParseToDouble(textFieldSellerBaseSalary.getText()));
		
		if(comboBoxDepartment.getValue() == null) {
			exception.addError("department", "Field can't be empty");
		}
		obj.setDepartment(comboBoxDepartment.getValue());

		if (exception.getErrors().size() > 0) {
			throw exception;
		}
		return obj;
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
		initializeComboBoxDepartment();

	}

	public void updateFormData() {
		if (entitySeller == null) {
			throw new IllegalStateException("Entity was null");
		}

		textFieldSellerId.setText(String.valueOf(entitySeller.getId()));
		textFieldSellerName.setText(entitySeller.getName());
		textFieldSellerEmail.setText(entitySeller.getEmail());
		Locale.setDefault(Locale.US);
		textFieldSellerBaseSalary.setText(String.format("%.2f", entitySeller.getBaseSalary()));
		if (entitySeller.getBirthDate() != null) {
			datePickerSellerBirthDate
					.setValue(LocalDate.ofInstant(entitySeller.getBirthDate().toInstant(), ZoneId.systemDefault()));
		}
		
		if(entitySeller.getDepartment() == null) {
			comboBoxDepartment.getSelectionModel().selectFirst();
		}
		comboBoxDepartment.setValue(entitySeller.getDepartment());
	}

	public void loadAssociatedObjects() {
		if (departmentService == null) {
			throw new IllegalStateException("Service was null");
		}
		List<Department> list = departmentService.findAll();
		obsList = FXCollections.observableArrayList(list);
		comboBoxDepartment.setItems(obsList);
	}

	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		
		labelErrorName.setText(fields.contains("name") ? errors.get("name"):"");
		labelErrorEmail.setText(fields.contains("email") ? errors.get("email"):"");
		labelErrorBaseSalary.setText(fields.contains("baseSalary") ? errors.get("baseSalary"):"");
		labelErrorBirthDate.setText(fields.contains("birthDate") ? errors.get("birthDate"):"");
		labelErrorBirthDate.setText(fields.contains("department") ? errors.get("department"):"");
	}

	private void initializeComboBoxDepartment() {
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
			@Override
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		comboBoxDepartment.setCellFactory(factory);
		comboBoxDepartment.setButtonCell(factory.call(null));
	}
}
