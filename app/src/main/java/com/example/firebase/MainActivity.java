package com.example.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.firebase.databinding.ActivityMainBinding;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnItemsClick {

    ActivityMainBinding binding;
    private ExpenseAdapter expenseAdapter;
    Intent intent;


    private long income = 0 , expense = 0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Button button = (Button) findViewById(R.id.category_pie);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Category_pie.class);
                startActivity(intent);
            }
        });

        expenseAdapter = new ExpenseAdapter(this, this);
        binding.recyclerview.setAdapter(expenseAdapter);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));

       intent = new Intent(MainActivity.this, Add_expense.class);

        binding.addinc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("type", "Income");
                startActivity(intent);
            }
        });
        binding.addexp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("type", "Expense");
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please");
        progressDialog.setMessage("Wait");
        progressDialog.setCancelable(false);

        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            progressDialog.show();
            FirebaseAuth.getInstance().signInAnonymously()
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(MainActivity.this, "Authentication Successfull", Toast.LENGTH_SHORT).show();
                            progressDialog.cancel();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.cancel();
                            Toast.makeText(MainActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        income =0;
        expense = 0;
        getData();
    }

    private void getData() {
        FirebaseFirestore.getInstance()
                .collection("expenses")
                .whereEqualTo("uid", FirebaseAuth.getInstance().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        expenseAdapter.clear();
                        List<DocumentSnapshot> dsList = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot ds:dsList){
                            ExpenseModel expenseModel = ds.toObject(ExpenseModel.class);
                            if(expenseModel.getType().equals("Income")){
                                income+= expenseModel.getAmount();
                            }else{

                                expense += expenseModel.getAmount();

                            }
                            expenseAdapter.add(expenseModel);

                        }

                        setUpGraph();
                    }
                });
    }

    private void setUpGraph() {

        List<PieEntry> pieEntryList = new ArrayList<>();
        List<Integer> colorsList = new ArrayList<>();

        if (income!=0){
            pieEntryList.add(new PieEntry(income, "Income"));
            colorsList.add(getResources().getColor(R.color.teal_700));
        }

        if (expense!=0){
            pieEntryList.add(new PieEntry(expense, "Expense"));
            colorsList.add(getResources().getColor(R.color.red));
        }
        PieDataSet pieDataSet = new PieDataSet(pieEntryList, String.valueOf(income-expense));
        pieDataSet.setColors(colorsList);
        PieData pieDat = new PieData(pieDataSet);

        binding.piechart.setData(pieDat);
        binding.piechart.invalidate();


    }

    @Override
    public void onClick(ExpenseModel expenseModel) {
        intent.putExtra("model", expenseModel);
        startActivity(intent);
    }
}