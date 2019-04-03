package DirectoryScan.constant;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public interface DirectoryScanConstants {
	String BASE_DIR_PATH = "watch.dir";
	String FILE_EXTENSIONS = "file.extensions";
	String FILE_EXTENSIONS_DEFAULT = "txt";
	String FILE_EXTENSIONS_SEPARATOR = "file.extensions.separator";
	String FILE_EXTENSIONS_SEPARATOR_DEFAULT = ",";
	String FILE_SEPARATOR_PATTERN = Pattern.quote(System.getProperty("file.separator"));
	List<Character> VOWELS = Arrays.asList('a', 'e', 'i' , 'o', 'u');
	List<Character> SPECIALCHARS = Arrays.asList('@', '#', '$', '*');
	String SORTINGORDER = "sorting.order";

}
