module ee.tlu.game.infiniterunner {
	requires javafx.controls;
	requires javafx.fxml;

	opens ee.tlu.game.infiniterunner to javafx.fxml;
	exports ee.tlu.game.infiniterunner;
}