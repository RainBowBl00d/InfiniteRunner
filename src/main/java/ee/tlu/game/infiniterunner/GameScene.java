package ee.tlu.game.infiniterunner;

import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

/**
 * Põhiline mängustreen Infinite Runner mängule.
 * Haldab mängutsüklit, takistusi, tausta ja mängija interaktsiooni.
 */
public class GameScene {
	private static final double INITIAL_SPEED = 1.0;
	private static final double MAX_SPEED = 8.0;
	private static final int SPEED_INCREASE_INTERVAL = 1200;
	private static final double PATTERN_DISTANCE = 800;
	private static final int GAME_WIDTH = 800;
	private static final int GAME_HEIGHT = 400;

	private Scene scene;
	private StackPane root;
	private Pane manguväli;
	private Scale scaleTransform;
	private AnimationTimer timer;

	private Player mangija;
	private List<Rectangle> takistused = new ArrayList<>();
	private boolean gameOver = false;
	private int skoor = 0;
	private Text skoorTekst;

	private Consumer<Integer> gameOverCallback;

	private double gameSpeed = INITIAL_SPEED;
	private double maxSpeed = MAX_SPEED;
	private int frameCount = 0;
	private int speedIncreaseInterval = SPEED_INCREASE_INTERVAL;

	private List<ObstaclePattern> patterns = new ArrayList<>();
	private Random random = new Random();
	private double nextPatternX = 900;
	private double patternDistance = PATTERN_DISTANCE;

	private List<javafx.scene.shape.Circle> pilved = new ArrayList<>();
	private Rectangle muru1, muru2;
	private List<javafx.scene.shape.Line> muruJooned1 = new ArrayList<>();
	private List<javafx.scene.shape.Line> muruJooned2 = new ArrayList<>();
	private double muruOffset = 0;

	/**
	 * Loob uue mängustseeni.
	 *
	 * @param gameOverCallback Funktsioon, mida kutsuda kui mäng lõpeb
	 * @param width            Akna laius
	 * @param height           Akna kõrgus
	 */
	public GameScene(Consumer<Integer> gameOverCallback, double width, double height) {
		this.gameOverCallback = gameOverCallback;

		manguväli = new Pane();
		manguväli.setPrefSize(GAME_WIDTH, GAME_HEIGHT);
		manguväli.setMaxSize(GAME_WIDTH, GAME_HEIGHT);
		manguväli.setMinSize(GAME_WIDTH, GAME_HEIGHT);

		looTaust();
		looManguväli(width, height);
		looMangija();
		looMustrid();
		seadistaKlaviatuur();

		scene.setOnKeyPressed(event -> {
			switch (event.getCode()) {
				case SPACE:
				case UP:    // Nii tühik kui nool üles panevad hüppama
					mangija.hyppa();
					break;
				case DOWN:  // Nool alla paneb sliidima
					mangija.alustaSliidimist();
					break;
			}
		});

		scene.setOnKeyReleased(event -> {
			switch (event.getCode()) {
				case SPACE:
				case UP:
					mangija.lopetaHype();
					break;
			}
		});

		alustaMangutsyklit();
	}

	private void alustaMangutsyklit() {
		timer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				uuendaMangu();
			}
		};
		timer.start();
	}

	private void uuendaMangu() {
		if (gameOver) {
			return;
		}

		frameCount++;

		// Suurenda kiirust aja jooksul
		if (frameCount % speedIncreaseInterval == 0 && gameSpeed < maxSpeed) {
			gameSpeed += 0.3; // Suurenda kiirust 0.3 võrra
			System.out.println("Kiirus suurenes: " + gameSpeed);
		}

		// Liiguta tausta (pilved aeglasemalt parallax efekt)
		for (javafx.scene.shape.Circle pilv : pilved) {
			pilv.setCenterX(pilv.getCenterX() - gameSpeed * 0.3);
			// Kui pilv läheb ekraanilt välja vasakul, liiguta paremale
			if (pilv.getCenterX() < -100) {
				pilv.setCenterX(900);
			}
		}

		// Liiguta muru (täiskiirusel)
		muruOffset += gameSpeed;
		if (muruOffset >= 800) {
			muruOffset = 0;
		}
		muru1.setX(-muruOffset);
		muru2.setX(800 - muruOffset);

		// Liiguta ka muru tekstuuri
		for (javafx.scene.shape.Line grass : muruJooned1) {
			grass.setStartX(grass.getStartX() - gameSpeed);
			grass.setEndX(grass.getEndX() - gameSpeed);
			// Kui joon läheb ekraanilt välja, tõsta ta paremale
			if (grass.getStartX() < -100) {
				grass.setStartX(grass.getStartX() + 1600);
				grass.setEndX(grass.getEndX() + 1600);
			}
		}

		for (javafx.scene.shape.Line grass : muruJooned2) {
			grass.setStartX(grass.getStartX() - gameSpeed);
			grass.setEndX(grass.getEndX() - gameSpeed);
			// Kui joon läheb ekraanilt välja, tõsta ta paremale
			if (grass.getStartX() < -100) {
				grass.setStartX(grass.getStartX() + 1600);
				grass.setEndX(grass.getEndX() + 1600);
			}
		}

		mangija.uuenda();

		// Liiguta takistusi praeguse kiirusega
		for (Rectangle takistus : takistused) {
			takistus.setX(takistus.getX() - gameSpeed);
		}

		// Kontrollime, kas on aeg luua uus muster
		if (nextPatternX <= 850) {
			looMuster();
			nextPatternX = 850 + patternDistance;
		}

		// Liiguta nextPatternX vasakule
		nextPatternX -= gameSpeed;

		kontrolliKokkuporded();

		takistused.removeIf(t -> {
			if (t.getX() < -50) {
				manguväli.getChildren().remove(t);
				skoor += 10;
				skoorTekst.setText("Skoor: " + skoor);
				return true;
			}
			return false;
		});
	}

	private void looMuster() {
		// Vali juhuslik muster
		ObstaclePattern pattern = patterns.get(random.nextInt(patterns.size()));

		// Arvuta spawn tõenäosus (suureneb aja jooksul)
		// Alguses 50%, maksimaalselt 95%
		double spawnChance = 0.5 + (Math.min(frameCount, 3000) / 3000.0) * 0.45;

		double currentX = 850;
		for (ObstacleType type : pattern.getObstacles()) {
			if (random.nextDouble() < spawnChance) {
				looTakistus(type, currentX);
			}
			currentX += pattern.getSpacing();
		}
	}

	private void looTakistus(ObstacleType type, double x) {
		Rectangle t;
		if (type == ObstacleType.LOW) {
			// Madal takistus - kaktus maapinnal
			t = new Rectangle(30, 40);
			t.setX(x);
			t.setY(310);

			// Kaktuse stiil
			t.setFill(new javafx.scene.paint.LinearGradient(
				0, 0, 1, 0, true, javafx.scene.paint.CycleMethod.NO_CYCLE,
				new javafx.scene.paint.Stop(0, Color.web("#2E7D32")),
				new javafx.scene.paint.Stop(0.5, Color.web("#4CAF50")),
				new javafx.scene.paint.Stop(1, Color.web("#2E7D32"))
			));
			t.setArcWidth(10);
			t.setArcHeight(10);
			t.setStroke(Color.web("#1B5E20"));
			t.setStrokeWidth(2);
		} else {
			// Kõrge takistus - lendav lind/takistus
			t = new Rectangle(30, 60);
			t.setX(x);
			t.setY(240);

			// Linnu/putukas stiil
			t.setFill(new javafx.scene.paint.RadialGradient(
				0, 0, 0.5, 0.5, 0.5, true, javafx.scene.paint.CycleMethod.NO_CYCLE,
				new javafx.scene.paint.Stop(0, Color.web("#FF6B6B")),
				new javafx.scene.paint.Stop(1, Color.web("#C92A2A"))
			));
			t.setArcWidth(15);
			t.setArcHeight(15);
			t.setStroke(Color.web("#A61E1E"));
			t.setStrokeWidth(2);
		}
		takistused.add(t);
		manguväli.getChildren().add(t);
	}

	private void kontrolliKokkuporded() {
		for (Rectangle takistus : takistused) {
			if (takistus.getBoundsInParent().intersects(mangija.getKuvand().getBoundsInParent())) {
				System.out.println("Kokkupõrge!");
				gameOver = true;
				timer.stop();
				if (gameOverCallback != null) {
					gameOverCallback.accept(skoor);
				}
				return;
			}
		}
	}

	private void lisaPilv(double x, double y) {
		// Loo pilv kolmest ringist
		javafx.scene.shape.Circle ring1 = new javafx.scene.shape.Circle(x, y, 20);
		javafx.scene.shape.Circle ring2 = new javafx.scene.shape.Circle(x + 25, y, 25);
		javafx.scene.shape.Circle ring3 = new javafx.scene.shape.Circle(x + 50, y, 20);

		ring1.setFill(Color.WHITE);
		ring2.setFill(Color.WHITE);
		ring3.setFill(Color.WHITE);

		ring1.setOpacity(0.8);
		ring2.setOpacity(0.8);
		ring3.setOpacity(0.8);

		manguväli.getChildren().addAll(ring1, ring2, ring3);

		// Lisa kõik ringid pilve listi, et saaksime neid liigutada
		pilved.add(ring1);
		pilved.add(ring2);
		pilved.add(ring3);
	}

	private void looMuruTekstuur(double offsetX, List<javafx.scene.shape.Line> joondeList) {
		// Lisa muru tekstuur (väikesed roheline jooned)
		for (int i = 0; i < 50; i++) {
			double x = offsetX + Math.random() * 800;
			javafx.scene.shape.Line grass = new javafx.scene.shape.Line(
				x, 350 + Math.random() * 10,
				x, 360 + Math.random() * 10
			);
			grass.setStroke(Color.web("#228B22"));
			grass.setStrokeWidth(2);
			manguväli.getChildren().add(grass);
			joondeList.add(grass);
		}
	}

	/**
	 * Loob tausta (taevas, päike, pilved, muru).
	 */
	private void looTaust() {
		Rectangle taevas = new Rectangle(0, 0, GAME_WIDTH, GAME_HEIGHT);
		taevas.setFill(new javafx.scene.paint.LinearGradient(
			0, 0, 0, 1, true, javafx.scene.paint.CycleMethod.NO_CYCLE,
			new javafx.scene.paint.Stop(0, Color.web("#87CEEB")),
			new javafx.scene.paint.Stop(0.7, Color.web("#B0E0E6")),
			new javafx.scene.paint.Stop(1, Color.web("#E0F6FF"))
		));
		manguväli.getChildren().add(taevas);

		javafx.scene.shape.Circle paike = new javafx.scene.shape.Circle(700, 80, 40);
		paike.setFill(Color.web("#FFD700"));
		paike.setEffect(new javafx.scene.effect.Glow(0.8));
		manguväli.getChildren().add(paike);

		lisaPilv(150, 60);
		lisaPilv(400, 100);
		lisaPilv(650, 50);
		lisaPilv(900, 80);

		javafx.scene.paint.LinearGradient muruGradient = new javafx.scene.paint.LinearGradient(
			0, 0, 0, 1, true, javafx.scene.paint.CycleMethod.NO_CYCLE,
			new javafx.scene.paint.Stop(0, Color.web("#90EE90")),
			new javafx.scene.paint.Stop(1, Color.web("#228B22"))
		);

		muru1 = new Rectangle(0, 350, GAME_WIDTH, 50);
		muru1.setFill(muruGradient);
		manguväli.getChildren().add(muru1);

		muru2 = new Rectangle(GAME_WIDTH, 350, GAME_WIDTH, 50);
		muru2.setFill(muruGradient);
		manguväli.getChildren().add(muru2);

		looMuruTekstuur(0, muruJooned1);
		looMuruTekstuur(GAME_WIDTH, muruJooned2);
	}

	/**
	 * Loob mänguvälja konteineri ja skaleerimise.
	 */
	private void looManguväli(double width, double height) {
		root = new StackPane(manguväli);
		root.setStyle("-fx-background-color: black;");
		root.setAlignment(Pos.CENTER);

		scene = new Scene(root, width, height);

		scaleTransform = new Scale();
		scaleTransform.setPivotX(GAME_WIDTH / 2.0);
		scaleTransform.setPivotY(GAME_HEIGHT / 2.0);
		manguväli.getTransforms().add(scaleTransform);

		updateScale(width, height);

		scene.widthProperty().addListener((obs, oldVal, newVal) ->
			updateScale(newVal.doubleValue(), scene.getHeight()));
		scene.heightProperty().addListener((obs, oldVal, newVal) ->
			updateScale(scene.getWidth(), newVal.doubleValue()));

		skoorTekst = new Text(10, 30, "Skoor: 0");
		skoorTekst.setFont(Font.font("Arial", 24));
		skoorTekst.setFill(Color.BLACK);
		manguväli.getChildren().add(skoorTekst);
	}

	/**
	 * Loob mängija.
	 */
	private void looMangija() {
		mangija = new Player(manguväli);
	}

	/**
	 * Loob takistuste mustrid.
	 */
	private void looMustrid() {
		patterns.add(new ObstaclePattern(0, ObstacleType.LOW));
		patterns.add(new ObstaclePattern(0, ObstacleType.HIGH));
		patterns.add(new ObstaclePattern(200, ObstacleType.LOW, ObstacleType.LOW));
		patterns.add(new ObstaclePattern(220, ObstacleType.LOW, ObstacleType.HIGH));
		patterns.add(new ObstaclePattern(220, ObstacleType.HIGH, ObstacleType.LOW));
		patterns.add(new ObstaclePattern(180, ObstacleType.LOW, ObstacleType.LOW, ObstacleType.LOW));
		patterns.add(new ObstaclePattern(200, ObstacleType.LOW, ObstacleType.HIGH, ObstacleType.LOW));
		patterns.add(new ObstaclePattern(200, ObstacleType.HIGH, ObstacleType.HIGH));
		patterns.add(new ObstaclePattern(180, ObstacleType.HIGH, ObstacleType.LOW, ObstacleType.HIGH, ObstacleType.LOW));
	}

	/**
	 * Seadistab klaviatuuri sisendi käsitlejad.
	 */
	private void seadistaKlaviatuur() {
		alustaMangutsyklit();
	}

	/**
	 * Updates the scale transformation to fit the window.
	 */
	private void updateScale(double width, double height) {
		double scaleX = width / GAME_WIDTH;
		double scaleY = height / GAME_HEIGHT;
		double scale = Math.min(scaleX, scaleY);

		scaleTransform.setX(scale);
		scaleTransform.setY(scale);
	}

	/**
	 * Gets the JavaFX scene.
	 *
	 * @return The scene
	 */
	public Scene getScene() {
		return scene;
	}
}