package Dialog;

import Controller.Db.ConnDB;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by SunnyD on 2017/5/10.
 */
public class Login {

    public static Login dialog=new Login();
    public static boolean logined=false;



    private String username;
    private String password;
    private String title;

    public static Login getInstance(){
        return dialog;
    }

    public boolean show(Stage owner, String message, String title){
        Dialogs.LoginResponse res=Dialogs.getInstance().showLoginDialog(owner);
        if(res!=null){
            return checkLogin(res);
        }
        return false;
    }

    private boolean checkLogin(Dialogs.LoginResponse lr){
        ConnDB conn=new ConnDB();
        String sql = "select title from users where userid = '"+lr.username+"' and "+"password = '"+lr.password+"'";
        ResultSet rs=conn.executeQuery(sql);
        try {
            if(rs.next()){
                String title=rs.getString("title");
                this.logined=true;
                this.username=lr.username;
                this.password=lr.password;
                this.title=title;
                return true;
            }else{
                Dialogs.getInstance().showMessageDialog(new Stage(),"用户名密码错误！","错误信息");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getTitle() {
        return title;
    }

}
