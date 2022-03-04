import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class MainTest {
    @Test
    public void deleteOneToDoWithValidID_returnsStatusCode200() throws IOException, InterruptedException {
        // Schlechter Test weil das Item effektiv entfernt wird - kann nicht mehrfach ausgeführt werden
        // failed beim 2. Versuch
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create("http://localhost:4567/todos/1"))
                .header("accept", "application/json")
                .build();

        HttpClient client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assert.assertEquals(200, response.statusCode());

    }
    @Test
    public void deleteOneToDoWithInvalidID_returnsStatusCode406() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create("http://localhost:4567/todos/999923949239422"))
                .header("accept", "application/json")
                .build();

        HttpClient client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assert.assertEquals(406, response.statusCode());
    }
}
