package DirectoryScan.start;

import javax.naming.ConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

import DirectoryScan.Service.DirectoryProcessorService;
import DirectoryScan.Service.impl.DirectoryProcessorServiceImpl;

/**
 * This class is used for start up application , create and inject beans and
 * delegate request to for further processing .
 * 
 * @author ravi
 *
 */
@SpringBootApplication
@PropertySource("classpath:app.properties")
@ComponentScan(basePackages = { "DirectoryScan" })
public class StartAppLication {
	private static final Logger logger = LoggerFactory.getLogger(StartAppLication.class);

	public static void main(String[] args) throws ConfigurationException {

		if (logger.isInfoEnabled()) {
			logger.info("Start of the apllication");
		}

		// STEP-1 - start up application and setting configuration.
		ConfigurableApplicationContext context = SpringApplication.run(StartAppLication.class, args);

		/*
		 * STEP-2 - instantiate DirectoryProcessorService and inject required
		 * dependencies.
		 */
		DirectoryProcessorService directoryProcessorService = context.getBean(DirectoryProcessorServiceImpl.class);

		// STEP-3 - call method to read required data and start initialization process.
		directoryProcessorService.processDirectoryScanRequest();

	}
}
