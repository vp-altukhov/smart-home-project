package ru.newvasuki.smarthome.bot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.newvasuki.smarthome.data.entity.Device;
import ru.newvasuki.smarthome.data.entity.Profile;

import java.util.Arrays;
import java.util.List;

import static ru.newvasuki.smarthome.bot.Constants.*;

public class KeyboardFactory {

    public static ReplyKeyboard getAction(){
        KeyboardRow row = new KeyboardRow();
        row.add(VIEW_STATE);
        row.add(GO_TO_CONTROL);
        row.add(STOP);
        return new ReplyKeyboardMarkup(Arrays.asList(row));
    }

    public static ReplyKeyboard selectDevice(List<Device> devices) {
        KeyboardRow row = new KeyboardRow();
        for (Device device: devices) row.add(device.getId().toString() + ":" + device.getName());
        row.add(STOP);
        return new ReplyKeyboardMarkup(Arrays.asList(row));
    }

    public static ReplyKeyboard selectProfile(List<Profile> profiles) {
        KeyboardRow row = new KeyboardRow();
        for (Profile profile: profiles) row.add(profile.getId().toString() + ":" + (profile.getEnable() ? "ВКЛЮЧЕНО " : "") + profile.getName());
        row.add(STOP);
        return new ReplyKeyboardMarkup(Arrays.asList(row));
    }
}
