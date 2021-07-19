package tech.beepbeep.beept05.activities;

import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import tech.beepbeep.beept05.R;
import tech.beepbeep.beept05.adapters.ChargerListAdapter;
import tech.beepbeep.beept05.models.ChargerObject;
import tech.beepbeep.beept05.utils.ClickListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// https://stackoverflow.com/questions/37621934/inflateexception-binary-xml-file-line-8-error-inflating-class-imageview
// https://code.luasoftware.com/tutorials/android/android-use-recylerview-as-viewpager/
// https://www.figma.com/file/FKgC7gb2UHXOpMWunWBWq0/EV-Charger-CC-Terminal-UI-Draft?node-id=1%3A171
// https://stackoverflow.com/questions/8631095/how-to-prevent-going-back-to-the-previous-activity

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MAIN-ACTIVITY";

    List<ChargerObject> sampleData = new ArrayList<>(
        Arrays.asList(
            new ChargerObject(
                    "CHARGER-01",
                    "AC1",
                    "22",
                    "kWh",
                    "Condo A",
                    "HKD",
                    "1.50",
                    "/kWh",
                    "Approx time for full charge if you're at 10% - 30% is 5hrs"
            ),
            new ChargerObject(
                    "CHARGER-02",
                    "AC2",
                    "23",
                    "kWh",
                    "Condo B",
                    "HKD",
                    "1.50",
                    "/kWh",
                    "Approx time for full charge if you're at 10% - 30% is 5hrs"
            ),
            new ChargerObject(
                    "CHARGER-03",
                    "AC3",
                    "24",
                    "kWh",
                    "Condo C",
                    "HKD",
                    "1.50",
                    "/kWh",
                    "Approx time for full charge if you're at 10% - 30% is 5hrs"
            ),
            new ChargerObject(
                    "CHARGER-04",
                    "AC4",
                    "25",
                    "kWh",
                    "Condo D",
                    "HKD",
                    "1.50",
                    "/kWh",
                    "Approx time for full charge if you're at 10% - 30% is 5hrs"
            ),
            new ChargerObject(
                    "CHARGER-05",
                    "AC5",
                    "26",
                    "kWh",
                    "Condo E",
                    "HKD",
                    "1.50",
                    "/kWh",
                    "Approx time for full charge if you're at 10% - 30% is 5hrs"
            )
//            ),
//            new ChargerObject(
//                    "AC4",
//                    "25 kWh",
//                    "Condo D",
//                    "HKD1.50/kWh",
//                    "Approx time for full charge if you're at 10% - 30% is 5hrs"
//            )
        )
    );

    // Uncomment SnapHelper to enable Page Snap
    // I cannot do up a 1 row 2 column LayoutManager, so I designed my XML to have 2 charger at once (limitation)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView chargerList = findViewById(R.id.list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        chargerList.setLayoutManager(layoutManager);
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(chargerList);

        ChargerListAdapter chargerListAdapter = new ChargerListAdapter(this, sampleData, new ClickListener() {
            @Override
            public void onCardClicked(int position) {

            }
        });

        chargerList.setAdapter(chargerListAdapter);
        Log.i(TAG, String.valueOf(chargerList.getAdapter().getItemCount()));

    }

}