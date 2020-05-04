import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserManager {
    private HashMap<String, String> users; // <username, password>
    private HashMap<List<String>, HashMap<String, String>> permission; // < path, list<username, permissions on that path> >
    private String activeUser =""; // current logged in user.
    private VFileSystem vfs;

    UserManager(VFileSystem vfs){
        users = new HashMap<>();
        permission = new HashMap<>();
        this.users.put("admin", "123");
        this.vfs = vfs;
    }

    public void setLoggedIn(String username){
        this.activeUser = username;
    }

    public String CreateUser(String username, String password){
        if(!activeUser.equals("admin")){
            return "Access to CreateUser function denied.";
        }
        if(users.containsKey(username)){
            return "Username already exits.";
        }
        users.put(username, password);
        return "User successfully created.";
    }

    public String login(String username, String password){
        if(users.get(username) != null && users.get(username).equals(password)){
            activeUser = username;
            return "Hello, " + username + "!";
        } else{
            return "Incorrect login credentials.";
        }
    }

    public String grant(String username, List<String> path, String pCode){
        if(!activeUser.equals("admin")){
            return "Access to CreateUser function denied.";
        }
        if(!users.containsKey(username)){
            return "User does not exist.";
        }
        if(vfs.pathExists(path)){
            if(!permission.containsKey(path)){
                HashMap<String, String> container = new HashMap<>();
                permission.put(path, container);
            }
            permission.get(path).put(username,pCode);

//            permission.get(path).add(p);
            return "Permission granted.";
        }else{
            return "Path does not exist.";
        }
    }
    /// the function takes the string with the permission we want to check on and a list for the path
    public boolean hasPermission(String permissionNeeded ,List<String> path ){
        if(activeUser.equals("admin")){
            return true;
        }
        while (!path.isEmpty()){
            if(checkPermissionEquality(getPermission(path), permissionNeeded)){
                return true;
            }
            path.remove(path.size() -1);    /// we remove the leftmost in the path then check again
                                                ///  so we grant permission if a parent directory has permission for this user
        }
        return false;
    }
    private String getPermission(List<String> path){
        if(permission.get(path)==null)
            return null;
        return  permission.get(path).get(getUser());
    }
    private boolean checkPermissionEquality(String permissionWeHave , String permissionToCheckWith){
        if(permissionWeHave == null){
            return false;
        }
        /// if we have 11 then true for all cases
        if(permissionWeHave.equals("11")){
            return true;
        }
        else {
            /// otherwise if we have 10 or 01 we need to check for equality of permission
            return permissionToCheckWith.equals(permissionWeHave);
        }
    }
    public String getActiveUser() {
        return "Current active user: " + activeUser;
    }
    public String getUser(){
        return this.activeUser;
    }
    public HashMap<List<String>, HashMap<String, String>> getPermissions(){
        return this.permission;
    }
    public HashMap<String, String> getUsers(){
        return this.users;
    }
}
