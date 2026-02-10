module com.example.javaproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    opens Start to javafx.fxml;
    opens Course to javafx.fxml;
    opens userTypes to javafx.fxml;
    opens Login to javafx.fxml;
    opens studentfunc to javafx.fxml;
    opens professorfunc to javafx.fxml;
    opens adminfunc to javafx.fxml;
    opens adminfunc.admindelete to javafx.fxml;
    opens adminfunc.adminadd to javafx.fxml;
    opens adminfunc.adminedit to javafx.fxml;
    opens adminfunc.adminview to javafx.fxml;

    exports studentfunc;
    exports Start;
    exports Course;
    exports userTypes;
    exports Login;
    exports professorfunc;
    exports adminfunc;
}