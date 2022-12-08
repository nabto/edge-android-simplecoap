package com.nabto.edge.simplecoapdemoo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import android.os.Bundle;
import android.widget.TextView;

import com.nabto.edge.client.Coap;
import com.nabto.edge.client.Connection;
import com.nabto.edge.client.ConnectionEventsCallback;
import com.nabto.edge.client.NabtoClient;
import com.nabto.edge.client.NabtoRuntimeException;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private NabtoClient nabtoClient;
    private Connection deviceConnection;

    // FILL IN YOUR DEVICE SETTINGS HERE!!
    // You can get the info from the Nabto Cloud Console and then fill out these strings.
    private String productId = "pr-xxxxxxxx";
    private String deviceId = "de-xxxxxxxx";
    private String serverConnectToken = "demosct";

    // This LiveData will be updated with with a coap response.
    private MutableLiveData<String> coapResponse = new MutableLiveData<>();

    // This listener will be called when the connection to your device is connected or is closed.
    private ConnectionEventsCallback deviceConnectionListener = new ConnectionEventsCallback() {
        @Override
        public void onEvent(int event) {
            if (event == CONNECTED) {
                onDeviceConnected();
            } else if (event == CLOSED) {
                // do something if the connection has closed...
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create a NabtoClient using our activity as context.
        // You should create one NabtoClient and keep it throughout the application's lifetime.
        nabtoClient = NabtoClient.create(this);

        // Now we make a private key for this client app.
        // You should save this private key in for example shared preferences or a database
        // A device will need this key to recognize the client.
        String clientPrivateKey = nabtoClient.createPrivateKey();

        // The settings for the connection are passed as JSON
        JSONObject connectionOptions = new JSONObject();
        try {
            connectionOptions.put("ProductId", productId);
            connectionOptions.put("DeviceId", deviceId);
            connectionOptions.put("PrivateKey", clientPrivateKey);
            connectionOptions.put("ServerConnectToken", serverConnectToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Now we create a connection object.
        deviceConnection = nabtoClient.createConnection();

        // Fill it with our settings and set our listener
        deviceConnection.updateOptions(connectionOptions.toString());
        deviceConnection.addConnectionEventsListener(deviceConnectionListener);

        // Now we're finally ready to connect to our device.
        // Note that doing this in onCreate of an activity is _VERY BAD_ as it is blocking
        // the main thread. We're doing it here to keep everything simple and easy to understand.
        // You should put this on a separate thread, or use connectCallback().
        // In Kotlin you can use coroutines and awaitConnect() which is the preferred method.
        deviceConnection.connect();
    }

    @Override
    protected void onStart() {
        super.onStart();

        TextView textView = findViewById(R.id.helloworld);
        coapResponse.observe(this, textView::setText);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Shut down the connection as we're done with it now.
        deviceConnection.close();
    }

    private void onDeviceConnected() {
        // Our device is connected, let's make a COAP request
        // GET /hello-world
        StringBuilder str = new StringBuilder();
        try {
            Coap coap = deviceConnection.createCoap("GET", "/hello-world");
            coap.execute();
            int response = coap.getResponseStatusCode();
            str.append("Received response code ")
            .append(response)
            .append("\n");
            if (response == 205)
            {
                byte[] bytes = coap.getResponsePayload();
                str.append("Received CoAP GET response data: ")
                .append(new String(bytes))
                .append("\n");
            }
            else
            {
                str.append("Look up COAP response codes in RFC7252 to see what went wrong.");
            }
        } catch (NabtoRuntimeException e) {
            str.append("Received runtime error ").append(e).append("\n");
            e.printStackTrace();
        }
        coapResponse.postValue(str.toString());
    }
}