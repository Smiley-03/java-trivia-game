module com.example.triviagame {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.example.triviagame to javafx.fxml;
    exports com.example.triviagame;
}