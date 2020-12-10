package org.tbplusc.app.telegram.interaction;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NumbersKeyboard extends InlineKeyboardMarkup {
    public NumbersKeyboard() {
        super();
        var r1 = new ArrayList<InlineKeyboardButton>();
        var r2 = new ArrayList<InlineKeyboardButton>();
        for (var i = 1; i <= 5; i++) {
            r1.add(inlineKeyboardButtonFactory(Integer.toString(i)));
            r2.add(inlineKeyboardButtonFactory(Integer.toString(i + 5)));
        }
        var rowList = new ArrayList<List<InlineKeyboardButton>>(Arrays.asList(r1, r2));
        this.setKeyboard(rowList);
    }

    private static InlineKeyboardButton inlineKeyboardButtonFactory(String name) {
        var b = new InlineKeyboardButton(name);
        b.setCallbackData(name);
        return b;
    }
}
