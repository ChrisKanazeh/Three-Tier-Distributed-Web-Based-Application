
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class Project4 extends HttpServlet {
   
    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;
    private ResultSetMetaData metaData;
    public boolean valid;
    public int affected;
    public String error;
    public String tester = "Connection Failed!! :(";
    
	public Project4() {
        affected = 0;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String sqlStatement = request.getParameter("queryArea");
        valid = true;
        try {
            connectToDatabase();
        } catch(Exception e) {
            error = e.getMessage();
        }
        
        if(sqlStatement.equals(""))
            sqlStatement = "select * from suppliers";
        
        if(sqlStatement.length() < 5)
            sqlStatement = "Statement must be 'Delete,' 'Insert,' 'Select,' or 'Update'";
        
        if(!sqlStatement.substring(0, 6).equals("insert") && !sqlStatement.substring(0, 6).equals("update") && !sqlStatement.substring(0, 6).equals("delete"))
            try {
                createResultSetMetaData(sqlStatement);
            }
            catch(Exception e) {
                error = e.getMessage();
                valid = false;
            }
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<?xml version = \"1.0\"?>");
        out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
        out.println("<html xmlns = \"http://www.w3.org/1999/xhtml\">");
        out.println("<head>");
        out.println("<title>CNT 4714 Remote Database Management System</title>");
        out.println("<style type='text/css'>");
        out.println("<!-- div{text-align:center; margin:10px;} textarea{display: block; margin-left: auto; margin-right: auto; max-width:500px;} p{text-align:center; margin:2px;} hr{height:1px; color:black; background-color:black} h1{text-align:center;} h3{text-align:center;} h4{color:white;}body{background-color:#0066ff;} table{text-align: center; margin-left: auto; margin-right: auto;}th{background-color:#CCCCCC;}td{background-color:#FF4536;}#ERROR{display: block; margin-left: auto; margin-right: auto; background-color:red; width:275px; height:150px; border-style:solid; border-width:5px;}#ERROR_FONT{color:white;}#UPDATE{display: block; margin-left: auto; margin-right: auto; background-color:green; width:250px; height:150px; border-style:solid; border-width:5px;}-->");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>Welcome to the Project 4 Database Management System!</h1>");
        out.println("<hr>");
        out.println("<p> You are connected to the Project4 database. </p>");
        out.println("<p> Please enter any valid SQL query or update statement. </p>");
        out.println("<p>If no query/update command is given the Execute button will display all supplier information in the database.</p>");
        out.println("<p> All execution results will appear below. </p>");
        out.println("<div><form action='/Project4/Project4\' method='post'>");
        out.println("<textarea id='queryArea' name='queryArea' rows='10' cols='100'></textarea>");
        out.println("<div>");
        out.println("<input type='submit' value='Execute Command'/>");
        out.println("<input type='reset' value='Clear Form'/>");
        out.println("</div>");
        out.println("</form></div>");
        out.println("<hr>");
        out.println("<h3>Database Results:</h3>");
        out.println("<div>");
        
        if(valid && (sqlStatement.substring(0, 6).equals("insert") || sqlStatement.substring(0, 6).equals("update") || sqlStatement.substring(0, 6).equals("delete"))){
            try {
                affected = statement.executeUpdate(sqlStatement);
            }
            catch(SQLException e) {
                e.printStackTrace();
            }
            
            out.println("<div id='UPDATE'>");
            out.println((new StringBuilder("<h4> The statement executed successfully. ")).append(affected).append(" row(s) affected. </h4>").toString());
            out.println("</div>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
        } else
        if(valid && sqlStatement.substring(0, 6).equals("select"))
        {
            try
            {
                out.println("<table border='2' bordercolor=black>");
                for(int i = 1; i <= metaData.getColumnCount(); i++)
                    out.println((new StringBuilder("<th>")).append(metaData.getColumnLabel(i)).append("</th>").toString());

                out.println("<p></p>");
                while(resultSet.next()) 
                {
                    out.println("<tr>");
                    for(int i = 1; i <= metaData.getColumnCount(); i++)
                        out.print((new StringBuilder("<td>")).append(resultSet.getString(i)).append("</td>").toString());

                }
                resultSet.close();
                statement.close();
                connection.close();
            }
            catch(SQLException e)
            {
                out.println(e.getMessage());
            }
            out.println("</table>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
            out.close();
        } else
        {
            out.println("<div id='ERROR'>");
            out.println("<h4> Error executing the SQL Statement: </h4>");
            out.println((new StringBuilder("<p id='ERROR_FONT'>")).append(error).append("</p>").toString());
            out.println("</div>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    public void connectToDatabase() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/project4", "admin", "root");
        statement = connection.createStatement(1004, 1007);
        
        tester = "Connection Sucessful!";
    }

    public void createResultSetMetaData(String queryArea)
        throws SQLException, ClassNotFoundException
    {
        resultSet = statement.executeQuery(queryArea);
        metaData = resultSet.getMetaData();
    }

}