package pdm.pratica04.model;

public class Centro {
    private int id;
    private String usuario;
    private String nome;
    private double latitude;
    private double longitude;
    private boolean CentroOuPessoa;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isCentroOuPessoa() {
        return CentroOuPessoa;
    }

    public void setCentroOuPessoa(boolean centroOuPessoa) {
        CentroOuPessoa = centroOuPessoa;
    }

    // Rest of the class code
}
