package todo.infra;

import com.fasterxml.jackson.core.type.TypeReference;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.LoggerFactory;
import shared.infra.JSONSerializer;
import spark.Filter;
import todo.model.TodoItem;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

import static spark.Spark.*;
import static spark.Spark.exception;

public class TodoController {

    public TodoController() {
        List<TodoItem> todos = new ArrayList<>(
                Arrays.asList(
                        TodoItem.create(1L,"Mein erster Task", "Die beschreibung"),
                        TodoItem.create(2L,"Mein zweiter Task", "Die Beschreibung"),
                        TodoItem.create(3L,"Mein dritter Task", "Die dritte Beschreibung")
                )
        );

        get("/todos", "application/json", (req, res) -> {
            String json = "";
            if(req.queryParams("filter") != null){
                // der Query Parameter enthält das Wort Filter
                String filterString = req.queryParams("filter").toLowerCase(Locale.ROOT);
                ArrayList filteredItems = new ArrayList();
                for (TodoItem item: todos) {
                    if(item.label.toLowerCase(Locale.ROOT).contains(filterString)){
                        filteredItems.add(item);
                    }
                }
                System.out.println(filterString);
                json = new JSONSerializer().serialize(filteredItems);

            }else{
                json = new JSONSerializer().serialize(todos);
            }
            res.status(HttpStatus.OK_200);
            return json;
        });

        get("/todos/:id", "application/json", (req, res) -> {
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
            int oldLength = todos.size();
            System.out.println(oldLength);
            Long idToDelete = Long.valueOf(req.params().get(":id"));
            // Long id = Long.valueOf(req.params("id"));

            boolean success = todos.removeIf( todoItem -> todoItem.getId().equals(idToDelete));

            System.out.println(todos.size());
            if(!success){
                res.status(404);
            }else{
                res.status(HttpStatus.NO_CONTENT_204);
            }

            return "";
        });

        post("/todos", "application/json", (req, res) -> {
            System.out.println("post called");
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
        options("*", (req, res) -> "{}");           // Nötig damit HTML Client funktioniert FF etc

        // Vor jedem Routen Aufruf
        before((req,res) ->{
            // kann in der Methode überschrieben werden:
            res.header("Content-Type", "*/*;charset=utf-8");
        });

        // Nach jedem Routen Aufruf
        after((Filter) (req, res) -> {
            res.header("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS");
            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Headers", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin,");
//            res.header("Access-Control-Allow-Credentials", "true");
        });

        // Bei einer Exception
        exception(Exception.class, (ex,req,res) ->{
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);

            LoggerFactory.getLogger("server").error(sw.toString());
            res.body("");
            res.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
        });
    }
}
