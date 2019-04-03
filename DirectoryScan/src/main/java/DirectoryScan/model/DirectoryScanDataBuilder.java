package DirectoryScan.model;

public class DirectoryScanDataBuilder {
	
	private String resourceName;
	private long wordCount;
	private long vowelCount;
	private long specialCharCount;
	private String resourcePath;
	private boolean file;
	
	public DirectoryScanDataBuilder setResourceName(String resourceName) {
		this.resourceName = resourceName;
		return this; 
	}
	
	public DirectoryScanDataBuilder setWordCount(long wordCount) {
		this.wordCount = wordCount;
		return this;
	}
	
	public DirectoryScanDataBuilder setVowelCount(long vowelCount) {
		this.vowelCount = vowelCount;
		return this;
	}
	
	public DirectoryScanDataBuilder setSpecialCharCount(long specialCharCount) {
		this.specialCharCount = specialCharCount;
		return this;
	}
	
	public DirectoryScanDataBuilder setResourcePath(String resourcePath) {
		this.resourcePath = resourcePath;
		return this;
	}
	
	public DirectoryScanDataBuilder setFile(boolean file) {
		this.file = file;
		return this;
	}
	
	public DirectoryScanData build() {
		return new DirectoryScanData(resourcePath, resourceName, wordCount, vowelCount, specialCharCount, file);
	}
	

}
