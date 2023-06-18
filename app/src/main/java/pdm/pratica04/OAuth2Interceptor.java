package pdm.pratica04;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.ResponseBody;
import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.Protocol;
import java.io.IOException;

public class OAuth2Interceptor implements Interceptor, Authenticator {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER = "Bearer";
    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");

    private final String accessToken;

    public OAuth2Interceptor(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        Request.Builder requestBuilder = originalRequest.newBuilder()
                .header(AUTHORIZATION_HEADER, String.format("%s %s", BEARER, accessToken));

        Request request = requestBuilder.build();
        return chain.proceed(request);
    }

    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        // Realizar a autenticação de acordo com o fluxo de OAuth2 e obter um novo token de acesso
        // Retornar a nova solicitação com o novo token de acesso ou null em caso de falha na autenticação

        // Exemplo simplificado que retorna null para indicar falha na autenticação
        return null;
    }
}
