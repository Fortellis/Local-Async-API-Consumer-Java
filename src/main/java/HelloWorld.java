import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HelloWorld extends HttpServlet{

    public HelloWorld(){
        super();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException{ 
        PrintWriter healthCheckResponse = response.getWriter();
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
            buffer.append(System.lineSeparator());
        }
        String data = buffer.toString();
        System.out.println("This is your request: "+ data);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_ACCEPTED);
        healthCheckResponse.print("");
        healthCheckResponse.flush();
        newAsynchronousAPIPost(data);

    }
    public void newAsynchronousAPIPost(String newEvent) {

        try{
            ClassLoader classLoader = getClass().getClassLoader();
            File  wholeFile =new File (classLoader.getResource("queue.json").getFile());
            InputStream inputStream = new FileInputStream(wholeFile);
            FileInputStream fis = new FileInputStream(wholeFile);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String concatenatedFile = "";
            String strLine;
            //Put all the lines from the file back together to make the original object.
            while((strLine = br.readLine()) != null){
                //System.out.println(strLine);
                concatenatedFile = concatenatedFile + strLine;
            }
            System.out.println("This is the concatenatedFile: " + concatenatedFile);
            //Make the concatenated lines a JSON object again. 
            JSONObject objectForConcatenatedFile = new JSONObject(concatenatedFile);
            //Change the connectionRequests from the file into just an array. 
            JSONArray parsedRequestsInQueue = objectForConcatenatedFile.getJSONArray("queue");
            System.out.println("This is just the array of the connectionRequests: "+ parsedRequestsInQueue);
            //Change the connectionRequest into an object
            JSONObject addObject = new JSONObject(newEvent);
            //Put the connection request object into the array.
            parsedRequestsInQueue.put(addObject);
            System.out.println("This is the object for the concatenated file: "+ objectForConcatenatedFile.toString(4));

            Path path = Paths.get(wholeFile.toString());
            String str = objectForConcatenatedFile.toString(4);
            byte[] arr = str.getBytes();
            try{
                Files.write( path, arr);
            }catch(IOException ex){
                System.out.print("Invalid Path");
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}