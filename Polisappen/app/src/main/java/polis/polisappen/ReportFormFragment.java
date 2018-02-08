package polis.polisappen;

/**
 * Created by karolwojtulewicz on 2018-02-07.
 */
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;

public class ReportFormFragment extends Fragment {
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

        Button commitButton = (Button) this.getActivity().findViewById(R.id.commitButton);
        commitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MapsActivity) getActivity()).addMarkerToDatabase(latLng,"my marker",editText.getText().toString());
                Log.v("edittext", editText.getText().toString());
                Log.v("edittext", "text");
                getActivity().getFragmentManager().beginTransaction().remove(myFragment).commit();
            }
        });
    }
}