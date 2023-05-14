package pdm.pratica04;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;

public class MarkerTitleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_title);
    }
    public void onOkButtonClick(View view) {
        EditText markerTitleEditText = findViewById(R.id.markerTitleEditText);
        String markerTitle = markerTitleEditText.getText().toString();
        Intent intent = null;
        LatLng latLng = intent.getParcelableExtra("latLng");

        intent = new Intent();
        intent.putExtra("markerTitle", markerTitle);
        intent.putExtra("latLng", latLng);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void onCancelButtonClick(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }

}