package ee.tlu.game.infiniterunner;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class GameScene {
	private Scene scene;
	private Pane manguväli;
	private AnimationTimer timer;

	private Rectangle mangija;
	private List<Rectangle> takistused = new ArrayList<>();

	public GameScene() {
		manguväli = new Pane();
		scene = new Scene(manguväli, 800, 400);

		mangija = new Rectangle(50, 50, Color.RED);
		mangija.setX(100);
		mangija.setY(300);
		manguväli.getChildren().add(mangija);

		scene.setOnKeyPressed(event -> {
			switch (event.getCode()) {
				case SPACE:
					hyppa();
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
		for (Rectangle takistus : takistused) {
			takistus.setX(takistus.getX() - 5);
		}

		if (Math.random() < 0.01) {
			looTakistus();
		}

		takistused.removeIf(t -> {
			if (t.getX() < -50) {
				manguväli.getChildren().remove(t);
				return true;
			}
			return false;
		});
	}

	private void looTakistus() {
		Rectangle t = new Rectangle(30, 30, Color.BLUE);
		t.setX(850);
		t.setY(320);
		takistused.add(t);
		manguväli.getChildren().add(t);
	}

	private void hyppa() {
		System.out.println("Hüpe!");
	}

	public Scene getScene() {
		return scene;
	}
}