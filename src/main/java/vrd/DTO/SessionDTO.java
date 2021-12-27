package vrd.DTO;

public class SessionDTO {
    private String name;
    private String date;
    private String ref;

    public SessionDTO() {
    }

    public SessionDTO(String name, String date, String ref) {
        this.name = name;
        this.date = date;
        this.ref = ref;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }
}
