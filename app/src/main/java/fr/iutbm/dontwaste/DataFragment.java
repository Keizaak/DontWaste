package fr.iutbm.dontwaste;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DataFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DataFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private List<Meal> mealList = new ArrayList<>();
    private RecyclerView recyclerView;

    private SharedPreferences sharedPref;

    private View root = null;

    private AppDatabase appdb;
    private MealDAO mealDAO;

    private Button addMealButton;

    public DataFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DataFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DataFragment newInstance(String param1, String param2) {
        DataFragment fragment = new DataFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_data, container, false);
        recyclerView = root.findViewById(R.id.meal_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(new MealListAdapter(mealList, this.getContext()));

        addMealButton = (Button) root.findViewById(R.id.add_meal);
        addMealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAddMeal = new Intent(getContext(), AddNewMeal.class);
                startActivity(intentAddMeal);
            }
        });

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (!(sharedPref.getBoolean(getResources().getString(R.string.key_init_bdd), false))) {
            prepareMealData();
            sharedPref.edit().putBoolean("key_init_bdd", true).apply();
        }
        mealDAO = ((AppDatabase.getDatabase(getContext())).mealDAO());
        (new GetAllMealsAsyncTask(mealDAO)).execute();

        return root;
    }

    public void updateUI(){
        /*sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Boolean locationEnabled =
                sharedPref.getBoolean(getResources().getString(R.string.key_location_switch), false);
        String isLocationEnable = ": ";
        isLocationEnable += locationEnabled ? "True" : "False";
        tv_loc_enabled_out = (TextView) root.findViewById(R.id.text_location_switch_out);
        tv_loc_enabled_out.setText(isLocationEnable);*/
    }

    @Override
    public void onResume(){
        super.onResume();
        //updateUI();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void prepareMealData(){
        Meal m1, m2, m3;

        m1 = new Meal("Pâtes carbonara", "carbonara.jpg", 3.0f, "Bernard L.", 47.642900f, 6.840027f);
        m2 = new Meal("Couscous", "couscous.jpg", 5.0f, "bestcook90", 47.659518f, 6.813337f);
        m3 = new Meal("Salade composée", "salade.jpg", 2.0f, "Nadia A.", 47.6387143f, 6.8370225f);
        mealDAO = ((AppDatabase.getDatabase(getContext())).mealDAO());
        (new InsertAsyncTask(mealDAO)).execute(m1, m2, m3);
    }

    private class InsertAsyncTask extends AsyncTask<Meal, Void, Void> {

        private MealDAO dao;
        InsertAsyncTask(MealDAO dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(final Meal... params) {
            for (Meal m : params) {
                this.dao.insertMeals(m);
            }
            return null;
        }
    }

    private class GetAllMealsAsyncTask extends AsyncTask<Meal, Void, Void> {

        private MealDAO dao;

        GetAllMealsAsyncTask(MealDAO dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(final Meal... params) {
            mealList = dao.getAllMeals();
            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            super.onPostExecute(voids);
            recyclerView.setAdapter(new MealListAdapter(mealList, getContext()));
        }
    }
}
