package com.example.demo.pojo;

import java.util.List;

import lombok.Data;

@Data
public class CreatePayment {

	private String successUrl;
	private String cancelUrl;
	private List<ListItem> listItems;
	
}
