package DirectoryScan.Service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import DirectoryScan.model.DirectoryScanData;

@Service
public class DirectoryScanDataWriterService {

	private static final Logger logger = LoggerFactory.getLogger(DirectoryScanDataWriterService.class);

	/**
	 * Create file with extension '.mtd' and save below data corresponding to each file: -
	 * 1. Number of words count
	 * 2. Number of vowels count 
	 * 3. Number of special Characters: count
	 * 
	 * @param directoryScanData
	 */
	public void writeMTD(DirectoryScanData directoryScanData) {

		String fileName = directoryScanData.getResourceName();
		fileName = fileName.substring(0, fileName.lastIndexOf("."));
		File file = new File(directoryScanData.getResourcePath() + File.separator + fileName + ".mtd");

		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
			if (file.exists()) {
				file.delete();
				file.createNewFile();
			}
			bufferedWriter.write("Number of words: " + directoryScanData.getWordCount());
			bufferedWriter.write("\nNumber of vowels: " + directoryScanData.getVowelCount());
			bufferedWriter.write("\nNumber of special Characters: " + directoryScanData.getSpecialCharCount());
			bufferedWriter.flush();
		} catch (IOException e) {
			logger.info(e.getMessage());
		}

	}
	
	/**
	 * Create file with extension '.dmtd' and save below data  corresponding to each : -
	 * 1. Total Number of words count
	 * 2. Total Number of vowels count 
	 * 3. Total Number of special Characters: count
	 * 
	 * @param directoryScanData
	 */
	public void writeDMTD(DirectoryScanData directoryScanData) {
		
		
		File file = new File(directoryScanData.getResourcePath() + File.separator + directoryScanData.getResourceName() + ".dmtd");
		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
			if (file.exists()) {
				file.delete();
				file.createNewFile();
			}
			bufferedWriter.write("Total Number of words: " + directoryScanData.getWordCount());
			bufferedWriter.write("\nTotal Number of vowels: " + directoryScanData.getVowelCount());
			bufferedWriter.write("\nTotal Number of special Characters: " + directoryScanData.getSpecialCharCount());
			bufferedWriter.flush();
		} catch (IOException e) {
			logger.info(e.getMessage());
		}

	}

}
