package pl.torun.jug.nativeoauth.demo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.apache.commons.text.RandomStringGenerator;

import java.security.SecureRandom;
import java.util.UUID;

import okhttp3.HttpUrl;

import static pl.torun.jug.nativeoauth.demo.OAuthServiceSettings.AUTHZ_ENDPOINT_URL;
import static pl.torun.jug.nativeoauth.demo.OAuthServiceSettings.CLIENT_ID;

public class MainActivity extends AppCompatActivity {

        private static final String LOG_TAG = MainActivity.class.getSimpleName();

        private static final String RETURN_REDIRECT_CUSTOM_SCHEME = "net.bixbit.jugdemo://hi";
        private static final String RETURN_REDIRECT_CLAIMED_HTTPS = "https://dev.unity-idm.eu/oauthReturn";

        private SecureRandom secureRandom = new SecureRandom();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);
        }

        public void openInClassicBrowser(View view) {
                Uri webpage = Uri.parse(prepareRequest(RETURN_REDIRECT_CUSTOM_SCHEME));
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                }
        }

        public void openInCustomTab(View view) {
                Uri webpage = Uri.parse(prepareRequest(RETURN_REDIRECT_CUSTOM_SCHEME));
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(this, webpage);
        }

        public void openInCustomTabHttps(View view) {
                Uri webpage = Uri.parse(prepareRequest(RETURN_REDIRECT_CLAIMED_HTTPS));
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(this, webpage);
        }

        private String prepareRequest(String returnURI) {
                ApplicationState applicationState = (ApplicationState) getApplication();

                String relayState = UUID.randomUUID().toString();

                String codeVerifier = generateCodeVerifier();
                String codeChallenge = codeVerifier;

                applicationState.setOauthState(new OAuthState(relayState, codeVerifier, returnURI));

                return HttpUrl.parse(AUTHZ_ENDPOINT_URL)
                        .newBuilder()
                        .addQueryParameter("response_type", "code")
                        .addQueryParameter("client_id", CLIENT_ID)
                        .addQueryParameter("state", relayState)
                        .addQueryParameter("scope", "openid profile")
                        .addQueryParameter("redirect_uri", returnURI)
                                // in production s256 method is strongly advised instead,
                                // plain here is used to simplify example only.
                        .addQueryParameter("code_challenge_method", "plain")
                        .addQueryParameter("code_challenge", codeChallenge)
                        .build()
                        .toString();
        }

        private String generateCodeVerifier() {
                RandomStringGenerator generator = new RandomStringGenerator.Builder()
                        .withinRange('a', 'z')
                        .usingRandom(secureRandom::nextInt)
                        .build();

                return generator.generate(43);
        }

}
