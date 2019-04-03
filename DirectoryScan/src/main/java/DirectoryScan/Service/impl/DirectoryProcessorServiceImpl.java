package DirectoryScan.Service.impl;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.naming.ConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import DirectoryScan.Service.DirectoryProcessorService;
import DirectoryScan.constant.DirectoryScanConstants;

@Service
public class DirectoryProcessorServiceImpl implements DirectoryProcessorService {

	private static final Logger logger = LoggerFactory.getLogger(DirectoryProcessorServiceImpl.class);

	@Autowired
	private Environment env;
	@Autowired
	private CacheService cacheService;

	@Autowired
	private DirectoryWatchService dirwatchingservice;
	private List<String> fileExtensions;
	private String baseDirPath;
	private String absoluteDirPath;

	@Override
	public void processDirectoryScanRequest() throws ConfigurationException {

		// STEP-1 - read data from configuration file and get base directory info.

		if (!env.containsProperty(DirectoryScanConstants.BASE_DIR_PATH)) {
			throw new ConfigurationException("directory is not defined in properties file.");
		}

		baseDirPath = env.getProperty(DirectoryScanConstants.BASE_DIR_PATH);

		if (logger.isInfoEnabled()) {
			logger.info("baseDir :: " + baseDirPath);
		}

		File baseDirectory = new File(baseDirPath);
		if (!baseDirectory.exists()) {
			throw new ConfigurationException("base directory does not exist.");
		}

		if (!baseDirectory.isDirectory()) {
			throw new ConfigurationException("base directory property is not a directory.");
		}

		absoluteDirPath = baseDirectory.getAbsolutePath();

		if (logger.isInfoEnabled()) {
			logger.info("absoluteDirPath :: " + absoluteDirPath);
		}

		if (env.containsProperty(DirectoryScanConstants.FILE_EXTENSIONS)) {
			String fileExtensionSeparator = env.containsProperty(DirectoryScanConstants.FILE_EXTENSIONS_SEPARATOR)
					? env.getProperty(DirectoryScanConstants.FILE_EXTENSIONS_SEPARATOR)
					: DirectoryScanConstants.FILE_EXTENSIONS_SEPARATOR_DEFAULT;
			fileExtensions = Arrays
					.asList(env.getProperty(DirectoryScanConstants.FILE_EXTENSIONS).split(fileExtensionSeparator));
		} else {
			fileExtensions = Arrays.asList(DirectoryScanConstants.FILE_EXTENSIONS_DEFAULT);

		}

		if (logger.isInfoEnabled()) {
			logger.info("fileExtensions :: " + fileExtensions);
		}

		// STEP-2 - read data for configuration file.
		cacheService.initializeData(env);

		/*
		 * STEP-3 - call method for initialize basic info to scan directory and start
		 * watching base directory and its sub directories.
		 */
		dirwatchingservice.initializeAndStartDirWatching(absoluteDirPath, fileExtensions);

	}

}
