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

    public static TodoItem create(Long id, String label, String description){
        TodoItem td = new TodoItem();
        td.id = id;
        td.label = label;
        td.description = description;
        return td;
    }

    public Long getId(){
        return this.id;
    }

    @Override
    public String toString() {
        return new JSONSerializer().serialize(this);
    }
}
