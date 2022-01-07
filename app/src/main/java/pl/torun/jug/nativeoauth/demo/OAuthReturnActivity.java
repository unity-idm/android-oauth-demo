package pl.torun.jug.nativeoauth.demo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static pl.torun.jug.nativeoauth.demo.OAuthServiceSettings.CLIENT_ID;
import static pl.torun.jug.nativeoauth.demo.OAuthServiceSettings.PROFILE_ENDPOINT_URL;
import static pl.torun.jug.nativeoauth.demo.OAuthServiceSettings.TOKEN_ENDPOINT_URL;

/**
 * Activity is started after receiving return redirect from OAuth Authorization Server,
 * with authorization code as parameter.
 *
 * This class exchanges the authZ code for access token, and then uses the token to fetch
 * basic data from user's profile. All results are shown on the screen.
 */
public class OAuthReturnActivity extends AppCompatActivity {
        private static final String LOG_TAG = OAuthReturnActivity.class.getSimpleName();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_oauth_return_custom_scheme);

                Intent intent = getIntent();
                Uri returnURI = intent.getData();
                if (returnURI != null) {

                        showReceivedReturnURI(returnURI);
                        try {
                                fetchAccessToken(returnURI);
                        } catch (IOException | JSONException e) {
                                Log.e(LOG_TAG, "Error fetching access token", e);
                        }
                }
        }

        public void startOver(View view) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
        }

        private void showReceivedReturnURI(Uri returnURI) {
                String returnedURI = returnURI.toString();
                TextView textView = findViewById(R.id.returnedURL);
                textView.setText("Returned URI: " + returnedURI);
        }


        private void fetchAccessToken(Uri returnURI) throws IOException, JSONException {

                ApplicationState applicationState = (ApplicationState) getApplication();
                OAuthState oauthState = applicationState.getOAuthState();

                verifyRelayState(oauthState, returnURI);

                Request request = createAccessTokenRequest(returnURI, oauthState);

                OkHttpClient client = InsecureHttpClientFactory.getUnsafeOkHttpClient();
                client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                                Log.e(LOG_TAG, "Error fetching access token", e);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                                if (response.isSuccessful()) {
                                        try {
                                                String accessToken = extractAndShowAccessToken(response);
                                                fetchProfile(accessToken);
                                        } catch (JSONException e) {
                                                Log.e(LOG_TAG, "Error parsing response", e);
                                        }
                                } else {
                                        Log.e(LOG_TAG, "Error received from profile endpoint: " + response.message());
                                }
                        }
                });
        }

        private Request createAccessTokenRequest(Uri returnURI, OAuthState oauthState) {
                String url = HttpUrl.parse(TOKEN_ENDPOINT_URL).newBuilder()
                                .build().toString();
                String authzCode = returnURI.getQueryParameter("code");
                RequestBody formBody = new FormBody.Builder()
                                .add("grant_type", "authorization_code")
                                .add("code", authzCode)
                                .add("redirect_uri", oauthState.returnURL)
                                .add("client_id", CLIENT_ID)
                                .add("code_verifier", oauthState.codeVerifier)
                                .build();
                return new Request.Builder()
                                .post(formBody)
                                .url(url)
                                .build();
        }


        private void verifyRelayState(OAuthState oauthState, Uri returnURI) {
                String relayState = returnURI.getQueryParameter("state");
                if (!oauthState.state.equals(relayState))
                        throw new IllegalStateException("Error in OAuth authN process: state does not match");
        }

        private String extractAndShowAccessToken(Response response) throws IOException, JSONException {
                String responseRaw = response.body().string();
                JSONObject responseJson = new JSONObject(responseRaw);
                String accessToken = responseJson.getString("access_token");
                runOnUiThread(() -> {
                        TextView viewById = findViewById(R.id.accessToken);
                        viewById.setText("Received access token: " + accessToken);
                });
                return accessToken;
        }

        private void fetchProfile(String accessCode) throws IOException, JSONException {

                String url = HttpUrl.parse(PROFILE_ENDPOINT_URL).newBuilder()
                        .build().toString();

                Request request = new Request.Builder()
                        .addHeader("Authorization", "Bearer " + accessCode)
                        .get()
                        .url(url)
                        .build();

                OkHttpClient client = InsecureHttpClientFactory.getUnsafeOkHttpClient();
                client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                                Log.e(LOG_TAG, "Error fetching profile", e);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                                if (response.isSuccessful()) {
                                        try {
                                                extractAndShowProfile(response);
                                        } catch (JSONException e) {
                                                Log.e(LOG_TAG, "Error parsing response", e);
                                        }
                                } else {
                                        Log.e(LOG_TAG, "Error received from token endpoint: " + response.message());
                                }
                        }
                });
        }

        private void extractAndShowProfile(Response response) throws IOException, JSONException {
                String responseRaw = response.body().string();
                JSONObject responseJson = new JSONObject(responseRaw);

                Log.i(LOG_TAG, "Received token: " + responseJson);
                String email = responseJson.getString("email");
                String name = responseJson.getString("name");
                runOnUiThread(() -> {
                        TextView viewById = findViewById(R.id.userInfo);
                        viewById.setText(String.format("Email: %s Name: %s", email, name));
                });
        }
}
