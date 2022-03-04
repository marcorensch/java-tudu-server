import org.eclipse.jetty.http.HttpStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
//        get("/hello", (req, res) -> "Hello World");
//        get("/hello/:id", (req, res) -> {
//            System.out.println(req);
//            return "Thanks";
//        });

        List<TodoItem> todos = new ArrayList<>(
                Arrays.asList(
                TodoItem.create(1L,"Mein erster Task", "Die beschreibung"),
                TodoItem.create(2L,"Mein zweiter Task", "Die Beschreibung"),
                TodoItem.create(3L,"Mein dritter Task", "Die dritte Beschreibung")
                )
        );

        get("/todos", "application/json", (req, res) -> {
            res.header("Content-Type", "application/json;charset=utf-8");

            return new JSONSerializer().serialize(todos);
        });

        delete("/todos/:id", "application/json", (req, res) -> {
            res.header("Content-Type", "application/json;charset=utf-8");
            int oldLength = todos.size();
            System.out.println(oldLength);
            Long id = Long.valueOf(req.params().get(":id"));

            todos.removeIf( e -> e.getId().equals(id));

            System.out.println(todos.size());
            if(oldLength <= todos.size()){
                res.status(406);
            }

            return new JSONSerializer().serialize(todos);
        });
    }
}
