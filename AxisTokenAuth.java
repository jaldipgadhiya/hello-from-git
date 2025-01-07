package com.bajaj.balicaxisws.util;

import com.balic.axis.encdecr.E2EEncDecrUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.TimeZone;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

public class AxisTokenAuth {
	
	
	  private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
	  private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
	  private static final String NUMBER = "0123456789";
	  private static final String DATA_FOR_RANDOM_STRING = CHAR_LOWER + CHAR_UPPER + NUMBER;
	  private static SecureRandom random = new SecureRandom();
	  private static final String CLIENT_KEYSTORE_TYPE = "PKCS12";
	  private static final String CLIENT_KEYSTORE_PATH = "D:\\AxisFileUploadCert\\sales.p12";
	  private static final String CLIENT_KEYSTORE_PASS = "pass_1234";

	  public static void main(String[] args)
	    throws Exception
	  {
	    String dec = E2EEncDecrUtil.E2EDecryption(
	      "9lnzkUJG6ewJ5O+pgWfJfwjl31pGeXBYMkwZOIUvmbhDQ3KO/QfvtH3ksj+cISSws3Jvk88sCDihcXYP/k2X79smKs0hrE8SwiBqu9/EM6MBHjGr0BRQTx767mrRWoSuT/Oi/3EcqmkCe6dabpXpwS3ZVYHPhr24rjIR4RngBhygt/pYK+NQ");
	    //String dec = E2EEncDecrUtil.E2EDecryption("kfF5nbrMkYANRRfNhwu3LWOAWFsIDLOA09BySe8DLN/9yQWooouExLW88/kev6zPRZK5G0tDnAH5n3V0ryhBzpf/J1hIr250FinRCoYzIJcuOtRiJ3D1sJMjjie1YzLo5Zink9W2qIgEvURipK98nggBCkHYk6GR+soc9wbLKdGP1I7KAiGT9GTJ1DP1NZgqLqpu7ZbTlA63gw==>");
	    System.out.println(dec);
	    String productCode = "1";
	    String identifierName = "2";
	    String identifierValue = "2";
	    String status = "3";
	    String subStatus = "3";
	    String statusDate = "2022-10-18 22:10:40.000";
	    String followUpDate = "2022-10-18 22:10:40.000";
	    String comments = "3";
	    String raName = "3";
	    String b = "3";
	    String typeOfBusinessInsurance = "3";
	    String i = "3";
	    String proposalNumber = "3";
	    String token = "3";
	    String basePremium = null;
	    String totalPremium = "1";
	    String totalTax = "3";

	    String req = "";
	    if ((basePremium == null) || (basePremium.equals("null"))) {
	      req = "{\r\n  \"productCode\": \"" + productCode + "\",\r\n" + "  \"identifierName\": \"" + 
	        identifierName + "\",\r\n" + 
	        "    \"identifierValue\": \"" + identifierValue + "\",\r\n" + "    \"status\": \"" + status + 
	        "\",\r\n" + "    \"subStatus\": \"" + subStatus + "\",\r\n" + "    \"statusDate\": \"" + 
	        getUtcTime(statusDate) + "\",\r\n" + "    \"followUpDate\": \"" + getUtcTime(followUpDate) + 
	        "\",\r\n" + "    \"comments\": \"" + comments + "\",\r\n" + "    \"totalPremium\": \"" + 
	        totalPremium + "\",\r\n" + "    \"raName\": \"" + raName + "\",\r\n" + 
	        "    \"intrestedInBusinessInsurance\": \"" + b + "\",\r\n" + "    \"totalTax\": \"" + totalTax + 
	        "\",\r\n" + "    \"typeOfBusinessInsurance\": \"" + typeOfBusinessInsurance + "\",\r\n" + 
	        "    \"stage\":  " + i + ",\r\n" + "    \"proposalNumber\": \"" + proposalNumber + "\"\r\n" + 
	        "  }\r\n" + "}";
	    }

	    System.out.format("req::::" + req, new Object[0]);
	  }

	  public static String generateRandomString(int length) {
	    if (length < 1) {
	      throw new IllegalArgumentException();
	    }
	    StringBuilder sb = new StringBuilder(length);
	    for (int i = 0; i < length; i++)
	    {
	      int rndCharAt = random.nextInt(DATA_FOR_RANDOM_STRING.length());
	      char rndChar = DATA_FOR_RANDOM_STRING.charAt(rndCharAt);

	      sb.append(rndChar);
	    }

	    return sb.toString();
	  }

	  public static String getUtcTime(String dateStr)
	    throws ParseException
	  {
	    DateFormat formatterIST = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	    formatterIST.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
	    Date date = formatterIST.parse(dateStr);
	    System.out.println("Pass data format:::  " + formatterIST.format(date));

	    DateFormat formatterUTC = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
	    formatterUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
	    String utcTime = formatterUTC.format(date);
	    System.out.println("Output data format:::  " + formatterUTC.format(date));
	    return utcTime;
	  }

	  public static String getStandardTime(String dateStr) {
	    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
	    df.setTimeZone(TimeZone.getTimeZone("UTC"));
	    Date date = null;
	    try {
	      date = df.parse(dateStr);
	    } catch (ParseException e) {
	      e.printStackTrace();
	    }
	    df.setTimeZone(TimeZone.getDefault());
	    String formattedDate = df.format(date).replace("T", " ");
	    return formattedDate;
	  }

	  public static String getToken(String targetApp, String sourceChannel) throws Exception {
	    String resp = "";

	    
	    //String Url = "https://sakshamuat.axisbank.co.in/gateway/api/v1/external-token-service/v1/token";
	    String Url = "https://saksham.axisbank.co.in/gateway/api/v1/external-token-service/v1/token";
	    String req = "{\r\n\"targetApp\":\"" + targetApp + "\",\r\n" + "\"sourceChannel\":\"" + sourceChannel + 
	      "\"\r\n" + "}";
	    
	    System.out.println("axis  Request::::>>>>>>" + req);
	    System.out.println("Inside the Axis Token  API consume Method Call");
	    SSLContext context = getSSLSocketFactory("D:\\AxisFileUploadCert\\sales.p12", "pass_1234");
        
	    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

	    HttpClient client = HttpClients.custom().setSslcontext(context).build();
	    requestFactory.setHttpClient(client);
	    requestFactory.setBufferRequestBody(false);

	    RestTemplate restTemplate = new RestTemplate(requestFactory);
	    HttpHeaders header = new HttpHeaders();
	    long now = Instant.now().toEpochMilli();
	    System.out.println("now:::::>>>" + now);
	    try {
	      header.setContentType(MediaType.APPLICATION_JSON);

	      //header.add("X-IBM-Client-Id", "f2920aeff00ff1778a2317e899ae745f");
	      //header.add("X-IBM-Client-Secret", "b44b1d65a6e010fad63f5fef959ffd1e");
	      //header.add("X-IBM-Client-Id", "2e6a7ce1acf10d03f4afccc70c3b67f1");
		  //header.add("X-IBM-Client-Secret", "858abb76f90392660032ec19d4ceb55d");
	      header.add("X-IBM-Client-Id", "4ca736822fb93404f8f220896f962e7b");
		  header.add("X-IBM-Client-Secret", "0025fc7d0d7eba857596e35cda9b9623");
	      header.add("x-fapi-epoch-millis", String.valueOf(now));
	      header.add("x-fapi-channel-id", "BALIC");
	      header.add("x-fapi-uuid", generateRandomString(6));
	      header.add("X-MARKETPLACE-SOURCE-APP-ID", "BALIC");
	      header.add("X-MARKETPLACE-APP-SECRET", "VHzvSgKQdolZXZoC");//ud0Btxz26J375boh

	      HttpEntity entity = new HttpEntity(req, header);
	      ResponseEntity resp1 = restTemplate.postForEntity(Url, entity, String.class, new Object[0]);

	      System.out.println("Token resp :::::" + (String)resp1.getBody());

	      JSONObject jsonObj = new JSONObject((String)resp1.getBody());
	      System.out.println("Token From JSON " + jsonObj.get("authToken"));

	      resp = jsonObj.get("authToken").toString();
	    } catch (Exception ex) {
	      System.out.println("Inside Exception of  API Call");
	      ex.printStackTrace();
	    }

	   
	    return resp;
	  }

	  public static ResponseEntity<String> updateLeadRevfeed(String reqJson, String token) throws Exception
	  {
	    String resp = "";
	    System.out.println("TOKEN Print ::::: " + token);
	    System.out.println("reqJson-" + reqJson);
	    //String Url = "https://sakshamuat.axisbank.co.in/gateway/api/v1/lead-reverse-feed-update";
	    String Url = "https://saksham.axisbank.co.in/gateway/api/v1/lead-reverse-feed-update";
	    System.out.println("Inside the Axis update  API consume Method Call");

	    String enc = E2EEncDecrUtil.E2EEncryption(reqJson);

	    String mainReqJson = "{\"payload\": \"" + enc + "\"}";
	    try {
	      System.out.println("MAIN JSON PAYLOAD ::: :: " + mainReqJson);
	      System.out.println("-----updated password----changeit-----");
	      SSLContext context = getSSLSocketFactory("D:\\AxisFileUploadCert\\sales.p12", "pass_1234");

	      HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

	      HttpClient client = HttpClients.custom().setSSLContext(context).build();
	      requestFactory.setHttpClient(client);
	      requestFactory.setBufferRequestBody(false);

	      RestTemplate restTemplate = new RestTemplate(requestFactory);
	      HttpHeaders header = new HttpHeaders();
	      long now = Instant.now().toEpochMilli();
	      System.out.println("now:::::>>>" + now);

	      header.setContentType(MediaType.APPLICATION_JSON);

	      //header.add("X-IBM-Client-Id", "f2920aeff00ff1778a2317e899ae745f");
	      //header.add("X-IBM-Client-Secret", "b44b1d65a6e010fad63f5fef959ffd1e");
	      //header.add("X-IBM-Client-Id", "2e6a7ce1acf10d03f4afccc70c3b67f1");
		  //header.add("X-IBM-Client-Secret", "858abb76f90392660032ec19d4ceb55d");
	      header.add("X-IBM-Client-Id", "4ca736822fb93404f8f220896f962e7b");
		  header.add("X-IBM-Client-Secret", "0025fc7d0d7eba857596e35cda9b9623");
	      header.add("x-fapi-epoch-millis", String.valueOf(now));
	      header.add("x-fapi-channel-id", "BALIC");
	      header.add("x-fapi-uuid", generateRandomString(6));
	      header.add("X-MARKETPLACE-SOURCE-APP-ID", "BALIC");
	      header.add("X-MARKETPLACE-APP-SECRET", "VHzvSgKQdolZXZoC");//ud0Btxz26J375boh

	      header.add("Authorization", token);

	      HttpEntity entity = new HttpEntity(mainReqJson, header);
	      ResponseEntity resp1 = restTemplate.exchange(Url, HttpMethod.POST, entity, String.class, new Object[0]);

	      resp = (String)resp1.getBody();
	      System.out.println("AXIS ENC RESP-" + resp);
	    }
	    catch (HttpStatusCodeException e) {
	      return ((ResponseEntity.BodyBuilder)ResponseEntity.status(e.getRawStatusCode()).headers(e.getResponseHeaders()))
	        .body(e.getResponseBodyAsString());
	    } catch (Exception ex) {
	      System.out.println("Inside Exception of  API Call");
	      ex.printStackTrace();
	    }

	    return new ResponseEntity(resp, HttpStatus.OK);
	  }

	  private static SSLContext getSSLSocketFactory(String PFX_location, String PFX_Password) throws Exception
	  {
	    SSLContext context = null;
	    File pKeyFile = new File(PFX_location);
	    KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
	    KeyStore keyStore = KeyStore.getInstance("PKCS12");
	    InputStream keyInput = new FileInputStream(pKeyFile);
	    keyStore.load(keyInput, PFX_Password.toCharArray());
	    keyInput.close();
	    keyManagerFactory.init(keyStore, PFX_Password.toCharArray());
	    context = SSLContext.getInstance("TLS");
	    context.init(keyManagerFactory.getKeyManagers(), null, new SecureRandom());
	    return context;
	  }


}
