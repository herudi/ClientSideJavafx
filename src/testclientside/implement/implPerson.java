/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testclientside.implement;

import java.util.ArrayList;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import testclientside.controller.layoutController;
import testclientside.interfaces.interPerson;
import testclientside.model.Person;
import testclientside.model.dialog;
import testclientside.service.Service;


public class implPerson implements interPerson {
    Service Service = new Service();
    
    @Override
    public ObservableList<Person> select() {
        ObservableList<Person> listData = FXCollections.observableArrayList();
        JSONArray arr;
        String jsonStr = Service.ServiceGet("select.php");
        if (jsonStr != null) {
            layoutController.statusConnection = 1;
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                arr = jsonObj.getJSONArray("hobi");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject c = arr.getJSONObject(i);
                    Person p = new Person();
                    p.setId(c.getString("id"));
                    p.setNama(c.getString("nama"));
                    p.setHobi(c.getString("hobi"));
                    listData.add(p);
                }
            } catch (JSONException e) {
            }
        } else {
            Platform.runLater(() -> {
                dialog.dialog(Alert.AlertType.ERROR, "Error Connection To Server");
            });
            layoutController.statusConnection = 0;
        }
        return listData;
    }

    @Override
    public void save(Person p) {
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<>(3);
        nameValuePairs.add(new BasicNameValuePair("id", p.getId()));
        nameValuePairs.add(new BasicNameValuePair("nama", p.getNama()));
        nameValuePairs.add(new BasicNameValuePair("hobi", p.getHobi()));
    	Service.ServicePost("insert.php", nameValuePairs);
    }

    @Override
    public void update(Person p) {
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<>(3);
        nameValuePairs.add(new BasicNameValuePair("id", p.getId()));
        nameValuePairs.add(new BasicNameValuePair("nama", p.getNama()));
        nameValuePairs.add(new BasicNameValuePair("hobi", p.getHobi()));
    	Service.ServicePost("update.php", nameValuePairs);
    }

    @Override
    public void delete(Person p) {
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<>(1);
        nameValuePairs.add(new BasicNameValuePair("id", p.getId()));
    	Service.ServicePost("delete.php", nameValuePairs);
    }

    @Override
    public ObservableList<Person> search(String a) {
        ObservableList<Person> listData = FXCollections.observableArrayList();
        JSONArray arr;
        String jsonStr = Service.ServiceGet("search.php?nama="+a);
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                arr = jsonObj.getJSONArray("hobi");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject c = arr.getJSONObject(i);
                    Person p = new Person();
                    p.setId(c.getString("id"));
                    p.setNama(c.getString("nama"));
                    p.setHobi(c.getString("hobi"));
                    listData.add(p);
                }
            } catch (JSONException e) {
            }
        } else {
            System.err.println("No Data");
        }
        return listData;
    }

    @Override
    public String maxID() {
        String max = null;
        JSONArray arr;
        String jsonStr = Service.ServiceGet("select_max_id.php");
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                arr = jsonObj.getJSONArray("hobi");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject c = arr.getJSONObject(i);
                    String a = c.getString("id");   
                    String auto = ""+(Integer.parseInt(a)+1);
                    String nol = "";
                    if(auto.length()==1){
                        nol = "000";
                    }else if (auto.length()==2){
                        nol = "00";
                    }else if (auto.length()==3){
                        nol = "0";
                    }else if (auto.length()==4){
                        nol = "";
                    }
                    max = "P-"+nol+auto;
                }
            } catch (JSONException e) {
            }
        } else {
            System.err.println("No Data");
        }
        return max;
    }

    
}
