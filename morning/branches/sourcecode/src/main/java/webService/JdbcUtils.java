package webService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Created by YN on 2018/4/16.
 */
public class JdbcUtils {
    //数据库用户名
    private static final String USERNAME = "root";
    //数据库密码
    private static final String PASSWORD = "yanzi";
    //驱动信息
    private static final String DRIVER = "com.mysql.jdbc.Driver";
    //数据库地址
    private static final String URL = "jdbc:mysql://localhost:3306/mydb";
    private Connection connection;
    private PreparedStatement pstmt;
    private ResultSet resultSet;
    public JdbcUtils() {
        // TODO Auto-generated constructor stub
        try{
            Class.forName(DRIVER);
            System.out.println("数据库连接成功！");

        }catch(Exception e){

        }
    }

    /**
     * 获得数据库的连接
     * @return
     */
    public Connection getConnection(){
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return connection;
    }
}
