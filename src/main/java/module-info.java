module fr.kazejiyu.playfx {
	// JDK9
	requires java.base;
	requires java.logging;
	
	// JavaFX
	requires javafx.base;
	requires javafx.fxml;
	requires javafx.graphics;
	
	// Exported packages
	exports fr.kazejiyu.playfx;
	exports fr.kazejiyu.playfx.exceptions;
}