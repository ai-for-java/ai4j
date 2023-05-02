package dev.ai4j.agent.tool.search;

import dev.ai4j.agent.tool.Tool;
import lombok.Builder;
import lombok.val;
import serpapi.SerpApiSearch;
import serpapi.SerpApiSearchException;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

/**
 * Uses <a href="https://serpapi.com/">SerpAPI</a> internally
 * You have to provide a valid API key, which you can find <a href="https://serpapi.com/manage-api-key">here</a>
 * See also: <a href="https://github.com/serpapi/google-search-results-java">Java client library</a>
 */

@Builder
public class SearchTool implements Tool {

    private final String serpApiKey;
    private final SearchProvider searchProvider;
    private final String location;

    public SearchTool(String serpApiKey, SearchProvider searchProvider, String location) {
        this.serpApiKey = serpApiKey;
        this.searchProvider = searchProvider == null ? SearchProvider.GOOGLE : searchProvider;
        this.location = location;
    }

    @Override
    public String id() {
        return "search";
    }

    @Override
    public String description() {
        return "A search engine. Useful for when you need to answer questions about current events. Input should be a search query.";
    }

    @Override
    public Optional<String> execute(String searchQuery) {

        val parameters = new HashMap<String, String>();
        parameters.put("q", searchQuery);
        if (location != null) {
            parameters.put("location", location);
        }

        val search = new SerpApiSearch(parameters, serpApiKey, searchProvider.name().toLowerCase());

        try {
            val json = search.getJson();

            val links = new ArrayList<String>();

            json.get("organic_results").getAsJsonArray().forEach(
                    result -> {
                        try {
                            val link = result.getAsJsonObject().get("link").getAsString();
                            links.add(validate(link));
                        } catch (Exception e) {
                            // ignore malformed URI
                        }
                    }
            );

            return Optional.of(String.join("\n", links));

        } catch (SerpApiSearchException e) {
            // TODO retry?
        }

        return Optional.empty();
    }

    private String validate(String link) {
        URI.create(link);
        return link;
    }
}
