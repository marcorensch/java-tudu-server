package todo.service;

import todo.model.TodoItem;

import java.util.List;

public interface ToDoRepository {
    List<TodoItem> getAll();
    long add( TodoItem item);
    boolean delete( long id );
}
