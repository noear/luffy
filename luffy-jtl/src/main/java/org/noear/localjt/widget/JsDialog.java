package org.noear.localjt.widget;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.web.PromptData;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Optional;

public class JsDialog {
    public static void alert(String content) {
        Alert dialog = new Alert(Alert.AlertType.NONE);
        dialog.initStyle(StageStyle.UTILITY);

        dialog.setTitle("提示");
        dialog.setHeaderText(null);
        dialog.setContentText(content);

        ButtonType okBtn = new ButtonType("确定", ButtonBar.ButtonData.OK_DONE);

        dialog.getButtonTypes().setAll(okBtn);

        dialog.showAndWait();
        dialog.hide();
    }

    public static boolean confirm(String content) {
        Alert dialog = new Alert(Alert.AlertType.NONE);
        dialog.initStyle(StageStyle.UTILITY);

        dialog.setTitle("确认");
        dialog.setHeaderText(null);
        dialog.setContentText(content);

        ButtonType okBtn = new ButtonType("确定", ButtonBar.ButtonData.YES);
        ButtonType cancelBtn = new ButtonType("取消", ButtonBar.ButtonData.NO);

        dialog.getButtonTypes().setAll(cancelBtn, okBtn);

        Optional<ButtonType> rst = dialog.showAndWait();
        dialog.hide();

        return (rst.get() == okBtn);
    }

    public static String prompt(PromptData pd) {
        TextInputDialogX dialog = new TextInputDialogX(pd.getDefaultValue());
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setTitle("输入框");
        dialog.setWidth(371);
        dialog.setContentText(pd.getMessage());

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            return result.get();
        } else {
            return "";
        }
    }

    public static WebEngine popup(){
        Stage stage = new Stage(StageStyle.UTILITY);
        WebView popupView = new WebView();

        stage.setScene(new Scene(popupView));
        stage.show();

        return popupView.getEngine();
    }
}
