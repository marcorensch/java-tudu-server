public class TodoItem {
    public static int count;

    public Long id;
    public String label;
    public String description;

    private TodoItem() {
        this.id = (long) (count++ + 1);
    }

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
