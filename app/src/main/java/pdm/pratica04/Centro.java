package pdm.pratica04;

import com.google.gson.annotations.SerializedName;

public class Centro {
    @SerializedName("id")
    private int id;

    @SerializedName("nome")
    private String nome;

    @SerializedName("latitude")
    private String latitude;

    @SerializedName("longitude")
    private String longitude;

    @SerializedName("CentroOuPessoa")
    private boolean isCentro;

    @SerializedName("usuario")
    private int usuario;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public boolean isCentro() {
        return isCentro;
    }

    public void setCentro(boolean centro) {
        isCentro = centro;
    }

    public int getUsuario() {
        return usuario;
    }

    public void setUsuario(int usuario) {
        this.usuario = usuario;
    }
}
