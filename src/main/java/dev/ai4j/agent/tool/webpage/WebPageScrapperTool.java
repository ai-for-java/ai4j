package dev.ai4j.agent.tool.webpage;

import dev.ai4j.agent.Tool;
import lombok.val;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Optional;

public class WebPageScrapperTool implements Tool {

    @Override
    public String id() {
        return "webpage-scrapper";
    }

    @Override
    public String description() {
        return "A portal to the internet. Use this when you need to get content from a specific web page."
                + " You should provide a valid URL as an input and the tool with output all the text from that web page.";
    }

    @Override
    public Optional<String> execute(String webPageUri) {
        try {
            val webPage = Jsoup.connect(webPageUri).get();
            return Optional.of(webPage.text()); // TODO try html
        } catch (IOException e) {
            // TODO retry?
        }

        return Optional.empty();
    }
}
