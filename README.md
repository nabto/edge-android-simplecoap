# Edge Android Simple CoAP
This Android app demonstrates how to open a connection to a Nabto Edge enabled device and use Constrained Application Protocol (CoAP) to do a GET request on the device in an extremely simplified manner. See MainActivity.java for the code.

Note that this is not at all production ready code and is purely for understanding the basics of Nabto Edge. For production, access control must be included - ie client/device pairing and token exchange. This has all been omitted from this app.

# Building and running
Open the project in Android Studio and run the app as you would any other.

# Usage application
On the device side, start up a [Simple CoAP](https://github.com/nabto/nabto-embedded-sdk/tree/master/examples/simple_coap) example application from the embedded SDK. In MainActivity.java you can set `productId` and `deviceId` to the IDs you specified when running the Simple CoAP application. Then simply build and run the app.