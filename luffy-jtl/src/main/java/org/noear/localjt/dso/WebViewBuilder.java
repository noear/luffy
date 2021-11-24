package org.noear.localjt.dso;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.noear.localjt.LocalJtApp;
import org.noear.localjt.widget.JsDialog;
import org.noear.solon.Solon;;
import org.noear.luffy.dso.PluginUtil;
import org.noear.luffy.utils.TextUtils;

import java.awt.*;
import java.net.URI;

public class WebViewBuilder {

    private String url;

    public void setUrl(String url) {
        this.url = url;
    }

    public WebView build(){
        final WebView webView = new WebView();
        final WebEngine webEngine = webView.getEngine();

        webView.autosize();

        webEngine.setOnAlert((event)->{
            JsDialog.alert(event.getData());
        });

        webEngine.setConfirmHandler((txt)-> JsDialog.confirm(txt));

        webEngine.setPromptHandler((param -> JsDialog.prompt(param)));

        createContextMenu(webView);

//        webEngine.setCreatePopupHandler(param -> JsDialog.popup());

        webEngine.load(url);

        return webView;
    }

    private void createContextMenu(WebView webView) {
        webView.setContextMenuEnabled(false);

        ContextMenu contextMenu = new ContextMenu();

        MenuItem goback = new MenuItem("返回");
        goback.setOnAction(e -> webView.toBack());
        contextMenu.getItems().add(goback);

        MenuItem reload = new MenuItem("重新加载");
        reload.setOnAction(e -> webView.getEngine().reload());
        contextMenu.getItems().add(reload);


        //进入管理界面
        if(LocalJtApp.home.indexOf("/.admin/") < 0) {
            MenuItem shift = new MenuItem("进入管理界面");
            String adminUrl = "http://localhost:" + Solon.global().port() + "/.admin/?_L0n5=81057AF6D4931710A5370514A4EE2DB5D2033055";
            shift.setOnAction(e -> {
                if(shift.getText().indexOf("管理") > 0){
                    shift.setText("进入应用界面");
                    webView.getEngine().load(adminUrl);
                }else{
                    shift.setText("进入管理界面");
                    webView.getEngine().load(LocalJtApp.home);
                }
            });
            contextMenu.getItems().add(shift);
        }

        //使用浏览器打开
        MenuItem browse = new MenuItem("使用浏览器打开");
        browse.setOnAction(e -> {
            openBrowse(webView.getEngine().getLocation());
        });
        contextMenu.getItems().add(browse);



        if(TextUtils.isEmpty(LocalJtApp.plugin_add) == false) {

            if (TextUtils.isEmpty(LocalJtApp.title) == false) {
                String menuTitle = "更新应用: " + LocalJtApp.title;

                MenuItem update = new MenuItem(menuTitle);
                update.setOnAction(e -> {
                    PluginUtil.udp(LocalJtApp.plugin_add);
                    webView.getEngine().reload();
                });

                contextMenu.getItems().add(update);
            }
        }


        webView.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                contextMenu.show(webView, e.getScreenX(), e.getScreenY());
            } else {
                contextMenu.hide();
            }
        });
    }

    private void openBrowse(String url) {
        Desktop d = Desktop.getDesktop();
        try {
            URI address = new URI(url);
            d.browse(address);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
