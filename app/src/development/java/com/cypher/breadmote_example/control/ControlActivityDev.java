package com.cypher.breadmote_example.control;

import android.content.Intent;
import android.os.Bundle;

import com.cypher.breadmote.ButtonComponent;
import com.cypher.breadmote.CheckBoxComponent;
import com.cypher.breadmote.Component;
import com.cypher.breadmote.Error;
import com.cypher.breadmote.LabelComponent;
import com.cypher.breadmote.RadioGroupComponent;
import com.cypher.breadmote.SliderComponent;
import com.cypher.breadmote.SwitchComponent;
import com.cypher.breadmote.TextFieldComponent;
import com.cypher.breadmote.TimePickerComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cypher on 6/25/16.
 */
public class ControlActivityDev extends ControlActivity {

    private ButtonComponent buttonComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<Component> componentList = new ArrayList<>();
        componentList.add(new SwitchComponent("Alarm", 0, false));
        componentList.add(new TimePickerComponent("Wakeup at", 0, 7, 30));
        componentList.add(new SliderComponent("Volume", 0, -50, 100, 75));
        componentList.add(new RadioGroupComponent("Ringtone", 0, 1, new String[]{
                "Mozart",
                "Bowie",
                "Nature",
                "Random"
        }));
        componentList.add(new LabelComponent("%s", "Label!"));
        componentList.add(new CheckBoxComponent("CheckBox", 0, false));
        componentList.add(new TextFieldComponent("A hint", 0, "about what to put here"));
        buttonComponent = new ButtonComponent("Generate Error", 0);
        componentList.add(buttonComponent);

        setComponents(componentList);
        componentAdapter.setComponents(componentList);
    }

    @Override
    protected boolean handleConnectionInfo(Intent intent) {
        return true;
    }

    @Override
    public void onRemoteCommand(Component component) {
        if (component == buttonComponent) {
            Error error = new Error("Example", "Of an error toast");
            onError(error);
        }
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
    }
}
