package com.credr.inspsheettesting;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.credr.inspsheettesting.models.CollectedData;
import com.credr.inspsheettesting.models.PartStats;
import com.credr.inspsheettesting.models.PartsObject;
import com.credr.inspsheettesting.models.Question;
import com.credr.inspsheettesting.models.QuestionStats;
import com.credr.inspsheettesting.models.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

/**
 * Created by vijayagnihotri on 23/06/16.
 */
public class CollectSampleActivity extends AppCompatActivity {

    private ArrayList<String> partsArray = new ArrayList<>(
            Arrays.asList("Vehicle Number", "Chassis Number", "Make", "Engine Number", "Model", "Variant", "Date Of Manufacturing", "CC", "Location in RC", "Bike Color", "Date Of First Registration", "Vehicle Modified or Not", "Class", "Registration Certificate", "HP/Lease", "Insurance", "Insurance Validity", "PUC", "Loan History", "Shock Absorbers", "ABS", "Meter Board", "Fuel Guage", "Handle Lock", "Saare Guard", "Leg Guard", "Wheel Type", "Headlight Fairing", "Wind Screen", "Mudguard", "Clutch Lever", "Brake Lever", "Main Stand", "Side Stand", "Gear Shifter", "Kick", "Brake Pedal", "Chain Cover", "Seat", "Seat Lock", "Headlight Lense", "Tail Light Lense", "Indicator Lense", "Switches", "Engine Belly", "Foot Rest", "Tank Cover", "Side Panel", "Seat Cover Panel", "Handle Bar", "Handle Lock", "Left Mirror", "Right Mirror", "Fuel Tank", "Fork Assembly", "Fork Tube", "Fork", "Fork Oil Seal", "Fork T-Plate", "Bearing", "Fork Alignment", "Swing Arm", "Mounting Bush", "Bearing", "Rear Axel", "Mono Shock Absorbers", "Dual Shock Absorbers", "Shock Absorber", "Coil Spring", "Wheel", "Alloy Wheel", "Spoke Wheel", "Wheel Rim", "Tyre Code", "Tyre Manufacturing Date", "Tyre Make", "Tyre Condition", "Wheel Drum Effectiveness", "Wheel Drum Brake Spring", "Wheel Drum Brake LinerMarking", "Wheel Drum Brake Cable", "Wheel Disk Brake Pads", "Wheel Disk Brake Oil Pipe", "Wheel Disk Brake Oil Level", "Wheel Disk Disk Condition", "Wheel Disk Master KitAssembly", "Silencer Mounting", "Silencer Front End", "Silencer Mid End", "Silencer Rear End", "Silencer Cover", "Foot Rest", "Muffler", "Silencer Mounting Bush", "Kick Performance", "Kick Ratchet", "Kick Spring", "Gear", "Engine", "Tappet", "Timing Chain", "Chain Tensioner", "Valve", "Camshaft", "Piston Rings", "Piston", "Connecting Rod", "Cylinder Bore", "Gudian Pin", "Engine Oil", "Acceleration", "Transmission", "Chain Lubrication", "Chain Slackness", "Gear Shifter", "Clutch Wire", "Gears", "Gear Shifter", "Gear Drum", "Fork", "Clutch Plates", "Chain", "Chain Sprocket", "Shifter Spring", "Drive Comfort", "Speedometer", "Odometer", "Tachometer", "Fuel Guage", "Meter Tampering", "Neutral Light", "Head Light", "Indicator", "Meter Light", "Tail Lamp", "Horn", "Wiring Harness", "Self Start Check", "Battery Status"));

    private CollectedData collectedData = new CollectedData();
    private TextView questName;
    private Button yesButton, noButton;
    private PartsObject partsObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_layout);
        partsObject = (PartsObject) getIntent().getSerializableExtra("parts");
        if(partsObject != null) {
            partsArray = new ArrayList<>();
            for(PartStats partStat : partsObject.parts) {
                partsArray.add(partStat.name);
            }
        } else {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference dataCollectionRef = database.getReference();
            partsObject =  new PartsObject();
            partsObject.version = "v1";
            partsObject.parts = new ArrayList<>();
            for(String part : partsArray) {
                PartStats partStats = new PartStats();
                partStats.name = part;
                partsObject.parts.add(partStats);
            }
            dataCollectionRef.child("parts").child("partsObject").setValue(partsObject);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        questName = (TextView) findViewById(R.id.questName);
        yesButton = (Button) findViewById(R.id.yesButton);
        noButton = (Button) findViewById(R.id.noButton);

        collectedData.user = new User();
        collectedData.user.questionList = new ArrayList<>();
        collectedData.questionStats = new QuestionStats();

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_layout);
        TextView greet = (TextView) dialog.findViewById(R.id.greet_message);
        final EditText explanation = (EditText) dialog.findViewById(R.id.explanation);
        greet.setText("Enter name");
        dialog.setCancelable(false);
        dialog.show();
        dialog.findViewById(R.id.okay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectedData.user.name = explanation.getText().toString();
                displayQuestions(0);
                dialog.dismiss();
            }
        });

    }

    private void displayQuestions(final int index) {
        if(partsArray.size() == index) {
            Toast.makeText(this, "Data Collected", Toast.LENGTH_LONG).show();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference dataCollectionRef = database.getReference();
            String key = collectedData.user.name + " " + Calendar.getInstance().getTime().toString();
            dataCollectionRef.child("data").child(key).setValue(collectedData);
            dataCollectionRef.child("parts").child("partsObject").setValue(partsObject);
            finish();
        } else {
            final Question question = new Question();
            question.text = partsArray.get(index);
            questName.setText((index+1) + ". " + question.text + "?");
            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    question.answer = "Yes";
                    collectedData.user.questionList.add(question);
                    collectedData.questionStats.noOfYes++;
                    partsObject.parts.get(index).noOfYes++;
                    displayQuestions(index+1);
                }
            });
            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    question.answer = "No";
                    collectedData.user.questionList.add(question);
                    collectedData.questionStats.noOfNo++;
                    partsObject.parts.get(index).noOfNo++;
                    displayQuestions(index+1);
                }
            });
        }
    }
}
