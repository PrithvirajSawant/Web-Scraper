package com.web_Scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

//@WebServlet("/scrape")
public class WebScraperServlet extends HttpServlet {

//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        List<String> urls = Arrays.asList(
//                "https://prithvirajsawant.github.io/",
//                "https://www.python.org",
//                "https://www.wikipedia.org",
//                "https://www.openai.com",
//                "https://www.youtube.com"
//        );
	
//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        req.getRequestDispatcher("/input.jsp").forward(req, resp);
//    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String urlsParam = req.getParameter("urls");
        List<String> urls = Arrays.asList(urlsParam.split("\\s*,\\s*"));

        ExecutorService executorService = Executors.newFixedThreadPool(5);
        List<String> results = new ArrayList<>();

        for (String url : urls) {
            executorService.execute(() -> {
                String result = scrapeUrl(url);
                synchronized (results) {
                    results.add(result);
                }
                try {
                    Thread.sleep(1000); // Sleep for 1 second between requests
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        req.setAttribute("results", results);
        req.getRequestDispatcher("/results.jsp").forward(req, resp);
    }

    private String scrapeUrl(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                    .get();

            // Extract title
            String title = doc.title();

            // Extract headings
            Elements headings = doc.select("h1, h2, h3, h4, h5, h6");
            List<String> headingTexts = new ArrayList<>();
            for (Element heading : headings) {
                headingTexts.add(heading.text());
            }

            // Extract paragraphs
            Elements paragraphs = doc.select("p");
            List<String> paragraphTexts = new ArrayList<>();
            for (Element paragraph : paragraphs) {
                paragraphTexts.add(paragraph.text());
            }

            // Extract links
            Elements links = doc.select("a[href]");
            List<String> linkTexts = new ArrayList<>();
            for (Element link : links) {
                linkTexts.add(link.attr("href") + " - " + link.text());
            }

            // Combine the extracted information into a single string
            StringBuilder result = new StringBuilder();
            result.append("<div><strong>URL:</strong> ").append(url).append("</div>");
            result.append("<div><strong>Title:</strong> ").append(title).append("</div>");
            result.append("<div><strong>Headings:</strong> ").append(headingTexts).append("</div>");
            // result.append("<div><strong>Paragraphs:</strong> ").append(paragraphTexts).append("</div>");
            result.append("<div><strong>Links:</strong> ").append(linkTexts).append("</div>");

            return result.toString();
        } catch (IOException e) {
            return "Error fetching " + url + ": " + e.getMessage();
        }
    }
}
