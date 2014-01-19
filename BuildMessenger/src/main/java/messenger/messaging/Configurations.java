package messenger.messaging;

import java.io.File;

public class Configurations {

    // public final static String SEND_COMMAND =
    // "src/main/resources/sendMessage.sh";
	static {
		System.out.println(new File(".").getAbsolutePath());
	}
    public final static String LISTEN_COMMAND = "python yowsup-cli.py -l --resourceLocation " 
            + new File("../resources").getAbsolutePath();
    public final static String YOWSUP_HOME = new File("../resources/Yowsup/").getAbsolutePath()+"/";
    public final static String TEMP_DIR = new File("../resources/tmp/").getAbsolutePath()+"/";
    public final static String LOG_DIR = new File("../resources/logs/").getAbsolutePath()+"/";
}
