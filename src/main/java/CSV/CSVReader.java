package CSV;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class CSVReader{
    public final String filePath;
    public CSVReader(String filePath){
        this.filePath = filePath;
    }
    public String[] read(){
        try {
            String line;
            BufferedReader br = new BufferedReader((new FileReader(filePath)));
            line = br.readLine();
            String[] parameters = line.split(",");
            return parameters;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] err = new String[]{"d"};
        return err;
    }
}
