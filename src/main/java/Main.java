import com.fasterxml.jackson.core.type.TypeReference;
import org.eclipse.jetty.http.HttpStatus;

import java.util.*;

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

        get("/todos/:id", "application/json", (req, res) -> {
            res.header("Content-Type", "application/json;charset=utf-8");

            Long idToRead = Long.valueOf(req.params().get(":id"));

            for(var item : todos){
                if(item.getId().equals(idToRead)){
                    res.status(200);
                    return new JSONSerializer().serialize(item);
                }
            }

            return null;
        });

        delete("/todos/:id", "application/json", (req, res) -> {
            res.header("Content-Type", "application/json;charset=utf-8");
            int oldLength = todos.size();
            System.out.println(oldLength);
            Long idToDelete = Long.valueOf(req.params().get(":id"));
            // Long id = Long.valueOf(req.params("id"));

            boolean success = todos.removeIf( todoItem -> todoItem.getId().equals(idToDelete));

            System.out.println(todos.size());
            if(!success){
                res.status(406);
            }

            return new JSONSerializer().serialize(todos);
        });

        post("/todos", "application/json", (req, res) -> {
            res.header("Content-Type", "application/json;charset=utf-8");

            final TodoItem newItem = new JSONSerializer().deserialize(req.body(), new TypeReference<>() {});

            newItem.id = (long) new Random().nextInt(1_000_000);

            System.out.println(req.body());

            // todos.add ==> Immer True mit Arraylist aber vorbereitet für mit Server
            if(todos.add(newItem)){
                res.status(201);
                return new JSONSerializer().serialize(newItem);
            }

            return null;
        });
    }
}
