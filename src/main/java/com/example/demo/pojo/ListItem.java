package com.example.demo.pojo;

import lombok.Data;

@Data
public class ListItem {
	private String currency;
	private int quantity;
	private String productName;
	private int unitAmount;
}
