module org.codefest.nghenhan {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.codefest2024.nghenhan to javafx.fxml;
    exports org.codefest2024.nghenhan;
}