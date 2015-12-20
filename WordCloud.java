/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wordcloud;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.layout.StackPane;
import javafx.scene.text.*;
import javafx.stage.Stage;

/**
 *
 * @author Kaiheng He
 */
public class WordCloud extends Application {

    VBox bd = new VBox();
    //FlowPane bd = new FlowPane();
    Stage sg = new Stage();

    @Override
    public void start(Stage primaryStage) {
        Button btn = new Button();
        TextArea ta = new TextArea();
        ta.setText("text here");
        ta.setPrefSize(100, 100);
        ta.setWrapText(true);
        btn.setText("get Text'");

        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                String tx = ta.getText();
                textShow(tx);
                Scene asd = new Scene(bd, 300, 300);
                sg.setScene(asd);
                sg.show();
            }
        });

        StackPane root = new StackPane();
        root.getChildren().add(ta);
        root.getChildren().add(btn);

        Scene scene = new Scene(root, 300, 250);

        primaryStage.setTitle("wordCloud");
        primaryStage.setScene(scene);

        primaryStage.show();
        sg.hide();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    public void textShow(String text) {
        Map<String, Integer> map = new TreeMap<>();

        String[] words = text.split("[ \n\t\r.,;:!?(){}]");
        for (int i = 0; i < words.length; i++) {
            String key = words[i].toLowerCase();

            if (key.length() > 0) {
                if (!map.containsKey(key)) {
                    map.put(key, 1);
                } else {
                    int value = map.get(key);
                    value++;
                    map.put(key, value);
                }
            }
        }

        // Get all entries into a set
        Set<Map.Entry<String, Integer>> entrySet = map.entrySet();
        // Get key and value from each entry
        for (Map.Entry<String, Integer> entry : entrySet) {
            // System.out.println(entry.getValue() + "\t" + entry.getKey());
            Text fx = new Text(entry.getKey());

            fx.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.ITALIC, entry.getValue() * 10));
            bd.getChildren().add(fx);

        }
    }

}
