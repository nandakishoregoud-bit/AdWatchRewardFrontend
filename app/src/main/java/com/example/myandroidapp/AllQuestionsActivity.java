package com.example.myandroidapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class AllQuestionsActivity extends AppCompatActivity {

    private ChipGroup categoryChipGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_questions);



        // Initialize ChipGroup
        categoryChipGroup = findViewById(R.id.category_chip_group);

        // Sample categories (replace with your dynamic data)
        String[] categories = {"All", "Technology", "Science", "Health", "Sports", "Entertainment"};

        // Dynamically add chips to the ChipGroup
        for (String category : categories) {
            Chip chip = new Chip(this);
            chip.setText(category);
            chip.setCheckable(true); // Make chip selectable
            /*chip.setChipBackgroundColorResource(R.color.category_chip_bg); // Define this in your colors.xml*/
            categoryChipGroup.addView(chip);
        }

        // Set a listener to detect selected category
        categoryChipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                if (checkedId != -1) { // Ensure a chip is selected
                    Chip selectedChip = findViewById(checkedId);
                    String selectedCategory = selectedChip.getText().toString();

                    // Fetch and display questions based on selected category
                    fetchQuestionsForCategory(selectedCategory);
                }
            }
        });
    }


    // Method to fetch questions based on category
    private void fetchQuestionsForCategory(String category) {
        // Replace this with your API call logic
        System.out.println("Selected Category: " + category);
        // TODO: Fetch and display questions based on the selected category
    }


}
