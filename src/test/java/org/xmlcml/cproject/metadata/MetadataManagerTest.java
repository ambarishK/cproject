package org.xmlcml.cproject.metadata;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.xmlcml.cproject.CMineFixtures;
import org.xmlcml.cproject.files.CProject;
import org.xmlcml.cproject.files.CTree;
import org.xmlcml.cproject.util.RectangularTable;

public class MetadataManagerTest {

	private static final Logger LOG = Logger.getLogger(MetadataManagerTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
//	private static final String GETPAPERS_NEW = "../getpapersNew";

	/** EXTRACTS SINGLE COLUMN FROM TABLE AND WRITES TO NEW CSV.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGetDOIColumnAsCSV() throws IOException {
		
		CProject cProject = new CProject(CMineFixtures.GETPAPERS_SRC);
		File inputCsvFile = new File(cProject.getDirectory(), "crossref_common.csv");
		RectangularTable table = RectangularTable.readTable(inputCsvFile, true);
		List<String> col2 = table.getColumn(MetadataManager.DOI);
		Assert.assertEquals(12141, col2.size());
		Assert.assertEquals("col02", "10.1002/1873-3468.12075", col2.get(0));

	}

	/** NOT FINISHED? */
	@Test
	// FIXME
	public void testGetFreshQuickscrapeDirectories() throws IOException {
		if (!CMineFixtures.exist(CMineFixtures.GETPAPERS_NEW)) return;
		int i = 1;
		CProject getpapersProject = new CProject(new File(CMineFixtures.GETPAPERS_NEW, "2016020"+i+"-articles"));
		List<String> flattenedCrossrefUrls = getpapersProject.extractShuffledFlattenedCrossrefUrls();
		File quickscrapeDir = new File(getpapersProject.getDirectory(), MetadataManager.QUICKSCRAPE_DIR);
		CProject quickscrapeProject = new CProject(quickscrapeDir);
		
		for (CTree cTree : quickscrapeProject.getResetCTreeList()) {
			String doiname = cTree.getDirectory().getName();
			if (flattenedCrossrefUrls.contains(doiname)) {
				// add me 
			} else {
				LOG.warn("cannot find: "+doiname);
			}
		}
		MetadataManager metadataManager = new MetadataManager();
	}
	
}
