public class TodoItem {
    public int id;
    public String label;
    public String description;

    private TodoItem() {}

    public static TodoItem create(String label, String description){
        TodoItem td = new TodoItem();
        td.label = label;
        td.description = description;
        return td;
    }

    public static TodoItem create(int id, String label, String description){
        TodoItem td = new TodoItem();
        td.id = id;
        td.label = label;
        td.description = description;
        return td;
    }

}
