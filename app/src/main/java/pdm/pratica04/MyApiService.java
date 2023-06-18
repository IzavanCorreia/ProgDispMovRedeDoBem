package pdm.pratica04;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MyApiService {

    @GET("/rededobem/Centro/")
    Call<ResponseBody> getCentros();

    @POST("/rededobem/Centro/")
    Call<ResponseBody> createCentro(@Body Centro centro);

    @POST("/token")
    static Call<TokenResponse> getToken(@Body RequestBody requestBody) {
        return null;
    }
    @DELETE("/rededobem/Centro/{id}/")
    Call<ResponseBody> deleteCentro(@Path("id") int id);
}
