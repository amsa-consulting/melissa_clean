package com.amsa.melissa_clean.controller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import com.amsa.melissa_clean.service.ErrorCodes;

@RestController
public class melissa_controller {
    @Value("${request.url}")
    private String REQUEST_URL;
    @Value("${request_clean.url}")
    private String REQUESTL_CLEAN_URL;

    //@Value("${access.url}")
    // private String BASE_URL;
    private final RestTemplate restTemplate;
    Logger logger = LoggerFactory.getLogger(melissa_controller.class);

    @Autowired
    private ErrorCodes errorCodes;

    public melissa_controller(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    /*
     *The endpoint below is for Melissa real time address cleaning
     */

    @GetMapping(value ="/melissa/clean",
            // consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_XML_VALUE },
            produces = { MediaType.APPLICATION_JSON_VALUE}
    )
    public String cleanAddressRequest(
            @RequestParam String id,
            @RequestParam String id1,
            @RequestParam String opt,
            @RequestParam String a,
            @RequestParam String city,
            @RequestParam String state,
            @RequestParam String zip,
            @RequestParam String country,
            @RequestParam String t
    ) {
        if (t.contains("*") == false) {
           // logger.info("/melissa/clean");
            String requestUrl = REQUEST_URL + "?id=" + id + "&a1=" + a + "&loc=" + city + "&admarea=" + state + "&postal=" + zip + "&ctry=" + country + "&format=json";

            String requestCleanUrl = REQUESTL_CLEAN_URL + "?id=" + id1 + "&opt=" + opt + "&a=" + a + "&city=" + city + "&state=" + state + "&zip=" + zip + "&country=" + country + "&t=" + t + "&format=json";
            String resp_clean = "";

            String resp = restTemplate.getForObject(requestUrl, String.class);
            JSONObject root_transaction = new JSONObject(resp);
            JSONArray transaction = (JSONArray) root_transaction.get("Records");
            // JSONArray transaction1 = transaction;

            String concat = "";
            String errorCodeDesc = "";
            String res1 = "";
            String jsonString = "";
            String resultCode = "";
            String errorDescription = "";
            //Integer count=0;
            for (int i = 0; i < transaction.length(); i++) {
                JSONObject json = new JSONObject();

                String[] results = new String[0];
                String result = transaction.getJSONObject(i).optString("Results");
                results = result.split(",");
                // List<String> resultList = new ArrayList<>(Arrays.asList(results));
                // boolean found = false;

                for (String res : results) {

                    JSONObject root_transaction_clean1 = new JSONObject();
                    String transaction_clean = "";
                    Integer totalResult = 0;

                    JSONArray transaction_clean1 = new JSONArray();
                    JSONObject transaction_clean2 = new JSONObject();

                    resp_clean = restTemplate.getForObject(requestCleanUrl, String.class);
                    json = XML.toJSONObject(resp_clean);
                    jsonString = json.toString(4);

                    JSONObject root_transaction_clean = new JSONObject();
                    root_transaction_clean = new JSONObject(jsonString);

                    if (res.equals("AE09") || res.equals("AE08")) {
                        //res1=res1+res;
                        errorCodeDesc = errorCodes.ErrorDescription(res);
                        // transaction_clean = root_transaction_clean.getJSONObject("ResponseArray").getJSONObject("StreetRecord").get("Suite").toString();
                        //transaction_clean = root_transaction_clean.getJSONObject("ResponseArray").getJSONArray("StreetRecord").get(0).toString();
                        transaction_clean = root_transaction_clean.getJSONObject("ResponseArray").toString();
                        totalResult = root_transaction_clean.getJSONObject("ResponseArray").optInt("TotalRecords");
                        resultCode = root_transaction_clean.getJSONObject("ResponseArray").optString("Results");
                        root_transaction_clean1 = new JSONObject(transaction_clean);
                        if (totalResult > 0) {
                            transaction_clean1 = root_transaction_clean1.getJSONArray("StreetRecord");

                            for (int j = 0; j < transaction_clean1.length(); j++) {
                                String high = transaction_clean1.getJSONObject(j).getJSONObject("Suite").optString("High");
                                String low = transaction_clean1.getJSONObject(j).getJSONObject("Suite").optString("Low");
                                String name = transaction_clean1.getJSONObject(j).getJSONObject("Suite").optString("Name");
                                if (!high.isEmpty()) {
                                    if (!high.equals(low)) {
                                        concat = concat + a + " " + name + " " + high + "-" + low + ",";
                                    } else if (high.equals(low)) {
                                        concat = concat + a + " " + name + " " + high + ",";
                                    }
                                }
                            }
                        } else if (totalResult == 0) {
                            concat = "Error With Result Code " + resultCode + " ( " + errorCodes.ErrorDescription(resultCode) + " ) " + " Returned,";
                        }
                        // count=transaction_clean1.length();
                        //break;
                    } else if (res.startsWith("AE") && (!res.equals("AE09") && !res.equals("AE08"))) {
                        // res1=res1+res;
                        errorCodeDesc = errorCodes.ErrorDescription(res);
                        transaction_clean = root_transaction_clean.getJSONObject("ResponseArray").toString();
                        root_transaction_clean1 = new JSONObject(transaction_clean);
                        totalResult = root_transaction_clean.getJSONObject("ResponseArray").optInt("TotalRecords");
                        resultCode = root_transaction_clean.getJSONObject("ResponseArray").optString("Results");
                        if (totalResult > 0) {
                            transaction_clean1 = root_transaction_clean1.getJSONArray("StreetRecord");
                            for (int j = 0; j < transaction_clean1.length(); j++) {
                                String fullAddressLine = transaction_clean1.getJSONObject(j).optString("FullAddressLine");

                                if (!fullAddressLine.isEmpty()) {
                                    concat = concat + fullAddressLine + ",";
                                }
                            }
                        } else if (totalResult == 0) {
                            concat = concat + "Error With Result Code " + resultCode + " ( " + errorCodes.ErrorDescription(resultCode) + " ) " + " Returned,";
                        }
                    }
                }
                // break;
            }

            String[] concats = new String[0];
            concats = concat.split(",");
            //concats.toString().replace("[","").replace("]","");
            JSONObject jsonObj = new JSONObject(resp);
            JSONObject resp_object = new JSONObject(resp);
            JSONArray resp_object_formated = (JSONArray) resp_object.get("Records");

            JSONObject resp_obj1 = new JSONObject();
            resp_obj1 = (JSONObject) resp_object_formated.get(0);

            // JSONArray concat_object=new JSONArray(concats);
       /*
        for(int k=0; k < concat_object.length();k++){
            jsonObj.append("SuggestList",concat_object.get(k));
        }
        resp_obj1.append("SuggestList",jsonObj.get("SuggestList"));
        */
            String subPremises = resp_obj1.get("SubPremises").toString();
            String addressLine2 = "";
            String addressLine1 = resp_obj1.get("AddressLine1").toString();
            String locality = resp_obj1.get("Locality").toString();
            String subAdministrativeArea = resp_obj1.get("SubAdministrativeArea").toString();

            if (!subPremises.equals("") && !subPremises.equals(null)) {
                addressLine1 = addressLine1.replaceAll(subPremises, "");
                addressLine2 = subPremises;
            } else {
                //addressLine2=resp_obj1.get("AddressLine2").toString();
                addressLine2 = "";
            }

            String postalCodeNew = "";
            String postalCodeExtended = "";
            String postalCodeFull = "";

            String administrativeArea = resp_obj1.get("AdministrativeArea").toString();

            String postalCode = resp_obj1.get("PostalCode").toString();
            if (!postalCode.equals("") && !postalCode.equals(null)) {
                String[] arrOfpostalCode = postalCode.split("-");
                postalCodeFull = postalCode;
                if (arrOfpostalCode.length == 1) {
                    postalCodeNew = arrOfpostalCode[0];
                } else if (arrOfpostalCode.length == 2) {
                    postalCodeNew = arrOfpostalCode[0];
                    postalCodeExtended = arrOfpostalCode[1];
                } else {
                    postalCodeNew = "";
                    postalCodeExtended = "";
                }
            }
            resp_obj1.put("Address", addressLine1);
            resp_obj1.put("AddressLine1", addressLine1);
            resp_obj1.put("AddressLine2", addressLine2);
            resp_obj1.put("SuggestionList", concats);
            resp_obj1.put("ErrorString", errorCodeDesc);
            resp_obj1.put("Company", t);
            resp_obj1.remove("Locality");
            resp_obj1.put("City", locality);
            resp_obj1.put("CountryCode", "US");
            resp_obj1.put("CountryName", "United States");
            resp_obj1.put("PostalCode", postalCodeNew);
            resp_obj1.put("PostalCodeExtended", postalCodeExtended);
            resp_obj1.put("PostalCodeFull", postalCodeFull);
            resp_obj1.remove("AdministrativeArea");
            resp_obj1.put("Province", administrativeArea);
            resp_obj1.put("State", administrativeArea);
            resp_obj1.put("Suite", subPremises);
            resp_obj1.put("CountyName", subAdministrativeArea);

            return resp_obj1.toString();
            //return resp_obj1.toString().replace("[[","[").replace("]]","]");

            //Else block which will be accessed if the t value contains "*"
        } else {
           // logger.info("/melissa/clean");
            String requestUrl=REQUEST_URL+"?id="+id+"&a1="+a+"&loc="+city+"&admarea="+state+"&postal="+zip+"&ctry="+country+"&format=json";

            //   String requestCleanUrl=REQUESTL_CLEAN_URL+"?id="+id1+"&opt="+opt+"&a="+a+"&city="+city+"&state="+state+"&zip="+zip+"&country="+country+"&t="+t+"&format=json";
            //  String resp_clean="";

            String resp= restTemplate.getForObject(requestUrl,String.class);
            JSONObject root_transaction = new JSONObject(resp);
            JSONArray transaction = (JSONArray) root_transaction.get("Records");
            // JSONArray transaction1 = transaction;

            String concat="";
            String errorCodeDesc="";
            String res1="";
            String jsonString="";
            String resultCode="";
            String errorDescription="";
            //Integer count=0;
            for (int i = 0; i < transaction.length(); i++) {
                JSONObject json = new JSONObject();

                String[] results = new String[0];
                String result=transaction.getJSONObject(i).optString("Results");
                results = result.split(",");
                // List<String> resultList = new ArrayList<>(Arrays.asList(results));
                // boolean found = false;

                for (String res : results) {

                    JSONObject root_transaction_clean1 =new JSONObject();
                    String transaction_clean="";
                    Integer totalResult=0;

                    JSONArray transaction_clean1= new JSONArray();
                    JSONObject transaction_clean2= new JSONObject();

                    JSONObject root_transaction_clean =new JSONObject();
                    //root_transaction_clean = new JSONObject(jsonString);

                    if (res.equals("AE09") || res.equals("AE08")) {
                        //res1=res1+res;
                        errorCodeDesc = errorCodes.ErrorDescription(res);

                        root_transaction_clean1 = new JSONObject(transaction_clean);
                        if (totalResult > 0) {
                            transaction_clean1 = root_transaction_clean1.getJSONArray("StreetRecord");

                            for (int j = 0; j < transaction_clean1.length(); j++) {
                                String high = transaction_clean1.getJSONObject(j).getJSONObject("Suite").optString("High");
                                String low = transaction_clean1.getJSONObject(j).getJSONObject("Suite").optString("Low");
                                String name = transaction_clean1.getJSONObject(j).getJSONObject("Suite").optString("Name");
                                if (!high.isEmpty()) {
                                    if (!high.equals(low)) {
                                        concat = concat + a + " " + name + " " + high + "-" + low + ",";
                                    } else if (high.equals(low)) {
                                        concat = concat + a + " " + name + " " + high + ",";
                                    }
                                }
                            }
                        }else if(totalResult == 0){
                            concat = "Error With Result Code "+ resultCode+" ( "+errorCodes.ErrorDescription(resultCode)+" ) " + " Returned,";
                        }
                        // count=transaction_clean1.length();
                        //break;
                    } else if (res.startsWith("AE") && (!res.equals("AE09") && !res.equals("AE08"))) {
                        // res1=res1+res;
                        errorCodeDesc = errorCodes.ErrorDescription(res);
                        String fullAddressLine ="";
                    }
                }
                // break;
            }

            String[] concats = new String[0];
            concats = concat.split(",");
            //concats.toString().replace("[","").replace("]","");
            JSONObject jsonObj = new JSONObject(resp);
            JSONObject resp_object = new JSONObject(resp);
            JSONArray resp_object_formated = (JSONArray) resp_object.get("Records");

            JSONObject resp_obj1= new JSONObject();
            resp_obj1= (JSONObject) resp_object_formated.get(0);

            // JSONArray concat_object=new JSONArray(concats);
       /*
        for(int k=0; k < concat_object.length();k++){
            jsonObj.append("SuggestList",concat_object.get(k));
        }
        resp_obj1.append("SuggestList",jsonObj.get("SuggestList"));
        */
            String subPremises=resp_obj1.get("SubPremises").toString();
            String addressLine2="";
            String addressLine1=resp_obj1.get("AddressLine1").toString();
            String locality = resp_obj1.get("Locality").toString();
            String subAdministrativeArea=resp_obj1.get("SubAdministrativeArea").toString();

            if(!subPremises.equals("") && !subPremises.equals(null)){
                addressLine1 = addressLine1.replaceAll(subPremises, "");
                addressLine2=subPremises;
            }else{
                //addressLine2=resp_obj1.get("AddressLine2").toString();
                addressLine2="";
            }

            String postalCodeNew="";
            String postalCodeExtended="";
            String postalCodeFull="";

            String administrativeArea=resp_obj1.get("AdministrativeArea").toString();

            String postalCode = resp_obj1.get("PostalCode").toString();
            if(!postalCode.equals("") && !postalCode.equals(null)) {
                String[] arrOfpostalCode = postalCode.split("-");
                postalCodeFull=postalCode;
                if (arrOfpostalCode.length == 1) {
                    postalCodeNew = arrOfpostalCode[0];
                } else if (arrOfpostalCode.length == 2) {
                    postalCodeNew = arrOfpostalCode[0];
                    postalCodeExtended = arrOfpostalCode[1];
                } else {
                    postalCodeNew = "";
                    postalCodeExtended = "";
                }
            }
            resp_obj1.put("Address",addressLine1);
            resp_obj1.put("AddressLine1",addressLine1);
            resp_obj1.put("AddressLine2",addressLine2);
            resp_obj1.put("SuggestionList",concats);
            resp_obj1.put("ErrorString",errorCodeDesc);
            resp_obj1.put("Company",t);
            resp_obj1.remove("Locality");
            resp_obj1.put("City",locality);
            resp_obj1.put("CountryCode", "US");
            resp_obj1.put("CountryName", "United States");
            resp_obj1.put("PostalCode",postalCodeNew);
            resp_obj1.put("PostalCodeExtended",postalCodeExtended);
            resp_obj1.put("PostalCodeFull",postalCodeFull);
            resp_obj1.remove("AdministrativeArea");
            resp_obj1.put("Province",administrativeArea);
            resp_obj1.put("State",administrativeArea);
            resp_obj1.put("Suite",subPremises);
            resp_obj1.put("CountyName",subAdministrativeArea);

            return resp_obj1.toString();
        }
    }
}
