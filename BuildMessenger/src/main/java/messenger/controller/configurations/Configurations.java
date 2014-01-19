package messenger.controller.configurations;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import messenger.controller.dto.JobDto;
import messenger.controller.dto.JobsDto;
import messenger.controller.dto.User;
import messenger.controller.dto.Users;
import messenger.util.FileUtil;

public class Configurations {

    private final static String CONFIG_DIR = new File("../resources/config/").getAbsolutePath();
    private static final String USER_XML = CONFIG_DIR + "/Users.xml";
    private static final String JOB_XML = CONFIG_DIR + "/Jobs.xml";
    private final static Users users;
    private final static JobsDto jobsDto;

    static {
        try {
            users = (Users) FileUtil.readXml(USER_XML, Users.class);
            jobsDto = (JobsDto) FileUtil.readXml(JOB_XML, JobsDto.class);
        } catch (JAXBException e) {
            e.printStackTrace();
            throw new IllegalStateException(e.getMessage());
        }
    }

    public static User getUser(String number) {
        List<User> list = users.getUsers();
        for (User user : list) {
            if (user.getNumber().equals(number)) {
                return user;
            }
        }
        return null;
    }

    public static List<User> getRegisteredUsers(JobDto jobDto) {
        List<User> list = users.getUsers();
        List<User> registered = new ArrayList<User>();
        for (User user : list) {
            if (user.getRegisteredJobs().contains(jobDto)) {
                registered.add(user);
            }
        }
        return registered;
    }

    public static JobsDto getJobs() {
        return jobsDto;
    }

	public static String getDefaultJob(User user) {
		return user.getRegisteredJobs().iterator().next().getJobId();
	}

}
