package todo.service;
import todo.model.TodoItem;

import java.util.ArrayList;
import java.util.Locale;

public class TodoService {

    private final ToDoRepository repo;

    public TodoService(ToDoRepository repo) {
        this.repo = repo;
    }

    // ToDo: Erstelle Methode im Kntroller hierfür?
    public TodoItem getById(Long idToRead) {
        for(var item : repo.getAll()){
            if(item.getId().equals(idToRead)){
                return item;
            }
        }
        return null;
    }

    // Todo: Erstelle Methode im Kontroller hierzu
    public ArrayList getFilteredItems(String filter) {

        ArrayList filteredItems = new ArrayList();

        System.out.println(filter);

        for (TodoItem item: repo.getAll()) {
            if(item.label.toLowerCase(Locale.ROOT).contains(filter)){
                filteredItems.add(item);
            }
        }

        return filteredItems;

    }

    public ArrayList getAllItems() {
        return (ArrayList) repo.getAll();
    }

    public long add(TodoItem item){
        return repo.add(item);
    }

    public boolean delete( long id ){
        return repo.delete( id );
    }
}
