package com.tcn.tcnbay.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tcn.tcnbay.R;

public class ServerSetupActivity extends AppCompatActivity {
    
    EditText host, port, imagePort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_setup);
        Toolbar t = findViewById(R.id.serverSetupToolbar);
        setSupportActionBar(t);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Button b = findViewById(R.id.btnConfigServer);
        host = findViewById(R.id.txtServerHost);
        port = findViewById(R.id.txtServerPort);
        imagePort = findViewById(R.id.txtServerPortHttp);
        SharedPreferences sp = getSharedPreferences("server_setup", MODE_PRIVATE);
        host.setText(sp.getString("host", "casaamorim.no-ip.biz"));
        port.setText(String.valueOf(sp.getInt("port", 50000)));
        imagePort.setText(String.valueOf(sp.getInt("httpport", 8000)));
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validate())
                    return;
                saveSettings();
            }
        });
    }

    private void saveSettings() {
        String host = this.host.getText().toString();
        int port = Integer.parseInt(this.port.getText().toString());
        int httpport = Integer.parseInt(this.imagePort.getText().toString());
        SharedPreferences sp = getSharedPreferences("server_setup", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("host", host);
        editor.putInt("port", port);
        editor.putInt("httpport", httpport);
        editor.apply();
        Intent intent = new Intent();
        intent.putExtra("result", true);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private boolean validate() {
        boolean valid = true;
        String host = this.host.getText().toString();
        String port = this.port.getText().toString();
        String porthttp = this.imagePort.getText().toString();
        if (host.isEmpty()) {
            valid = false;
            this.host.setError(getString(R.string.invalid_host_name_error));
        }
        else
            this.host.setError(null);
        int portNum;
        try {
            portNum = Integer.parseInt(port);
        }
        catch (Exception e) {
            portNum = -1;
        }
        if (portNum > 65535 || portNum < 0) {
            valid = false;
            this.port.setError(getString(R.string.invalid_port_number_error));
        }
        else
            this.port.setError(null);
        try {
            portNum = Integer.parseInt(porthttp);
        }
        catch (Exception e) {
            portNum = -1;
        }
        if (portNum > 65535 || portNum < 0) {
            valid = false;
            this.imagePort.setError(getString(R.string.invalid_port_number_error));
        }
        else
            this.imagePort.setError(null);
        return valid;
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
