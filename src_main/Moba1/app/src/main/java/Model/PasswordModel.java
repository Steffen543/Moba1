package Model;

/**
 * Created by Steffen on 22.06.2017.
 */

public class PasswordModel implements ISearchable {
    private int id;
    private String name;
    private String email;
    private String username;
    private String password;
    private String description;
    private String added;
    private String edited;
    private int parentId;

    public PasswordModel(int id, String name, String email, String username,
                         String password, String description, String added, String edited, int parentId)
    {
        this.setId(id);
        setName(name);
        setEmail(email);
        setUsername(username);
        setPassword(password);
        setDescription(description);
        setAdded(added);
        setEdited(edited);
        setParentId(parentId);
    }





    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdded() {
        return added;
    }

    public void setAdded(String added) {
        this.added = added;
    }

    public String getEdited() {
        return edited;
    }

    public void setEdited(String edited) {
        this.edited = edited;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString()
    {
        return getId() + ", " + getName() + ", " + getDescription() + ", " + getAdded() + ", " + getEdited();
    }

    @Override
    public boolean search(String search) {
        search = search.toLowerCase();
        if(name.toLowerCase().contains(search) || email.toLowerCase().contains(search) || username.toLowerCase().contains(search) || description.toLowerCase().contains(search) || password.toLowerCase().contains(search))
            return true;
        return false;
    }
}
