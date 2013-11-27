package at.ac.tuwien.dsg.cloud.services.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.services.SymbolProvider;

public class ConfigurationFileSymbolProvider implements SymbolProvider {

	private Properties symbolsFromFile;
	private Logger logger = Logger
			.getLogger(ConfigurationFileSymbolProvider.class);

	public ConfigurationFileSymbolProvider(String file) throws IOException {
		symbolsFromFile = new Properties();

		File confFile = new File(file);

		try {
			if (confFile.exists() && confFile.isFile()) {
				symbolsFromFile.load(new FileInputStream(file));
			} else {
				this.logger.warn("The file " + confFile.getAbsolutePath()
						+ " is missing.");
			}
		} catch (FileNotFoundException e) {
			logger.error("Error while reading Configuration file " + confFile,
					e);
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("Error while reading Configuration file  " + confFile,
					e);
			throw e;
		} catch (Exception e) {
			logger.error(
					"Error while reading Configuration file from classpath "
							+ confFile, e);
			throw new RuntimeException(e);
		}

		logger.info("Properties read from " + file);
		logger.info("" + symbolsFromFile.keySet());

	}

	// I suspect that this is something already there... like for message
	// service
	public ConfigurationFileSymbolProvider(InputStream resourceAsStream)
			throws IOException {
		try {
			symbolsFromFile.load(resourceAsStream);
		} catch (IOException e) {
			logger.error(
					"Error while reading Configuration file from classpath", e);
			throw e;
		} catch (Exception e) {
			logger.error(
					"Error while reading Configuration file from classpath", e);
			throw new RuntimeException(e);
		}

		logger.info("Properties read from classpath resources");
		logger.info("" + symbolsFromFile.keySet());
	}

	public String valueForSymbol(String symbolKey) {
		if (symbolsFromFile.containsKey(symbolKey)) {
			return symbolsFromFile.getProperty(symbolKey);
		} else {
			return null;
		}
	}

}
