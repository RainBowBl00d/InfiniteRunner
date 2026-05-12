package ee.tlu.game.infiniterunner;

import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Mängija tegelane (kollane tibu).
 * Haldab liikumist, hüppamist, sliidimist ja animatsiooni.
 */
public class Player {
	private static final double GRAVITATSIOON = 0.2;
	private static final int MAAPIND_Y = 300;
	private static final int FRAME_WIDTH = 128;
	private static final int FRAME_HEIGHT = 128;
	private static final int PLAYER_HEIGHT = 50;
	private static final int PLAYER_WIDTH = 40;
	private static final int RUN_FRAME_COUNT = 12;
	private static final int JUMP_FRAME_COUNT = 6;
	private static final int SLIDE_FRAME_COUNT = 9;
	private static final int FRAME_DELAY = 6;

	private enum AnimState { RUNNING, JUMPING, SLIDING }

	private double jumpHeight = 8.0;
	private double minJumpHeight = 1.0;
	private double slideHeight = 25;
	private int slideDuration = 100;

	private boolean huppabPraegu = false;

	private ImageView pilt;
	private Rectangle ristkülik;
	private Rectangle hitbox;
	private boolean kasKasutabPilti = true;

	private Image jooksuPilt;
	private Image hyppePilt;
	private Image sliidiPilt;

	private AnimState animState = AnimState.RUNNING;
	private int currentFrame = 0;
	private int frameTimer = 0;

	private double x, y;
	private double kiirusY = 0;
	private boolean onMaas = true;
	private boolean sliidib = false;
	private int sliidiTaimer = 0;

	/**
	 * Loob uue mängija.
	 *
	 * @param manguvali Pane, kuhu mängija elemendid lisatakse
	 */
	public Player(Pane manguvali) {
		try {
			jooksuPilt = new Image(getClass().getResourceAsStream("/ee/tlu/game/infiniterunner/sprites/Running.png"));
			hyppePilt = new Image(getClass().getResourceAsStream("/ee/tlu/game/infiniterunner/sprites/jumping.png"));
			sliidiPilt = new Image(getClass().getResourceAsStream("/ee/tlu/game/infiniterunner/sprites/rolling.png"));

			pilt = new ImageView(jooksuPilt);
			pilt.setViewport(new Rectangle2D(0, 0, FRAME_WIDTH, FRAME_HEIGHT));
			pilt.setFitHeight(PLAYER_HEIGHT);
			pilt.setPreserveRatio(true);
			pilt.setX(100);
			pilt.setY(MAAPIND_Y);
			manguvali.getChildren().add(pilt);
			kasKasutabPilti = true;

		} catch (Exception e) {
			System.out.println("Pilte ei leitud, kasutan fallback-ristkülikut.");

			ristkülik = new Rectangle(PLAYER_WIDTH, PLAYER_HEIGHT, Color.RED);
			ristkülik.setX(100);
			ristkülik.setY(MAAPIND_Y);
			manguvali.getChildren().add(ristkülik);
			kasKasutabPilti = false;
		}

		hitbox = new Rectangle(PLAYER_WIDTH, PLAYER_HEIGHT);
		hitbox.setFill(Color.TRANSPARENT);
		hitbox.setStroke(Color.RED); // Debug jaoks
		hitbox.setStrokeWidth(1);
		hitbox.setX(100);
		hitbox.setY(MAAPIND_Y);
		manguvali.getChildren().add(hitbox);

		this.x = 100;
		this.y = MAAPIND_Y;
	}

	/**
	 * Uuendab mängija olekut (liikumine, füüsika, animatsioon).
	 * Kutsutakse iga kaadri kohta.
	 */
	public void uuenda() {
		if (!huppabPraegu && kiirusY < 0) {
			kiirusY *= 0.6;
		}

		kiirusY += GRAVITATSIOON;
		y += kiirusY;

		if (y >= MAAPIND_Y) {
			y = MAAPIND_Y;
			kiirusY = 0;

			if (!onMaas && !sliidib) {
				setAnimState(AnimState.RUNNING);
			}
			onMaas = true;
		} else {
			onMaas = false;
		}

		if (sliidib) {
			sliidiTaimer--;
			if (sliidiTaimer <= 0) {
				lopetaSliidimine();
			}
		}

		if (kasKasutabPilti) {
			updateAnimation();
			pilt.setY(y);
		} else {
			ristkülik.setY(y);
		}

		if (sliidib && onMaas) {
			hitbox.setHeight(slideHeight);
			hitbox.setY(y + (PLAYER_HEIGHT - slideHeight));
		} else {
			hitbox.setHeight(PLAYER_HEIGHT);
			hitbox.setY(y);
		}
	}

	/**
	 * Paneb mängija hüppama.
	 * Kui mängija sliidib, lõpetab sliidi ja hüppab.
	 */
	public void hyppa() {
		if (onMaas || sliidib) {
			if (sliidib) {
				lopetaSliidimine();
			}

			kiirusY = -jumpHeight;
			onMaas = false;
			huppabPraegu = true;
			setAnimState(AnimState.JUMPING);
		}
	}

	/**
	 * Lõpetab hüppe (vabastab hüppenupu).
	 * Vähendab hüppe kõrgust kui kutsutud õhus olles.
	 */
	public void lopetaHype() {
		huppabPraegu = false;
	}

	/**
	 * Alustab sliidimist.
	 * Saab kutsuda nii maapinnal kui õhus (õhus = kiire kukkumine).
	 */
	public void alustaSliidimist() {
		if (!sliidib) {
			sliidib = true;
			sliidiTaimer = slideDuration;
			setAnimState(AnimState.SLIDING);

			if (!onMaas) {
				kiirusY = Math.max(kiirusY, 5.0);
			}

			System.out.println("Mängija alustas sliidimist.");
		}
	}

	private void lopetaSliidimine() {
		sliidib = false;
		setAnimState(AnimState.RUNNING);
		System.out.println("Sliidimine lõppes.");
	}

	private void setAnimState(AnimState uusOlek) {
		if (animState == uusOlek) {
			return;
		}
		animState = uusOlek;
		currentFrame = 0;
		frameTimer = 0;
		updateFrameViewport();
	}

	private void updateAnimation() {
		if (!kasKasutabPilti) {
			return;
		}

		frameTimer++;
		if (frameTimer < FRAME_DELAY) {
			return;
		}

		frameTimer = 0;
		currentFrame++;
		int maxFrames = getFrameCount(animState);
		if (currentFrame >= maxFrames) {
			currentFrame = 0;
		}
		updateFrameViewport();
	}

	private void updateFrameViewport() {
		if (!kasKasutabPilti) {
			return;
		}

		Image image = getCurrentSheet();
		if (image == null) {
			return;
		}

		pilt.setImage(image);
		pilt.setViewport(new Rectangle2D(currentFrame * FRAME_WIDTH, 0, FRAME_WIDTH, FRAME_HEIGHT));
	}

	private int getFrameCount(AnimState state) {
		switch (state) {
			case JUMPING:
				return JUMP_FRAME_COUNT;
			case SLIDING:
				return SLIDE_FRAME_COUNT;
			default:
				return RUN_FRAME_COUNT;
		}
	}

	private Image getCurrentSheet() {
		switch (animState) {
			case JUMPING:
				return hyppePilt;
			case SLIDING:
				return sliidiPilt;
			default:
				return jooksuPilt;
		}
	}

	/**
	 * Määrab hüppe kõrguse.
	 *
	 * @param jumpHeight Uus hüppe kõrgus (peab olema positiivne)
	 */
	public void setJumpHeight(double jumpHeight) {
		if (jumpHeight > 0) {
			this.jumpHeight = jumpHeight;
		}
	}

	/**
	 * Määrab sliidi kestuse kaadrites.
	 *
	 * @param durationFrames Sliidi kestus kaadrites (peab olema positiivne)
	 */
	public void setSlideDuration(int durationFrames) {
		if (durationFrames > 0) {
			this.slideDuration = durationFrames;
		}
	}

	/**
	 * Määrab sliidi kõrguse (hitboxi kõrgus sliidimise ajal).
	 *
	 * @param slideHeight Sliidi kõrgus (peab olema vahemikus 0 kuni PLAYER_HEIGHT)
	 */
	public void setSlideHeight(double slideHeight) {
		if (slideHeight > 0 && slideHeight <= PLAYER_HEIGHT) {
			this.slideHeight = slideHeight;
		}
	}

	/**
	 * Tagastab mängija hitboxi kokkupõrgete kontrollimiseks.
	 *
	 * @return Mängija hitbox Node
	 */
	public Node getKuvand() {
		return hitbox;
	}

	/**
	 * Kontrollib, kas mängija on hetkel sliidimas.
	 *
	 * @return true kui mängija sliidib, false kui ei
	 */
	public boolean onSliidimas() {
		return sliidib;
	}
}