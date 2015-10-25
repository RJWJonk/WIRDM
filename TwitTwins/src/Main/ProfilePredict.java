/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Main;

import com.facepp.error.FaceppParseException;
import com.facepp.http.PostParameters;
import com.facepp.result.FaceppResult;

/**
 *
 * @author s119503
 */
public class ProfilePredict {
    
    public String getGender(String URL) throws FaceppParseException {
        FaceppResult result = returnResult(URL);
        String s="n.a.";
        String r=""+result.get("face");
        if (!r.contains("[]")){
           s = ""+result.get("face").get(0).get("attribute").get("gender").get("value", FaceppResult.JsonType.STRING); 
        }
        return s;
    }
    
    public int getAge(String URL) throws FaceppParseException {
        FaceppResult result = returnResult(URL);
        int a=-1;
        String r=""+result.get("face");
        if (!r.contains("[]")){
            String s;
            s = ""+result.get("face").get(0).get("attribute").get("age").get("value", FaceppResult.JsonType.INT);
            a = Integer.parseInt(s);
        }
        return a;
    }
    
    private FaceppResult returnResult(String URL) throws FaceppParseException{
        HttpRequests httpRequests = new HttpRequests("25ad86cdad3ea4ac04204464a210058d", "phbtAB6cTT4LcArmskLCin2O5v6hDZfa");
        PostParameters postParameters;
        postParameters = new PostParameters().setUrl(URL).setAttribute("all");
        httpRequests.detectionDetect(postParameters);
        FaceppResult result = httpRequests.detectionDetect(postParameters);
        return result;
    }
}