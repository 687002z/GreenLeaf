package Model.Node.TreeNode;


import Model.Node.INode;

/**
 * Created by SunnyD on 2017/6/19.
 */
public class UserNode implements INode{
    private String userID;
    private int typeID;
    public UserNode(String userid,int typeid){
        userID=userid;
        typeID=typeid;
    }

    public String getUserID() {
        return userID;
    }

    public int getTypeID() {
        return typeID;
    }

    @Override
    public String getName() {
        return userID;
    }

    @Override
    public void setName(String name) {
        this.userID=name;
    }

    @Override
    public String toString() {
        return this.userID;
    }
}
