package pdm.pratica04.model;

public class RegisterRequest {
    private String username;
    private String password;
    private String password2;
    private String email;
    private String first_name;
    private String last_name;

    public RegisterRequest(String username, String password, String password2, String email, String first_name, String last_name) {
        this.username = username;
        this.password = password;
        this.password2 = password2;
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
    }

    // getters e setters
}
