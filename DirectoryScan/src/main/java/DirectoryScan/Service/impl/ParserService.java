package DirectoryScan.Service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import DirectoryScan.constant.DirectoryScanConstants;
import DirectoryScan.model.DirectoryScanData;
import DirectoryScan.model.DirectoryScanDataBuilder;

@Service
public class ParserService {
	private static final Logger logger = LoggerFactory.getLogger(ParserService.class);

	/**
	 * This method is used to create DirectoryScanData with the help of provided
	 * resources as parameter.
	 * 
	 * @param path , this is the path for resource.
	 * @return
	 */
	public DirectoryScanData buildDirectoryScanData(Path path) {

		return new DirectoryScanDataBuilder().setResourcePath(path.toFile().getParent())
				.setResourceName(path.toFile().getName()).setWordCount(getWordCountInFile(path))
				.setVowelCount(getVowelsCountInFile(path)).setSpecialCharCount(getSpecialCharactersCountInFile(path))
				.setFile(true).build();
	}

	/**
	 * This method is used to accept file path as a parameter and count total number
	 * of words in that file
	 * 
	 * @param path , this is path for file.
	 * @return , number of word count in file.
	 */
	public long getWordCountInFile(Path path) {

		try {
			return Files.lines(path).flatMap(str -> Stream.of(str.split("[ ,.!?\r\n]"))).filter(s -> s.length() > 0)
					.count();
		} catch (IOException e) {
			logger.info(e.getMessage());
		}

		return 0;

	}

	/**
	 * This method is used to accept file path as a parameter and count total number
	 * of vowels in that file
	 * 
	 * @param path , this is path for file.
	 * @return , number of vowels count in file.
	 */
	public long getVowelsCountInFile(Path path) {

		return getCountOfChars(DirectoryScanConstants.VOWELS, path);
	}

	/**
	 * This method is used to accept file path as a parameter and count total number
	 * of given characters in that file
	 * 
	 * @param path , this is path for file.
	 * @return , total number of characters in file.
	 */
	private long getCountOfChars(List<Character> charsToFilter, Path path) {
		try {
			return Files.lines(path).flatMap(str -> Stream.of(str.split("[ ,.!?\r\n]"))).flatMap(
					str -> str.toLowerCase().chars().mapToObj(c -> (char) c).collect(Collectors.toList()).stream())
					.filter(x -> charsToFilter.contains(x)).count();
		} catch (IOException e) {
			logger.info(e.getMessage());
		}

		return 0;
	}

	/**
	 * This method is used to accept file path as a parameter and count total number
	 * of special characters in that file
	 * 
	 * @param path , this is path for file.
	 * @return , number of characters count in file.
	 */
	public long getSpecialCharactersCountInFile(Path path) {
		return getCountOfChars(DirectoryScanConstants.SPECIALCHARS, path);
	}

}
