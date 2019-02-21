public class A {

	private void processPage(WebURL curURL) {
		parser.parse(page, curURL.getURL());

		if (shouldFollowLinksIn(page.getWebURL())) {
			ParseData parseData = page.getParseData();
			List<WebURL> toSchedule = new ArrayList<>();
			int maxCrawlDepth = myController.getConfig().getMaxDepthOfCrawling();
			for (WebURL webURL : parseData.getOutgoingUrls()) {
				webURL.setParentDocid(curURL.getDocid());
				webURL.setParentUrl(curURL.getURL());
				int newdocid = docIdServer.getDocId(webURL.getURL());
				if (newdocid > 0) {
					// This is not the first time that this Url is visited. So, we set the depth to a negative number.
					webURL.setDepth((short) -1);
					webURL.setDocid(newdocid);
				} else {
					webURL.setDocid(-1);
					webURL.setDepth((short) (curURL.getDepth() + 1));
					if ((maxCrawlDepth == -1) || (curURL.getDepth() < maxCrawlDepth)) {
						if (shouldVisit(page, webURL)) {
							if (!shouldFollowLinksIn(webURL) || robotstxtServer.allows(webURL)) {
								webURL.setDocid(docIdServer.getNewDocID(webURL.getURL()));
								toSchedule.add(webURL);
							} else {
								logger.debug("Not visiting: {} as per the server's \"robots.txt\" policy", webURL.getURL());
							}
						} else {
							logger.debug("Not visiting: {} as per your \"shouldVisit\" policy", webURL.getURL());
						}
					}
				}
			}
			frontier.scheduleAll(toSchedule);
		} else {
			logger.debug("Not looking for links in page {}, as per your \"shouldFollowLinksInPage\" policy",
					page.getWebURL().getURL());
		}
		visit(page);
	}
}

