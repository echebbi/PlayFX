module fr.kazejiyu.playfx {
	// JDK 9
	requires java.base;
	requires java.logging;
	
	// JavaFX
	requires javafx.fxml;
	requires transitive javafx.graphics;
	
	// Exported packages
	exports fr.kazejiyu.playfx;
	exports fr.kazejiyu.playfx.exceptions;
	exports fr.kazejiyu.playfx.injection;
}