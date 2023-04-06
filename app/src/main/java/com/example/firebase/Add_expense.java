package com.example.firebase;

import static com.example.firebase.R.menu.add_menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.firebase.databinding.ActivityAddExpenseBinding;
import com.example.firebase.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.UUID;

public class Add_expense extends AppCompatActivity {

    ActivityAddExpenseBinding binding;
    private String type;
    private ExpenseModel expenseModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddExpenseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        type=getIntent().getStringExtra("type");
        expenseModel=(ExpenseModel) getIntent().getSerializableExtra("model");




        if (expenseModel!=null) {
            type = expenseModel.getType();
            binding.amount.setText(String.valueOf(expenseModel.getAmount()));
            binding.category.setText(expenseModel.getCategory());
            binding.note.setText(expenseModel.getNote());
        }
        if (type.equals("Income")){
            binding.incomeradio.setChecked(true);

        }else{
           binding.expenseradio.setChecked(true);
        }

        binding.incomeradio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "Income";
            }
        });

        binding.expenseradio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "Expense";
            }
        });



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        if (expenseModel!=null){

            menuInflater.inflate(R.menu.add_menu, menu);


        }else {

            menuInflater.inflate(R.menu.update_menu, menu);

        }
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.saveExpense){
            if (expenseModel== null){
                createExpense();
            }
            else{
                updateExpense();
            }
            return true;
        }
        if(id==R.id.deleteExpense){

            deleteExpense();

        }
        return false;
    }

    private void deleteExpense() {

        FirebaseFirestore
                .getInstance()
                .collection("expenses")
                .document(expenseModel.getExpenseID())
                .delete();

        finish();

    }

    private void createExpense() {
        String expenseID = UUID.randomUUID().toString();
        String amount=binding.amount.getText().toString();
        String note=binding.note.getText().toString();
        String category=binding.category.getText().toString();

        boolean incomeChecked = binding.incomeradio.isChecked();

        if(incomeChecked){
            type="Income";
        }else{
            type="expense";
        }


        if (amount.trim().length()==0){
            binding.amount.setError("Empty");
            return;
        }


        ExpenseModel expenseModel = new ExpenseModel(expenseID, note, category, type, Long.parseLong(amount), Calendar.getInstance().getTimeInMillis(),
                FirebaseAuth.getInstance().getUid());

        FirebaseFirestore
                .getInstance()
                .collection("expenses")
                .document(expenseID)
                .set(expenseModel);
        finish();


    }

    private void updateExpense() {
        String expenseID = expenseModel.getExpenseID();
        String amount=binding.amount.getText().toString();
        String note=binding.note.getText().toString();
        String category=binding.category.getText().toString();

        boolean incomeChecked = binding.incomeradio.isChecked();

        if(incomeChecked){
            type="Income";
        }else{
            type="expense";
        }


        if (amount.trim().length()==0){
            binding.amount.setError("Empty");
            return;
        }


        ExpenseModel model = new ExpenseModel(expenseID, note, category, type, Long.parseLong(amount),expenseModel.getTime(),
                FirebaseAuth.getInstance().getUid());

        FirebaseFirestore
                .getInstance()
                .collection("expenses")
                .document(expenseID)
                .set(model);
        finish();


    }

}