package DirectoryScan.Service.impl;

import java.io.File;

import javax.naming.ConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import DirectoryScan.constant.DirectoryScanConstants;
import DirectoryScan.model.DirectoryScanData;
import DirectoryScan.model.DirectoryScanDataBuilder;

@Service
public class CacheService {
	private String baseDirPath;
	private String baseDirAbsolutePath;
	private DirectoryScanData directoryScanData;

	@Autowired
	private DirectoryScanDataWriterService writerService;

	/**
	 * This method is used to read data from properties file ,and build the
	 * directoryScanData.
	 * 
	 * @param env , this is used read properties file.
	 * @throws ConfigurationException , throw ConfigurationException if any
	 *                                exception occurred.
	 */
	public void initializeData(Environment env) throws ConfigurationException {
		baseDirPath = env.getProperty(DirectoryScanConstants.BASE_DIR_PATH);
		File baseDir = new File(baseDirPath);
		baseDirAbsolutePath = baseDir.getAbsolutePath();
		directoryScanData = new DirectoryScanDataBuilder().setResourcePath(baseDirAbsolutePath)
				.setResourceName(baseDir.getName()).build();
	}

	
	/**
	 * This method is used to find out the resource that to be added in map for
	 * caching purpose.
	 * 
	 * @param key it hold the path for resource.
	 */
	public void getOrAddChildFolder(String key) {

		if (!StringUtils.isEmpty(key)) {
			String[] filePart = splitByFileSeparator(key);
			for (int index = 0; index < filePart.length - 1; index++) {
				directoryScanData = directoryScanData.getOrAddChildFolder(filePart[index]);
			}
			directoryScanData.getOrAddChildFolder(filePart[filePart.length - 1]);
		}

	}

	/**
	 * This method is used to split resource. 
	 * @param key , this is resource path.
	 * @return, it will return resource after spiting.
	 */
	private String[] splitByFileSeparator(String key) {
		return key.split(DirectoryScanConstants.FILE_SEPARATOR_PATTERN);
	}

	/**
	 * This method is used to add resource like file ,folder and sub folder in map.
	 * 
	 * @param key , this is the resource path.
	 * @param scanData , it hold the data regarding file or folder.
	 */
	public void add(String key, DirectoryScanData scanData) {
		String[] filePart = splitByFileSeparator(key);
		DirectoryScanData cacheFolder = directoryScanData;
		for (int index = 0; index < filePart.length - 1; index++) {
			cacheFolder = cacheFolder.getOrAddChildFolder(filePart[index]);
		}
		cacheFolder.addFileData(filePart[filePart.length - 1], scanData);

		this.computeAndWriteDataToFIle(filePart, directoryScanData, 0);

	}

	
	/**
	 * This method is used to write required and aggregate data to the file.
	 * 
	 * @param filePart
	 * @param directoryScanData
	 * @param index
	 */
	private void computeAndWriteDataToFIle(String[] filePart, DirectoryScanData directoryScanData, int index) {
		if (index < filePart.length) {
			computeAndWriteDataToFIle(filePart, directoryScanData.getOrAddChildFolder(filePart[index]), ++index);
			directoryScanData.computeAgg();
			if (directoryScanData.isFile()) {
				writerService.writeMTD(directoryScanData);
			} else {
				for (DirectoryScanData scanData : directoryScanData.getChildItemData().values()) {
					if (scanData.isFile()) {
						writerService.writeMTD(scanData);
					}
				}
				writerService.writeDMTD(directoryScanData);
			}
		}
	}

}
