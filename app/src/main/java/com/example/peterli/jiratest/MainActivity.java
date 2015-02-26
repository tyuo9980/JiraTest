package com.example.peterli.jiratest;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


public class MainActivity extends ActionBarActivity {

    public String RootURL = "https://plastic.atlassian.net/rest/api/2";
    String credentials = "userid:password";
    String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void createIssue(View view) {
        System.out.println(base64EncodedCredentials);
        System.out.println("clicked create button");

        EditText descText = (EditText)findViewById(R.id.description_text);
        EditText sumText = (EditText)findViewById(R.id.summary_text);
        String desc = descText.getText().toString();
        String summ = descText.getText().toString();

        System.out.println(desc);

        try {
            JSONObject jsonArgs = new JSONObject();
            jsonArgs.put("summary", summ);
            jsonArgs.put("description", desc);
            jsonArgs.put("project", new JSONObject().put("key", "CREA"));
            jsonArgs.put("issuetype", new JSONObject().put("id", "1"));
            jsonArgs.put("assignee", new JSONObject().put("name", "peter"));

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("fields", jsonArgs);

            String json = jsonObject.toString();
            String[] args = {"/issue", json};

            postData(args);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void postData(String[] args) {
        new HttpPostTask().execute(args);
    }

    private class HttpPostTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            String json = params[1];

            System.out.println(url + " " + json);

            StringBuilder builder = new StringBuilder();
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(RootURL + url);

            try {
                StringEntity se = new StringEntity(json);

                httppost.setEntity(se);
                httppost.setHeader("Authorization", "Basic " + base64EncodedCredentials);
                httppost.setHeader("Content-Type", "application/json");

                HttpResponse response = httpclient.execute(httppost);

                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();

                if (statusCode == 201){
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                    String line;

                    while((line = reader.readLine()) != null){
                        builder.append(line);
                    }
                }
                else{
                    System.out.println ("Failed with error: " + statusCode);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            /*
            //Adding Attachments
            String result = builder.toString();

            try {
                JSONObject jsonObj = new JSONObject(result);

                String id = jsonObj.getString("id");
                String key = jsonObj.getString("key");
                String self = jsonObj.getString("self");

                httpclient = new DefaultHttpClient();
                httppost = new HttpPost(RootURL + "/issue/" + key + "/attachments");
                httppost.setHeader("Authorization", "Basic " + base64EncodedCredentials);
                httppost.setHeader("Content-Type", "application/json");
                httppost.setHeader("X-Atlassian-Token", "nocheck");

                File filesDir = getApplicationContext().getFilesDir();
                File imageFile = new File(filesDir, "attachment.jpg");
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.troll);

                OutputStream os;

                os = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                os.flush();
                os.close();

                FileBody imageFileBody = new FileBody(imageFile);
                HttpEntity entity = MultipartEntityBuilder.create().addPart("file", imageFileBody).build();

                httppost.setEntity(entity);

                HttpResponse response = httpclient.execute(httppost);

                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();

                if (statusCode == 200){
                    System.out.println("success");
                }
                else{
                    System.out.println("failed with error " + statusCode);
                }

            }
            catch(Exception e){
                e.printStackTrace();
            }
            */

            return builder.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            System.out.println(result);

            try {
                JSONObject jsonObj = new JSONObject(result);

                String id = jsonObj.getString("id");
                String key = jsonObj.getString("key");
                String self = jsonObj.getString("self");
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}
