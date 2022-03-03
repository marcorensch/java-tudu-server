import org.eclipse.jetty.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
//        get("/hello", (req, res) -> "Hello World");
//        get("/hello/:id", (req, res) -> {
//            System.out.println(req);
//            return "Thanks";
//        });

        get("/todos", "application/json", (req, res) -> {
            res.header("Content-Type", "application/json;charset=utf-8");
            List<TodoItem> todos = List.of(
                    TodoItem.create(1,"Mein erster Task", "Die beschreibung"),
                    TodoItem.create(2,"Mein zweiter Task", "Die Beschreibung")
                    TodoItem.create(2,"Mein dritter Task", "Die dritte Beschreibung")
            );
            return new JSONSerializer().serialize(todos);
        });
    }
}
