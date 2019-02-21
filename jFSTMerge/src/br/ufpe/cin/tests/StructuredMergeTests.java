package br.ufpe.cin.tests;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.BeforeClass;
import org.junit.Test;

import br.ufpe.cin.app.JFSTMerge;
import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.mergers.util.MergeContext;

public class StructuredMergeTests {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		//hidding sysout output
		@SuppressWarnings("unused")
		PrintStream originalStream = System.out;
		PrintStream hideStream    = new PrintStream(new OutputStream(){
			public void write(int b) {}
		});
		System.setOut(hideStream);
	}

	@Test
	public void test0() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/0/left.java"), 
				new File("testfiles/0/base.java"), 
				new File("testfiles/0/right.java"), 
				null);
		assertTrue(
				FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.structuredOutput)
				.equals("publicclassA{voidm(){x=x+3;}}")
				);
	}

	@Test
	public void test1() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/1/left.java"), 
				new File("testfiles/1/base.java"), 
				new File("testfiles/1/right.java"), 
				null);
		assertTrue(
				FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.structuredOutput)
				.contains("publicclassA{voidm(){x=0;<<<<<<<Unknownfile:ThisisabuginJDime.=======x=1;>>>>>>>")
				);
	}

	@Test
	public void test2() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/2/left.java"), 
				new File("testfiles/2/base.java"), 
				new File("testfiles/2/right.java"), 
				null);
		assertTrue(
				FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.structuredOutput)
				.equals("publicclassA{voidm(){y=0;x=1;}}")
				);
	}

	@Test
	public void test3() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/3/left.java"), 
				new File("testfiles/3/base.java"), 
				new File("testfiles/3/right.java"), 
				null);
		assertTrue(
				FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.structuredOutput)
				.equals("publicclassA{voidm(){z=0;x=1;y=1;}}")
				);
	}

	@Test
	public void test4() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/4/left.java"), 
				new File("testfiles/4/base.java"), 
				new File("testfiles/4/right.java"), 
				null);
		assertTrue(
				FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.structuredOutput)
				.contains("publicclassA{voidm(){z=0;x=1;y=<<<<<<<")
				);
	}

	@Test
	public void test5() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/5/left.java"), 
				new File("testfiles/5/base.java"), 
				new File("testfiles/5/right.java"), 
				null);
		assertTrue(
				FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.structuredOutput)
				.equals("publicclassA{privatebooleanwithHistory;publicbooleanuseHistory(){returnthis.withHistory;}}")
				);
	}

	@Test
	public void test6() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/6/left.java"), 
				new File("testfiles/6/base.java"), 
				new File("testfiles/6/right.java"), 
				null);
		assertTrue(
				FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.structuredOutput)
				.equals("publicclassA{publicstatic<Textendsjava.lang.Object>Collection<T>getServices(Class<T>serviceType){returngetServiceProvider().getServices(serviceType);}publicstatic<Textendsjava.lang.Object>TgetServices(Class<T>serviceType){List<T>services=getServiceProvider().getServices(serviceType);returnservices.stream().findFirst().orElseThrow(()->newMonetaryException(\"Nosuchservicefound:\"+serviceType));}}")
				);
	}

	@Test
	public void test7() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/7/left.java"), 
				new File("testfiles/7/base.java"), 
				new File("testfiles/7/right.java"), 
				null);
		assertTrue(
				FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.structuredOutput)
				.equals("publicclassA{publicstaticfinalStringEDITABLE_PARAMETER_ERROR_DATA=\"org.hdiv.action.EDITABLE_PARAMETER_ERROR_DATA\";publicstaticfinalStringMESSAGE_SOURCE_PATH=\"org.hdiv.msg.MessageResources\";publicstaticfinalStringAJAX_REQUEST=\"org.hdiv.ajaxrequest\";}")
				);
	}

	@Test
	public void test8() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/8/left.java"), 
				new File("testfiles/8/base.java"), 
				new File("testfiles/8/right.java"), 
				null);
		assertTrue(
				FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.structuredOutput)
				.equals("publicclassA{@SuppressWarnings(value={\"Duplicates\"})privatestaticvoidloadUserProperties()throwsIOException{try{UserConfigurationServiceuserConfigurationService=newUserConfigurationServiceImpl();userConfigurationService.loadUserProperties();}catch(Exceptionex){System.out.println(\"error\");ex.printStackTrace();}<<<<<<<MINEfor(Map.Entry<Object,Object>property:propertyEntries){Stringkey=(String)property.getKey();Stringvalue=(String)property.getValue();value=substitutePropertyReferences(value);setProperty(key,value);}=======>>>>>>>YOURS}")
				);
	}

	@Test
	public void test9() {
		MergeContext ctx = 	new JFSTMerge().mergeFiles(
				new File("testfiles/9/left.java"), 
				new File("testfiles/9/base.java"), 
				new File("testfiles/9/right.java"), 
				null);
		assertTrue(
				FilesManager.getStringContentIntoSingleLineNoSpacing(ctx.structuredOutput)
				.equals("publicclassA{privatevoidprocessPage(WebURLcurURL){if(page.isTruncated()){logger.warn(\"Warning:unknownpagesizeexceededmax-download-size,truncatedto:\"+\"({}),atURL:{}\",myController.getConfig().getMaxDownloadSize(),curURL.getURL());}parser.parse(page,curURL.getURL());if(shouldFollowLinksIn(page.getWebURL())){ParseDataparseData=page.getParseData();List<WebURL>toSchedule=newArrayList<>();intmaxCrawlDepth=myController.getConfig().getMaxDepthOfCrawling();for(WebURLwebURL:parseData.getOutgoingUrls()){webURL.setParentDocid(curURL.getDocid());webURL.setParentUrl(curURL.getURL());intnewdocid=docIdServer.getDocId(webURL.getURL());if(newdocid>0){webURL.setDepth((short)-1);webURL.setDocid(newdocid);}else{webURL.setDocid(-1);webURL.setDepth((short)(curURL.getDepth()+1));if((maxCrawlDepth==-1)||(curURL.getDepth()<maxCrawlDepth)){if(shouldVisit(page,webURL)){if(!shouldFollowLinksIn(webURL)||robotstxtServer.allows(webURL)){webURL.setDocid(docIdServer.getNewDocID(webURL.getURL()));toSchedule.add(webURL);}else{logger.debug(\"Notvisiting:{}aspertheserver\\'s\\\"robots.txt\\\"policy\",webURL.getURL());}}else{logger.debug(\"Notvisiting:{}asperyour\\\"shouldVisit\\\"policy\",webURL.getURL());}}}}frontier.scheduleAll(toSchedule);}else{logger.debug(\"Notlookingforlinksinpage{},asperyour\\\"shouldFollowLinksInPage\\\"policy\",page.getWebURL().getURL());}<<<<<<<MINE=======for(WebURLwebURL:parseData.getOutgoingUrls()){webURL.setParentDocid(curURL.getDocid());webURL.setParentUrl(curURL.getURL());intnewdocid=docIdServer.getDocId(webURL.getURL());if(newdocid>0){webURL.setDepth((short)-1);webURL.setDocid(newdocid);}else{webURL.setDocid(-1);webURL.setDepth((short)(curURL.getDepth()+1));if((maxCrawlDepth==-1)||(curURL.getDepth()<maxCrawlDepth)){if(shouldVisit(page,webURL)){if(robotstxtServer.allows(webURL)){webURL.setDocid(docIdServer.getNewDocID(webURL.getURL()));toSchedule.add(webURL);}else{logger.debug(\"Notvisiting:{}aspertheserver\\'s\\\"robots.txt\\\"\"+\"policy\",webURL.getURL());}}else{logger.debug(\"Notvisiting:{}asperyour\\\"shouldVisit\\\"policy\",webURL.getURL());}}}}>>>>>>>YOURS}}")
				);
	}
}
