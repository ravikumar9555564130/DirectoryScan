package DirectoryScan.Service;

import javax.naming.ConfigurationException;

public interface DirectoryProcessorService {
	/**
	 * @throws ConfigurationException
	 */
	void processDirectoryScanRequest() throws ConfigurationException;
}
