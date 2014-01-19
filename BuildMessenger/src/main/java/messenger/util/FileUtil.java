package messenger.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class FileUtil {

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
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWritter = new FileWriter(file.getAbsolutePath(), true);
            BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
            bufferWritter.write(content);

            bufferWritter.close();

//            System.out.println("Done at " + file.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteFile(String path) {
        File file = new File(path);
        file.delete();
    }

    @SuppressWarnings("rawtypes")
    public static Object readXml(String pathname, Class class1) throws JAXBException {
        File file = new File(pathname);
        JAXBContext jaxbContext = JAXBContext.newInstance(class1);

        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        Object unmarshal = jaxbUnmarshaller.unmarshal(file);
        return unmarshal;
    }


}
