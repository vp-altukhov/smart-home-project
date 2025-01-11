package ru.newvasuki.smarthome.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import ru.newvasuki.smarthome.config.SmartHomeConfigurationProperties;
import ru.newvasuki.smarthome.data.entity.Profile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.newvasuki.smarthome.bot.Constants.*;

public class ResponseHandler {
    private static final Logger logger = LoggerFactory.getLogger(ResponseHandler.class);
    private final AbilityBot bot;
    private final SilentSender sender;
    private final SmartHomeConfigurationProperties config;
    private final StatusService statusService;
    private final Map<Long, UserState> chatStates;

    public ResponseHandler(AbilityBot bot, SilentSender sender, SmartHomeConfigurationProperties config, StatusService statusService) {
        this.bot = bot;
        this.sender = sender;
        this.config = config;
        this.statusService = statusService;
        this.chatStates = new HashMap<>();
    }

    public void replyToButtons(long chatId, Message message) {
        if (message.getText().equalsIgnoreCase("/stop") || message.getText().equalsIgnoreCase(STOP)) {
            stopChat(chatId);
        }

        switch (chatStates.get(chatId)) {
            case STARTING: selectAction(chatId);
                break;
            case ACTION_SELECT: replyToActionSelection(chatId, message);
                break;
            case DEVICE_SELECT: replyToDeviceSelection(chatId, message);
                break;
            case PROFILE_SELECT: replyToProfileSelection(chatId, message);
                break;
            default: unexpectedMessage(chatId);
        }
    }

    public void selectAction(long chatId) {
        promptWithKeyboardForState(chatId, SELECT_ACTION, KeyboardFactory.getAction(), UserState.ACTION_SELECT);
    }

    private void replyToProfileSelection(long chatId, Message message) {
        this.statusService.profileEnable(message.getText());
        promptWithKeyboardForState(chatId, SELECT_ACTION, KeyboardFactory.getAction(), UserState.ACTION_SELECT);
    }

    private void replyToDeviceSelection(long chatId, Message message) {
        List<Profile> profiles = this.statusService.getProfileList(message.getText());
        promptWithKeyboardForState(chatId, SELECT_PROFILE, KeyboardFactory.selectProfile(profiles), UserState.PROFILE_SELECT);
    }

    private void replyToActionSelection(long chatId, Message message) {
        if (VIEW_STATE.equalsIgnoreCase(message.getText())) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(this.statusService.getState());
            sender.execute(sendMessage);
            chatStates.put(chatId, UserState.STARTING);
            selectAction(chatId);
        } else if (GO_TO_CONTROL.equalsIgnoreCase(message.getText())) {
            promptWithKeyboardForState(chatId, SELECT_DEVICE, KeyboardFactory.selectDevice(this.statusService.getDeviceList()), UserState.DEVICE_SELECT);
        }
    }

    public void replyToStart(long chatId, User user) {
        if (!this.config.getAccessIsAllowed().contains(user.getId())) {
            logger.warn("Попытка получить доступ ID: {} firstName: {} lastName: {}", user.getId(), user.getFirstName(), user.getLastName());
            accessDeniedChat(chatId);
            return;
        }

        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream inputStream = classloader.getResourceAsStream("newvasuki.png");
            SendPhoto sendPhoto = SendPhoto.builder()
                    .chatId(chatId)
                    .photo(new InputFile(inputStream, "newvasuki.png"))
                    .build();
            this.bot.execute(sendPhoto);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(WELCOME);
        sender.execute(message);
        chatStates.put(chatId, UserState.STARTING);

        selectAction(chatId);
    }

    private void accessDeniedChat(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(ACCESS_DENIED);
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
        sender.execute(sendMessage);
    }

    private void stopChat(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(GOOD_BYE);
        chatStates.remove(chatId);
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
        sender.execute(sendMessage);
    }

    private void unexpectedMessage(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(NOT_EXPECTED);
        sender.execute(sendMessage);
    }

    private void promptWithKeyboardForState(long chatId, String text, ReplyKeyboard YesOrNo, UserState awaitingReorder) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(YesOrNo);
        sender.execute(sendMessage);
        chatStates.put(chatId, awaitingReorder);
    }

    public boolean userIsActive(Long chatId) {
        return chatStates.containsKey(chatId);
    }
}
