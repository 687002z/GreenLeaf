package Controller.Db;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnDB {
	private final String DB_DRIVER = "com.mysql.jdbc.Driver";
    private final String DB_URL = "jdbc:mysql://127.0.0.1:3306/mydb?useUnicode=true&characterEncoding=utf-8";
    private final String DB_USER = "root";
    private final String DB_PASSWORD = "root";
    private Connection conn = null;
    private static ConnDB instance = new ConnDB();

    public static ConnDB getInstance(){
        return instance;
    }
    public Connection getConn() {
        try {
           // Class.forName(DBDRIVER).newInstance();
        	Class.forName(DB_DRIVER);
            conn = DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWORD);
            System.out.println("数据库连接成功");
            
        } catch (Exception e) {
        	//System.out.println();
        	e.printStackTrace();
            System.out.println("数据库连接失败");
        }
        return conn;
    }

    public void close() {
        try {
            this.conn.close();
        } catch (Exception e) {
            System.out.println("数据库关闭");
        }
    }

    public ResultSet executeQuery(String sql,Connection conn) {
        ResultSet rs = null;
        try {
            rs = null;
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
        } catch (SQLException ex) {
            System.out.println("执行错误");
            ex.printStackTrace();
        }
        return rs;
    }

    public boolean executeUpdate(String strSQL) {
        try {
            Connection conn = this.getConn();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(strSQL);
        
        } catch (SQLException ex) {
            System.err.println("连接数据库失败，异常为：" + ex.getMessage());
        }
        return true;
    }
    

}
