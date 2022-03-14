package todo.infra;

import com.fasterxml.jackson.core.type.TypeReference;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.LoggerFactory;
import shared.infra.JSONSerializer;
import spark.Filter;
import spark.Redirect;
import spark.Service;
import todo.model.TodoItem;
import todo.service.TodoService;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

import static spark.Spark.*;
import static spark.Spark.exception;

public class TodoController {

    public TodoController(Service server, boolean isTest) {
        var todoService = new TodoService(isTest ? new InMemoryRepository() : new SQLiteRepository());

        server.get("/todos", "application/json", (req, res) -> {
            String json;
            if(req.queryParams("filter") != null){
                // der Query Parameter enthält das Wort Filter
                List filteredItems = todoService.getFilteredItems(req.queryParams("filter").toLowerCase(Locale.ROOT));
                json = new JSONSerializer().serialize(filteredItems);
            }else{
                List items = todoService.getAllItems();
                json = new JSONSerializer().serialize(items);
            }
            res.status(HttpStatus.OK_200);
            return json;
        });

        server.redirect.get("/todos/", "/todos", Redirect.Status.MOVED_PERMANENTLY);

        server.get("/todos/:id", "application/json", (req, res) -> {
            Long idToRead = Long.valueOf(req.params().get(":id"));
            TodoItem item = todoService.getById(idToRead);

            res.status(HttpStatus.OK_200);
            return new JSONSerializer().serialize(item);
        });



        server.delete("/todos/:id", "application/json", (req, res) -> {

            long idToDelete = Long.valueOf(req.params().get(":id"));

            if(!todoService.delete( idToDelete )){
                res.status(404);
            }else{
                res.status(HttpStatus.NO_CONTENT_204);
            }

            return "";
        });

        server.post("/todos", "application/json", (req, res) -> {

            System.out.println("post called");
            final TodoItem newItem = new JSONSerializer().deserialize(req.body(), new TypeReference<>() {});

            long createdId = todoService.add(newItem);

            if(createdId > 0){
                res.status(201);
                return new JSONSerializer().serialize(createdId);
            }else{
                // Server Error hier
                // Todo: Header
                res.status(500);
            }

            return null;
        });


        server.options("*", (req, res) -> "{}");           // Nötig damit HTML Client funktioniert FF etc

        // Vor jedem Routen Aufruf
        server.before((req,res) ->{
            // kann in der Methode überschrieben werden:
            res.header("Content-Type", "application/json;charset=utf-8");
        });

        // Nach jedem Routen Aufruf
        server.after((req, res) -> {
            res.header("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS");
            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Headers", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin,");
//            res.header("Access-Control-Allow-Credentials", "true");
        });

        // Bei einer Exception
        server.exception(Exception.class, (ex,req,res) ->{
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);

            LoggerFactory.getLogger("server").error(sw.toString());
            res.body("");
            res.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
        });

        server.awaitInitialization();
    }
}
