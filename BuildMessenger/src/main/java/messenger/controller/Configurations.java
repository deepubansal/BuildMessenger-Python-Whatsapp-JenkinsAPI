package messenger.controller;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import messenger.dto.JobsDto;
import messenger.dto.User;
import messenger.dto.Users;

public class Configurations {

    private final static Users users;
    private final static JobsDto jobsDto;

    static {
        try {
            File file = new File("src/main/resources/Users.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(Users.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            users = (Users) jaxbUnmarshaller.unmarshal(file);
//            System.out.println(users.getUsers().get(0).getName());

        } catch (JAXBException e) {
            e.printStackTrace();
            throw new IllegalStateException(e.getMessage());
        }

        try {
            File file = new File("src/main/resources/Jobs.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(JobsDto.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            jobsDto = (JobsDto) jaxbUnmarshaller.unmarshal(file);
//            System.out.println(jobsDto.getJobDtos().get(0).getJobId());

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

    public static JobsDto getJobs() {
        return jobsDto;
    }
}
