package ee.tlu.game.infiniterunner;

import ee.tlu.game.infiniterunner.GameScene;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.nio.charset.*;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

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

		Button scoreboardNupp = new Button("SCOREBOARD");
		scoreboardNupp.setFont(Font.font("Arial", 20));
		scoreboardNupp.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 10 25;");
		scoreboardNupp.setOnAction(e -> naitaScoreboard());

		menuBox.getChildren().addAll(pealkiri, juhised, alustaNupp, scoreboardNupp);

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

		Text nimiLabel = new Text("Sisesta oma nimi:");
		nimiLabel.setFont(Font.font("Arial", 20));
		nimiLabel.setFill(Color.WHITE);

		TextField nimiVäli = new TextField();
		nimiVäli.setMaxWidth(300);
		nimiVäli.setFont(Font.font("Arial", 18));
		nimiVäli.setPromptText("Sinu nimi");
		nimiVäli.setStyle("-fx-background-color: white; -fx-text-fill: black;");

		Button salvestaNupp = new Button("SALVESTA SKOOR");
		salvestaNupp.setFont(Font.font("Arial", 20));
		salvestaNupp.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-padding: 10 25;");

		salvestaNupp.setOnAction(e -> {
			try {
				String nimi = nimiVäli.getText().trim();
				if (nimi.isEmpty()) {
					nimi = "Anonüümne";
				} else if (nimi.matches(".*\\d.*")) {
					throw new ViganeMängijaNimi("Mängija nimi ei tohi sisaldada numbreid!");
				}
				salvestaSkoor(nimi, skoor);
				nimiVäli.setDisable(true);
				salvestaNupp.setDisable(true);
			}
			catch (ViganeMängijaNimi erind) {
				System.out.println("Mängija nimi ei tohi sisaldada numbreid!");
			}
		});


		Button uuestiNupp = new Button("MÄNGI UUESTI");
		uuestiNupp.setFont(Font.font("Arial", 24));
		uuestiNupp.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 15 30;");
		uuestiNupp.setOnAction(e -> alustaMangu());

		Button scoreboardNupp = new Button("VAATA SCOREBOARD");
		scoreboardNupp.setFont(Font.font("Arial", 20));
		scoreboardNupp.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 10 25;");
		scoreboardNupp.setOnAction(e -> naitaScoreboard());

		Button menuNupp = new Button("PEAMENÜÜ");
		menuNupp.setFont(Font.font("Arial", 20));
		menuNupp.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-padding: 10 25;");
		menuNupp.setOnAction(e -> naitaPeamenuu());

		gameOverBox.getChildren().addAll(gameOverText, skoorText, nimiLabel, nimiVäli, salvestaNupp, uuestiNupp, scoreboardNupp, menuNupp);

		double width = primaryStage.getWidth();
		double height = primaryStage.getHeight();
		Scene gameOverStseen = new Scene(gameOverBox, width, height);
		primaryStage.setScene(gameOverStseen);
	}

	private void salvestaSkoor(String nimi, int skoor) {
		try {
			Map<String, Integer> tulemused = loeFailist();

			if (tulemused.containsKey(nimi)) {
				if (tulemused.get(nimi) < skoor)
					tulemused.put(nimi, skoor);
			} else {
				tulemused.put(nimi, skoor);
			}
			kirjutaFaili(tulemused);
		}
		catch (Exception e){
			System.out.println("Skoori salvestamisel läks midagi persse!");
			e.printStackTrace();
		}
	}

	private void naitaScoreboard() {
		VBox scoreboardBox = new VBox(15);
		scoreboardBox.setAlignment(Pos.CENTER);
		scoreboardBox.setStyle("-fx-background-color: #2c3e50; -fx-padding: 20;");

		Text pealkiri = new Text("SCOREBOARD");
		pealkiri.setFont(Font.font("Arial", 48));
		pealkiri.setFill(Color.WHITE);

		VBox tulemusteTabel = new VBox(10);
		tulemusteTabel.setAlignment(Pos.CENTER);

		try {
			Map<String, Integer> tulemused = loeFailist();

			if (tulemused.isEmpty()) {
				Text tühi = new Text("Tulemusi pole veel salvestatud!");
				tühi.setFont(Font.font("Arial", 20));
				tühi.setFill(Color.LIGHTGRAY);
				tulemusteTabel.getChildren().add(tühi);
			} else {
				// Sorteeri tulemused skoori järgi kahanevalt
				List<Map.Entry<String, Integer>> sorteeritud = tulemused.entrySet()
					.stream()
					.sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
					.limit(10)
					.collect(Collectors.toList());

				// Näita top 10 tulemust
				for (int i = 0; i < sorteeritud.size(); i++) {
					Map.Entry<String, Integer> entry = sorteeritud.get(i);
					HBox rida = new HBox(20);
					rida.setAlignment(Pos.CENTER);

					Text koht = new Text((i + 1) + ".");
					koht.setFont(Font.font("Arial", 24));
					koht.setFill(i < 3 ? Color.GOLD : Color.WHITE);

					Text nimi = new Text(entry.getKey());
					nimi.setFont(Font.font("Arial", 24));
					nimi.setFill(Color.WHITE);

					Text skoor = new Text(entry.getValue() + " punkti");
					skoor.setFont(Font.font("Arial", 24));
					skoor.setFill(Color.LIGHTGREEN);

					rida.getChildren().addAll(koht, nimi, skoor);
					tulemusteTabel.getChildren().add(rida);
				}
			}
		} catch (Exception e) {
			Text viga = new Text("Viga tulemuste laadimisel!");
			viga.setFont(Font.font("Arial", 20));
			viga.setFill(Color.RED);
			tulemusteTabel.getChildren().add(viga);
			e.printStackTrace();
		}

		Button tagasiNupp = new Button("TAGASI");
		tagasiNupp.setFont(Font.font("Arial", 20));
		tagasiNupp.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-padding: 10 25;");
		tagasiNupp.setOnAction(e -> naitaPeamenuu());

		scoreboardBox.getChildren().addAll(pealkiri, tulemusteTabel, tagasiNupp);

		double width = primaryStage.getWidth();
		double height = primaryStage.getHeight();
		Scene scoreboardStseen = new Scene(scoreboardBox, width, height);
		primaryStage.setScene(scoreboardStseen);
	}

	private void kirjutaFaili(Map<String,Integer> tulemused) {
		try (OutputStream välja = new FileOutputStream("skoorid.dat");
			 DataOutputStream kirjutaja = new DataOutputStream(välja)){
			kirjutaja.writeInt(tulemused.size());
			for (String nimi : tulemused.keySet()) {
				kirjutaja.writeUTF(nimi);
				kirjutaja.writeInt(tulemused.get(nimi));
			}
		}
		catch (Exception e){
			System.out.println("Faili kirjutamisel läks midagi valesti, tulemus ei pruukinud salvestuda.");
			e.printStackTrace();
		}
	}

	private static Map<String,Integer> loeFailist() throws IOException{
		Map<String,Integer> tulemused = new HashMap<>();
		File fail  = new File("skoorid.dat");
		try (InputStream sisse = new FileInputStream(fail);
			 DataInputStream lugeja = new DataInputStream(sisse)){
			int arv = lugeja.readInt();
			for (int i = 0; i < arv; i++) {
				String nimi = lugeja.readUTF();
				int skoor = lugeja.readInt();
				tulemused.put(nimi, skoor);
			}
		}
		catch (FileNotFoundException failPuudub){
			fail.createNewFile();
			return tulemused;
		}
		catch (Exception e){
			System.out.println("Failist lugemisel läks midagi katki!");
			e.printStackTrace();
		}
		return tulemused;
	}

	public static void main(String[] args) {
		launch(args);
	}
}