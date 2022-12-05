package io.igrant.igrant_org_sdk.Api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.igrant.igrant_org_sdk.models.Login.RefreshTokenRequest;
import io.igrant.igrant_org_sdk.models.user.Token;
import io.igrant.igrant_org_sdk.utils.LoginUtils;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ApiManager {
    //        public static final String BASE_URL = "https://api.igrant.io/";//production
    public static final String BASE_URL = "https://demo-api.igrant.io/"; //demo
//    public static final String BASE_URL = "https://staging-api.igrant.io/";

    private static OkHttpClient okClient;
    private static APIService service;
    private static OkHttpClient.Builder httpClient;

    private static ApiManager apiManager = null;


    private ApiManager() {

    }

    public static ApiManager getApi(String token) {
        if (apiManager == null) {
            apiManager = new ApiManager();

            httpClient = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS);
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(httpLoggingInterceptor);
            httpClient.interceptors().add(new HttpInterceptor(token));
            okClient = httpClient.build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            service = retrofit.create(APIService.class);
        }
        return apiManager;
    }

    public static void resetApi() {
        apiManager = null;
    }


    public APIService getService() {
        return service;
    }

    private static class HttpInterceptor implements Interceptor {

        String token;

        public HttpInterceptor(String s) {
            token = s;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            //Build new request
            Request.Builder builder = request.newBuilder();
            builder.header("Accept", "application/json");
            if (!token.equalsIgnoreCase("")) {
                setAuthHeader(builder, token);
            }//write current token to request

            request = builder.build(); //overwrite old request
            Response response = chain.proceed(request); //perform request, here original request will be executed

            if (LoginUtils.isUserLoggedIn())
                if (response.code() == 401) { //if unauthorized
                    synchronized (httpClient) { //perform all 401 in sync blocks, to avoid multiply token updates

                        try {
                            RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest();
                            refreshTokenRequest.setClientId("igrant-ios-app");
                            refreshTokenRequest.setRefreshToken(token);
                            Gson gson = new Gson();
                            String json = gson.toJson(refreshTokenRequest);


                            Request newRequest = requestBuild(
                                    new Request.Builder()
                                            .url(String.format("%sv1/user/token", BASE_URL))
                                            .method("POST", RequestBody.create(MediaType.parse("application/json"), json.getBytes("UTF-8")))
                                            .build()).build(); // create simple requestBuilder

                            Response newResponse = chain.proceed(newRequest);
                            if (newResponse.code() == 200) {
                                Token token = new GsonBuilder().create().fromJson(newResponse.body().string(), Token.class);
                                response = chain.proceed(requestBuild(request, token.getAccess_token()).build());
                                resetApi();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            return response;
        }

        private void setAuthHeader(Request.Builder builder, String token) {
            if (token != null) //Add Auth token to each request if authorized
                builder.header("Authorization", "ApiKey " + token);
        }

        private static Request.Builder requestBuild(Request request) {
            return request.newBuilder()
                    .header("Accept", "application/json")
                    .method(request.method(), request.body());
        }

        private static Request.Builder requestBuild(Request request, String auth) {
            return request.newBuilder()
                    .header("Accept", "application/json")
                    .header("Authorization", "ApiKey " + auth)
                    .method(request.method(), request.body());
        }
    }

}
