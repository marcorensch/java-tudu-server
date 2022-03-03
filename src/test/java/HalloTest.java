import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class HalloTest {

    @Test
    public void testHelloWorld_returnsText() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:4567/todos"))
                .header("accept", "application/json")
                .build();

        HttpClient client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List todos = new JSONSerializer().deserialize(response.body(), new TypeReference<List<TodoItem>>(){});
        Assert.assertTrue(todos.size() > 0);
    }
}
