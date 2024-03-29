package com.rexisoftware.connstatus.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.rexisoftware.connstatus.R;
import com.rexisoftware.connstatus.data.GpsData;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GpsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GpsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GpsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GpsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GpsFragment newInstance(String param1, String param2) {
        GpsFragment fragment = new GpsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_gps, container, false);
        ArrayAdapter<String> aa = new ArrayAdapter<>(inflater.getContext(), R.layout.myline, R.id.text1, GpsData.getDataArray());
        GpsData.setAdapter(aa);
        ListView lv = v.findViewById(R.id.gpslist);
        lv.setAdapter(aa);
        aa.notifyDataSetChanged();

        // Inflate the layout for this fragment
        return v;
    }
}