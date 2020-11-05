package org.noear.localjt.dso;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.noear.localjt.LocalJtApp;
import org.noear.solon.XApp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class WebShell extends Application {

    @Override
    public void start(Stage window) throws Exception {
        window.getIcons().add(new Image(getImage("icon96.png")));
        window.getIcons().add(new Image(getImage("icon64.png")));
        window.getIcons().add(new Image(getImage("icon32.png")));

        window.setMinWidth(getVisualScreenWidth() * 0.8);
        window.setMinHeight(getVisualScreenHeight() * 0.5);
        window.centerOnScreen();
        window.setOnCloseRequest(cl -> System.exit(0));

        Scene scene = new Scene(new Group());
        WebViewBuilder builder = new WebViewBuilder();
        builder.setUrl(LocalJtApp.home);

        scene.setRoot(builder.build());

        window.setScene(scene);
        window.setTitle(LocalJtApp.title);
        window.show();
    }

    public static InputStream getImage(String name) throws IOException {
        String extend = XApp.cfg().argx().get("extend");

        File file = new File(extend + name);
        if(file.exists()){
            return new FileInputStream(file);
        }

        URL url = LocalJtApp.class.getResource(name);
        if (url == null) {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            if (loader != null) {
                url = loader.getResource(name);
            } else {
                url = ClassLoader.getSystemResource(name);
            }
        }

        return url.openStream();
    }

    public static void start(String[] args) {
        //要用新线程启动
        launch(args);
    }


    public static double getVisualScreenWidth() {
        return Screen.getPrimary().getVisualBounds().getWidth();
    }

    public static double getVisualScreenHeight() {
        return Screen.getPrimary().getVisualBounds().getHeight();
    }

}
