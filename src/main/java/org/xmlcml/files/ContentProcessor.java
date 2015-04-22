package org.xmlcml.files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nu.xom.Element;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xmlcml.args.ArgumentOption;
import org.xmlcml.args.DefaultArgProcessor;
import org.xmlcml.xml.XMLUtil;

/** manages the processing by Norma or AMI.
 * 
 * important components are the CMDir being processed and the ResultsElementList.
 * 
 * @author pm286
 *
 */
public class ContentProcessor {

	
	private static final Logger LOG = Logger.getLogger(ContentProcessor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static final String OVERWRITE = "overwrite";
	private static final String NO_DUPLICATES = "noDuplicates";
	private static final String MERGE = "merge";


	private CMDir cmDir;
	private ResultsElementList resultsElementList;
	private HashMap<String, ResultsElement> resultsBySearcherNameMap;
	private String duplicates = OVERWRITE;
	
	public ContentProcessor(CMDir cmDir) {
		this.cmDir = cmDir;
	}
	
	private void ensureResultsElementList() {
		if (resultsElementList == null) {
			resultsElementList = new ResultsElementList();
		}
	}

	public void addResultsElement(ResultsElement resultsElement0) {
		this.ensureResultsElementList();
		String title = resultsElement0.getTitle();
		if (title == null) {
			throw new RuntimeException("Results Element must have title");
		}
		checkNoDuplicatedTitle(title);
		resultsElementList.add(resultsElement0);
	}

	private void checkNoDuplicatedTitle(String title) {
			for (ResultsElement resultsElement : resultsElementList) {
				if (title.equals(resultsElement.getTitle())) {
					if (OVERWRITE.equals(duplicates)) {
						// carry on
					} else if (NO_DUPLICATES.equals(duplicates)) {
						throw new RuntimeException("Cannot have two ResultsElement with same title: "+title);
					} else if (MERGE.equals(duplicates)) {
						throw new RuntimeException("Merge not supported: Cannot have two ResultsElement with same title: "+title);
					}
				}
			}
	}
	
	public void outputResultElements(ArgumentOption option, DefaultArgProcessor argProcessor ) {
		resultsElementList = new ResultsElementList();
		for (DefaultSearcher optionSearcher : argProcessor.getSearcherList()) {
			String name = optionSearcher.getName();
			ResultsElement resultsElement = resultsBySearcherNameMap.get(name);
			if (resultsElement != null) {
				resultsElement.setTitle(name);
				resultsElementList.add(resultsElement);
			}
		}
		this.createResultsDirectoriesAndOutputResultsElement(
				option, resultsElementList, CMDir.RESULTS_XML);
	}

	public void writeResults(String resultsFileName, String results) throws Exception {
		File resultsFile = new File(cmDir.getDirectory(), resultsFileName);
		FileUtils.writeStringToFile(resultsFile, results);
	}

	public void writeResults(File resultsFile, Element resultsXML) {
		try {
			XMLUtil.debug(resultsXML, new FileOutputStream(resultsFile), 1);
		} catch (IOException e) {
			throw new RuntimeException("cannot write XML ", e);
		}
	}

	public void writeResults(String resultsFileName, Element resultsXML) {
		writeResults(new File(cmDir.getDirectory(), resultsFileName), resultsXML);
	}
	
	/** creates a subdirectory of results/ and writes each result file to its own directory.
	 * 
	 * Example:
	 * 		cmdir1_2_3/
	 * 			results/
	 * 				words/
	 * 					frequencies/
	 * 						results.xml
	 * 					lengths/
	 * 						results.xml
	 * 
	 * here the option is defined in an element in args.xml with name="words"
	 * 
	 * @param option 
	 * @param resultsElementList
	 * @param resultsDirectoryName
	 */
	public List<File> createResultsDirectoriesAndOutputResultsElement(
			ArgumentOption option, ResultsElementList resultsElementList, String resultsDirectoryName) {
		File optionDirectory = new File(cmDir.getResultsDirectory(), option.getName());
		List<File> outputDirectoryList = new ArrayList<File>();
		for (ResultsElement resultsElement : resultsElementList) {
			File outputDirectory = createResultsDirectoryAndOutputResultsElement(optionDirectory, resultsElement);
			outputDirectoryList.add(outputDirectory);
		}
		return outputDirectoryList;
		
	}

	public File createResultsDirectoryAndOutputResultsElement(
			ArgumentOption option, ResultsElement resultsElement, String resultsDirectoryName) {
		File optionDirectory = new File(cmDir.getResultsDirectory(), option.getName());
		File outputDirectory = createResultsDirectoryAndOutputResultsElement(optionDirectory, resultsElement);
		return outputDirectory;
		
	}

	private File createResultsDirectoryAndOutputResultsElement(File optionDirectory, ResultsElement resultsElement) {
		File resultsSubDirectory = null;
		String title = resultsElement.getTitle();
		if (title == null) {
			LOG.error("null title");
		} else {
			resultsSubDirectory = new File(optionDirectory, title);
			resultsSubDirectory.mkdirs();
			File resultsFile = new File(resultsSubDirectory, CMDir.RESULTS_XML);
			writeResults(resultsFile, resultsElement);
			LOG.debug("Wrote "+resultsFile.getAbsolutePath());
		}
		return resultsSubDirectory;
	}
	
	public String getDuplicates() {
		return duplicates;
	}

	public void setDuplicates(String duplicates) {
		this.duplicates = duplicates;
	}

	public CMDir getCmDir() {
		return cmDir;
	}

	public void setCmDir(CMDir cmDir) {
		this.cmDir = cmDir;
	}

	public ResultsElementList getResultsElementList() {
		return resultsElementList;
	}

	public void setResultsElementList(ResultsElementList resultsElementList) {
		this.resultsElementList = resultsElementList;
	}
	
	
}
