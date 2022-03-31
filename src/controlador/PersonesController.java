package controlador;

import java.io.IOException;

import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import model.Persona;
import model.PersonesDAO;

public class PersonesController{

	//Objecte per gestionar la persistència de les dades
	private PersonesDAO persones;

	//Elements gràfics de la UI
	private Stage ventana;
	@FXML private TextField personID;
	@FXML private TextField personName;
	@FXML private TextField personLastname;
	@FXML private TextField personDNI;
	@FXML private TextField personEmail;
	@FXML private DatePicker personDate;
	@FXML private TextArea personPhones;

	private ValidationSupport vs;

	/**
	 * Inicialitza la classe. JAVA l'executa automàticament després de carregar el fitxer fxml
	 */
	@FXML private void initialize() {
		//Obrir el fitxer de persones
		persones = new PersonesDAO();
		persones.openAll();

		//Validació dades
		//https://github.com/controlsfx/controlsfx/issues/1148
		//produeix error si no posem a les VM arguments això: --add-opens=javafx.graphics/javafx.scene=ALL-UNNAMED
		vs = new ValidationSupport();
		vs.registerValidator(personID, true, Validator.createEmptyValidator("ID obligatoria"));
		vs.registerValidator(personName, true, Validator.createEmptyValidator("Nombre obligatoria"));
		vs.registerValidator(personLastname, true, Validator.createEmptyValidator("Apellidos obligatoria"));
		vs.registerValidator(personDNI, true, Validator.createEmptyValidator("DNI obligatoria"));
        vs.registerValidator(personEmail, Validator.createRegexValidator("E-mail incorrecto", "^(.+)@(.+)$", Severity.ERROR));
        vs.registerValidator(personPhones, Validator.createEmptyValidator("Telefono tiene que se un numero"));
        vs.registerValidator(personDate, Validator.createEmptyValidator("Fecha de nacimiento obligatoria"));
		
	}

	public Stage getVentana() {
		return ventana;
	}

	public void setVentana(Stage ventana) {
		this.ventana = ventana;
	}

	@FXML private void onKeyPressedId(KeyEvent e) throws IOException {

		if (e.getCode() == KeyCode.ENTER || e.getCode() == KeyCode.TAB){
			//Comprovar si existeix la persona indicada en el control idTextField
			// Persona persona = persones.find(Integer.parseInt(idTextField.getText()));
			// if(persona != null){ 
			// 	//posar els valors per modificarlos
			// 	nomTextField.setText(persona.getNom());
			// 	cognomsTextField.setText(persona.getApellidos());
			// 	emailTextField.setText(persona.getEmail());
			// 	telefonTextField.setText(persona.getTelefon());
			// }else{ 
			// 	//nou registre
			// 	nomTextField.setText("");
			// 	cognomsTextField.setText("");
			// 	emailTextField.setText("");
			// 	telefonTextField.setText("");
			// }
		}
	}
	 
	@FXML private void onActionGuardar(ActionEvent e) throws IOException {
		//verificar si les dades són vàlides				
		// if(isDatosValidos()){
		// 	Persona persona = new Persona(Integer.parseInt(idTextField.getText()), nomTextField.getText(), cognomsTextField.getText(),
		// 			emailTextField.getText(), telefonTextField.getText());

		// 	persones.save(persona);
		// 	limpiarFormulario();
		// 	persones.showAll();
		// }
	}

	@FXML private void onActionEliminar(ActionEvent e) throws IOException {

		// if(isDatosValidos()){
		// 	if(persones.delete(Integer.parseInt(idTextField.getText()))){
		// 		limpiarFormulario();
		// 		persones.showAll();
		// 	}
		// }
	}

	@FXML private void onActionSortir(ActionEvent e) throws IOException {
		System.out.println("Salir");
		sortir();

		ventana.close();
	}

	public void sortir(){
		persones.saveAll();
		persones.showAll();
	}

	private boolean isDatosValidos() {

		//Comprovar si totes les dades són vàlides
		if (vs.isInvalid()) {
			String errors = vs.getValidationResult().getMessages().toString();
			// Mostrar finestra amb els errors
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.initOwner(ventana);
			alert.setTitle("Camps incorrectes");
			alert.setHeaderText("Corregeix els camps incorrectes");
			alert.setContentText(errors);
			alert.showAndWait();
		
			return false;
		}

		return true;

	}

	private void limpiarFormulario(){
		// idTextField.setText("");
		// nomTextField.setText("");
		// cognomsTextField.setText("");
		// emailTextField.setText("");
		// telefonTextField.setText("");
	}
}
