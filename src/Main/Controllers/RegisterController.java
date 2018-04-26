package Main.Controllers;

import Main.ErrorAndInfo.AlertBox;
import Main.JdbcConnection.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;

public class RegisterController {

    ObservableList<String> StoreType = FXCollections.observableArrayList("Wholesaler", "Retailer");

    @FXML
    private TextField owner_name, owner_phone, owner_address, owner_pan, owner_email, store_name,
            store_phone, store_address, store_gst, license_number, register_username;

    @FXML
    private DatePicker license_validity;

    @FXML
    private PasswordField register_password;

    @FXML
    private ComboBox store_type;

    @FXML
    private Button cancel_button;

    public void initialize() {
        register_username.setDisable(true);
        store_type.setItems(StoreType);
        store_type.getSelectionModel().selectFirst();
    }

    public void onRegister(ActionEvent actionEvent) {
        String ownerName = owner_name.getText();
        String ownerPhone = owner_phone.getText();
        String ownerAddress = owner_address.getText();
        String ownerPan = owner_pan.getText();
        String ownerEmail = owner_email.getText();
        String storeName = store_name.getText();
        String storePhone = store_phone.getText();
        String storeAddress = store_address.getText();
        String storeGst = store_gst.getText();
        String licenseNumber = license_number.getText();
        String loginUsername = licenseNumber;
        String loginPassword = register_password.getText();

        Node source = (Node) actionEvent.getSource();
        Stage currentStage = (Stage) source.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../Resources/Layouts/alert_stage.fxml"));

        String sqlQuery = null;
        int increment = 0;
        String regexPhone = "^[0-9]*$";
        String regexName = "^[A-Za-z]+$";

        int user_type_id;
        if (store_type.getSelectionModel().getSelectedItem() == "Wholesaler") {
            user_type_id = 1;
        } else
            user_type_id = 2;

        if (ownerName.equals("") || ownerAddress.equals("") || ownerPan.equals("") || ownerPhone.equals("") || storeName.equals("") || storeAddress.equals("")
                || storeGst.equals("") || storePhone.equals("") || licenseNumber.equals("") || license_validity.getValue() == null || loginPassword.equals("")) {
            new AlertBox(currentStage, fxmlLoader, false, "Fill in the missing fields !!");
            if (ownerName.equals(""))
                owner_name.setStyle("-fx-border-color: red ; -fx-border-width: 3px ;");
            if (ownerAddress.equals(""))
                owner_address.setStyle("-fx-border-color: red ; -fx-border-width: 3px ;");
            if (ownerPan.equals(""))
                owner_pan.setStyle("-fx-border-color: red ; -fx-border-width: 3px ;");
            if (ownerPhone.equals(""))
                owner_phone.setStyle("-fx-border-color: red ; -fx-border-width: 3px ;");
            if (storeName.equals(""))
                store_name.setStyle("-fx-border-color: red ; -fx-border-width: 3px ;");
            if (storeAddress.equals(""))
                store_address.setStyle("-fx-border-color: red ; -fx-border-width: 3px ;");
            if (storeGst.equals(""))
                store_gst.setStyle("-fx-border-color: red ; -fx-border-width: 3px ;");
            if (storePhone.equals(""))
                store_phone.setStyle("-fx-border-color: red ; -fx-border-width: 3px ;");
            if (licenseNumber.equals(""))
                license_number.setStyle("-fx-border-color: red ; -fx-border-width: 3px ;");
            if (license_validity.getValue() == null)
                license_validity.setStyle("-fx-border-color: red ; -fx-border-width: 3px ;");
            if (loginPassword.equals(""))
                register_password.setStyle("-fx-border-color: red ; -fx-border-width: 3px ;");
        } else if (!ownerPhone.matches(regexPhone) || !storePhone.matches(regexPhone)) {
            new AlertBox(currentStage, fxmlLoader, false, "Phone must be a number !!");
            if (!ownerPhone.matches(regexPhone))
                owner_phone.setStyle("-fx-text-inner-color: red;");
            if (!storePhone.matches(regexPhone))
                store_phone.setStyle("-fx-text-inner-color: red;");
        } else if (!ownerName.matches(regexName) || !storeName.matches(regexName)) {
            new AlertBox(currentStage, fxmlLoader, false, "Name must be alphabets only");
            if (!ownerName.matches(regexName))
                owner_name.setStyle("-fx-text-inner-color: red;");
            if (!storeName.matches(regexName))
                store_name.setStyle("-fx-text-inner-color: red;");
        } else {
            String licenseValidity1 = license_validity.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            try {
                Connection dbConnection = JDBC.databaseConnect();

                PreparedStatement sqlStatement = dbConnection.prepareStatement("INSERT INTO user_access VALUES (?,?,?,DEFAULT )");
                sqlStatement.setString(1, loginUsername);
                sqlStatement.setString(2, loginPassword);
                sqlStatement.setInt(3, user_type_id);
                sqlStatement.executeUpdate();

                Statement st = dbConnection.createStatement();
                sqlQuery = "SELECT MAX(user_access_id) FROM user_access";
                ResultSet queryResult = st.executeQuery(sqlQuery);
                if (queryResult.next()) {
                    increment = queryResult.getInt(1);
                }

                sqlStatement = dbConnection.prepareStatement("INSERT INTO owner_info VALUES (?,?,?,?,?,?,?,?,DEFAULT )");
                sqlStatement.setInt(1, increment);
                sqlStatement.setString(2, ownerName);
                sqlStatement.setString(3, ownerPhone);
                sqlStatement.setString(4, ownerPan);
                sqlStatement.setString(5, ownerAddress);
                sqlStatement.setString(6, ownerEmail);
                sqlStatement.setString(7, licenseNumber);
                sqlStatement.setString(8, licenseValidity1);
                sqlStatement.executeUpdate();

                sqlStatement = dbConnection.prepareStatement("INSERT INTO store_info VALUES (?,?,?,?,?,DEFAULT )");
                sqlStatement.setInt(1, increment);
                sqlStatement.setString(2, storeName);
                sqlStatement.setString(3, storePhone);
                sqlStatement.setString(4, storeAddress);
                sqlStatement.setString(5, storeGst);
                sqlStatement.executeUpdate();

            } catch (Exception e) {
                e.printStackTrace();
            }

            new AlertBox(currentStage, fxmlLoader, true, loginUsername + "" + ": User Registered");

            owner_name.setText(null);
            owner_phone.setText(null);
            owner_address.setText(null);
            owner_pan.setText(null);
            owner_email.setText(null);
            store_name.setText(null);
            store_phone.setText(null);
            store_address.setText(null);
            store_gst.setText(null);
            license_number.setText(null);
            license_validity.setValue(null);
            register_username.setText(null);
            register_password.setText(null);
        }

    }

    public void onChangeLicense() {
        register_username.setText(license_number.getText());
    }

    public void cancel() {
        Stage stage = (Stage) cancel_button.getScene().getWindow();
        stage.close();
    }
}
