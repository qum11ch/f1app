package com.example.f1app;

import static com.example.f1app.MainActivity.getStringByName;
import static com.example.f1app.teamsStandingsActivity.localizeLocality;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class mainPastRaceAdapter extends RecyclerView.Adapter<mainPastRaceAdapter.DataHolder>{
    Activity context;
    List<concludedRacesData> dataList;

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1)
            return 1;
        else
            return 2;
    }

    public mainPastRaceAdapter(Activity context , List<concludedRacesData> datum){
        this.context = context;
        dataList = datum;
    }

    @NonNull
    @Override
    public DataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.item_main_concluded_race, parent , false);
        return new DataHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataHolder holder, int position) {
        LocalDate currentDate = LocalDate.now();
        String currentYear = Integer.toString(currentDate.getYear());

        concludedRacesData datum = dataList.get(position);

        String round = datum.getRoundNumber();
        holder.round.setText(round);

        String dateStart_string = datum.getDateStart();
        String dateEnd_string = datum.getDateEnd();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-M-d");
        LocalDate dateStart = LocalDate.parse(dateStart_string, dateFormatter);
        String dayStart = dateStart.format(DateTimeFormatter.ofPattern("dd"));

        LocalDate dateEnd = LocalDate.parse(dateEnd_string, dateFormatter);
        String dayEnd = dateEnd.format(DateTimeFormatter.ofPattern("dd"));

        String monthStart = dateStart.format(DateTimeFormatter.ofPattern("MMM"));
        String monthEnd = dateEnd.format(DateTimeFormatter.ofPattern("MMM"));

        if(monthStart.equals(monthEnd)){
            holder.raceMonth.setText(monthStart);
        }
        else{
            String month = monthStart + "-" + monthEnd;
            holder.raceMonth.setText(month);
        }

        String localeRaceName = datum.getRaceName().toLowerCase().replaceAll("\\s+", "_");
        String pastRaceName = context.getString(getStringByName(localeRaceName + "_text")) + " " + currentYear;
        holder.raceName.setText(pastRaceName);
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("circuits").orderByChild("circuitName").equalTo(datum.getCircuitName()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot circtuitSnap: snapshot.getChildren()){
                    String circuitId = circtuitSnap.child("circuitId").getValue(String.class);
                    holder.circuitName.setText(context.getString(getStringByName(circuitId + "_text")));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Fail while getting data from Firebase " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        String locale;
        if (Locale.getDefault().getLanguage().equals("ru")){
            ArrayList<String> localizedData = localizeLocality(datum.getLocality(), datum.getCountry(), context);
            //String country = localizedData.get(0);
            //String cityName = localizedData.get(1);
            locale = localizedData.get(2);
        }else{
            String cityName = datum.getLocality();
            switch (cityName){
                case "Monaco":
                case "Singapore":
                    cityName = null;
                    break;
                default:
                    break;
            }
            if (cityName!=null){
                locale = cityName + ", " + datum.getCountry();
            }else{
                locale = datum.getCountry();
            }

        }

        holder.raceCountry.setText(locale);
        holder.day_start.setText(dayStart);
        holder.day_end.setText(dayEnd);


        String firstPlace_code = datum.getWinnerDriverCode();
        String secondPlace_code = datum.getSecondPlaceCode();
        String thirdPlace_code = datum.getThirdPlaceCode();
        holder.firstPlace_code.setText(firstPlace_code);
        holder.secondPlace_code.setText(secondPlace_code);
        holder.thirdPlace_code.setText(thirdPlace_code);

        holder.constraintLayout.setOnClickListener(v -> {
            Intent intent = new Intent(context, concludedRaceActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("raceName" , datum.getRaceName());
            bundle.putString("raceStartDay" , dayStart);
            bundle.putString("raceEndDay" , dayEnd);
            bundle.putString("raceStartMonth" , monthStart);
            bundle.putString("raceEndMonth" , monthEnd);
            bundle.putString("circuitName" , datum.getCircuitName());
            bundle.putString("roundCount" , datum.getRoundNumber());
            bundle.putString("raceCountry" , datum.getCountry());
            bundle.putString("dateStart", datum.getDateStart());
            bundle.putString("firstPlaceCode", firstPlace_code);
            bundle.putString("secondPlaceCode", secondPlace_code);
            bundle.putString("thirdPlaceCode", thirdPlace_code);
            intent.putExtras(bundle);
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class DataHolder extends RecyclerView.ViewHolder{

        TextView round, day_start, day_end, raceMonth, raceCountry, raceName, circuitName,
                secondPlace_code, firstPlace_code, thirdPlace_code;
        ConstraintLayout constraintLayout;
        public DataHolder(@NonNull View itemView) {
            super(itemView);
            round = itemView.findViewById(R.id.round);
            day_start = itemView.findViewById(R.id.day_start);
            day_end = itemView.findViewById(R.id.day_end);
            raceMonth = itemView.findViewById(R.id.raceMonth);
            raceCountry = itemView.findViewById(R.id.raceCountry);
            raceName = itemView.findViewById(R.id.raceName);
            circuitName = itemView.findViewById(R.id.circuitName);
            secondPlace_code = itemView.findViewById(R.id.secondPlace_code);
            firstPlace_code = itemView.findViewById(R.id.firstPlace_code);
            thirdPlace_code = itemView.findViewById(R.id.thirdPlace_code);
            constraintLayout = itemView.findViewById(R.id.main_layout);
        }
    }

}
