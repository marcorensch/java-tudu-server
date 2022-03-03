public class TodoItem {
    public Long id;
    public String label;
    public String description;

    private TodoItem() {}

    public static TodoItem create(String label, String description){
        TodoItem td = new TodoItem();
        td.label = label;
        td.description = description;
        return td;
    }


}
