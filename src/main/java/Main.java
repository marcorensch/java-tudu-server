import com.fasterxml.jackson.core.type.TypeReference;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.LoggerFactory;
import shared.infra.JSONSerializer;
import spark.Service;
import todo.infra.TodoController;
import todo.model.TodoItem;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
//        get("/hello", (req, res) -> "Hello World");
//        get("/hello/:id", (req, res) -> {
//            System.out.println(req);
//            return "Thanks";
//        });

        final Service server = Service.ignite();
        server.port(4567);

        // Hohe Kohesion = Aufsplittung
        new TodoController(server, false);

    }
}
