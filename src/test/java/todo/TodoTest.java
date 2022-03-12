package todo;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Assert;
import org.junit.Test;
import shared.infra.JSONSerializer;
import todo.model.TodoItem;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class TodoTest {
    @Test
    public void getTodos_should_returnListWith200OK() throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:4567/todos"))
                .GET()
                .header("accept", "application/json")
                .build();

        final HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        Assert.assertEquals("application/json;charset=utf-8", response.headers().firstValue("content-type").get());
        Assert.assertEquals(200, response.statusCode());

        final List<TodoItem> todos = new JSONSerializer().deserialize(response.body(), new TypeReference<>() { });

        Assert.assertTrue("Should have any items", 0 < todos.size());
    }

    @Test
    public void deleteOneToDo_shouldDelete_andNoMoreRead() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create("http://localhost:4567/todos/1"))
                .header("accept", "application/json")
                .build();

        HttpClient client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assert.assertEquals(204, response.statusCode());

        HttpRequest verifyDelete = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:4567/todos/1"))
                .header("accept", "application/json")
                .build();

        HttpClient VerifyClient = HttpClient.newBuilder().build();
        var VerifyResponse = VerifyClient.send(verifyDelete, HttpResponse.BodyHandlers.ofString());

        Assert.assertEquals(404, VerifyResponse.statusCode());

    }

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

        Assert.assertEquals(204, response.statusCode());

    }

    @Test
    public void deleteOneToDoWithInvalidID_returnsStatusCode404() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create("http://localhost:4567/todos/999923949239422"))
                .header("accept", "application/json")
                .build();

        HttpClient client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assert.assertEquals(404, response.statusCode());
    }

    @Test
    public void createTodo_shouldReturnStatus201AndReadIsPossible() throws IOException, InterruptedException {
        TodoItem newToDo = TodoItem.create("Test", "Test ToDo");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(new JSONSerializer().serialize(newToDo)))
                .uri(URI.create("http://localhost:4567/todos"))
                .header("accept", "application/json")
                .build();

        HttpClient client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assert.assertEquals(201, response.statusCode());

        // Aufgaben: Wir erhalten oben in der Rsponse das Objekt zurück dieses können wir anschliessend die ID rausholen um mit get zu prüfen
        TodoItem createdToDo = new JSONSerializer().deserialize(response.body(), new TypeReference<>() {});

        HttpRequest checkReq = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:4567/todos"))
                .header("accept", "application/json")
                .build();

        var checkResponse = client.send(checkReq,HttpResponse.BodyHandlers.ofString());

        Assert.assertEquals(200, checkResponse.statusCode());
    }

    @Test
    public void getTodosWithFilterShouldReturnCustomElement() throws IOException, InterruptedException {
        //PreFlight (Arrange):
        TodoItem newToDo = TodoItem.create("Custom Filter Test Element2kF", "ToDo for Testing");
        HttpRequest crequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(new JSONSerializer().serialize(newToDo)))
                .uri(URI.create("http://localhost:4567/todos"))
                .header("accept", "application/json")
                .build();

        HttpClient client = HttpClient.newBuilder().build();
        var created = client.send(crequest, HttpResponse.BodyHandlers.ofString());

        Assert.assertEquals(201, created.statusCode());

        // Filtered GET Request (Act):
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:4567/todos?filter=custom%20filter%20test2kF"))
                .header("accept", "application/json")
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        ArrayList<TodoItem> createdToDos = new JSONSerializer().deserialize(response.body(), new TypeReference<>() {});

        // Check (Assert)
        for (TodoItem item: createdToDos) {
            Assert.assertEquals(newToDo.label, item.label);
        }
    }
}
