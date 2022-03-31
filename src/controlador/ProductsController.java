package controlador;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.TreeSet;

import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Product;
import model.Pack;
import model.ProductsDAO;

public class ProductsController {
    
    private ProductsDAO products;

    private Stage window;
    @FXML private TextField productID;
    @FXML private TextField productName;
    @FXML private TextField productPrice;
    @FXML private TextField productStock;
    @FXML private DatePicker productInit;
    @FXML private DatePicker productEnd;
    @FXML private CheckBox productIsPack;
    @FXML private TextArea productPackID;
    @FXML private TextField productDTO;
    @FXML private Text productsTextProductsIDs;
    @FXML private Text productsTextDTO;

    private ValidationSupport vs;

    @FXML private void initialize(){
        products = new ProductsDAO();
        products.load();

        vs = new ValidationSupport();
        vs.registerValidator(productID, true, Validator.createEmptyValidator("ID del producto obligatorio"));
        vs.registerValidator(productName, true, Validator.createEmptyValidator("Nombre del producto obligatorio"));
        vs.registerValidator(productPrice, true, Validator.createEmptyValidator("Precio del producto obligatorio"));
        vs.registerValidator(productStock, true, Validator.createEmptyValidator("Stock del producto obligatorio"));
        vs.registerValidator(productInit, true, Validator.createEmptyValidator("Fecha de Inicio Catalogo del producto obligatorio"));
        vs.registerValidator(productEnd, true, Validator.createEmptyValidator("Fecha de Fin Catalogo del producto obligatorio"));  
    }

    @FXML private void onKeyPressedId(KeyEvent e) throws IOException{
        
        if(e.getCode() == KeyCode.ENTER || e.getCode() == KeyCode.TAB){
            Object p = products.search(Integer.parseInt(productID.getText()));
            if(p!=null){
                if(p instanceof Pack){
                    showPack();
                    Pack pack = (Pack)p;
                    productName.setText(pack.getName());
                    productPrice.setText(String.valueOf(pack.getPrice()));
                    productStock.setText(String.valueOf(pack.getStock()));
                    productInit.setValue(pack.getCatalogStartDate());
                    productEnd.setValue(pack.getCatalogFinishDate());
                    productIsPack.setSelected(true);
                    String productListString = packProductListToString(pack);
                    productPackID.setText(productListString);
                    System.out.println(pack.getProductList());
                    productDTO.setText(String.valueOf(pack.getDiscount()));
                } else {
                    Product product = (Product)p;
                    productName.setText(product.getName());
                    productPrice.setText(String.valueOf(product.getPrice()));
                    productStock.setText(String.valueOf(product.getStock()));
                    productInit.setValue(product.getCatalogStartDate());
                    productEnd.setValue(product.getCatalogFinishDate());
                }
                productStock.setDisable(true);
            }
            

        }
    }

    @FXML private void onActionGuardar(ActionEvent e) throws IOException{
        if(isDataValid()){
            if(productIsPack.isSelected()){
                TreeSet<Product> productList = getProductList();
                Pack p = new Pack(
                    Integer.parseInt(productID.getText()), 
                    productName.getText(), 
                    Integer.parseInt(productPrice.getText()), 
                    Integer.parseInt(productStock.getText()), 
                    productList, 
                    Integer.parseInt(productDTO.getText()), 
                    productInit.getValue(), 
                    productEnd.getValue()
                );
                if(products.search(p.getID())!=null){
                    System.out.println("Modificar");
                    products.modify(p.getID(), p);
                    productStock.setDisable(false);
                    hidePack();
                } else {
                    System.out.println("No modifcar");
                    products.add(p);
                    hidePack();
                }
                
                clearForm();
                System.out.println(products.getMap()); 
            } else {
                Product p = new Product(
                    Integer.parseInt(productID.getText()), 
                    productName.getText(), 
                    Integer.parseInt(productPrice.getText()), 
                    Integer.parseInt(productStock.getText()), 
                    productInit.getValue(), 
                    productEnd.getValue()
                );
                if(products.search(p.getID())!=null){
                    System.out.println("Modificar");
                    products.modify(p.getID(), p);
                    productStock.setDisable(false);
                } else {
                    System.out.println("No modifcar");
                    products.add(p);
                }
                clearForm();
                System.out.println(products.getMap()); 
            }
        }
    }

    @FXML private void onActionEliminar(ActionEvent e) throws IOException {

		if(isDataValid()){
			if(products.delete(Integer.parseInt(productID.getText()))!=null){
				clearForm();
				System.out.println(products.getMap());
			}
		}
	}

    @FXML private void onActionExit(ActionEvent e) throws IOException {

		exit();

		window.close();
	}
    
    @FXML private void onActionIsPack(ActionEvent e) throws IOException{
        if(productIsPack.isSelected()){
            showPack();
        } else {
            hidePack();
        }
    }

    private void showPack(){
        productPackID.setVisible(true);
        productsTextProductsIDs.setVisible(true);
        productsTextDTO.setVisible(true);
        productDTO.setVisible(true);

        vs.registerValidator(productPackID, true, Validator.createEmptyValidator("IDs de productos del pack obligatorio"));
        vs.registerValidator(productDTO, true, Validator.createEmptyValidator("Descuento del Pack obligatorio"));
    }

    private void hidePack(){
        productPackID.setVisible(false);
        productsTextProductsIDs.setVisible(false);
        productsTextDTO.setVisible(false);
        productDTO.setVisible(false);
        vs = new ValidationSupport();
        vs.registerValidator(productID, true, Validator.createEmptyValidator("ID del producto obligatorio"));
        vs.registerValidator(productName, true, Validator.createEmptyValidator("Nombre del producto obligatorio"));
        vs.registerValidator(productPrice, true, Validator.createEmptyValidator("Precio del producto obligatorio"));
        vs.registerValidator(productStock, true, Validator.createEmptyValidator("Stock del producto obligatorio"));
        vs.registerValidator(productInit, true, Validator.createEmptyValidator("Fecha de Inicio Catalogo del producto obligatorio"));
        vs.registerValidator(productEnd, true, Validator.createEmptyValidator("Fecha de Fin Catalogo del producto obligatorio")); 
    }

    private TreeSet<Product> getProductList(){
        TreeSet<Product> productList = new TreeSet<Product>();
        String[] ids = productPackID.getText().split(","); 
        for(int i=0;i<ids.length;i++){
            Product temp = products.search(Integer.parseInt(ids[i].trim()));
            if(temp!=null){
                productList.add(temp);
            } else{
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.initOwner(window);
                alert.setTitle("Producto Inexistente");
                alert.setHeaderText("Uno de los productos introducidos no existe");
                alert.setContentText("El producto: "+i+" no existe.");
                alert.showAndWait();
                return null;
            }
        }
        return productList;
    }

    private String packProductListToString(Pack pack){
        String productListString = "";
        TreeSet<Product> packProductList = pack.getProductList();
        for(Product temp : packProductList){
            productListString += String.valueOf(temp.getID())+",";
        }
        return productListString;
    }

    public Stage getWindow(){
        return window;
    }

    public void setWindow(Stage window){
        this.window = window;
    }

    public void exit(){
        products.save();
    }

    private void clearForm(){
        productID.setText("");
        productName.setText("");
        productPrice.setText("");
        productStock.setText("");
        productInit.setValue(null);
        productEnd.setValue(null);
        productIsPack.setSelected(false);
        productPackID.setText("");
        productDTO.setText("");
    }

    private boolean isDataValid(){
        if(vs.isInvalid()){
            String errors = vs.getValidationResult().getMessages().toString();
			// Mostrar finestra amb els errors
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.initOwner(window);
			alert.setTitle("Campos Incorrectos");
			alert.setHeaderText("Arregla los campos incorrectos");
			alert.setContentText(errors);
			alert.showAndWait();

            return false;
        }
        return true;
    }
    



}
