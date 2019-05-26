package pl.torun.jug.nativeoauth.demo;

class OAuthState {
        final String state;
        final String codeVerifier;
        final String returnURL;

        OAuthState(String state, String codeVerifier, String returnURL) {
                this.state = state;
                this.codeVerifier = codeVerifier;
                this.returnURL = returnURL;
        }
}
