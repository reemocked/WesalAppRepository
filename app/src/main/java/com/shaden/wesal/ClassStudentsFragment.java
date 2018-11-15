package com.shaden.wesal;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ClassStudentsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ClassStudentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClassStudentsFragment extends Fragment {

    FirebaseDatabase database;
    DatabaseReference ref, classRef;
    ListView listView;
    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    students student;
    studentPAM studentPAM;
    TextView noStudents;
    ArrayList<students> allStudents;
    FirebaseAuth mAuth;
    Classes staffClass, classes;
    String staffId;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ClassStudentsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ClassStudentsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ClassStudentsFragment newInstance(String param1, String param2) {
        ClassStudentsFragment fragment = new ClassStudentsFragment();
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
        View v = inflater.inflate(R.layout.fragment_class_students, container, false);
        student = new students();
        studentPAM = new studentPAM();
        mAuth = FirebaseAuth.getInstance();


        listView = (ListView) v.findViewById(R.id.studentsList);
        noStudents = (TextView) v.findViewById(R.id.noStudents);
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("students");
        classRef = database.getReference("classes");
        list = new ArrayList<>();
        allStudents = new ArrayList<>();
        adapter = new ArrayAdapter<String>(getContext(), R.layout.onestudent,R.id.studentInfo,list);
        FirebaseUser user =  mAuth.getCurrentUser();
        staffId = user.getUid();
        staffClass = new Classes();

        classRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    classes = ds.getValue(Classes.class);
                    if(classes.getTeacherID().equals(staffId)){
                        staffClass.setID(classes.getID());
                        staffClass.setName(classes.getName());
                        staffClass.setTeacher(classes.getTeacher());
                        staffClass.setTeacherID(classes.getTeacherID());
                    }

                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (staffClass == null){
            noStudents.setText("عذرا لا يوجد لديك فصل");
        }
        else{

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds:dataSnapshot.getChildren())
                    {
                        student = ds.getValue(students.class);
                        if(staffClass.getID().equals(student.getClassID())) {
                            allStudents.add(student);
                            list.add(student.getFirstname().toString() + " " + student.getMiddleName().toString() + " " + student.getLastname().toString());
                        }
                    }
                    listView.setAdapter(adapter);
                    if(list.isEmpty()){
                        noStudents.setText("لا يوجد طلاب حاليّا");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    StaffHomePage.setClassStudentId(allStudents.get(position).getStId());
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.main_frame, studentPAM);
                    fragmentTransaction.commit();
                }
            });

       }



        return v;
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
}