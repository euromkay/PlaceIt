package ucsd.cse105.placeit;

//account object with username/password
public class Account {
	
	private String userName;
	private String password;
	
	public Account(String userName, String password){
		this.userName = userName;
		this.password = password;
	}
	//gets username
	public String getName(){
		return userName;
	}
	//gets password
	public String getPassword(){
		return password;
	}
}
