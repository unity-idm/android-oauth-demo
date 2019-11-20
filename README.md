# android-oauth-demo
Example Android app, showing how OAuth2 works on native app, including PKCS protocol extension.

Important: this is example showing, using simple tools, how native OAuth with PKCS works. It is NOT a guide how production app should be developed. In case of creating production app, use of a stable supporting library is strongly advised. AppAuth is probably the best choice, see https://github.com/openid/AppAuth-Android


To run the example an OAuth Authorization Server is needed. Its coordinates need to be set in `OAuthServiceSettings` class.

The code was prepared to work with Unity IdM software: https://unity-idm.eu, other servers may need some changes in the code fetching user's profile (this is not standard part of OAuth).

To test it against Unity use the version 2.8.1 or newer (before native clients were not supported). Thi instruction assumes version 3.1.0 or newer. 

After installation the IP of the server must be configured in the OAuthServiceSettings class of the app, unless you are fine with the default 10.10.0.1. On Unity side the follwoing changes are needed:

* Configure basic server settings in unityServer.conf before starting it:

Set your (non localhost) IP address of the server & disable accepting client certificates, e.g.:

```
unityServer.core.httpServer.host=10.10.0.1
unityServer.core.httpServer.port=2443
unityServer.core.httpServer.advertisedHost=10.10.0.1:2443
unityServer.core.httpServer.wantClientAuthn=false
```

* Start the server and login to Admin Console. With the above settings it will be on `https://10.10.0.1:2443/console`
login: `admin`, pass: `the!unity`
you will need to setup new password...

* Go to `Identity Provider` view and select `UNITY OAuth2 Authorization Server`.

* In `Clients` tab edit the only client: set it to `PUBLIC`, modify its allowed return URLs to contain two values: `net.bixbit.jugdemo://hi` and `https://dev.unity-idm.eu/oauthReturn`. For nicer presentation you can set logo and displayed name.

* Assuming you will use Unity admin user for the test drive you can add an email attribute to it in the `Directory browser` view. Or you can can create a new user with at least username identity, password, name and email attributes. 
