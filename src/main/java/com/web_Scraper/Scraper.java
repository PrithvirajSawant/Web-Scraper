package com.web_Scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Scraper {

	public static void main(String[] args) {
		List<String> urls = Arrays.asList("https://prithvirajsawant.github.io/","https://www.python.org", "https://www.wikipedia.org",
				"https://www.openai.com", "https://news.ycombinator.com"
		// Add more URLs as needed
		);

		// Create a thread pool with 10 threads
		ExecutorService executorService = Executors.newFixedThreadPool(10);

		for (String url : urls) {
			executorService.execute(() -> {
				scrapeUrl(url);
				try {
					Thread.sleep(1000); // Sleep for 2 seconds between requests
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			});
		}

		// Shut down the executor service
		executorService.shutdown();
	}

	private static void scrapeUrl(String url) {
		try {
			Document doc = Jsoup.connect(url).get();
			String title = doc.title();
			System.out.println("URL: " + url + " - Title: " + title);
		} catch (IOException e) {
			System.err.println("Error fetching " + url + ": " + e.getMessage());
		}
	}
}
