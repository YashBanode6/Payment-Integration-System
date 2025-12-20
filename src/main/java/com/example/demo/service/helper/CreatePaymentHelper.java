package com.example.demo.service.helper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.example.demo.constant.Constants;
import com.example.demo.http.HttpRequest;
import com.example.demo.pojo.CreatePayment;
import com.example.demo.pojo.ListItem;

@Service
public class CreatePaymentHelper {
	@Value("${stripe.api.key}")
	public String stripeApiKey;
	
	@Value("${stripe.create-session.url}")
    public String paymentSessionUrl;
	
	public HttpRequest prepareHttpRequest(CreatePayment createPayment) {
		HttpHeaders headers = new HttpHeaders();
		
		headers.setBasicAuth(stripeApiKey, Constants.EmptyString);
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		
		
		MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
	        
		requestBody.add(Constants.MODE, Constants.MODE_TYPE);
	        
		requestBody.add(Constants.SUCCESSURL, "https://example.com/success");

		requestBody.add(Constants.CANCELURL, "https://example.com/cancel");
	       
		for (int i = 0; i < createPayment.getListItems().size(); i++) {
			ListItem lineItem = createPayment.getListItems().get(i);
			requestBody.add("line_items[" + i + "][price_data][currency]", lineItem.getCurrency());
			requestBody.add("line_items[" + i + "][price_data][product_data][name]",
					lineItem.getProductName());
			requestBody.add("line_items[" + i + "][price_data][unit_amount]",
					String.valueOf((int) (lineItem.getUnitAmount())));
			requestBody.add("line_items[" + i + "][quantity]",
					String.valueOf(lineItem.getQuantity()));
		}
	        
	     HttpRequest httpRequest = new HttpRequest();
	     httpRequest.setHttpMethod(HttpMethod.POST);
		 String stripePaymentSessionUrl = paymentSessionUrl;
		 httpRequest.setUrl(stripePaymentSessionUrl);
	     httpRequest.setHttpHeaders(headers);
	     httpRequest.setRequestBody(requestBody);
		return httpRequest;
	}

}
