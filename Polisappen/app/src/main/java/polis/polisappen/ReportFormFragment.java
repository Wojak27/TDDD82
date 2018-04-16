package polis.polisappen;

/**
 * Created by karolwojtulewicz on 2018-02-07.
 */
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;

public class ReportFormFragment extends Fragment implements CompoundButton.OnCheckedChangeListener{

    String sensitivityType = "1";
    CheckBox sensitiveCheckBox;
    CheckBox superTopSecretCheckBox;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.report_form_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle extras = getArguments();
        view.setBackgroundColor(Color.WHITE);
        final LatLng latLng = new LatLng(extras.getDouble("latidude"), extras.getDouble("longitude"));

        Button dismissButton = (Button) this.getActivity().findViewById(R.id.dismissButton);
        final Fragment myFragment = this;
        dismissButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getActivity().getFragmentManager().beginTransaction().remove(myFragment).commit();
//                getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
            }
        });

        final EditText editText = (EditText) getActivity().findViewById(R.id.reportText);
         sensitiveCheckBox = (CheckBox) getActivity().findViewById(R.id.sensitiveCheckBox);
        superTopSecretCheckBox = (CheckBox) getActivity().findViewById(R.id.top_secret_checkbox);
        sensitiveCheckBox.setOnCheckedChangeListener(this);
        superTopSecretCheckBox.setOnCheckedChangeListener(this);
        Button commitButton = (Button) this.getActivity().findViewById(R.id.commitButton);
        commitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!sensitiveCheckBox.isChecked() && !superTopSecretCheckBox.isChecked()){
                    sensitivityType = "1";
                    Log.w("skickar type", sensitivityType);
                }
                Log.w("skickar type", sensitivityType);
                ((MapsActivity) getActivity()).addMarkerToDatabase(latLng,"my marker",editText.getText().toString(), sensitivityType);
                Log.v("edittext", editText.getText().toString());
                Log.v("edittext", "text");
                getActivity().getFragmentManager().beginTransaction().remove(myFragment).commit();
            }
        });

        Button manipluatedButton = (Button) this.getActivity().findViewById(R.id.manipulatedButton);
        manipluatedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type = "1";
                if(sensitiveCheckBox.isChecked()){
                    type = "2";
                }
                ((MapsActivity) getActivity()).addMarkerToDatabase(latLng,"manipulated",editText.getText().toString(),type);
                getActivity().getFragmentManager().beginTransaction().remove(myFragment).commit();
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.sensitiveCheckBox){
            if(isChecked){
                sensitivityType = "2";
                superTopSecretCheckBox.setChecked(false);
                sensitiveCheckBox.setChecked(true);
            }else {
                sensitiveCheckBox.setChecked(false);
            }

        }else if (buttonView.getId() == R.id.top_secret_checkbox){
            if(isChecked){
                sensitivityType = "3";
                sensitiveCheckBox.setChecked(false);
                superTopSecretCheckBox.setChecked(true);
            }else {
                superTopSecretCheckBox.setChecked(false);
            }

        }
    }
}