package in.kriger.newkrigercampus.extras;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;

import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.classes.City;
import in.kriger.newkrigercampus.classes.Country;
import in.kriger.newkrigercampus.classes.State;

public class CityStateCountry {

    Context context;

    ArrayList<String> countries = new ArrayList<>();
    ArrayList<String> states = new ArrayList<>();
    ArrayList<String> cities = new ArrayList<>();

    ArrayList<Country> countries_list = new ArrayList<>();

    ArrayList<State> states_list = new ArrayList<>();

    ArrayList<City> cities_list = new ArrayList<>();

    public CityStateCountry(Context context){

        this.context = context;
    }

    public ArrayList<String> getCountries(){

        InputStream is = context.getResources().openRawResource(R.raw.countries);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        }catch (IOException e){

        }finally {
            try {
                is.close();
            }catch (IOException e){

            }
        }

        String jsonString = writer.toString();
        try {
            JSONObject countries_json = new JSONObject(jsonString);
            JSONArray jsonArray = countries_json.getJSONArray("countries");

            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject1=jsonArray.getJSONObject(i);
                countries_list.add(new Country(jsonObject1.getInt("id"),jsonObject1.getString("name")));
                countries.add(jsonObject1.getString("name"));
            }
        }catch (JSONException e){

        }

        return countries;

    }

    public int getCountryCode(int value){

        return 1;
    }

    public String getCountry(int value){
        InputStream is = context.getResources().openRawResource(R.raw.countries);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        }catch (IOException e){

        }finally {
            try {
                is.close();
            }catch (IOException e){

            }
        }

        String jsonString = writer.toString();
        try {
            JSONObject countries_json = new JSONObject(jsonString);
            JSONArray jsonArray = countries_json.getJSONArray("countries");

            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject1=jsonArray.getJSONObject(i);
               if (jsonObject1.getInt("id") == value) {
                   return jsonObject1.getString("name");
               }
            }
        }catch (JSONException e){

        }

        return "null";

    }

    public ArrayList<String> getStates(String country_id){

        InputStream is = context.getResources().openRawResource(R.raw.states);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        }catch (IOException e){

        }finally {
            try {
                is.close();
            }catch (IOException e){

            }
        }
        ArrayList<String> temp_states = new ArrayList<>();

        String jsonString = writer.toString();
        try {
            JSONObject countries_json = new JSONObject(jsonString);
            JSONArray jsonArray = countries_json.getJSONArray("states");
            states_list.clear();
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject1=jsonArray.getJSONObject(i);
                if (jsonObject1.getString("country_id").equals(country_id)) {
                    states_list.add(new State(jsonObject1.getString("id"), jsonObject1.getString("name"), jsonObject1.getString("country_id")));
                   temp_states.add(jsonObject1.getString("name"));
                }
            }
        }catch (JSONException e){

        }

        return temp_states;

    }

    public int getStateCode(int value){
        return Integer.valueOf(states_list.get(value).getId());
    }

    public int getStateId(int value){
        String temp = String.valueOf(value);

        int j = 0;
        for (int i =0;i<states_list.size();i++){
            if (states_list.get(i).getId().equals(temp)){
                j=i;
            }
        }

        return j;

    }

    public String getState(int value){
        InputStream is = context.getResources().openRawResource(R.raw.states);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        }catch (IOException e){

        }finally {
            try {
                is.close();
            }catch (IOException e){

            }
        }

        String jsonString = writer.toString();
        try {
            JSONObject countries_json = new JSONObject(jsonString);
            JSONArray jsonArray = countries_json.getJSONArray("states");

            String temp = String.valueOf(value);
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject1=jsonArray.getJSONObject(i);
                if (jsonObject1.getString("id").equals(temp)) {
                    return jsonObject1.getString("name");

                }
            }
        }catch (JSONException e){

        }

        return "null";


    }

    /*public ArrayList<String> getCities(String state_id){

        InputStream is = context.getResources().openRawResource(R.raw.cities);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        }catch (IOException e){

        }finally {
            try {
                is.close();
            }catch (IOException e){

            }
        }
        ArrayList<String> temp_states = new ArrayList<>();

        String jsonString = writer.toString();
        try {
            JSONObject countries_json = new JSONObject(jsonString);
            JSONArray jsonArray = countries_json.getJSONArray("cities");
            cities_list.clear();
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject1=jsonArray.getJSONObject(i);
                if (jsonObject1.getString("state_id").equals(state_id)) {
                    cities_list.add(new City(jsonObject1.getString("id"), jsonObject1.getString("name"), jsonObject1.getString("state_id")));
                    temp_states.add(jsonObject1.getString("name"));
                }
            }
        }catch (JSONException e){

        }

        return temp_states;

    }


    public int getCityCode(int value){
            return Integer.valueOf(cities_list.get(value).getId());
    }

    public String getCity(int value){
        InputStream is = context.getResources().openRawResource(R.raw.cities);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        }catch (IOException e){

        }finally {
            try {
                is.close();
            }catch (IOException e){

            }
        }

        String jsonString = writer.toString();
        try {
            JSONObject countries_json = new JSONObject(jsonString);
            JSONArray jsonArray = countries_json.getJSONArray("cities");

            String temp = String.valueOf(value);
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject1=jsonArray.getJSONObject(i);
                if (jsonObject1.getString("id").equals(temp)) {
                    return jsonObject1.getString("name");

                }
            }
        }catch (JSONException e){

        }

        return "null";

    }

    public int getCityId(int value){
        String temp = String.valueOf(value);

        int j = 0;
        for (int i =0;i<cities_list.size();i++){
            if (cities_list.get(i).getId().equals(temp)){
                j=i;
            }
        }

        return j;

    }*/






}
