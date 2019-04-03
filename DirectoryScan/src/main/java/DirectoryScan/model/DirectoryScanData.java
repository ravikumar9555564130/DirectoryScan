package DirectoryScan.model;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import DirectoryScan.constant.DirectoryScanConstants;

public class DirectoryScanData {
	
	private static final Logger logger = LoggerFactory.getLogger(DirectoryScanData.class);

	private static final String SPECIAL_CHARS = "specialChars";
	private static final String VOWELS = "vowels";
	private static final String WORDS = "words";

	private String resourceName;
	private long wordCount;
	private long vowelCount;
	private long specialCharCount;
	private final String resourcePath;
	private final boolean isFile;
	/**
	 * For every parent and child directory or file we are creating this map with
	 * value of ItemData in map
	 */
	private final ConcurrentHashMap<String, DirectoryScanData> folderDirectoryScanDataMap;

	public DirectoryScanData(String resourcePath, String resourceName, long wordCount, long vowelCount,
			long specialCharCount, boolean isFile) {
		this.resourcePath = resourcePath;
		this.resourceName = resourceName;
		this.wordCount = wordCount;
		this.vowelCount = vowelCount;
		this.specialCharCount = specialCharCount;

		this.folderDirectoryScanDataMap = new ConcurrentHashMap<>();
		this.isFile = isFile;
	}

	/**
	 * this method is used to compute aggregate data for file inside each folder and
	 * their sub folder.
	 * 
	 */
	public void computeAgg() {
		if (!isFile()) {
			synchronized (this) {
				this.wordCount = 0;
				this.vowelCount = 0;
				this.specialCharCount = 0;
				for (DirectoryScanData itemData : folderDirectoryScanDataMap.values()) {
					this.wordCount += itemData.getWordCount();
					this.vowelCount += itemData.getVowelCount();
					this.specialCharCount += itemData.getSpecialCharCount();
				}
			}
		}
	}

	/**
	 * This method is used to check folder is available in map or not.
	 * 
	 * @param childFolder
	 * @return
	 */
	public boolean containsChildFolder(String childFolder) {
		return folderDirectoryScanDataMap.containsKey(childFolder);
	}

	/**
	 * This method is used to check resource(folder or file) is available in map or
	 * not.if available then return, if not available then prepare DirectoryScanData
	 * and put in map as value and resource as key.
	 * 
	 * @param childFolder
	 * @return
	 */
	public DirectoryScanData getOrAddChildFolder(String childFolder) {
		
		if (logger.isInfoEnabled()) {
			logger.info("folderDirectoryScanDataMap :: " +folderDirectoryScanDataMap);
		}
		
		
		if (containsChildFolder(childFolder)) {
			return folderDirectoryScanDataMap.get(childFolder);
		} else {
			DirectoryScanData data = new DirectoryScanDataBuilder()
					.setResourcePath(resourcePath + File.separator + childFolder).setResourceName(childFolder).build();
			folderDirectoryScanDataMap.put(childFolder, data);
			return data;
		}
	}

	public ConcurrentHashMap<String, DirectoryScanData> getChildItemData() {
		return folderDirectoryScanDataMap;
	}

	public void addFileData(String fileName, DirectoryScanData fileData) {
		folderDirectoryScanDataMap.put(fileName, fileData);
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceNamee(String itemName) {
		this.resourceName = itemName;
	}

	public long getWordCount() {
		return wordCount;
	}

	public void setWordCount(long wordCount) {
		this.wordCount = wordCount;
	}

	public long getVowelCount() {
		return vowelCount;
	}

	public void setVowelCount(long vowelCount) {
		this.vowelCount = vowelCount;
	}

	public long getSpecialCharCount() {
		return specialCharCount;
	}

	public void setSpecialCharCount(long specialCharCount) {
		this.specialCharCount = specialCharCount;
	}

	public String getResourcePath() {
		return resourcePath;
	}

	public ConcurrentHashMap<String, DirectoryScanData> getItemData() {
		return folderDirectoryScanDataMap;
	}

	public boolean isFile() {
		return isFile;
	}

	public long getCount() {
		long count = 0;
		String sortingOrder = DirectoryScanConstants.SORTINGORDER;
		if (!StringUtils.isEmpty(sortingOrder)) {
			switch (sortingOrder.toLowerCase()) {
			case WORDS:
				count = getWordCount();
				break;
			case VOWELS:
				count = getVowelCount();
				break;
			case SPECIAL_CHARS:
				count = getSpecialCharCount();
				break;
			}
		}

		return count;
	}

	@Override
	public String toString() {
		return "DirectoryScanData [resourceName=" + resourceName + ", wordCount=" + wordCount + ", vowelCount="
				+ vowelCount + ", specialCharCount=" + specialCharCount + ", resourcePath=" + resourcePath + ", isFile="
				+ isFile + ", folderDirectoryScanDataMap=" + folderDirectoryScanDataMap + "]";
	}

}
