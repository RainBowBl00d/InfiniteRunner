package ee.tlu.game.infiniterunner;

import ee.tlu.game.infiniterunner.GameScene;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) {
		StackPane root = new StackPane();
		Text tervitus = new Text("Tere tulemast Infinite Runner mängu!\n" +
				"Vajuta hiirega siia, et alustada.\n\n" +
				"Juhtimine: Space = Hüpe");
		root.getChildren().add(tervitus);

		Scene menuuStseen = new Scene(root, 800, 400);

		root.setOnMouseClicked(event -> {
			GameScene mang = new GameScene();
			primaryStage.setScene(mang.getScene());
		});

		primaryStage.setTitle("Infinite Runner Projekt");
		primaryStage.setScene(menuuStseen);

		primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
			System.out.println("Akna laius on nüüd: " + newVal);
		});

		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}