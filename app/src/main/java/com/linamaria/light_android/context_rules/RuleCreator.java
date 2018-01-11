package com.linamaria.light_android.context_rules;

import android.content.Context;
import android.media.AudioManager;

import com.linamaria.light_android.ContextManagementActivity;
import com.linamaria.light_android.RoomContextState;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by LinaMaria on 12/20/2017.
 */

public class RuleCreator {

    private ContextManagementActivity cm;

    public static List<RoomContextRule> createRules(final ContextManagementActivity cm){
        RoomContextRule rule1 = new RoomContextRule(cm) {
            @Override
            public void apply(RoomContextState roomContextState) {
                super.apply(roomContextState);
                if (condition(roomContextState))
                    cm.displayMessage(this + " applies: silent mode switched on!");
            }

            @Override
            protected boolean condition(RoomContextState roomContextState) {
                return roomContextState.getLightLevel() > 100
                        && roomContextState.getNoiseLevel() > 1.0;
            }

            @Override
            protected void action() {
                ((AudioManager)cm.getApplicationContext().getSystemService(
                        Context.AUDIO_SERVICE))
                        .setRingerMode(AudioManager.RINGER_MODE_SILENT);
            }

            public String toString() {
                return "Rule 1";
            }
        };
        return Collections.singletonList(rule1);
    }
}
