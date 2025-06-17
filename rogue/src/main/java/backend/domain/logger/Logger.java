package backend.domain.logger;

import java.util.ArrayList;

public class Logger {
    private ArrayList<String> messages;
    private ArrayList<Integer> ageOfMessages;

    public Logger() {
        messages = new ArrayList<>();
        ageOfMessages = new ArrayList<>();
    }

    public ArrayList<String> getMessages() {
        return messages;
    }

    public void addMessage(String message) {
        if (message != null) {
            messages.add(message);
            ageOfMessages.add(1);
        }
    }

    public void clearOldMessages() {
        for (int i = 0; i < messages.size(); ++i) {
            if (ageOfMessages.get(i) > 3) {
                messages.remove(i);
                ageOfMessages.remove(i);
                --i;
            }
        }
    }

    public void fullClear() {
        messages.clear();
        ageOfMessages.clear();
    }

    public void updateMessagesAge() {
        for (int i = 0; i < ageOfMessages.size(); ++i) {
            ageOfMessages.set(i, ageOfMessages.get(i) + 1);
        }
    }
}
