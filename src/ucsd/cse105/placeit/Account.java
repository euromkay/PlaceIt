package ucsd.cse105.placeit;

public class Account {
	private String userName;
	private String password;
	public Account(String userName, String password){
		this.userName = userName;
		this.password = password;
	}
	
	public String getName(){
		return userName;
	}
	public String getPassword(){
		return password;
	}
}
