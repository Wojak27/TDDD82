package polis.polisappen;

/**
 * Created by jespercrimefighter on 3/13/18.
 */

public class Contact {
    private String name, id;

    public Contact(String name, String id){
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }
}
