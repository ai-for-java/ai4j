import dev.ai4j.agent.tool.search.SearchTool;

import java.util.Optional;

import static dev.ai4j.agent.tool.search.SearchProvider.GOOGLE;

public class SearchToolExample {

    public static void main(String[] args) {

        SearchTool searchTool = SearchTool.builder()
                .serpApiKey(System.getenv("SERP_API_KEY")) // https://serpapi.com/manage-api-key
                .searchProvider(GOOGLE)
                .build();

        Optional<String> searchResult = searchTool.execute("Weather in New York");

        searchResult.ifPresent(System.out::println);
    }
}
