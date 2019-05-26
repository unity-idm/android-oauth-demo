package pl.torun.jug.nativeoauth.demo;

class OAuthServiceSettings {
        static final String CLIENT_ID = "oauth-client";
        static final String CLIENT_INSECURE_PASSWORD = "oauth-pass1";

        private static final String OAUTH_SERVER = "https://10.10.0.1:2443";
        static final String AUTHZ_ENDPOINT_URL = OAUTH_SERVER + "/oauth2-as/oauth2-authz";
        static final String TOKEN_ENDPOINT_URL = OAUTH_SERVER + "/oauth2/token";
        static final String PROFILE_ENDPOINT_URL = OAUTH_SERVER + "/oauth2/userinfo";
}
