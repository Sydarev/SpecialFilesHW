package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = ParserCSV.parseCSV(columnMapping, fileName);
        String jsonCSV = listToJson(list);
        writeString(jsonCSV, "data.json");

        List<Employee> employeeList = parserXML("data.xml");
        String jsonXML = listToJson(employeeList);
        writeString(jsonXML, "data2.json");

        String json = readString("data.json");
//        System.out.println(json);
        employeeList = jsonToList(json);
        for (Employee emp: employeeList){
            System.out.println(emp);
        }
    }

    public static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder()/*.setPrettyPrinting()*/;
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        return gson.toJson(list, listType);
    }

    public static void writeString(String json, String fileName) {
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Employee> parserXML(String fileName) {
        DocumentBuilderFactory documentBuilderFactory = new DocumentBuilderFactoryImpl();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(new File(fileName));
            Node root = document.getDocumentElement();
            NodeList nodes = root.getChildNodes();
            List<Employee> list = new ArrayList<>(nodes.getLength());
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                if (Node.ELEMENT_NODE == node.getNodeType()) {
                    Element element = (Element) nodes.item(i);
                    int id = Integer.parseInt(element.getElementsByTagName("id").item(0).getTextContent());
                    String firstName = element.getElementsByTagName("firstName").item(0).getTextContent();
                    String lastName = element.getElementsByTagName("lastName").item(0).getTextContent();
                    String country = element.getElementsByTagName("country").item(0).getTextContent();
                    int age = Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent());
                    Employee employee = new Employee(id, firstName, lastName, country, age);
                    list.add(employee);
                }
            }
            return list;
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    public static String readString(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            return br.readLine();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Employee> jsonToList(String json) {
        JSONParser parser = new JSONParser();
        List<Employee> list = new ArrayList<>();
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            JSONArray employees = (JSONArray) parser.parse(json);
            for (Object employee : employees) {
                list.add(gson.fromJson(employee.toString(), Employee.class));
            }
            return list;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }

}