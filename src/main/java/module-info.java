module org.codefest.nghenhan {
    requires javafx.controls;
    requires javafx.fxml;
    requires socket.io.client;
    requires okhttp3;

    opens org.codefest2024.nghenhan to javafx.fxml;
    exports org.codefest2024.nghenhan;
}