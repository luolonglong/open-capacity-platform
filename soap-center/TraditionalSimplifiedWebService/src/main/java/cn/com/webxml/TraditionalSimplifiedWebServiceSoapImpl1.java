
/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package cn.com.webxml;

import java.util.logging.Logger;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 2.2.12
 * Wed Nov 28 11:56:24 GMT+08:00 2018
 * Generated source version: 2.2.12
 * 
 */

@javax.jws.WebService(
                      serviceName = "TraditionalSimplifiedWebService",
                      portName = "TraditionalSimplifiedWebServiceSoap12",
                      targetNamespace = "http://webxml.com.cn/",
                      wsdlLocation = "http://www.webxml.com.cn/WebServices/TraditionalSimplifiedWebService.asmx?wsdl",
                      endpointInterface = "cn.com.webxml.TraditionalSimplifiedWebServiceSoap")
                      
public class TraditionalSimplifiedWebServiceSoapImpl1 implements TraditionalSimplifiedWebServiceSoap {

    private static final Logger LOG = Logger.getLogger(TraditionalSimplifiedWebServiceSoapImpl.class.getName());

    /* (non-Javadoc)
     * @see cn.com.webxml.TraditionalSimplifiedWebServiceSoap#toTraditionalChinese(java.lang.String  sText )*
     */
    public java.lang.String toTraditionalChinese(java.lang.String sText) { 
        LOG.info("Executing operation toTraditionalChinese");
        System.out.println(sText);
        try {
            java.lang.String _return = "";
            return _return;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    /* (non-Javadoc)
     * @see cn.com.webxml.TraditionalSimplifiedWebServiceSoap#toSimplifiedChinese(java.lang.String  sText )*
     */
    public java.lang.String toSimplifiedChinese(java.lang.String sText) { 
        LOG.info("Executing operation toSimplifiedChinese");
        System.out.println(sText);
        try {
            java.lang.String _return = "";
            return _return;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

}
