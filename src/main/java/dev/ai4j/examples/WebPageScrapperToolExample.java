package dev.ai4j.examples;

import dev.ai4j.agent.tool.webpage.WebPageScrapperTool;

import java.util.Optional;

public class WebPageScrapperToolExample {

    public static void main(String[] args) {

        WebPageScrapperTool scrapperTool = new WebPageScrapperTool();

        Optional<String> scrapperResult = scrapperTool.execute("https://en.wikipedia.org/wiki/Artificial_intelligence");

        scrapperResult.ifPresent(System.out::println);
    }
}
