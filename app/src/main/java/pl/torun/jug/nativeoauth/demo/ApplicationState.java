package pl.torun.jug.nativeoauth.demo;

import android.app.Application;

public class ApplicationState extends Application {
        private OAuthState oauthState;

        public OAuthState getOAuthState() {
                return oauthState;
        }

        public void setOauthState(OAuthState oauthstate) {
                this.oauthState = oauthstate;
        }
}
