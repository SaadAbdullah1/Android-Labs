package com.example.androidlabs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment {

    private long id;
    private AppCompatActivity parentActivity;

    public DetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        Bundle data = getArguments();
        assert data != null;
        id = data.getLong("id");
        TextView message = view.findViewById(R.id.fragmentMessage);
        message.setText(data.getString("message"));

        TextView idView = view.findViewById(R.id.fragmentMessageID);
        idView.setText("id: " + id);

        CheckBox checkBox = view.findViewById(R.id.fragmentMessageStatus);
        checkBox.setChecked(data.getBoolean("status"));

        Button button = view.findViewById(R.id.hideButton);
        button.setOnClickListener(click -> {
            parentActivity.getSupportFragmentManager().beginTransaction().remove(this).commit();
            if (!data.getBoolean("isTablet")) { //onPhone
                Objects.requireNonNull(getActivity()).onBackPressed(); //go back
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        parentActivity = (AppCompatActivity) context;
    }
}