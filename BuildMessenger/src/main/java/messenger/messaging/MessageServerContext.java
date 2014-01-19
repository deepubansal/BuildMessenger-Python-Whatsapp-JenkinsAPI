package messenger.messaging;

import java.io.File;
import java.io.IOException;

public class MessageServerContext {
    private boolean authed = false;
    private boolean serverUp = false;
    private boolean requestedToKill = false;
    private boolean whatsAppPoisoned = false;
    public boolean isWhatsAppPoisoned() {
		return whatsAppPoisoned;
	}

	public void setWhatsAppPoisoned(boolean whatsAppPoisoned) {
		this.whatsAppPoisoned = whatsAppPoisoned;
	}

	private File commandFile = new File(Configurations.TEMP_DIR + "commandFile");

    public MessageServerContext() {
        if (commandFile.exists()) {
            commandFile.delete();
        }
        try {
			commandFile.createNewFile();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
    }

    public File getCommanFile() {
        return commandFile;
    }

    public boolean isAuthed() {
        return authed;
    }

    public void setAuthed(boolean authed) {
        this.authed = authed;
    }

    public boolean isServerUp() {
        return serverUp;
    }

    public void setServerUp(boolean serverUp) {
        this.serverUp = serverUp;
    }

    public boolean isServerListening() {
        return authed && serverUp;
    }

    public boolean isRequestedToKill() {
        return requestedToKill;
    }

    public void requestKill() {
        this.requestedToKill = true;
    }
}
