package com.amsa.melissa_clean.service;

import org.springframework.stereotype.Service;

@Service
public class ErrorCodes {
    public ErrorCodes() {
    }

    public String ErrorDescription(String errorCode){
        String errorDescription="";

        if(errorCode.equals("AE01")){
            errorDescription="The ZIP or Postal Code does not exist and could not be determined by the city/municipality and state/province.";
        }else if(errorCode.equals("AE02")){
            errorDescription="The street name was not be found.";
        }else if(errorCode.equals("AE03")){
            errorDescription="Either the directionality (N, E, SW, etc) or the suffix (AVE, ST, BLVD) are missing or invalid.";
        }else if(errorCode.equals("AE04")){
            errorDescription="The physical location exists but there are no addresses on this side of the street.(US Only)";
        }else if(errorCode.equals("AE05")){
            errorDescription="Input matched to multiple addresses and there is not enough information to break the tie.";
        }else if(errorCode.equals("AE06")){
            errorDescription="This address cannot be verified now but will be at a future date.(US Only)";
        }else if(errorCode.equals("AE07")){
            errorDescription="The required combination of address/city/state or address/zip is missing.";
        }else if(errorCode.equals("AE08")){
            errorDescription="The suite or apartment number is not valid.";
        }else if(errorCode.equals("AE09")){
            errorDescription="The suite or apartment number is missing.";
        }else if(errorCode.equals("AE10")){
            errorDescription="The address number in the input address is not valid.";
        }else if(errorCode.equals("AE11")){
            errorDescription="The address number in the input address is missing.";
        }else if(errorCode.equals("AE12")){
            errorDescription="The input address box number is invalid.";
        }else if(errorCode.equals("AE13")){
            errorDescription="The input address box number is missing.";
        }else if(errorCode.equals("AE14")){
            errorDescription="The address is a Commercial Mail Receiving Agency (CMRA) and the Private Mail Box (PMB or #) number is missing.";
        }else if(errorCode.equals("AE15")){
            errorDescription="Limited to Demo Mode operation.";
        }else if(errorCode.equals("AE16")){
            errorDescription="The Database has expired.";
        }else if(errorCode.equals("AE17")){
            errorDescription="Address does not have Suites or Apartments.";
        }else if(errorCode.equals("AE19")){
            errorDescription="FindSuggestion function has exceeded time limit.";
        }else if(errorCode.equals("AE20")){
            errorDescription="FindSuggestion function is disabled, see manual for details.";
        }else if(errorCode.equals("AE21")){
            errorDescription="MAK Not Found";
        }else if(errorCode.equals("GE01")){
            errorDescription="The SOAP, JSON, or XML request structure is empty.";
        }else if(errorCode.equals("GE02")){
            errorDescription="The SOAP, JSON, or XML request record structure is empty.";
        }else if(errorCode.equals("GE03")){
            errorDescription="The counted records sent more than the number of records allowed per request.";
        }else if(errorCode.equals("GE04")){
            errorDescription="The License Key is empty.";
        }else if(errorCode.equals("GE05")){
            errorDescription="The License Key is invalid.";
        }else if(errorCode.equals("GE06")){
            errorDescription="The License Key is disabled.";
        }else if(errorCode.equals("GE07")){
            errorDescription="The SOAP, JSON, or XML request is invalid.";
        }else if(errorCode.equals("GE08")){
            errorDescription="The License Key is invalid for this product.";
        }else if(errorCode.equals("GE20")){
            errorDescription="The Verify package was requested but is not active for the License Key.";
        }else if(errorCode.equals("GE21")){
            errorDescription="The Append package was requested but is not active for the License Key.";
        }else if(errorCode.equals("GE22")){
            errorDescription="The Move package was requested but is not active for the License Key.";
        }else if(errorCode.equals("GE23")){
            errorDescription="No valid action was requested by the service. The request must include at least one of the following actions: Check, Verify, Append, or Move.";
        }else if(errorCode.equals("GE24")){
            errorDescription="The Demographics package was requested but is not active for the License Key.";
        }

        return errorDescription;
    }
}
