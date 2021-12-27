package vrd.DTO;

public class ResultDTO {
    private String ref;
    private String res;
    private String username;

    public ResultDTO(String ref, String res, String username) {
        this.ref = ref;
        this.res = res;
        this.username = username;
    }

    public ResultDTO() {
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getRes() {
        return res;
    }

    public void setRes(String res) {
        this.res = res;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
