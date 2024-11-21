module org.codefest.nghenhan {
    requires javafx.controls;
    requires javafx.fxml;
    requires socket.io.client;
    requires okhttp3;
    requires com.google.gson;
    requires engine.io.client;
    requires json;
    requires static lombok;
    requires java.desktop;

    opens org.codefest2024.nghenhan to javafx.fxml;
    exports org.codefest2024.nghenhan;
}