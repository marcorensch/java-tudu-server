package todo.infra;

import todo.model.TodoItem;
import todo.service.ToDoRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLiteRepository implements ToDoRepository {
    Connection conn = null;
    String url = "jdbc:sqlite::resource:db.sqlite";

    private static final String SQL_DELETE_BY_ID = "DELETE FROM TodoItem WHERE id=?";

    private final String user = "";
    private final String password = "";

    public SQLiteRepository() {
    }

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    @Override
    public List<TodoItem> getAll() {

        String SQL = "select * from TodoItem";

        final ArrayList<TodoItem> list = new ArrayList<>();

        try(Connection conn = connect();
            Statement stmt = conn.createStatement();
        ) {
            try (ResultSet rs = stmt.executeQuery(SQL)) {
                while(rs.next()) {
                    list.add(TodoItem.create(
                            rs.getLong("id"),
                            rs.getString("label"),
                            rs.getString("description")));
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {

        }
        return list;
    }

    @Override
    public long add(TodoItem item) {
        String SQL = "INSERT INTO TodoItem (label, description) VALUES(?,?)";

        long id = 0;

        try(Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(SQL,
                    Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, item.label);
            pstmt.setString(2, item.description);

            int affectedRows = pstmt.executeUpdate();
            // check the affected rows
            if (affectedRows > 0) {
                // get the ID back
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        id = rs.getLong(1);
                    }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } finally {
            return id;
        }
    }

    @Override
    public boolean delete(long id) {

        try (Connection conn = connect();
             PreparedStatement preparedStatement = conn.prepareStatement(SQL_DELETE_BY_ID)) {

            preparedStatement.setString(1, String.valueOf(id));

            int row = preparedStatement.executeUpdate();

            // rows affected
            System.out.println(row);

        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }


        return false;
    }
}
