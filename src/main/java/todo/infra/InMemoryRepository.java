package todo.infra;

import todo.model.TodoItem;
import todo.service.ToDoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class InMemoryRepository implements ToDoRepository {
    List<TodoItem> todos = new ArrayList<>();

    public InMemoryRepository() {
        this.todos.add(TodoItem.create(1L,"Mein erster Task", "Die beschreibung"));
        this.todos.add(TodoItem.create(2L,"Mein zweiter Task", "Die Beschreibung"));
        this.todos.add(TodoItem.create(3L,"Mein dritter Task", "Die dritte Beschreibung"));
    }

    @Override
    public List<TodoItem> getAll() {
        return todos;
    }

    @Override
    public long add( TodoItem item ) {
        item.id = (long) new Random().nextInt(1_000_000);
        todos.add(item);
        return item.id;
    }

    @Override
    public boolean delete(long id) {
        return todos.removeIf( todoItem -> todoItem.getId().equals(id));
    }
}
