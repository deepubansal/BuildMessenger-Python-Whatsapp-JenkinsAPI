package messenger.dto;

import javax.xml.bind.annotation.XmlElement;



public class User {
	
	String number;
	String name;
	public void setName(String name) {
		this.name = name;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	
	
	
	@XmlElement(name="Name")
	public String getName() {
		return name;
	}
	
	@XmlElement(name="Number")
	public String getNumber() {
		return number;
	}
}
