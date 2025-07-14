import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class SqlInjectionExample extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            String username = request.getParameter("username");

            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", "user", "pass");
            Statement stmt = conn.createStatement();
            String query = "SELECT * FROM users WHERE username = '" + username + "'";
            stmt.executeQuery(query);

        } catch (Exception e) {
            println("Exception!")
        }
    }
}
