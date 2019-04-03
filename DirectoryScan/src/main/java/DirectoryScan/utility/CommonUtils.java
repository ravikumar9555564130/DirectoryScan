package DirectoryScan.utility;

import java.util.List;

public class CommonUtils {

	/**
	 * This method is used to find out he relative path.
	 * 
	 * @param filePath
	 * @param watchDir
	 * @return
	 */
	public static final String relativePath(String filePath, String watchDir) {
		if (filePath.length() == watchDir.length()) {
			return "";
		}
		return filePath.substring(watchDir.length() + 1);
	}

	/**
	 * This method is used to check given file type.
	 * 
	 * @param fileName
	 * @param fileExtensions
	 * @return
	 */
	public static boolean checkFileType(String fileName, List<String> fileExtensions) {
		String extension = fileName.split("\\.")[1];
		return fileExtensions.stream().anyMatch(e -> e.trim().equalsIgnoreCase(extension));
	}

}
