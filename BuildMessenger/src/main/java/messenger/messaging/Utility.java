package messenger.messaging;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Utility {

    public static String readFile(String file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();

        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append("\n");
        }
        reader.close();
        return stringBuilder.toString();
    }

    public static void writeToFile(String fileName, String content) {
        try {
            File file = new File(fileName);
            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            // true = append file
            FileWriter fileWritter = new FileWriter(file.getName(), true);
            BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
            bufferWritter.write(content);
            bufferWritter.close();

            System.out.println("Done");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
//    public static void readJobsXml(String fileName, String content) {
//        try {
//            File file = new File("src/main/resources/JobsDto.xml");
//            JAXBContext jaxbContext = JAXBContext.newInstance(Users.class);
//
//            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
////            Users users = (Users) jaxbUnmarshaller.unmarshal(file);
////            System.out.println(users.getUsers().get(0).getName());
//
//        } catch (JAXBException e) {
//            e.printStackTrace();
//            throw new IllegalStateException(e.getMessage());
//        }
//
//    }
//    
    
    
}
