package com.morepranit.locationdemo;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FetchAddressService extends IntentService {
    private ResultReceiver receiver;

    public FetchAddressService() {
        super("FetchAddressService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Location location = intent.getParcelableExtra("location");
        receiver = intent.getParcelableExtra("receiver");

        if (!Geocoder.isPresent()) {
            return;
        }

        Geocoder geocoder = new Geocoder(this);
        String errorMessage = "";

        List<Address> addresses = new ArrayList<>();
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            errorMessage = "Invalid Latitude or Longitude";
            sendToReceiver(errorMessage);
        }

        if (addresses.size() <= 0) {
            errorMessage = "No Address found";
            sendToReceiver(errorMessage);
        } else {
            List<String> list = new ArrayList<>();
            Address addressFragments = addresses.get(0);

            for (int i = 0; i <= addressFragments.getMaxAddressLineIndex(); i++) {
                list.add(addressFragments.getAddressLine(i));
            }

            String address = TextUtils.join(System.getProperty("line.separator"), list);

            sendToReceiver(address);
        }
    }

    private void sendToReceiver(String message) {
        Bundle bundle = new Bundle();
        bundle.putString("address", message);
        receiver.send(Activity.RESULT_OK, bundle);
    }


}
