package Model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

public class CategoryModel implements Serializable, ISearchable {
    private int id;
    private String name;
    private String description;
    private String added;
    private String edited;
    private float childCount;

    public CategoryModel(int id, String name, String description, String added, String edited)
    {
        this.id = id;
        setName(name);
        setDescription(description);
        setAdded(added);
        setEdited(edited);
    }


    public int getId() { return id; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public String toString()
    {
        return getId() + ", " + getName() + ", " + getDescription() + ", " + getAdded() + ", " + getEdited();
    }

    public float getChildCount() {
        return childCount;
    }

    public void setChildCount(float childCount) {
        this.childCount = childCount;
    }

    @Override
    public boolean search(String search) {
        search = search.toLowerCase();
        if(name.toLowerCase().contains(search) || description.toLowerCase().contains(search) )
            return true;
        return false;
    }
}
