package polis.polisappen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ReportWindowActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_report_window);
        Intent intentExtras = getIntent();
        Bundle bundleExtras = intentExtras.getExtras();
        String reportText = bundleExtras.get("reportText").toString();
        Log.w("report text", "hello");

        TextView reportTextView = (TextView) findViewById(R.id.reportTextShow);
        reportTextView.setText(reportText);
        Button backButton = (Button) findViewById(R.id.backButtonReport);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

}
