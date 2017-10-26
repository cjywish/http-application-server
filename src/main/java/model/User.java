package model;

public class User {
	private String userId;
	private String password;
	private String name;
	private String email;
	
	public User(String userId, String password, String name, String email){
		this.userId = userId;
		this.password = password;
		this.name = name;
		this.email = email;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	@Override 
	public String toString() {
		return "User [userId=" + userId + ", password=" + password + ", name=" + name + ", email=" + email + "]";
	}
	
	
}
