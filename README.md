# android-oauth-demo
Example Android app, showing how OAuth2 works on native app, including PKCS protocol extension.

Important: this is example showing, using simple tools, how native OAuth with PKCS works. It is NOT a guide how production app should be developed. In case of creating production app, use of a stable supporting library is strongly advised. AppAuth is probably the best choice, see https://github.com/openid/AppAuth-Android


To run the example an OAuth Authorization Server is needed. Its coordinates need to be set in `OAuthServiceSettings` class.

The code was prepared to work with Unity IdM software: https://unity-idm.eu, other servers may need some changes in the code fetching user's profile (this is not standard part of OAuth).

To test it against Unity use the version 2.8.1 or newer. After installation IP of the server must be configured in the OAuthServiceSettings class. On Unity side the follwoing changes are needed:

* Configure basic server settings in unityServer.conf before starting it(!):

Set your (non localhost) IP address of the server:

```
unityServer.core.httpServer.host=10.10.0.1
unityServer.core.httpServer.port=2443
unityServer.core.httpServer.advertisedHost=10.10.0.1:2443
```

and uncomment this line to load demo contents:
```
$include.demoContents=${CONF}/modules/demoContents.module
```
* Start the server and login to Admin UI. With the above settings it will be on `https://10.10.0.1:2443/admin`
login: `admin`, pass: `the!unity`
you will need to setup new password...

* Select group `/oauth-clients` and in it the `OAuth client`. Add to it the `sys:oauth:clientType` attribute with value `PUBLIC`.
Modify its `sys:oauth:allowedReturnURI` to contain two values: `net.bixbit.jugdemo://hi` and `https://dev.unity-idm.eu/oauthReturn`. Attributes `sys:oauth:clientName` and `sys:oauth:clientLogo` can be also set for a nicer presentation.
