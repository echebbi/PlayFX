package fr.kazejiyu.piecefx.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

/**
 * Properties serialized into a file as pairs (key=value).
 * 
 * @author Emmanuel CHEBBI
 */
public class SerializedProperties {
	
	/** The path of the file */
	private final InputStream is;
	
	/** The properties loaded from the file */
	private final Properties properties = new Properties();
	
	public SerializedProperties(InputStream is) {
		this.is = is;
	}
	
	/**
	 * Loads the properties from the file.
	 * 
	 * @throws IOException if an error occurs while de-serializing the properties
	 */
	public void load() throws IOException {
		properties.load(is);
	}
	
	public List <?> getNames() {
		return Collections.list(properties.propertyNames());
	}
	
	public Optional <Object> get(String key) {
		return Optional.ofNullable(properties.getProperty(key));
	}

}