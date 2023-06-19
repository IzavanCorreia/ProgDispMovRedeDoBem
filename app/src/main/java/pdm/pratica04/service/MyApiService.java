package pdm.pratica04.service;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import pdm.pratica04.model.AccessTokenResponse;
import pdm.pratica04.model.Centro;
import pdm.pratica04.model.Item;
import pdm.pratica04.model.LoginRequest;
import pdm.pratica04.model.RegisterRequest;
import pdm.pratica04.model.TokenResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MyApiService {

    @GET("/rededobem/Centro/")
    Call<ResponseBody> getCentros();

    @POST("/rededobem/Centro/")
    Call<ResponseBody> createCentro(@Body Centro centro);

    @POST("/token/")
    Call<AccessTokenResponse> login(@Body LoginRequest loginRequest);

    @POST("/api/register")
    Call<Void> register(@Body RegisterRequest registerRequest);
    @DELETE("/rededobem/Centro/{id}/")
    Call<ResponseBody> deleteCentro(@Path("id") int id);

    @GET("rededobem/ItensCentro/")
    Call<List<Item>> getItensCentro(@Query("itens") String itens, @Query("centro") int centro);

}
