import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserManager {
    private HashMap<String, String> users; // <username, password>
    private HashMap<List<String>, ArrayList<HashMap<String, String>>> permission; // < path, list<username, permissions on that path> >
    private String activeUser; // current logged in user.
    private VFileSystem vfs;

    UserManager(VFileSystem vfs){
        users = new HashMap<>();
        permission = new HashMap<>();
        users.put("admin", "123"); /// TODO should only be executed if the program wasn't able to load UserManager from a file
        activeUser = "admin";
        this.vfs = vfs;
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
        }
        else
            return "Incorrect login credentials.";
    }

    public String grant(String username, List<String> path, String pCode){
        if(!activeUser.equals("admin")){
            return "Access to CreateUser function denied.";
        }
        if(!users.containsKey(username)){
            return "User does not exist.";
        }
        if(vfs.pathExists(path)){
            HashMap<String, String> p = new HashMap<>();
            p.put(username, pCode);

            if(!permission.containsKey(path)){
                ArrayList<HashMap<String, String>> container = new ArrayList<>();
                permission.put(path, container);
            }

            permission.get(path).add(p);
            return "Permission granted.";
        }else{
            return "Path does not exist.";
        }
    }

    public String getActiveUser() {
        return "Current active user: " + activeUser;
    }
}
