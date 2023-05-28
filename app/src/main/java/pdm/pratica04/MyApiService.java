package pdm.pratica04;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface MyApiService {

    @GET("/rededobem/Centro/")
    Call<ResponseBody> getCentros();

    @POST("/rededobem/Centro/")
    Call<ResponseBody> createCentro(@Body Centro centro);
}
