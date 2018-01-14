package fr.kazejiyu.playfx.configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

/**
 * Properties serialized into a file as pairs (key=value).
 * <br><br>
 * This class internally uses instance of the standard {@link Properties} class
 * in order to load the property file.
 * 
 * @author Emmanuel CHEBBI
 */
public class SerializedProperties {
	
	/** The path of the file */
	private final InputStream is;
	
	/** The properties loaded from the file */
	private final Properties properties = new Properties();
	
	public SerializedProperties() {
		this(emptyInputStream());
	}
	
	private static InputStream emptyInputStream() {
		return new ByteArrayInputStream("".getBytes());
	}
	
	/**
	 * Creates a lazy serialized property.
	 * 
	 * @param is
	 * 			The stream from which the properties will be loaded. 
	 * 			Must not be {@code null}.
	 */
	public SerializedProperties(InputStream is) {
		this.is = Objects.requireNonNull(is);
	}
	
	/**
	 * Loads the properties from the file.
	 * 
	 * @throws IOException if an error occurs while de-serializing the properties
	 */
	public void load() throws IOException {
		properties.load(is);
	}
	
	/**
	 * Returns the properties' name.
	 * @return the properties' name.
	 */
	public List <?> getNames() {
		return Collections.list(properties.propertyNames());
	}
	
	/**
	 * Returns, if present, the property identified by {@code key}.
	 * 
	 * @param key
	 * 			The name of the property to look up.
	 * 
	 * @return return the property's value if it exists.
	 */
	public Optional <Object> get(String key) {
		return Optional.ofNullable(properties.getProperty(key));
	}

}