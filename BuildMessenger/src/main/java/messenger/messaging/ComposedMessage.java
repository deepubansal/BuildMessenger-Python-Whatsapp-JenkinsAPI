package messenger.messaging;

import java.util.List;
import java.util.Map;

public class ComposedMessage {
    private String fileName;
    private String message;
    private List<String> numbers;
    private Map<String, String> sentMessageIds;

    public ComposedMessage(String fileName, String message, List<String> numbers) {
        super();
        this.fileName = fileName;
        this.message = message;
        this.numbers = numbers;
    }

    public String getCommand() {
        StringBuffer command = new StringBuffer(fileName + "|");
        for (String string : numbers) {
            command.append(string + ",");
        }
        command.deleteCharAt(command.lastIndexOf(","));
        return command.toString().trim();
    }
    
    public String getMessage() {
        return message;
    }

    public Map<String, String> getSentMessageIds() {
        return sentMessageIds;
    }

    public void setSentMessageIds(Map<String, String> sentMessageIds) {
        this.sentMessageIds = sentMessageIds;
    }

    public String getFileName() {
        return fileName;
    }

    public List<String> getNumbers() {
        return numbers;
    }

}
