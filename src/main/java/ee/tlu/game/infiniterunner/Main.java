package ee.tlu.game.infiniterunner;

import ee.tlu.game.infiniterunner.GameScene;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Main extends Application {
	private Stage primaryStage;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;

		primaryStage.setTitle("Infinite Runner Projekt");
		primaryStage.setResizable(true);
		primaryStage.setMinWidth(800);
		primaryStage.setMinHeight(400);

		primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
			System.out.println("Akna laius on nüüd: " + newVal);
		});

		naitaPeamenuu();
		primaryStage.show();
	}

	private void naitaPeamenuu() {
		VBox menuBox = new VBox(20);
		menuBox.setAlignment(Pos.CENTER);
		menuBox.setStyle("-fx-background-color: #2c3e50;");

		Text pealkiri = new Text("INFINITE RUNNER");
		pealkiri.setFont(Font.font("Arial", 48));
		pealkiri.setFill(Color.WHITE);

		Text juhised = new Text("Juhtimine:\nSpace / ↑ = Hüpe\n↓ = Sliid");
		juhised.setFont(Font.font("Arial", 18));
		juhised.setFill(Color.LIGHTGRAY);
		juhised.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

		Button alustaNupp = new Button("ALUSTA MÄNGU");
		alustaNupp.setFont(Font.font("Arial", 24));
		alustaNupp.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 15 30;");
		alustaNupp.setOnAction(e -> alustaMangu());

		menuBox.getChildren().addAll(pealkiri, juhised, alustaNupp);

		double width = primaryStage.getWidth() > 0 ? primaryStage.getWidth() : 800;
		double height = primaryStage.getHeight() > 0 ? primaryStage.getHeight() : 400;
		Scene menuuStseen = new Scene(menuBox, width, height);
		primaryStage.setScene(menuuStseen);
	}

	private void alustaMangu() {
		double width = primaryStage.getWidth();
		double height = primaryStage.getHeight();

		GameScene mang = new GameScene(this::naitaGameOver, width, height);
		primaryStage.setScene(mang.getScene());
	}

	private void naitaGameOver(int skoor) {
		VBox gameOverBox = new VBox(20);
		gameOverBox.setAlignment(Pos.CENTER);
		gameOverBox.setStyle("-fx-background-color: #34495e;");

		Text gameOverText = new Text("GAME OVER");
		gameOverText.setFont(Font.font("Arial", 56));
		gameOverText.setFill(Color.RED);

		Text skoorText = new Text("Skoor: " + skoor);
		skoorText.setFont(Font.font("Arial", 32));
		skoorText.setFill(Color.WHITE);

		Button uuestiNupp = new Button("MÄNGI UUESTI");
		uuestiNupp.setFont(Font.font("Arial", 24));
		uuestiNupp.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 15 30;");
		uuestiNupp.setOnAction(e -> alustaMangu());

		Button menuNupp = new Button("PEAMENÜÜ");
		menuNupp.setFont(Font.font("Arial", 20));
		menuNupp.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-padding: 10 25;");
		menuNupp.setOnAction(e -> naitaPeamenuu());

		gameOverBox.getChildren().addAll(gameOverText, skoorText, uuestiNupp, menuNupp);

		double width = primaryStage.getWidth();
		double height = primaryStage.getHeight();
		Scene gameOverStseen = new Scene(gameOverBox, width, height);
		primaryStage.setScene(gameOverStseen);

		String nimi = "BBB";
		try {
			Map<String, Integer> tulemused = loeFailist();
			
			if (tulemused.containsKey(nimi)) {
				if (tulemused.get(nimi) < skoor)
					tulemused.put(nimi, skoor);
			} else
				tulemused.put(nimi, skoor);
			kirjutaFaili(tulemused);
		}
		catch (Exception e){
			System.out.println("Skoori salvestamisel läks midagi persse!");
		}
	}

	private void kirjutaFaili(Map<String,Integer> tulemused) {
		try (OutputStream välja = new FileOutputStream("skoorid.dat");
			 DataOutputStream kirjutaja = new DataOutputStream(välja)){
			kirjutaja.write(tulemused.size());
			for (String nimi : tulemused.keySet()) {
				kirjutaja.writeUTF(nimi);
				kirjutaja.write(tulemused.get(nimi));
			}
		}
		catch (Exception e){
			System.out.println("Faili kirjutamisel läks midagi valesti, tulemus ei pruukinud salvestuda.");
		}
	}

	private static Map<String,Integer> loeFailist() throws IOException{
		Map<String,Integer> tulemused = new HashMap<>();
		File fail  = new File("skoorid.dat");
		try (InputStream sisse = new FileInputStream(fail);
			 DataInputStream lugeja = new DataInputStream(sisse)){
			for (int i = 0; i < lugeja.read(); i++) {
				tulemused.put(lugeja.readUTF(),lugeja.read());
			}
		}
		catch (FileNotFoundException failPuudub){
			fail.createNewFile();
			return tulemused;
		}
		catch (Exception e){
			System.out.println("Failist lugemisel läks midagi katki!");
		}
		return tulemused;
	}

	public static void main(String[] args) {
		launch(args);
	}
}