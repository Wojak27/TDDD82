package polis.polisappen;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ReportWindowActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_window);
        Button backButton = (Button) findViewById(R.id.backButtonReport);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intentExtras = getIntent();
        Bundle bundleExtras = intentExtras.getExtras();
        String reportText = bundleExtras.get("reportText").toString();

        TextView reportTextView = (TextView) findViewById(R.id.reportTextShow);
        reportTextView.setText(reportText);
    }

}
